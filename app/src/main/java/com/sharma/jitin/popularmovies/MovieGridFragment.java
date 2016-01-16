package com.sharma.jitin.popularmovies;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sharma.jitin.popularmovies.Utils.Utility;
import com.sharma.jitin.popularmovies.adapter.FavoritesAdapter;
import com.sharma.jitin.popularmovies.adapter.MovieAdapter;
import com.sharma.jitin.popularmovies.data.MoviesContract;
import com.sharma.jitin.popularmovies.model.AsyncTaskListener;
import com.sharma.jitin.popularmovies.model.MovieItem;
import com.sharma.jitin.popularmovies.model.OnItemClick;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment{
    View imageView;
    RecyclerView movieView;
    MovieAdapter movieAdapter;
    FavoritesAdapter favoritesAdapter;

    ArrayList<MovieItem> posterPaths = new ArrayList<>();
    ArrayList<String> movieIds = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<MovieItem> savedBitmaps = new ArrayList<>();

    final String M_POPULARITY = "popularity";
    final String M_RATING = "rating";
    final String M_FAVORITES = "favorites";
    final String LOG_TAG = this.getClass().getSimpleName();

    String sortBy = M_POPULARITY;

    public static final int MOVIE_POSTER = 0;

    public static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RATING,
            MoviesContract.MovieEntry.COLUMN_MOVIE_DATE
    };

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(String id);
    }

    public MovieGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            posterPaths = (ArrayList<MovieItem>)savedInstanceState.get("key");
            movieIds = (ArrayList<String>)savedInstanceState.get("id");
            savedBitmaps = (ArrayList<MovieItem>)savedInstanceState.get("favorite");
            sortBy = savedInstanceState.getString("sort");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_movie_grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(),new FetchMovieTaskListener());

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popularity) {
            if(Utility.isNetworkStatusAvailable(getContext())) {
                sortBy = M_POPULARITY;
                fetchMovieTask.execute("grid", M_POPULARITY);
            }
            else{
                Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
        if(id == R.id.action_sort_rating){
            if(Utility.isNetworkStatusAvailable(getContext())) {
                sortBy = M_RATING;
                fetchMovieTask.execute("grid", M_RATING);
            }
            else{
                Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.action_favorite){
            sortBy = M_FAVORITES;
            movieIds.clear();
            bitmaps.clear();
            Cursor cursor = getContext().getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int posterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER);
                    int idIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);

                    movieIds.add(cursor.getString(idIndex));
                    byte[] posterBytes = cursor.getBlob(posterIndex);

                    bitmaps.add(Utility.convertBytesToBitmap(posterBytes));
                    savedBitmaps.add(new MovieItem(Utility.convertBytesToBitmap(posterBytes)));
                } while (cursor.moveToNext());
                cursor.close();
            }
            favoritesAdapter = new FavoritesAdapter(getActivity(), bitmaps);
            movieView.setAdapter(favoritesAdapter);
            favoritesAdapter.setOnItemClick(new OnItemClick() {
                @Override
                public void onItemClicked(int position) {
                    String id = movieIds.get(position);
                    ((Callback)getActivity()).onItemSelected(id);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_main, container, false);

        imageView = home.findViewById(R.id.movie_grid_image);
        movieView = (RecyclerView)home.findViewById(R.id.movie_grid);
        movieView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        movieView.setItemAnimator(new DefaultItemAnimator());
        return home;
    }

    @Override
    public void onStart(){
        super.onStart();
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(),new FetchMovieTaskListener());
        if(Utility.isNetworkStatusAvailable(getContext())) {
            if(posterPaths.size()==0 && savedBitmaps.size()==0) {
                fetchMovieTask.execute("grid", M_POPULARITY);
            }
            else if(sortBy.equalsIgnoreCase(M_FAVORITES) && savedBitmaps.size()!=0){
                movieIds.clear();
                bitmaps.clear();
                getFavoriteData();
                favoritesAdapter = new FavoritesAdapter(getActivity(), bitmaps);
                movieView.setAdapter(favoritesAdapter);
                favoritesAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void onItemClicked(int position) {
                        String id = movieIds.get(position);
                        ((Callback)getActivity()).onItemSelected(id);
                    }
                });
            }
            else{
                movieAdapter = new MovieAdapter(getActivity(),posterPaths);
                movieView.setAdapter(movieAdapter);
                movieAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void onItemClicked(int position) {
                        String id = movieIds.get(position);
                        ((Callback)getActivity()).onItemSelected(id);
                    }
                });
            }
        }
        else if(sortBy.equalsIgnoreCase(M_FAVORITES) && savedBitmaps.size()!=0){
            movieIds.clear();
            bitmaps.clear();
            getFavoriteData();
            favoritesAdapter = new FavoritesAdapter(getActivity(), bitmaps);
            movieView.setAdapter(favoritesAdapter);
            favoritesAdapter.setOnItemClick(new OnItemClick() {
                @Override
                public void onItemClicked(int position) {
                    String id = movieIds.get(position);
                    ((Callback)getActivity()).onItemSelected(id);
                }
            });
        }
        else{
            movieIds.clear();
            bitmaps.clear();
            getFavoriteData();
            favoritesAdapter = new FavoritesAdapter(getActivity(), bitmaps);
            movieView.setAdapter(favoritesAdapter);
            favoritesAdapter.setOnItemClick(new OnItemClick() {
                @Override
                public void onItemClicked(int position) {
                    String id = movieIds.get(position);
                    ((Callback)getActivity()).onItemSelected(id);
                }
            });
            Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", posterPaths);
        savedInstanceState.putStringArrayList("id", movieIds);
        savedInstanceState.putParcelableArrayList("favorite", savedBitmaps);
        savedInstanceState.putString("sort", sortBy);
        super.onSaveInstanceState(savedInstanceState);
    }

    class FetchMovieTaskListener implements AsyncTaskListener<ArrayList<String>>{
        final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";
        @Override
        public void onTaskComplete(ArrayList<String> result) {
            String[] details;
            movieIds.clear();
            posterPaths.clear();
            for(int i = 0; i<result.size(); i++) {
                details = result.get(i).split(",");
                movieIds.add(details[0]);
                posterPaths.add(new MovieItem(M_POSTER_LINK + details[1]));
            }
                movieAdapter = new MovieAdapter(getActivity(),posterPaths);
                movieView.setAdapter(movieAdapter);
                movieAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void onItemClicked(int position) {
                        String id = movieIds.get(position);
                        ((Callback)getActivity()).onItemSelected(id);
                    }
                });
        }
    }

    public void getFavoriteData(){
        Cursor cursor = getContext().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int posterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER);
                int idIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);

                movieIds.add(cursor.getString(idIndex));
                byte[] posterBytes = cursor.getBlob(posterIndex);

                bitmaps.add(Utility.convertBytesToBitmap(posterBytes));
                savedBitmaps.add(new MovieItem(Utility.convertBytesToBitmap(posterBytes)));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
