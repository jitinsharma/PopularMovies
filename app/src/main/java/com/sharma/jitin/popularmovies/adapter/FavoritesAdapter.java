package com.sharma.jitin.popularmovies.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sharma.jitin.popularmovies.R;
import com.sharma.jitin.popularmovies.model.OnItemClick;

import java.util.List;

/**
 * Created by jitin on 30-11-2015.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>{
    List<Bitmap> bitmaps;
    OnItemClick onItemClick;
    private LayoutInflater inflater;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public FavoritesAdapter(Context context, List<Bitmap> bitmaps) {
        inflater = LayoutInflater.from(context);
        this.bitmaps = bitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_movie, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder myViewHolder, int i) {
        myViewHolder.articleImage.setImageBitmap(bitmaps.get(i));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
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
