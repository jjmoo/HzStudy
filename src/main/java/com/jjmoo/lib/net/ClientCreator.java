package com.jjmoo.lib.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class ClientCreator {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

    private DebugCallback mDebugger;
    private IAuthChecker mAuthChecker;
    private ICookieControl mCookieControl;
    private Gson mGson = new Gson();

    public ClientCreator debug(DebugCallback debugger) {
        mDebugger = debugger;
        return this;
    }

    public ClientCreator authChecker(IAuthChecker checker) {
        mAuthChecker = checker;
        return this;
    }

    public ClientCreator cookieControl(ICookieControl cookieSaver) {
        mCookieControl = cookieSaver;
        return this;
    }

    public OkHttpClient create() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new MyCookieJar())
                .addInterceptor(new MyInterceptor());
        if (null != mDebugger) {
            builder.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    mDebugger.onStart(DebugCallback.SOURCE_NETWORK, chain.request().url().toString());
                    Response response = chain.proceed(chain.request());
                    mDebugger.onFinish(DebugCallback.SOURCE_NETWORK, response.request().url().toString());
                    return response;
                }
            });
        }
        return builder.build();
    }

    public interface DebugCallback {
        String SOURCE_NETWORK = "network-interceptor";
        String SOURCE_APP = "app-interceptor";
        void onStart(String source, String url);
        void onFinish(String source, String url);
    }

    private class MyInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (null != mDebugger) {
                mDebugger.onStart(DebugCallback.SOURCE_APP, chain.request().url().toString());
            }
            Request request = chain.request().newBuilder().addHeader(
                    "User-Agent", USER_AGENT).build();
            Response response = chain.proceed(request);
            String originalUrl = request.url().toString();
            String currentUrl = response.request().url().toString();
            if ( null != mAuthChecker
                    && !mAuthChecker.needAuthenticate(originalUrl) && mAuthChecker.needAuthenticate(currentUrl)) {
                mAuthChecker.authenticate(response);
                response.close();
                response = chain.proceed(chain.request());
            }
            if (null != mDebugger) {
                mDebugger.onFinish(DebugCallback.SOURCE_APP, response.request().url().toString());
            }
            return response;
        }
    }

    private class MyCookieJar implements CookieJar {
        private Map<String, List<Cookie>> mCookiesMap = new HashMap<>();
        private String mLastUsername;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            String key = url.host();
            if (!mCookiesMap.containsKey(key)) {
                mCookiesMap.put(key, new ArrayList<Cookie>());
            }
            List<Cookie> savedCookies = mCookiesMap.get(key);
            for (Cookie cookie : cookies) {
                boolean isContained = false;
                for (int i = 0; i < savedCookies.size(); i++) {
                    if (savedCookies.get(i).name().equals(cookie.name())) {
                        savedCookies.remove(i);
                        savedCookies.add(i, cookie);
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    savedCookies.add(cookie);
                }
            }
            if (null != mCookieControl) {
                mCookieControl.save(url.host(), mGson.toJson(savedCookies));
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            String key = url.host();
            if (null != mCookieControl && mCookieControl.isUserChanged() || !mCookiesMap.containsKey(key)) {
                List<Cookie> cookies = null;
                if (null != mCookieControl) {
                    String json = mCookieControl.load(key);
                    if (null != json) {
                        cookies = new Gson().fromJson(
                                json, new TypeToken<List<Cookie>>(){}.getType());
                    }
                }
                if (null == cookies) {
                    cookies = new ArrayList<>();
                }
                mCookiesMap.put(key, cookies);
            }
            return mCookiesMap.get(key);
        }
    }
}
