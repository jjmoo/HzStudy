package com.jjmoo.java.hzstudy.entity;

/**
 * Created by Zohn on 2017/10/17.
 *
 */
public class RichCourse {
    private String link;
    private String idFromCourse;
    private String id;
    private String courseId;
    private String length;
    private int maxTimeMs;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIdFromCourse() {
        return idFromCourse;
    }

    public void setIdFromCourse(String idFromCourse) {
        this.idFromCourse = idFromCourse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getMaxTimeMs() {
        return maxTimeMs;
    }

    public void setMaxTimeMs(int maxTimeMs) {
        this.maxTimeMs = maxTimeMs;
    }
}
