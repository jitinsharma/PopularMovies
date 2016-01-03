package com.sharma.jitin.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharma.jitin.popularmovies.R;
import com.sharma.jitin.popularmovies.model.MovieReviewItem;

import java.util.List;

/**
 * Created by jitin on 02-01-2016.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder>{
    private List<MovieReviewItem> authorNames;
    private List<MovieReviewItem> contents;
    Context context;


    public MovieReviewAdapter(Context context, List<MovieReviewItem> authorNames, List<MovieReviewItem> contents) {
        this.context = context;
        this.authorNames = authorNames;
        this.contents = contents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_review, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieReviewItem authorName = authorNames.get(position);
        MovieReviewItem content = contents.get(position);
        holder.authorName.setText(authorName.getAuthorName());
        holder.content.setText(content.getAuthorName());
    }

    @Override
    public int getItemCount() {
        return authorNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView authorName;
        TextView content;
        public ViewHolder(View itemView) {
            super(itemView);
            authorName = (TextView)itemView.findViewById(R.id.author_name);
            content = (TextView)itemView.findViewById(R.id.content);
        }
    }
}
