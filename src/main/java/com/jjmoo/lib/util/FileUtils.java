/*
 * MIT License
 *
 * Copyright (c) 2017 Zohn
 * email <pengzhuo1993@foxmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.jjmoo.lib.util;

import java.io.*;

/**
 * Descriptions
 * <p><br>
 *
 * @author Zohn
 */
@SuppressWarnings("unused")
public class FileUtils {
    private static final String TAG = "FileUtils";

    public static void closeIgnoreException(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                LogUtils.w(TAG, "failed to close it: " + closeable, e);
            }
        } else {
            LogUtils.d(TAG, "Closeable is null.");
        }
    }

    public static String readTextFileIgnoreError(String path) {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[80];
        FileReader reader = null;
        try {
            reader = new FileReader(path);
            int len;
            while (-1 != (len = reader.read(buf))) {
                sb.append(buf, 0, len);
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "[" + e.getMessage() + "] failed to read text file: " + path);
        } finally {
            closeIgnoreException(reader);
        }
        return sb.toString();
    }

    public static void writeTextFileIgnoreError(String path, String context) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(path);
            fw.write(context);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            LogUtils.e(TAG, "[" + e.getMessage() + "] failed to write text file: " + path);
        } finally {
            closeIgnoreException(fw);
        }
    }

    public static void writeBinaryFileIgnoreError(String path, byte[] bytes) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            LogUtils.e(TAG, "[" + e.getMessage() + "] failed to write binary file: " + path);
        } finally {
            closeIgnoreException(fos);
        }
    }
}
