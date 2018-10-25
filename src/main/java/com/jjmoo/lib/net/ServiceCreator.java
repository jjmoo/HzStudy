package com.jjmoo.lib.net;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.jjmoo.lib.util.LogUtils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class ServiceCreator {
    public interface Helper<T> {
        String getBaseUrl();
        Class<T> getServiceInterface();
        OkHttpClient getOkHttpClient();
    }

    public static <T> T create(final Helper<T> helper) {
        return new Retrofit.Builder()
                .baseUrl(helper.getBaseUrl())
                .client(helper.getOkHttpClient())
                .addConverterFactory(new MyFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(helper.getServiceInterface());
    }

    private static class MyFactory extends Converter.Factory {
        private Map<Class<? extends Converter<ResponseBody, ?>>, Converter<ResponseBody, ?>> mConverterCaches;
        private Converter<ResponseBody, String> mStringConverter;

        private MyFactory() {
            mConverterCaches = new HashMap<>();
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(
                Type type, Annotation[] annotations, Retrofit retrofit) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ResponseBodyConverter) {
                    ResponseBodyConverter converterAnnotation = (ResponseBodyConverter) annotation;
                    Class<? extends Converter<ResponseBody, ?>> convertClazz = converterAnnotation.value();
                    if (mConverterCaches.containsKey(convertClazz)) {
                        return mConverterCaches.get(convertClazz);
                    } else {
                        Converter<ResponseBody, ?> converter = null;
                        try {
                            converter = convertClazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            LogUtils.e("ServiceCreator", "failed to create converter for " + convertClazz);
                        }
                        mConverterCaches.put(convertClazz, converter);
                        return converter;
                    }
                }
            }
            if (type == String.class) {
                if (null == mStringConverter) {
                    mStringConverter = new Converter<ResponseBody, String>() {
                        @Override
                        public String convert(ResponseBody body) throws IOException {
                            return body.string();
                        }
                    };
                }
                return mStringConverter;
            }
            return super.responseBodyConverter(type, annotations, retrofit);
        }
    }
}
