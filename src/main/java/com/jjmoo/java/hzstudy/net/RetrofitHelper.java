package com.jjmoo.java.hzstudy.net;

import com.jjmoo.java.hzstudy.entity.AllCourseInterim;
import com.jjmoo.java.hzstudy.entity.Course;
import com.jjmoo.java.hzstudy.entity.RichCourse;
import com.jjmoo.java.hzstudy.entity.UserInfo;
import com.jjmoo.java.hzstudy.util.Utils;
import com.jjmoo.lib.image.ImgData;
import com.jjmoo.lib.net.*;
import com.jjmoo.lib.util.FileUtils;
import com.jjmoo.lib.util.LogUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.http.*;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 17-10-16.
 *
 */
@SuppressWarnings("unused")
public class RetrofitHelper implements ServiceCreator.Helper<RetrofitHelper.Service> {
    @Override
    public String getBaseUrl() {
        return Service.baseUrl;
    }

    @Override
    public Class<Service> getServiceInterface() {
        return Service.class;
    }

    @Override
    public OkHttpClient getOkHttpClient() {
        ClientCreator creator = new ClientCreator();
        if (Utils.DEBUG) {
            creator.debug(new ClientCreator.DebugCallback() {
                @Override
                public void onStart(String source, String url) {
                    LogUtils.d("RetrofitHelper", source + "[start]: " + url);
                }

                @Override
                public void onFinish(String source, String url) {
                    LogUtils.d("RetrofitHelper", source + "[finish]: " + url);
                }
            });
        }
        return creator.authChecker(new IAuthChecker() {
            @Override
            public boolean needAuthenticate(String url) {
                return url.contains("login");
            }

            @Override
            public void authenticate(Response response) throws IOException {
                throw new AuthError(AuthError.ERROR_UNKNOWN).setBody(response.body().string());
            }
        })
                .cookieControl(new ICookieControl() {
                    @Override
                    public void save(String key, String cookie) {
                        FileUtils.writeTextFileIgnoreError("json.cookie." + key + ".json", cookie);
                    }

                    @Override
                    public boolean isUserChanged() {
                        return false;
                    }

                    @Override
                    public String load(String key) {
                        return FileUtils.readTextFileIgnoreError("json.cookie." + key + ".json");
                    }
                })
                .create();
    }

    public interface Service {
        String baseUrl = "http://study.huizhou.gov.cn";

        @GET("my/MyCourse.aspx?type=3")
        @ResponseBodyConverter(UserBaseInfoConverter.class)
        Observable<UserInfo> getUserBaseInfo();

        @FormUrlEncoded
        @POST("my/MyCourse.aspx?type=3")
        @ResponseBodyConverter(MoreLearnedCourseListConverter.class)
        Call<List<Course>> getMoreLearnedCourses(@FieldMap Map<String, String> map);

        @GET("CodeImg.aspx")
        @ResponseBodyConverter(ImgDataConverter.class)
        Call<ImgData> getCodeImg(@Query("") int random);

        @FormUrlEncoded
        @POST("login.aspx?ReturnUrl=/my/mycourse.aspx?type=3")
        Call<String> login(@FieldMap Map<String, String> map);

        @GET("login.aspx?ReturnUrl=/my/mycourse.aspx?type=3&Kick=True")
        Call<String> kick(@Query("UserId") String user);

        @GET("Logout.aspx?from=cancel")
        Call<String> logout();

        @GET("Course/Default.aspx?txtvalue=&selecttype=coursetype&type=%E5%8D%95%E8%A7%86%E9%A2%91")
        @ResponseBodyConverter(FirstPageAllCourseListConverter.class)
        Observable<AllCourseInterim> getFirstPageAllCourse();

        @FormUrlEncoded
        @POST("Course/Default.aspx?txtvalue=&selecttype=coursetype&type=%E5%8D%95%E8%A7%86%E9%A2%91")
        @ResponseBodyConverter(MorePageAllCourseListConverter.class)
        Call<List<Course>> getMoreAllCourse(@FieldMap Map<String, String> map);

        @GET("play/redirect.aspx")
        @ResponseBodyConverter(PlayCoreUserNmConverter.class)
        Call<String> getPlayCoreUserNm(@Query("id") String idInCourse, @Query("user_id") String userId);

        @GET("/play/PlayCore.aspx")
        @ResponseBodyConverter(RealPlayAddressConverter.class)
        Call<RichCourse> getRealPlayAddress(@Query("user_nm") String userNm, @Query("course_id") String courseId);

        @GET("play/MmsProgress.ashx")
        Call<String> mockLearn(@QueryMap Map<String, String> map);
    }

