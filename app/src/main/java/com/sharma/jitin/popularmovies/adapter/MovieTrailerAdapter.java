package com.sharma.jitin.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharma.jitin.popularmovies.R;
import com.sharma.jitin.popularmovies.model.MovieTrailerItem;
import com.sharma.jitin.popularmovies.model.OnItemClick;

import java.util.List;

/**
 * Created by jitin on 02-01-2016.
 */
public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder>{
    //private LayoutInflater inflater;
    private List<MovieTrailerItem> movieTrailerItems;
    OnItemClick onItemClick;
    Context context;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public MovieTrailerAdapter(Context context, List<MovieTrailerItem> movieTrailerItems) {
        //inflater = LayoutInflater.from(context);
        this.context = context;
        this.movieTrailerItems = movieTrailerItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_trailer, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieTrailerItem movieTrailerItem = movieTrailerItems.get(position);
        holder.trailerText.setText(context.getString(R.string.youtube_link) + movieTrailerItem.getTrailerId());
    }
    
    @Override
    public int getItemCount() {
        return movieTrailerItems.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView trailerText;
        public ViewHolder(View itemView) {
            super(itemView);
            trailerText = (TextView)itemView.findViewById(R.id.trailer);
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
