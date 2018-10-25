package com.jjmoo.java.hzstudy.net;

import com.jjmoo.java.hzstudy.entity.AllCourseInterim;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.java.hzstudy.entity.UserInfo;
import com.jjmoo.lib.image.ImgData;
import com.jjmoo.lib.net.FormMap;
import com.jjmoo.lib.net.ServiceCreator;
import com.jjmoo.lib.util.LogUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 17-10-16.
 *
 */
public class DataLoader {
    private static final String TAG = "DataLoader";

    private static final RetrofitHelper.Service mService;

    static {
        mService = ServiceCreator.create(new RetrofitHelper());
    }

    public static Observable<UserInfo> getUserBaseInfo() {
        return mService.getUserBaseInfo().doOnNext(new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                try {
                    for (int i = 2; i <= userInfo.getPageNum(); i++) {
                        Map<String, String> map = userInfo.getFormMap();
                        map.put("__EVENTTARGET", "ctl10$ctl00$AspNetPager1");
                        map.put("__EVENTARGUMENT", String.valueOf(i));
                        userInfo.getLearnedCourses().addAll(mService.getMoreLearnedCourses(map).execute().body());
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG, "failed to load more learned courses.", e);
                }
            }
        });
    }

    public static ImgData getCodeImg() throws IOException {
        return mService.getCodeImg(100000 + (int) (Math.random() * 900000)).execute().body();
    }

    public static String login(Map<String, String> map) throws IOException {
        return mService.login(map).execute().body();
    }

    public static String kick(String user) throws IOException  {
        return mService.kick(user).execute().body();
    }

    public static void logout() {
        try {
            mService.logout().execute();
        } catch (IOException e) {
            LogUtils.e(TAG, "failed to logout.", e);
        }
    }

    public static Observable<List<Course>> getAllCourseList() {
        return mService.getFirstPageAllCourse().map(new Func1<AllCourseInterim, List<Course>>() {
            @Override
            public List<Course> call(AllCourseInterim allCourseInterim) {
                List<Course> allCourseList = allCourseInterim.getCourseList();
                Document doc = Jsoup.parse(allCourseInterim.getHtml());
                Elements elements = doc.select("#ctl13_AspNetPager1 div");
                if (elements.isEmpty()) {
                    elements = doc.select("#ctl12_AspNetPager1 div");
                }
                String info = elements.get(1).text();
                int pageNum = Integer.parseInt(info.substring(info.indexOf("，共") + 2, info.indexOf("页，第")));
                if (pageNum > 1) {
                    Map<String, String> map = FormMap.parse(allCourseInterim.getHtml());
                    try {
                        for (int i = 2; i <= pageNum; i++) {
                            map.put("__EVENTTARGET", "ctl13$AspNetPager1");
                            map.put("__EVENTARGUMENT", String.valueOf(i));
                            List<String> toRemoveKeyList = new ArrayList<>();
                            for (String key : map.keySet()) {
                                if (key.contains(".x") || key.contains(".y")) {
                                    toRemoveKeyList.add(key);
                                }
                            }
                            for (String key : toRemoveKeyList) {
                                map.remove(key);
                            }
                            List<Course> courses = mService.getMoreAllCourse(map).execute().body();
                            for (Course course : courses) {
                                if (!allCourseList.contains(course)) {
                                    allCourseList.add(course);
                                }
                            }
                        }
                    } catch (IOException e) {
                        LogUtils.e(TAG, "failed to load more all courses.");
                    }
                }
                return allCourseList;
            }
        });
    }

    public static RichCourse getRichCourse(Course course) {
        try {
            RichCourse richCourse = mService.getRealPlayAddress("0", course.getId()).execute().body();
            if (null != richCourse) {
                richCourse.setLink(course.getLink());
                richCourse.setIdFromCourse(course.getId());
                richCourse.setMaxTimeMs(0);
                richCourse.setLength("00:00");
                return richCourse;
            } else {
                LogUtils.e(TAG, "failed to load RichCourse from Course: data is null.");
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "failed to load RichCourse from Course.", e);
        }
        return null;
    }

    public static void validateCourse(Course course, String userId) throws KnownIOException.OutOfScoreException{
        KnownIOException.OutOfScoreException toThrow = null;
        try {
            String userNm;
            try {
                userNm = mService.getPlayCoreUserNm(course.getId(), userId).execute().body();
            } catch (KnownIOException.OutOfScoreException e) {
                toThrow = e;
                userNm = "0";
            }
            mService.getRealPlayAddress(userNm, course.getId()).execute().body();
        } catch (IOException e) {
            LogUtils.e(TAG, "maybe fail to validate course.", e);
        }

        if (null != toThrow) {
            throw toThrow;
        }
    }

    public static String sendMockLearnProgress(Map<String, String> map) {
        try {
            for (int i = 0; i < 3; i++) {
                String body = mService.mockLearn(map).execute().body();
                if (null != body) {
                    return body.trim();
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "failed to send mock learn progress.", e);
        }
        return null;
    }
}
