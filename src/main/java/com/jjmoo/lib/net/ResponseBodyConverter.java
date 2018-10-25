package com.jjmoo.lib.net;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by user on 17-10-17.
 *
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResponseBodyConverter {
    Class<? extends Converter<ResponseBody, ?>> value();
}
