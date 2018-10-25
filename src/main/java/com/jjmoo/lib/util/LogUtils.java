///*
// * MIT License
// *
// * Copyright (c) 2017 Zohn
// * email <pengzhuo1993@foxmail.com>
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//

package com.jjmoo.lib.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    public static void v(String tag, Object obj) {
        log("V", tag, obj);
    }

    public static void d(String tag, Object obj) {
        log("D", tag, obj);
    }

    public static void i(String tag, Object obj) {
        log("I", tag, obj);
    }

    public static void w(String tag, Object obj) {
        log("W", tag, obj);
    }

    public static void w(String tag, Object obj, Throwable e) {
        log("W", tag, obj);
        e.printStackTrace();
    }

    public static void e(String tag, Object obj) {
        log("E", tag, obj);
    }

    public static void e(String tag, Object obj, Throwable e) {
        log("E", tag, obj);
        e.printStackTrace();
    }

    private static void log(String level, String tag, Object msg) {
        System.out.print(String.format(Locale.CHINA, "%s  %s  %s:  %s\n",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(new Date()),
                level, tag, String.valueOf(msg)));
    }
}


//package com.jjmoo.lib.util;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * Descriptions
// * <p><br>
// *
// * @author user
// */
//@SuppressWarnings("unused")
//public class LogUtils {
////    private static final String LOG_TAG_PRE = Bunny.sAppName;
////    private static final boolean WRITE_TO_FILE = Boolean.parseBoolean(Bunny.sWriteLogToFile);
////    private static final String LOG_PATH_PRE = AppContext.getDefaultRootPath()
////            + "/" + Bunny.sLogFilePathHeader;
////
////    private static final String TAG = getFullTag("LogUtils");
////    private static final String PID = String.valueOf(android.os.Process.myPid());
////    private static final String UID = String.valueOf(android.os.Process.myUid());
//
//    private static final String LEVEL_V = "V";
//    private static final String LEVEL_D = "D";
//    private static final String LEVEL_I = "I";
//    private static final String LEVEL_W = "W";
//    private static final String LEVEL_E = "E";
//
//    private static String sLogPath;
//    private static FileWriter sWriter;
//    private static final Runnable SAVE_RUN = new Runnable() { public void run() { saveToFile(); }};
//
//    public static void v(Object msg) {
//        log(LEVEL_V, null, String.valueOf(msg), null);
//    }
//
//    public static void v(String tag, Object msg) {
//        log(LEVEL_V, tag, String.valueOf(msg), null);
//    }
//
//
//    public static void d(Object msg) {
//        log(LEVEL_D, null, String.valueOf(msg), null);
//    }
//
//    public static void d(String tag, Object msg) {
//        log(LEVEL_D, tag, String.valueOf(msg), null);
//    }
//
//
//    public static void i(Object msg) {
//        log(LEVEL_I, null, String.valueOf(msg), null);
//    }
//
//    public static void i(String tag, Object msg) {
//        log(LEVEL_I, tag, String.valueOf(msg), null);
//    }
//
//
//    public static void w(Object msg) {
//        log(LEVEL_W, null, String.valueOf(msg), null);
//    }
//
//    public static void w(String tag, Object msg) {
//        log(LEVEL_W, tag, String.valueOf(msg), null);
//    }
//
//    public static void w(String tag, Object msg, Throwable e) {
//        log(LEVEL_W, tag, String.valueOf(msg), e);
//    }
//
//
//    public static void e(Object msg) {
//        log(LEVEL_E, null, String.valueOf(msg), null);
//    }
//
//    public static void e(String tag, Object msg) {
//        log(LEVEL_E, tag, String.valueOf(msg), null);
//    }
//
//    public static void e(String tag, Object msg, Throwable e) {
//        log(LEVEL_E, tag, String.valueOf(msg), e);
//    }
//
//    public static String getBrief(Object obj) {
//        String className = obj.getClass().toString();
//        return className.substring(className.lastIndexOf('.') + 1);
//    }
//
//    public static synchronized void saveToFile() {
//        if (null != (sWriter = getWriter())) {
//            FileUtils.closeIgnoreException(sWriter);
//            sWriter = null;
//        } else {
////            Log.e(TAG, "getWriter() returned null when close file.");
//        }
//    }
//
////    private static String getFullTag(String tag) {
////        if (null == tag) {
////            tag = new Exception().getStackTrace()[3].toString().split("\\(|(\\.java:)")[1];
////        }
////        return String.format(Locale.CHINA, "%s/%s", LOG_TAG_PRE, tag);
////    }
//
//    private static void log(String level, String tag, String msg, Throwable e) {
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
//                .format(new Date());
//
////        if (WRITE_TO_FILE) {
////            writeToLogFile(time, level, tag, msg);
////            if (null != e) {
////                for (String line : getStackTrace(e).split("\n")) {
////                    writeToLogFile(time, level, tag, line);
////                }
////            }
////        }
//
////        tag = getFullTag(tag);
////        switch (level) {
////            case LEVEL_V:
////                if (null == e) {
////                    Log.v(tag, msg);
////                } else {
////                    Log.v(tag, msg, e);
////                }
////                break;
////            case LEVEL_D:
////                if (null == e) {
////                    Log.d(tag, msg);
////                } else {
////                    Log.d(tag, msg, e);
////                }
////                break;
////            case LEVEL_I:
////                if (null == e) {
////                    Log.i(tag, msg);
////                } else {
////                    Log.i(tag, msg, e);
////                }
////                break;
////            case LEVEL_W:
////                if (null == e) {
////                    Log.w(tag, msg);
////                } else {
////                    Log.w(tag, msg, e);
////                }
////                break;
////            case LEVEL_E:
////                if (null == e) {
////                    Log.e(tag, msg);
////                } else {
////                    Log.e(tag, msg, e);
////                }
////                break;
////        }
//    }
//
//    private static synchronized void writeToLogFile(
//            String time, String level, String tag, String line) {
//        if (null != (sWriter = getWriter())) {
//            try {
//                sWriter.write(
//                        String.format(Locale.CHINA, "%s  %s  %s:  %s\n", time, level, tag, line));
//            } catch (IOException e) {
////                Log.e(TAG, "failed to write log to file.", e);
//            }
//            DelayExecutor.getInstance().doIt("save log file", 3000, SAVE_RUN);
//        } else {
////            Log.e(TAG, "getWriter() returned null when write to file.");
//        }
//    }
//
//    private static FileWriter getWriter() {
////        if (null == sWriter) {
////            try {
////                boolean isFirst = (null == sLogPath);
////                if (isFirst) {
////                    sLogPath = LOG_PATH_PRE + new SimpleDateFormat("MMdd_HHmmss", Locale.CHINA)
////                            .format(new Date()) + ".txt";
////                    File file = new File(sLogPath.substring(0, sLogPath.lastIndexOf("/")));
////                    if (!file.exists() && file.mkdirs()) {
////                        Log.d(TAG, "create directory: " + file);
////                    }
////                }
////                sWriter = new FileWriter(sLogPath, true);
////                if (isFirst) {
////                    String baseInfo = getBaseInfo();
////                    sWriter.write(baseInfo);
////                    for (String line : baseInfo.split("\\n")) {
////                        Log.i(TAG, line);
////                    }
////                }
////            } catch (IOException e) {
////                Log.e(TAG, "failed to new FileWriter().", e);
////            }
////        }
//        return sWriter;
//    }
//
//    private static String getBaseInfo() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("----------------------------------------\n");
////
////        PackageManager pm = AppContext.get().getPackageManager();
////        try {
////            PackageInfo pi = pm.getPackageInfo(AppContext.get().getPackageName(), 0);
////            sb.append("Package: ").append(AppContext.get().getPackageName()).append("\n");
////            sb.append("Version Code: ").append(pi.versionCode).append("\n");
////            sb.append("Version Name: ").append(pi.versionName).append("\n");
////            sb.append("Pid: ").append(PID).append("\n");
////            sb.append("Uid: ").append(UID).append("\n");
////        } catch (PackageManager.NameNotFoundException e) {
////            sb.append("failed to get PackageInfo: ").append(e.getMessage()).append("\n");
////        }
////
////        sb.append("----------------------------------------\n");
//        return sb.toString();
//    }
//
//    private static String getStackTrace(Throwable e) {
//        StringWriter sw = null;
//        PrintWriter pw = null;
//        try {
//            sw = new StringWriter();
//            pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            pw.flush();
//            sw.flush();
//        } finally {
//            if (sw != null) {
//                try {
//                    sw.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//            if (pw != null) {
//                pw.close();
//            }
//        }
//        return sw.toString();
//    }
//}
