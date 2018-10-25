package com.jjmoo.java.hzstudy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
public class UserInfo {
    private String userId;
    private String name;
    private String score;
    private String netScore;
    private String offlineScore;
    private int pageNum;
    private List<Course> learnedCourses;
    private Map<String, String> formMap;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNetScore() {
        return netScore;
    }

    public void setNetScore(String netScore) {
        this.netScore = netScore;
    }

    public String getOfflineScore() {
        return offlineScore;
    }

    public void setOfflineScore(String offlineScore) {
        this.offlineScore = offlineScore;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<Course> getLearnedCourses() {
        return null != learnedCourses ? learnedCourses : (learnedCourses = new ArrayList<>());
    }

    public Map<String, String> getFormMap() {
        return formMap;
    }

    public void setFormMap(Map<String, String> formMap) {
        this.formMap = formMap;
    }
}
