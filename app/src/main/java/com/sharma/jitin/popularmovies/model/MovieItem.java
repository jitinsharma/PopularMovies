package com.sharma.jitin.popularmovies.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jitin on 29-11-2015.
 */
public class MovieItem implements Parcelable {
    String posterPath;
    String movieId;
    byte[] favoritePoster;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap bitmap;

    public byte[] getFavoritePoster() {
        return favoritePoster;
    }

    public void setFavoritePoster(byte[] favoritePoster) {
        this.favoritePoster = favoritePoster;
    }

    protected MovieItem(Parcel in) {
        posterPath = in.readString();
        movieId = in.readString();
        //favoritePoster = in.readByteArray(getFavoritePoster());
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

    public MovieItem(byte[] favoritePoster) {this.favoritePoster = favoritePoster;}

    public MovieItem(Bitmap bitmap) {this.bitmap = bitmap;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeString(movieId);
        parcel.writeByteArray(favoritePoster);
    }
}