    //-------------------------------------------------------------------------------
    public static class UserBaseInfoConverter implements Converter<ResponseBody, UserInfo> {
        @Override
        public UserInfo convert(ResponseBody body) throws IOException {
            UserInfo userInfo = new UserInfo();
            String html = body.string();
            Document doc = Jsoup.parse(html);

            Pattern pattern = Pattern.compile("auth\\.aspx.*?ID=(\\w+)(\\W)");
            for (Element element : doc.select("div.top_main_menu").get(0).select("a")) {
                String href = element.attr("href");
                Matcher matcher = pattern.matcher(href);
                if (matcher.find()) {
                    userInfo.setUserId(matcher.group(1));
                }
            }

            for (Element element : doc.select(".UCUserLogin_Ma_Ac_05_lesp")) {
                String text = element.text();
                if (text.contains("欢迎")) {
                    userInfo.setName(text.substring(text.indexOf("欢迎") + 4).trim());
                } else if (text.contains("总学分")) {
                    userInfo.setScore(text.substring(text.indexOf("总学分") + 4).trim());
                } else if (text.contains("络学分")) {
                    userInfo.setNetScore(text.substring(text.indexOf("络学分") + 4).trim());
                } else if (text.contains("产学分")) {
                    userInfo.setOfflineScore(text.substring(text.indexOf("产学分") + 4).trim());
                }
            }

            Elements elements = doc.select("#ctl10_ctl00_AspNetPager1 div");
            if (elements.isEmpty()) {
                userInfo.setUserType(UserInfo.LCP);
                elements = doc.select("#ctl09_ctl00_AspNetPager1 div");
            }
            String info = elements.get(1).text();
            userInfo.setPageNum(Integer.parseInt(info.substring(info.indexOf("，共") + 2, info.indexOf("页，第"))));

            List<Course> courseList = extractLearnedCourseListFromDocument(doc);
            userInfo.getLearnedCourses().addAll(courseList);

            userInfo.setFormMap(FormMap.parse(html));

            return userInfo;
        }
    }

    public static class ImgDataConverter implements Converter<ResponseBody, ImgData> {
        @Override
        public ImgData convert(ResponseBody body) throws IOException {
            return new ImgData(body.bytes());
        }
    }

    public static class MoreLearnedCourseListConverter implements Converter<ResponseBody, List<Course>> {
        @Override
        public List<Course> convert(ResponseBody body) throws IOException {
            String html = body.string();
            return extractLearnedCourseListFromDocument(Jsoup.parse(html));
        }
    }

    public static class FirstPageAllCourseListConverter implements Converter<ResponseBody, AllCourseInterim> {
        @Override
        public AllCourseInterim convert(ResponseBody body) throws IOException {
            AllCourseInterim allCourseInterim = new AllCourseInterim();
            String html = body.string();
            allCourseInterim.setHtml(html);
            allCourseInterim.getCourseList().addAll(extractAllCourseListFromDocument(Jsoup.parse(html)));
            return allCourseInterim;
        }
    }

    public static class MorePageAllCourseListConverter implements Converter<ResponseBody, List<Course>> {
        @Override
        public List<Course> convert(ResponseBody body) throws IOException {
            return extractAllCourseListFromDocument(Jsoup.parse(body.string()));
        }
    }

    public static class PlayCoreUserNmConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(ResponseBody body) throws IOException {
            String html = body.string();
            Pattern pattern = Pattern.compile("PlayCore\\.aspx\\?user_nm=(\\w+?)\\W");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            } else if (html.contains("您好，您今天已完成规定的学分！")) {
                throw new KnownIOException.OutOfScoreException();
            } else {
                throw new IOException("cannot extract \"user_nm\" from HTML content.");
            }
        }
    }

    public static class RealPlayAddressConverter implements Converter<ResponseBody, RichCourse> {
        @Override
        public RichCourse convert(ResponseBody body) throws IOException {
            String html = body.string();
            Document doc = Jsoup.parse(html);
            String action = doc.select("#form1").get(0).attr("action");
            Pattern pattern = Pattern.compile("right\\.aspx\\?.*?id=(.*?)&.*?course_id=(.*?)&");
            Matcher matcher = pattern.matcher(action);
            if (matcher.find()) {
                RichCourse richCourse = new RichCourse();
                richCourse.setId(matcher.group(1));
                richCourse.setCourseId(matcher.group(2));
                return richCourse;
            } else {
                throw new IOException("cannot extract RichCourse's \"id\" & \"course_id\"  from HTML content. " +
                        "action: " + action);
            }
        }
    }

    private static List<Course> extractLearnedCourseListFromDocument(Document doc) {
        List<Course> courseList = new ArrayList<>();
        Element element = doc.select("div.MyFinishList_Main_Ac_08 ul").get(1);
        Elements list4 = element.select("li.list4");
        Elements list6 = element.select("li.list6");
        Elements list9 = element.select("li.list9");
        for (int j = 0; j < list4.size(); j++) {
            Course course = new Course();
            course.setTitle(list4.get(j).select("span a").first().attr("title"));
            course.setScore(list6.get(4 * j).text());
            course.setType(list9.get(3 * j).getElementsByTag("span").first().text());
            course.setLink(list4.get(j).select("span a").first().attr("href").replace(":80", ""));
            course.setId(extractCourseIdFromLink(course.getLink()));
            if (!courseList.contains(course)) {
                courseList.add(course);
            }
        }
        return courseList;
    }

    private static List<Course> extractAllCourseListFromDocument(Document doc) {
        List<Course> courseList = new ArrayList<>();
        Elements trs = doc.select("#ctl13_gvCourse tbody tr");
        if (trs.isEmpty()) {
            trs = doc.select("#ctl12_gvCourse tbody tr");
        }
        for (int j = 1; j < trs.size(); j++) {
            Elements tds = trs.get(j).getElementsByTag("td");
            Element a = tds.get(1).getElementsByTag("a").first();
            Course course = new Course();
            course.setTitle(a.attr("title"));
            course.setScore(tds.get(4).text());
            course.setType("单视频");
            course.setLink("http://study.huizhou.gov.cn" + a.attr("href"));
            course.setId(extractCourseIdFromLink(course.getLink()));
            courseList.add(course);
        }
        return courseList;
    }

    private static String extractCourseIdFromLink(String link) {
        return link.substring(link.lastIndexOf("=") + 1);
    }
}
