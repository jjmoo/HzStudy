package com.jjmoo.java.hzstudy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-10-17.
 *
 */
public class AllCourseInterim {
    private String html;
    private List<Course> courseList;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<Course> getCourseList() {
        if (null == courseList) {
            courseList = new ArrayList<>();
        }
        return courseList;
    }
}
