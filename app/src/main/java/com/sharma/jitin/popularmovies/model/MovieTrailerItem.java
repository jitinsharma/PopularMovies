package com.sharma.jitin.popularmovies.model;

/**
 * Created by jitin on 02-01-2016.
 */
public class MovieTrailerItem {
    String trailerId;

    public MovieTrailerItem(String trailerId) {
        this.trailerId = trailerId;
    }

    public MovieTrailerItem(){

    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }
}
