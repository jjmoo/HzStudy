package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.Main;
import com.jjmoo.java.hzstudy.data.CourseStore;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.java.hzstudy.entity.UserInfo;
import com.jjmoo.java.hzstudy.net.DataLoader;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.java.hzstudy.util.Utils;
import rx.functions.Action1;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by user on 17-10-16.
 *
 */
public class StateUseInfo implements IState<UserCenter> {
    private static boolean isFirst = true;
    private static int recommend = new Random().nextInt(10) + 100;

    @Override
    public void execute(final Controller<UserCenter> controller) {
        DataLoader.getUserBaseInfo().subscribe(
                new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        UserCenter user = controller.get();
                        user.setBaseInfo(userInfo);
                        handle(controller, user);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.handleExceptionForController(controller, throwable);
                    }
                }
        );
    }

    private void handle(Controller<UserCenter> controller, UserCenter user) {
        List<Course> learnedCourseList = user.getBaseInfo().getLearnedCourses();
        List<Course> availableCourseList = CourseStore.getInstance().getAvailableCourseList(learnedCourseList);
        if (availableCourseList.isEmpty()) {
            controller.setStateAndExecute(new StateNoAllCourses());
            return;
        }

        Map<String, RichCourse> richCourseMap = CourseStore.getInstance().getRichCourseMap();
        if (richCourseMap.isEmpty() || 0 == richCourseMap.values().iterator().next().getMaxTimeMs()) {
            controller.setStateAndExecute(new StateNoRichCourse());
            return;
        }

        System.out.println();
        System.out.println("============================================================");
        System.out.println("name:    " + user.getBaseInfo().getName());
        System.out.println("score:   " + user.getBaseInfo().getScore());
        System.out.println("online:  " + user.getBaseInfo().getNetScore());
        System.out.println("offline: " + user.getBaseInfo().getOfflineScore());
        System.out.println("------------------------------------------------------------");

        Course courseToLearn = null;

        for (Course course : availableCourseList) {
            if (richCourseMap.containsKey(course.getLink())) {
                courseToLearn = course;
                break;
            }
        }

        if (null == courseToLearn) {
            for (Course course : availableCourseList) {
                System.out.println("    [" + course.getType() + "][" + course.getScore()
                        + "][" + course.getTitle() + "] " + course.getLink());
            }
            System.out.println("------------------------------------------------------------");
            System.out.println("Sorry. There is no course available to learn.");
            Main.exit();
        } else {
            System.out.println("Supposed to learn the class below: ");
            System.out.println("    [" + courseToLearn.getType() + "][" + courseToLearn.getScore()
                    + "][" + courseToLearn.getTitle() + "] " + courseToLearn.getLink());
            while (true) {
                System.out.print("\nLearn this? Please choose: Y/N? (default:Y) (target=" + recommend + ")");
                float netScore = Integer.MAX_VALUE;
                try {
                    netScore = Float.parseFloat(user.getBaseInfo().getNetScore());
                } catch (Exception e) {
                    // ignore
                }
                String cmd;
                if (isFirst || netScore >= recommend) {
                    isFirst = false;
                    Scanner scanner = new Scanner(System.in);
                    cmd = scanner.nextLine();
                } else {
                    cmd = "";
                }
                if ("".equals(cmd.trim()) || "Y".equals(cmd.trim()) || "y".equals(cmd.trim())) {
                    controller.setStateAndExecute(new StateMockLearn(courseToLearn));
                    break;
                }
                if ("N".equals(cmd.trim()) || "n".equals(cmd.trim())) {
                    Main.exit();
                    break;
                }
            }
        }
    }
}
