package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.data.CourseStore;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.net.DataLoader;
import com.jjmoo.java.hzstudy.util.Utils;
import rx.functions.Action1;

import java.util.List;

/**
 * Created by user on 17-10-17.
 *
 */
public class StateNoAllCourses implements IState<UserCenter>{
    @Override
    public void execute(final Controller<UserCenter> controller) {
        DataLoader.getAllCourseList().subscribe(new Action1<List<Course>>() {
            @Override
            public void call(List<Course> courses) {
                List<Course> allCourseList = CourseStore.getInstance().getAllCourseList();
                allCourseList.clear();
                allCourseList.addAll(courses);
                CourseStore.getInstance().save(courses);
                controller.setStateAndExecute(new StateUseInfo());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Utils.handleExceptionForController(controller, throwable);
            }
        });
    }
}
