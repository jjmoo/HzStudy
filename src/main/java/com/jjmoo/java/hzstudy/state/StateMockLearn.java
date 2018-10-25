package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.Main;
import com.jjmoo.java.hzstudy.data.CourseStore;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.java.hzstudy.net.DataLoader;
import com.jjmoo.java.hzstudy.net.KnownIOException;
import com.jjmoo.lib.util.LogUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by user on 17-10-18.
 *
 */
public class StateMockLearn implements IState<UserCenter> {
    private static final String TAG = "StateMockLearn";

    private static int sStopScore = -1;

    private final Course mCourse;

    public StateMockLearn(Course course) {
        mCourse = course;
    }

    @Override
    public void execute(Controller<UserCenter> controller) {
        if (null == mCourse) {
            LogUtils.e(TAG, "FATAL ERROR: mCourse is null.");
        } else {
            try {
                DataLoader.validateCourse(mCourse, controller.get().getBaseInfo().getUserId());
            } catch (KnownIOException.OutOfScoreException e) {
//                System.out.println("");
//                System.out.println("WARNING：THE SCORE YOU CAN GET IS LIMITED. " +
//                        "IT MAY PUT YOU AT UNCERTAIN RISK TO CONTINUE.");
//                while (true) {
//                    System.out.print("\nContinue? Please choose: Y/N? (default:Y) ");
//                    Scanner scanner = new Scanner(System.in);
//                    String cmd = scanner.nextLine();
//                    if ("".equals(cmd.trim()) || "Y".equals(cmd.trim()) || "y".equals(cmd.trim())) {
//                        break;
//                    }
//                    if ("N".equals(cmd.trim()) || "n".equals(cmd.trim())) {
//                        System.out.println("------------------------------------------------------------");
//                        System.out.println("User canceled with fear.");
//                        Main.exit();
//                        return;
//                    }
//                }
                if (-1 == sStopScore) {
                    System.out.println("");
                    System.out.println("WARNING：THE SCORE YOU CAN GET IS LIMITED. " +
                        "IT MAY PUT YOU AT UNCERTAIN RISK TO CONTINUE.");
                    while (true) {
                        System.out.print("\nCurrent net score: " + controller.get().getBaseInfo().getNetScore() + ". " +
                                "\n    Please input expected online score to continue or N/n to quit: (default: 81) ");
                        Scanner scanner = new Scanner(System.in);
                        String cmd = scanner.nextLine().trim();
                        if ("N".equals(cmd) || "n".equals(cmd)) {
                            System.out.println("------------------------------------------------------------");
                            System.out.println("User canceled with fear.");
                            Main.exit();
                            return;
                        } else if ("".equals(cmd)) {
                            sStopScore = 81;
                        } else {
                            try {
                                sStopScore = Integer.parseInt(cmd);
                                break;
                            } catch (NumberFormatException ne) {
                                System.out.println("Illegal input.");
                            }
                        }
                    }
                }
                float curScore = Float.parseFloat(controller.get().getBaseInfo().getNetScore());
                if (curScore >= sStopScore) {
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Got enough score. Thank you for support.");
                    Main.exit();
                    return;
                }
            }

            RichCourse richCourse = CourseStore.getInstance().getRichCourseMap().get(mCourse.getLink());
            Map<String, String> map = new LinkedHashMap<>();
            map.put("id", richCourse.getId());
            map.put("student_id", controller.get().getBaseInfo().getUserId());
            map.put("course_id", richCourse.getCourseId());
            map.put("uidcount", "1");
            map.put("timems", "1");
            map.put("length", richCourse.getLength());

            System.out.println("");
            System.out.print("Rate： 00%");
            String result = null;

            try {
                for (int i = 1; i <= richCourse.getMaxTimeMs(); i++) {
                    map.put("timems", String.valueOf(i));
                    result = DataLoader.sendMockLearnProgress(map);
                    if (null != result) {
                        int rate = Integer.parseInt(result);
                        System.out.print("\rRate： " + String.format(Locale.CHINA, "%02d%%", rate));
                    } else {
                        throw new RuntimeException("failed to send mock data for 3 times. restart again!");
                    }
                }
                System.out.println("\rRate： 100%");

            } catch (NumberFormatException e) {
                System.out.print("\nUnexpected data returned： " + result);
            }

            controller.setStateAndExecute(new StateUseInfo());
        }
    }
}
