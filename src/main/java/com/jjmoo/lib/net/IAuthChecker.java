package com.jjmoo.lib.net;

import okhttp3.Response;

import java.io.IOException;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
public interface IAuthChecker {
    boolean needAuthenticate(String url);
    void authenticate(Response response) throws IOException;
}
