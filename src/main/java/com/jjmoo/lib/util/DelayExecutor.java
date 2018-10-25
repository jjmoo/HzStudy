/*
 * Copyright Statement:
 *         This software/firmware and related documentation ("TCT Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to TCT Inc. and/or its licensors.
 * Without the prior written permission of TCT inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of TCT Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * TCT Inc. (C) 2017. All rights reserved.
 *
 * File Name:
 *
 * Description:
 *
 * Modify history:
 * |      Owner      |      Time      |    Bug ID    |        Bug Description                |
 * |    zhuo.peng    |    2017.6.1   |      /       |          Create class                 |
 *
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jjmoo.lib.util;

//import android.os.Handler;
//import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Created by pz on 16/5/25.
 *
 */
@SuppressWarnings("unused")
public class DelayExecutor {
//    private Handler mUiHandler;
    private ExecutorService mExecutor;
    private Map<String, Dog> mDogs;

    private static DelayExecutor sDelayExecutor;

    private DelayExecutor() {
//        mUiHandler = new Handler(Looper.getMainLooper());
        mExecutor = Executors.newCachedThreadPool();
        mDogs = new HashMap<>();
    }

    public static DelayExecutor getInstance() {
        if (null == sDelayExecutor) {
            synchronized (DelayExecutor.class) {
                if (null == sDelayExecutor) {
                    sDelayExecutor = new DelayExecutor();
                }
            }
        }
        return sDelayExecutor;
    }

    public void doIt(Runnable runnable) {
        doIt(null, 0, false, runnable);
    }

    public void doIt(boolean isUi, Runnable runnable) {
        doIt(null, 0, isUi, runnable);
    }

    public void doIt(int delay, Runnable runnable) {
        doIt(null, delay, false, runnable);
    }

    public void doIt(int delay, boolean isUi, Runnable runnable) {
        doIt(null, delay, false, runnable);
    }

    public void doIt(String key, int delay, Runnable runnable) {
        doIt(key, delay, false, runnable);
    }

    public synchronized void doIt(
            String key, int delay, final boolean isUi, final Runnable runnable) {
        if (null == key) {
            doItWithoutKey(delay, isUi, runnable);
        } else {
            Dog dog;
            if (mDogs.containsKey(key)) {
                // ignore the two following args
                dog = mDogs.get(key);
            } else {
                dog = new Dog(0, new Runnable() {
                    @Override
                    public void run() {
                        doItWithoutKey(0, isUi, runnable);
                    }
                });
                mDogs.put(key, dog);
            }
            dog.feed(delay);
        }
    }

    public void cancel(String key) {
        Dog dog = mDogs.get(key);
        if (null != dog) {
            dog.kill();
            mDogs.remove(key);
        }
    }

    private void doItWithoutKey(int delay, final boolean isUi, final Runnable runnable) {
        if (isUi) {
            uiDo(delay, runnable);
        } else {
            nUiDo(delay, runnable);
        }
    }

    private void uiDo(int delay, Runnable runnable) {
//        if (0 >= delay) {
//            mUiHandler.post(runnable);
//        } else {
//            mUiHandler.postDelayed(runnable, delay);
//        }
    }

    private void nUiDo(int delay, Runnable runnable) {
        if (0 >= delay) {
            mExecutor.execute(runnable);
        } else {
            new Dog(delay, runnable).feed();
        }
    }

    private class Dog {
        private int mTime;
        private boolean mIsAlive;
        private boolean mIsKilled;
        private Runnable mRunnable;

        private final Object[] LOCK = new Object[0];

        public Dog(int time, Runnable runnable) {
            mIsAlive = false;
            mIsKilled = false;
            mTime = time;
            mRunnable = runnable;
        }

        public void feed() {
            feed(mTime);
        }

        public synchronized void feed(final int ms) {
            if (!mIsAlive) {
                mIsAlive = true;
                mIsKilled = false;
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long timeBeforeWait, timeAfterWait;
                        synchronized (LOCK) {
                            while (true) {
                                try {
                                    timeBeforeWait = System.currentTimeMillis();
                                    LOCK.wait(ms);
                                    timeAfterWait = System.currentTimeMillis();
                                    if (ms > timeAfterWait - timeBeforeWait) {
                                        if (!mIsKilled) {
                                            continue;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    //
                                }
                                break;
                            }
                        }

                        if (!mIsKilled) {
                            mRunnable.run();
                        }
                        mIsAlive = false;
                    }
                });
            } else {
                synchronized (LOCK) {
                    LOCK.notifyAll();
                }
            }
        }

        public void kill() {
            synchronized (LOCK) {
                mIsKilled = true;
                LOCK.notifyAll();
            }
        }
    }
}
