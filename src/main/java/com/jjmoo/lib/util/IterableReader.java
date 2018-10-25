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
 * |    zhuo.peng    |    2017.3.15   |      /       |          Create class                 |
 * |    zhuo.peng    |   2017.09.04   |      /       |          Modify class                 |
 *
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jjmoo.lib.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by user on 17-8-1.
 *
 */
@SuppressWarnings("unused")
public abstract class IterableReader<T> implements Iterator<T>, Iterable<T>, Closeable {
    protected OnErrorCallback mOnErrorCallback = OnErrorCallback.DEFAULT;

    public IterableReader<T> setOnErrorCallback(OnErrorCallback callback) {
        mOnErrorCallback = callback;
        return this;
    }

    public final IterableReader<T> concatWith(final IterableReader<? extends T> ir) {
        return concat(new ArrayList<IterableReader<? extends T>>() {{
            add(IterableReader.this);
            add(ir);
        }});
    }

    public final IterableReader<T> concatWith(final List<IterableReader<? extends T>> irs) {
        return concat(new ArrayList<IterableReader<? extends T>>(){{
            add(IterableReader.this);
            addAll(irs);
        }});
    }

    public final IterableReader<T> mergeWith(Comparator<T> cmp, final IterableReader<? extends T> ir) {
        return merge(cmp, new ArrayList<IterableReader<? extends T>>() {{
            add(IterableReader.this);
            add(ir);
        }});
    }

    public final IterableReader<T> mergeWith(Comparator<T> cmp, final List<IterableReader<? extends T>> irs) {
        return merge(cmp, new ArrayList<IterableReader<? extends T>>() {{
            add(IterableReader.this);
            addAll(irs);
        }});
    }

    public final <R> IterableReader<R> mapTo(Func<? super T, ? extends R> func) {
        return map(this, func);
    }

    public final <R> IterableReader<Item<R, T>> mapWith(final Func<? super T, ? extends R> func) {
        return map(this, new Func<T, Item<R, T>>() {
            @Override
            public Item<R, T> call(T t) {
                return new Item<>(func.call(t), t);
            }
        });
    }

    public final IterableReader<T> filter(Func<? super T, Boolean> func) {
        return filter(this, func);
    }

    public final void forEach(Action<? super T> action) {
        for (T t : this) {
            action.call(t);
        }
    }

    @Override
    public Iterator<T> iterator() {
        if (mIsFirstTime) {
            mIsFirstTime = false;
            return this;
        }
        throw new IllegalAccessError("never call the method twice");
    }
    private boolean mIsFirstTime = true;

    @Override
    public void remove() { /* do nothing */ }

    //******

    public static <T> IterableReader<T> concat(List<IterableReader<? extends T>> irs) {
        return doConcat(irs);
    }

    public static <T> IterableReader<T> merge(Comparator<T> cmp, List<IterableReader<? extends T>> irs) {
        return doMerge(cmp, irs);
    }

    public static <T, R> IterableReader<R> map(IterableReader<T> ir, Func<? super T, ? extends R> func) {
        return doMap(ir, func);
    }

    public static <T> IterableReader<T> filter(IterableReader<T> ir, Func<? super T, Boolean> func) {
        return doFilter(ir, func);
    }

    public static IterableReader<String> from(String filePath) {
        assert (null != filePath);
        return new Impl(new File(filePath));
    }

    public static IterableReader<String> from(final File file) {
        assert (null != file);
        return new Impl(file);
    }

    public static IterableReader<String> from(final Reader reader) {
        assert (null != reader);
        return new Impl(reader);
    }

    //******

    public static class Item<V, D> {
        public final V value;
        public final D data;
        public Item(V v, D d) {
            value = v;
            data = d;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "value=" + value +
                    ", data=" + data +
                    '}';
        }
    }

    public interface Action<T> {
        void call(T t);
    }

    public interface Func<T, R> {
        R call(T t);
    }

