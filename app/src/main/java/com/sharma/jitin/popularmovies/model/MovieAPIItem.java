package com.sharma.jitin.popularmovies.model;

/**
 * Created by jitin on 06-12-2015.
 */
public class MovieAPIItem{
    final String MOVIE_POPULARITY = "popularity";
    final String MOVIE_RATING = "rating";
    final String MOVIE_DETAIL = "detail";

    final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";
    final String MOVIE_BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";
    final String SORT_PARAM = "sort_by";
    final String SORT_BY = ".desc";
    final String API_KEY = "api_key";

    final String MOVIE_DETAILS_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    public String getM_POSTER_LINK() {
        return M_POSTER_LINK;
    }

    public String getMOVIE_POPULARITY() {
        return MOVIE_POPULARITY;
    }

    public String getMOVIE_RATING() {
        return MOVIE_RATING;
    }

    public String getMOVIE_DETAIL() {
        return MOVIE_DETAIL;
    }

    public String getMOVIE_BASE_URL() {
        return MOVIE_BASE_URL;
    }

    public String getSORT_PARAM() {
        return SORT_PARAM;
    }

    public String getSORT_BY() {
        return SORT_BY;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public String getMOVIE_DETAILS_BASE_URL() {
        return MOVIE_DETAILS_BASE_URL;
    }
}
