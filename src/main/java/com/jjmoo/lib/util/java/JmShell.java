/*
 * Copyright Statement:
 *         This software/firmware and related documentation ("TCT Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to TCT Inc. and/or its licensors.
 * Without the prior written permission of TCT inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of TCT Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * TCT Inc. (C) 2016. All rights reserved.
 *
 * Class Name: ShellProxy
 *
 * Description: To execute command
 *
 * Modify history:
 * |--------Owner------|------Time------|-----Bug ID-----|---------Bug Description-----------------------------|
 * ------------------------------------------------------------------------------------------------------------
 * |     zhuo.peng     |   2016.10.21   |       /        |              Create class                           |
 *
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 *  Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 *  Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 *  Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 *  Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jjmoo.lib.util.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuo.peng on 16/10/21.
 *
 */
@SuppressWarnings("unused")
public class JmShell {
    private static final String TAG = "JmShell";

    private Map<String, ShellExec> mExecs;

    public static JmShell getInstance() {
        return SingletonHolder.obj;
    }
    private static class SingletonHolder {
        private static JmShell obj = new JmShell();
    }

    private JmShell() {
        mExecs = new HashMap<>();
    }

    public Handle exec(String command) {
        return exec(command, false);
    }

    public synchronized Handle exec(String command, boolean execBack) {
        String relCommand = getRelCommand(command, execBack);
        ShellExec.Filter filter = getFilter(command, execBack);

        //if there is a instance execute the command already, add the filter to it
        //else create a instance to execute the command and add the filter to it too
        if (mExecs.containsKey(relCommand)) {
            mExecs.get(relCommand).addFilter(filter);
        } else {
            ShellExec se = new ShellExecLocal(relCommand, execBack);
            mExecs.put(relCommand, se);
            se.addFilter(filter);
        }

        return filter;
    }

    //extract the real command to be executed
    private String getRelCommand(String command, boolean execBack) {
        return execBack ? command.split("\\|")[0].trim() : command;
    }

    //to generate a filter which has implemented JmShell.Handle
    //and know which line of result need to be get
    private ShellExec.Filter getFilter(String command, boolean execBack) {
        if (!execBack || 1 >= command.split("\\|").length) {
            return ShellExec.Filter.DEFAULT;
        }
        final String[] strNeeds = getStrNeed(command);
        return new ShellExec.Filter() {
            @Override
            protected String doFilter(String line) {
                for (String str : strNeeds) {
                    if (line.contains(str)) {
                        return line;
                    }
                }
                return null;
            }
        };
    }