    public interface OnErrorCallback {
        void onError(String msg, Throwable e);
        OnErrorCallback DEFAULT = new OnErrorCallback() {
            public void onError(String msg, Throwable e) { throw new RuntimeException(e); }
        };
    }

    //******

    private static <T> IterableReader<T> doConcat(final List<IterableReader<? extends T>> irs) {
        return new IterableReader<T>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                for ( ; cursor < irs.size(); cursor++) {
                    if (irs.get(cursor).hasNext()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                return irs.get(cursor).next();
            }

            @Override
            public void close() throws IOException {
                for (IterableReader ir : irs) {
                    ir.close();
                }
            }
        };
    }

    private static <T> IterableReader<T> doMerge(final Comparator<T> cmp,
                                                 final List<IterableReader<? extends T>> irs) {
        return new IterableReader<T>() {
            @SuppressWarnings("unchecked")
            private T[] nextItems = (T[]) new Object[irs.size()];

            {
                for (int i = 0; i < nextItems.length; i++) {
                    IterableReader<? extends T> IterableReader = irs.get(i);
                    nextItems[i] = IterableReader.hasNext() ? IterableReader.next() : null;
                }
            }

            @Override
            public boolean hasNext() {
                for (T nextItem : nextItems) {
                    if (null != nextItem) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                T nextItem = null;
                int index = 0;
                for (int i = 0; i < nextItems.length; i++) {
                    if (null != nextItems[i] && (null == nextItem || cmp.compare(nextItem, nextItems[i]) > 0)) {
                        nextItem = nextItems[i];
                        index = i;
                    }
                }
                if (null != nextItem) {
                    IterableReader<? extends T> IterableReader = irs.get(index);
                    nextItems[index] = IterableReader.hasNext() ? IterableReader.next() : null;
                    return nextItem;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void close() throws IOException {
                for (IterableReader ir : irs) {
                    ir.close();
                }
            }
        };
    }

    private static <T, R> IterableReader<R> doMap(final IterableReader<T> ir,
                                                  final Func<? super T, ? extends R> func) {
        return new IterableReader<R>() {
            @Override
            public boolean hasNext() {
                return ir.hasNext();
            }

            @Override
            public R next() {
                return func.call(ir.next());
            }

            @Override
            public void close() throws IOException {
                ir.close();
            }
        };
    }

    private static <T> IterableReader<T> doFilter(final IterableReader<T> ir,
                                                  final Func<? super T, Boolean> func) {
        return new IterableReader<T>() {
            private T next;

            @Override
            public boolean hasNext() {
                if (null != next) {
                    return true;
                }
                while (ir.hasNext()) {
                    next = ir.next();
                    if (func.call(next)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                if (null != next || hasNext()) {
                    T ret = next;
                    next = null;
                    return ret;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void close() throws IOException {
                ir.close();
            }
        };
    }

    private static class Impl extends IterableReader<String> {
        private File mFile;
        private BufferedReader mBufferedReader;
        private String next;

        private Impl(File file) {
            mFile = file;
        }

        private Impl(Reader in) {
            mBufferedReader = new BufferedReader(in);
        }

        @Override
        public boolean hasNext() {
            if (null != next) {
                return true;
            }
            if (null == mBufferedReader && null != mFile) {
                try {
                    mBufferedReader = new BufferedReader(new FileReader(mFile));
                } catch (FileNotFoundException e) {
                    mOnErrorCallback.onError(e.getMessage(), e);
                }
            }
            if (null != mBufferedReader) {
                try {
                    next = mBufferedReader.readLine();
                } catch (IOException e) {
                    mOnErrorCallback.onError(e.getMessage(), e);
                }
            }
            if (null == next) {
                try {
                    close();
                } catch (IOException e) {
                    mOnErrorCallback.onError(e.getMessage(), e);
                }
                return false;
            }
            return true;
        }

        @Override
        public String next() {
            if (null != next || hasNext()) {
                String ret = next;
                next = null;
                return ret;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void close() throws IOException {
            if (null != mBufferedReader) {
                mBufferedReader.close();
            }
            mBufferedReader = null;
            mFile = null;
        }
    }
}
