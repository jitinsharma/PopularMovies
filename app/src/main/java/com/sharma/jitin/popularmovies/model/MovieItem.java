package com.sharma.jitin.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 29-11-2015.
 */
public class MovieItem implements Parcelable {
    String posterPath;
    String movieId;

    protected MovieItem(Parcel in) {
        posterPath = in.readString();
        movieId = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeString(movieId);
    }
}
