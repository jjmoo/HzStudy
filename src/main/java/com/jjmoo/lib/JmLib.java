package com.jjmoo.lib;

/**
 * Created by pz on 17/10/15.
 *
 */
public class JmLib {
    public static final int PLATFORM_JAVA = 0;
    public static final int PLATFORM_ANDROID = 1;

    private static int platform;

    static {
        try {
            Class.forName("android.app.activity");
            platform = PLATFORM_ANDROID;
        } catch (ClassNotFoundException e) {
            platform = PLATFORM_JAVA;
        }
    }

    public static boolean isJavaPlatform() {
        return PLATFORM_JAVA == platform;
    }

    public static boolean isAndroidPlatform() {
        return PLATFORM_ANDROID == platform;
    }
}
