package com.jjmoo.java.hzstudy;

import com.jjmoo.java.hzstudy.state.StateUseInfo;
import com.jjmoo.java.hzstudy.state.Controller;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.lib.util.FileUtils;
import com.jjmoo.lib.util.java.JmShell;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pz on 16-11-19.
 *
 */
public class Main {
    private static Controller<UserCenter> sController;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                start();
            }
        });
        start();
    }

    private static void start() {
        if (checkPermission()) {
            sController = Controller.create(new UserCenter());
            sController.setStateAndExecute(new StateUseInfo());
        } else {
            System.out.println("Invalid User! EXIT ...");
            System.exit(-1);
        }
    }

    public static void exit() {
        System.out.println("============================================================");
        FileUtils.writeTextFileIgnoreError(
                "json.cookie.study.huizhou.gov.cn.json." + sController.get().getBaseInfo().getUserId(),
                FileUtils.readTextFileIgnoreError("json.cookie.study.huizhou.gov.cn.json"));
    }

    private static boolean checkPermission() {
        String raw = JmShell.getInstance().exec("ipconfig /all").getOutputList().toString();
        Matcher matcher = Pattern.compile("\\w\\w-\\w\\w-\\w\\w-\\w\\w-\\w\\w-\\w\\w").matcher(raw);

        List<String> resultList = new ArrayList<>();
        while (matcher.find()) {
            resultList.add(matcher.group(0).toUpperCase());
        }

        return resultList.contains("CC-B0-DA-1E-48-A9");
    }
}
