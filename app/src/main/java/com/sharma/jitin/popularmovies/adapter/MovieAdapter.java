package com.sharma.jitin.popularmovies.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sharma.jitin.popularmovies.R;
import com.sharma.jitin.popularmovies.model.MovieItem;
import com.sharma.jitin.popularmovies.model.OnItemClick;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jitin on 30-11-2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{
    List<MovieItem> movieItems;
    OnItemClick onItemClick;
    private LayoutInflater inflater;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public MovieAdapter(Context context, List<MovieItem> movieItems) {
        inflater = LayoutInflater.from(context);
        this.movieItems = movieItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_movie, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder myViewHolder, int i) {
        MovieItem current = movieItems.get(i);
        Uri uri = Uri.parse(current.getPosterPath());
        Context context = myViewHolder.articleImage.getContext();
        Picasso.with(context).load(uri)
                .into(myViewHolder.articleImage);
    }

    @Override
    public int getItemCount() {
        return movieItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView articleImage;

        public ViewHolder(View itemView) {
            super(itemView);
            articleImage = (ImageView) itemView.findViewById(R.id.movie_grid_image);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onItemClick != null){
                onItemClick.onItemClicked(getAdapterPosition());
            }
        }
    }
}
