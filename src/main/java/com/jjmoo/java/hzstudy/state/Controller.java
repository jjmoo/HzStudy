package com.jjmoo.java.hzstudy.state;

import com.jjmoo.java.hzstudy.util.Utils;
import com.jjmoo.lib.util.LogUtils;

/**
 * Created by user on 17-10-16.
 *
 */
public class Controller<T> {
    private static final String TAG = "Controller";

    private IState<T> mState;
    private T mCore;

    private Controller(T core) {
        mCore = core;
    }

    public static <T> Controller<T> create(T core) {
        return new Controller<>(core);
    }

    public T get() {
        return mCore;
    }

    public void setState(IState<T> state) {
        mState = state;
        if (Utils.DEBUG) {
            LogUtils.d(TAG, "setState(): state-->" + state);
        }
    }

    public void execute() {
        mState.execute(this);
    }

    public void setStateAndExecute(IState<T> state) {
        setState(state);
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute();
            }
        }).start();
    }
}
