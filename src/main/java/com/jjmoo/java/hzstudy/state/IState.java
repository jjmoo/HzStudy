package com.jjmoo.java.hzstudy.state;

/**
 * Created by user on 17-10-16.
 *
 */
public interface IState<T> {
    void execute(Controller<T> controller);
}
