package com.jjmoo.lib.net;

import okhttp3.Response;

import java.io.IOException;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class AuthError extends IOException {
    private static final long serialVersionUID = -1L;

    public static final int ERROR_UNKNOWN = 0;
    public static final int ERROR_USERNAME = 1;
    public static final int ERROR_PASSWORD = 2;
    public static final int ERROR_CODE = 3;

    private int mErr;
    private String mBody;

    public AuthError(int err) {
        super();
        setError(err);
    }

    public AuthError(int err, String message) {
        super(message);
        setError(err);
    }

    public AuthError(int err, Throwable cause) {
        super(cause);
        setError(err);
    }

    public AuthError(int err, String message, Throwable cause) {
        super(message, cause);
        setError(err);
    }

    public int getError() {
        checkErr();
        return mErr;
    }

    public AuthError setBody(String body) {
        mBody = body;
        return this;
    }

    public String getBody() {
        return mBody;
    }

    private void setError(int err) {
        mErr = err;
        checkErr();
    }

    private void checkErr() {
        if (mErr < ERROR_UNKNOWN || mErr > ERROR_CODE) {
            throw new IllegalArgumentException(
                    "illegal err: " + mErr + ", out of [" + ERROR_UNKNOWN + ", " + ERROR_CODE + "]");
        }
    }
}
