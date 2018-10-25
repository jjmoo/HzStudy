package com.jjmoo.java.hzstudy.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.lib.util.FileUtils;

import java.util.*;

/**
 * Created by user on 17-10-17.
 *
 */
public class CourseStore {
    private static final CourseStore sInstance = new CourseStore();
    public static CourseStore getInstance() { return sInstance; }

    private List<Course> mAllCourseList;
    private Map<String, RichCourse> mRichCourseMap;

    private CourseStore() {
        mAllCourseList = new ArrayList<>();
        mAllCourseList.addAll(loadCourseList());
        mRichCourseMap = new LinkedHashMap<>();
        mRichCourseMap.putAll(loadRichCourseMap());
    }

    public List<Course> getAllCourseList() {
        return mAllCourseList;
    }

    public List<Course> getAvailableCourseList(List<Course> learnedCourseList) {
        List<Course> availableCourseList = new ArrayList<>();
        for (Course course : getAllCourseList()) {
            if (!learnedCourseList.contains(course) && !availableCourseList.contains(course)) {
                availableCourseList.add(course);
            }
        }
        return availableCourseList;
    }

    public Map<String, RichCourse> getRichCourseMap() {
        return mRichCourseMap;
    }

    public void save(List<Course> allCourseList) {
        String json = new Gson().toJson(allCourseList);
        json = json.replaceAll("},\\{", "},\r\n{");
        FileUtils.writeTextFileIgnoreError("json.course.list.all.json", json);
    }

    public void save(Map<String, RichCourse> richCourseMap) {
        String json = new Gson().toJson(richCourseMap);
        json = json.replaceAll(":\\{", ":\r\n{").replaceAll("},", "},\r\n");
        FileUtils.writeTextFileIgnoreError("json.rich.course.map.json", json);
    }

    private List<Course> loadCourseList() {
        String json = FileUtils.readTextFileIgnoreError("json.course.list.all.json");
        List<Course> courseList = new Gson().fromJson(json, new TypeToken<List<Course>>(){}.getType());
        return null != courseList ? courseList : new ArrayList<Course>();
    }

    private Map<String, RichCourse> loadRichCourseMap() {
        String json = FileUtils.readTextFileIgnoreError("json.rich.course.map.json");
        Map<String, RichCourse> richCourseMap = new Gson().fromJson(json,
                new TypeToken<Map<String, RichCourse>>(){}.getType());
        return null != richCourseMap ? richCourseMap : new HashMap<String, RichCourse>();
    }
}
