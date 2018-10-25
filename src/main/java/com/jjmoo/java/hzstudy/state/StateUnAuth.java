package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.net.DataLoader;
import com.jjmoo.java.hzstudy.data.UserCenter;
import com.jjmoo.lib.net.FormMap;
import com.jjmoo.lib.util.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by user on 17-10-16.
 *
 */
public class StateUnAuth implements IState<UserCenter> {
    private static final String TAG = "StateUnAuth";
    private static final int FAIL_COUNT_MAX = 5;

    private static int sFailCount = 0;

    private Map<String, String> mLoginFieldMap;

    public StateUnAuth() {
        try {
            mLoginFieldMap = FormMap.parse(DataLoader.login(new HashMap<String, String>()));
        } catch (IOException e) {
            LogUtils.e(TAG, "failed to init mLoginFieldMap from DataLoader.", e);
        }
    }

    public StateUnAuth(String body) {
        mLoginFieldMap = FormMap.parse(body);
    }

    @Override
    public void execute(Controller<UserCenter> controller) {
        if (null == mLoginFieldMap) {
            if (++sFailCount > FAIL_COUNT_MAX) {
                throw new RuntimeException("FATAL ERROR: failed to init mLoginFieldMap.");
            }
            controller.setStateAndExecute(new StateUnAuth());
            return;
        }
        sFailCount = 0;

        DataLoader.logout();
        try {
            DataLoader.getCodeImg();
        } catch (IOException e) {
            LogUtils.e(TAG, "failed to get verify code.", e);
        }

        System.out.println("============================================================");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input username:  ");
        String username = scanner.nextLine().trim();
//        String username = "wqm770813";
//        String username = "hdx691123";
        System.out.print("Please input password:  ");
        String password = scanner.nextLine().trim();
//        String password = "888888";
//        System.out.print("Please input verify code:  ");
//        String code = scanner.nextLine().trim();

        UserCenter user = controller.get();
        user.setUsername(username);
        user.setPassword(password);

        mLoginFieldMap.put("ctl05$UserName", username);
        mLoginFieldMap.put("ctl05$Password", password);
        mLoginFieldMap.put("ctl05$code_op", "000000");
        mLoginFieldMap.put("ctl05$cbRemember", "on");

        String result = "";
        try {
            DataLoader.login(mLoginFieldMap);
            result = DataLoader.kick(username);
        } catch (IOException e) {
            LogUtils.e(TAG, "failed to login.", e);
        }

        while (result.contains("请输入验证码") || result.contains("验证码输入错误")) {
            try {
                DataLoader.getCodeImg().saveToFile("code.gif");
            } catch (IOException e) {
                LogUtils.e(TAG, "failed to get verify code.", e);
            }
            System.out.print("Please input verify code:  ");
            String code = scanner.nextLine().trim();
            mLoginFieldMap.put("ctl05$code_op", code);
            try {
                DataLoader.login(mLoginFieldMap);
                result = DataLoader.kick(username);
            } catch (IOException e) {
                LogUtils.e(TAG, "failed to login.", e);
                break;
            }
        }

        if (!result.contains("失败")) {
            controller.setStateAndExecute(new StateUseInfo());
            return;
        }

        if (result.contains("密码错误")) {
            System.out.println("密码错误");
        } else if (result.contains(("账号不存在"))) {
            System.out.println("账号不存在");
        } else {
            System.out.println("未知错误");
        }
        controller.setStateAndExecute(new StateUnAuth());
    }
}
