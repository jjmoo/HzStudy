package com.jjmoo.lib.net;

/**
 * Created by pz on 17/10/11.
 *
 */

public interface ICookieControl {
    void save(String key, String cookie);
    boolean isUserChanged();
    String load(String key);
}
