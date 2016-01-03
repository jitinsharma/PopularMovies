package com.sharma.jitin.popularmovies.model;

/**
 * Created by jitin on 03-01-2016.
 */
public class MovieReviewItem {
    String authorName;
    String content;

    public MovieReviewItem(String authorName, String content) {
        this.authorName = authorName;
        this.content = content;
    }

    public MovieReviewItem(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
