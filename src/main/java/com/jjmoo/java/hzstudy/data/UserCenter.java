package com.jjmoo.java.hzstudy.data;

import com.jjmoo.java.hzstudy.entity.UserInfo;

/**
 * Created by user on 17-10-16.
 *
 */
public class UserCenter {
    private String username;
    private String password;
    private UserInfo userInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBaseInfo(UserInfo info) {
        userInfo = info;
    }

    public UserInfo getBaseInfo() {
        return userInfo;
    }
}
