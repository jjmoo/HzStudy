package com.jjmoo.lib.util.java;

import com.jjmoo.lib.util.DelayExecutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class JmLog {
    public static class Utils {
        public static String getSimpleName(Object obj) {
            return null == obj ? null : obj.getClass().getSimpleName();
        }

        public static String getBriefName(Object obj) {
            String str = String.valueOf(obj);
            return str.contains(".") ? str.substring(str.lastIndexOf(".") + 1) : str;
        }

        public static String getStackTrace(Throwable e) {
            StringWriter sw = null;
            PrintWriter pw = null;
            try {
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                sw.flush();
            } finally {
                if (sw != null) {
                    try {
                        sw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (pw != null) {
                    pw.close();
                }
            }
            return sw.toString();
        }
    }
}
