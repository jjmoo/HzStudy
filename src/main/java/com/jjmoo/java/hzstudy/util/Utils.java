package com.jjmoo.java.hzstudy.util;

import com.jjmoo.java.hzstudy.Main;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.java.hzstudy.state.StateUnAuth;
import com.jjmoo.java.hzstudy.state.Controller;
import com.jjmoo.lib.net.AuthError;
import com.jjmoo.lib.util.LogUtils;

/**
 * Created by user on 17-10-17.
 *
 */
public class Utils {
    private static final String TAG = "Utils";

    public static final boolean DEBUG = false;

    public static void handleExceptionForController(Controller<UserCenter> controller, Throwable throwable) {
        if (throwable instanceof AuthError) {
            AuthError ae = (AuthError) throwable;
            controller.setStateAndExecute(new StateUnAuth(ae.getBody()));
        } else {
            LogUtils.e(TAG, "FATAL ERROR: unknown exception caught.", throwable);
            Main.exit();
        }
    }
}
