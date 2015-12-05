package com.sharma.jitin.popularmovies.model;

/**
 * Created by jitin on 29-11-2015.
 */
public class MovieItem {
    String posterPath;
    String movieId;

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public MovieItem(String posterPath){
        this.posterPath = posterPath;
    }
}
