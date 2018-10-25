package com.jjmoo.lib.util.java;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class JmUtils {

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                //
            }
        }
    }

}
