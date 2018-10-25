package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.Main;
import com.jjmoo.java.hzstudy.data.CourseStore;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.java.hzstudy.net.DataLoader;
import com.jjmoo.lib.util.LogUtils;

import java.util.Map;

/**
 * Created by Zohn on 2017/10/18.
 *
 */
public class StateNoRichCourse implements IState<UserCenter> {
    @Override
    public void execute(Controller<UserCenter> controller) {
        CourseStore courseStore = CourseStore.getInstance();
        Map<String, RichCourse> richCourseMap = courseStore.getRichCourseMap();
        for (Course course : courseStore.getAllCourseList()) {
            LogUtils.i("StateNoRichCourse", "get RichCourse for " + course.getLink());
            RichCourse richCourse = DataLoader.getRichCourse(course);
            if (null != richCourse) {
                richCourseMap.put(course.getLink(), richCourse);
            }
        }
        courseStore.save(richCourseMap);
        System.out.println("------------------------------------------------------------");
        System.out.println("App has to stop. Please perfect the json file and re-run again.");
        Main.exit();
    }
}
