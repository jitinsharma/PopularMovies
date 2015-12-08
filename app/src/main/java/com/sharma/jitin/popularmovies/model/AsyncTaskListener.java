package com.sharma.jitin.popularmovies.model;

/**
 * Created by jitin on 06-12-2015.
 */
public interface AsyncTaskListener<T> {
     void onTaskComplete(T result);
}