    private String[] getStrNeed(String command) {
        final String REGEX = "^.*?\\|\\s*(grep|egrep).*?\\s+(\"(.*)\"|\\S+)\\s*$";
//        Assume: REGEX = ^A1|\s*(G1)A2\s+(G2)\s*$
//        ^ and $          # to match the beginning and the end
//        A1 = .*?         # to ignore the content before "|", it should be the real command
//        G1 = grep|egrep  # to match grep or egrep
//        G2 = "(.*)"|\S+  # to match the last argument to grep or egrep, maybe inside " " or not
//        G2 = "(G3)"|\S+
//        G3 = .*          # to match the last argument to grep or egrep if it's inside " "
//        A2 = .*?         # to ignore the part divided by space which follow the grep or egrep but not the last
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            throw new IllegalArgumentException("cannot resolve the command: " + command);
        } else {
            String grepContent;
            if (null != matcher.group(3)) {    // That group(3) is not null means G3 is match in G2
                grepContent = matcher.group(3).replaceAll("\\\\\"", "\"");  //to replace \" with "
            } else {
                grepContent = matcher.group(2).replaceAll("\\\\\"", "\"");
            }

            if ("grep".equals(matcher.group(1))) {
                return new String[]{grepContent};
            } else {
                return grepContent.split("\\s*\\|\\s*");
            }
        }
    }

    public interface Handle {
        List<String> getOutputList();
        void terminate();
    }

    private static abstract class ShellExec {
        private List<Filter> mFilters = new ArrayList<>();

        private final Object[] LOCK = new Object[0];
        private final String COMMAND;
        private final boolean EXEC_BACK;

        public ShellExec(String command, boolean execBack) {
            COMMAND = command;
            EXEC_BACK = execBack;
        }

        //standard output, called by executor when get line of result
        protected void stdOutput(String line) {
            synchronized (LOCK) {
                for (Filter filter : mFilters) {
                    filter.filter(line);
                }
            }
        }

        //error output, called by executor when error occurs
        protected void errOutput(String info) {
            stdOutput(info);
        }

        //add filter, and execute the command when the first time
        public void addFilter(Filter filter) {
            synchronized (LOCK) {
                filter.setExec(this);
                mFilters.add(filter);
                if (1 == mFilters.size()) {
                    exec();
                }
            }
        }

        //remove filter, and stop executing the command when no filter
        private void removeFilter(Filter filter) {
            synchronized (LOCK) {
                mFilters.remove(filter);
                if (mFilters.isEmpty()) {
                    doTerminate();
                }
            }
        }

        private void exec() {
            if (!EXEC_BACK) {
                doExecCommand(new String[]{"cmd", "/c", COMMAND});
                mFilters.clear();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doExecCommand(new String[]{"cmd", "/c", COMMAND});
                        mFilters.clear();
                    }
                }).start();
            }
        }

        //let the subclass to define how to execute command
        protected abstract void doExecCommand(String[] command);

        //let the subclass to define how to stop executing command
        protected abstract void doTerminate();


        //if there is anything to output, ShellExec will call Filter.filter()
        //which will call doFilter() to decide which information should be add to
        //the output list.
        //by implementing JmShell.Handle, this class could be returned to user
        //to get command executor's outputs and to terminate the executor.
        public static abstract class Filter implements Handle {
            private List<String> mAllResult;
            private List<String> mCurResult;
            private ShellExec mShellExec;

            protected Filter() {
                mAllResult = new ArrayList<>();
                mCurResult = new ArrayList<>();
            }

            private void setExec(ShellExec se) {
                mShellExec = se;
            }

            @Override
            public List<String> getOutputList() {
                if (!mCurResult.isEmpty()) {
                    mCurResult.clear();
                }
                int size = mAllResult.size();
                for (int i = 0; i < size; i++) {
                    mCurResult.add(mAllResult.get(0));
                    mAllResult.remove(0);
                }
                return mCurResult;
            }

            @Override
            public void terminate() {
                mShellExec.removeFilter(this);
            }

            private void filter(String line) {
                String lineFiltered = doFilter(line);
                if (lineFiltered != null) {
                    mAllResult.add(lineFiltered);
                }
            }

            protected abstract String doFilter(String line);

            public static Filter DEFAULT =  new Filter() {
                @Override
                protected String doFilter(String line) {
                    return line;
                }
            };
        }
    }

    private class ShellExecLocal extends ShellExec {
        private boolean mNeedToStop;

        public ShellExecLocal(String command, boolean execBack) {
            super(command, execBack);
        }

        @Override
        protected void doExecCommand(String[] command) {
            mNeedToStop =false;
            Process process = null;
            BufferedReader brStd = null;
            BufferedReader brErr = null;
            try {
                process = Runtime.getRuntime().exec(command);

                brStd = new BufferedReader(new InputStreamReader(
                        process.getInputStream(), Charset.defaultCharset()));

                for (String line = brStd.readLine(); null != line && !mNeedToStop; line = brStd.readLine()) {
                    stdOutput(line);
                }

                if (!mNeedToStop) {
                    int exitValue = process.waitFor();
                    if (0 != exitValue) {
                        brErr = new BufferedReader(new InputStreamReader(
                                process.getErrorStream(), Charset.defaultCharset()));
                        for (String line = brErr.readLine(); null != line; line = brErr.readLine()) {
                            errOutput(line);
                        }
                    }
                }
            } catch (IOException e) {
                String stackTrace = JmLog.Utils.getStackTrace(e);
                errOutput(stackTrace);
            } catch (InterruptedException e) {
                // ignore
            } finally {
                if (null != process) {
                    process.destroy();
                }
                JmUtils.close(brStd);
                JmUtils.close(brErr);
            }
        }

        @Override
        protected void doTerminate() {
            mNeedToStop = true;
        }
    }
}
