package com.sharma.jitin.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharma.jitin.popularmovies.Utils.Utility;
import com.sharma.jitin.popularmovies.adapter.MovieReviewAdapter;
import com.sharma.jitin.popularmovies.adapter.MovieTrailerAdapter;
import com.sharma.jitin.popularmovies.data.MoviesContract;
import com.sharma.jitin.popularmovies.data.MoviesContract.MovieEntry;
import com.sharma.jitin.popularmovies.model.AsyncTaskListener;
import com.sharma.jitin.popularmovies.model.MovieReviewItem;
import com.sharma.jitin.popularmovies.model.MovieTrailerItem;
import com.sharma.jitin.popularmovies.model.OnItemClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    String releaseDate;
    String runtime;
    String rating;
    String overview;
    String title;
    String posterPath;
    String id;
    String trailerUrl;

    ImageView moviePoster;
    TextView movieTitle;
    TextView movieDate;
    TextView movieRuntime;
    TextView movieRating;
    TextView movieDescription;

    Button favoriteButton;

    CardView trailerCard;
    CardView reviewCard;

    RecyclerView trailerView;
    MovieTrailerAdapter movieTrailerAdapter;

    RecyclerView movieReviewView;
    MovieReviewAdapter movieReviewAdapter;

    Bitmap bitmap;
    byte[] test;
    String movieID;

    ShareActionProvider shareActionProvider;
    Cursor cursor;

    final String LOG_TAG = this.getClass().getSimpleName();
    String[] selectionArgs;

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

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (trailerUrl!=null){
            shareActionProvider.setShareIntent(createTrailerShareIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (trailerUrl!=null){
                shareActionProvider.setShareIntent(createTrailerShareIntent());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createTrailerShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getContext().getString(R.string.youtube_link) + trailerUrl);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle!=null) {
            movieID = bundle.getString("movieID");
        }

        if(getActivity().getIntent().hasExtra("movieID")) {
            if (movieID == null && !getActivity().getIntent().getStringExtra("movieID").isEmpty()) {
                movieID = getActivity().getIntent().getStringExtra("movieID");
            }
        }

        View home = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        movieTitle = (TextView)home.findViewById(R.id.movie_title);
        movieDate = (TextView)home.findViewById(R.id.movie_date);
        movieRuntime = (TextView)home.findViewById(R.id.movie_runtime);
        movieRating = (TextView)home.findViewById(R.id.movie_rating);
        moviePoster = (ImageView)home.findViewById(R.id.movie_detail_image);
        movieDescription = (TextView)home.findViewById(R.id.movie_description);

        favoriteButton = (Button)home.findViewById(R.id.favorite);

        trailerCard = (CardView)home.findViewById(R.id.card_view_trailer);
        reviewCard = (CardView)home.findViewById(R.id.card_view_review);

        trailerView = (RecyclerView)home.findViewById(R.id.movie_trailer_list);
        movieReviewView = (RecyclerView)home.findViewById(R.id.movie_review_list);

        trailerCard.setVisibility(View.GONE);
        reviewCard.setVisibility(View.GONE);

        if(movieID!=null){
            selectionArgs = new String[]{movieID};
        }
        cursor = getContext().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                MovieEntry.COLUMN_MOVIE_ID +" = ?",
                selectionArgs,
                null
        );
        if (cursor!=null && cursor.moveToFirst()){
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.mipmap.ic_favorite_white_24dp)
                    , null, null, null);
        }
        if (cursor!=null && cursor.moveToFirst() && !Utility.isNetworkStatusAvailable(getContext())){
            String id;
            releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_DATE));
            runtime = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RUNTIME));
            rating = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING));
            overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW));
            title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
            byte[] posterPath = cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER));

            movieTitle.setText(title);
            movieDescription.setText(overview);
            try {
                movieDate.setText(Utility.convertDate(releaseDate));
            } catch (Exception e) {
                e.printStackTrace();
            }
            movieRuntime.setText(runtime + getString(R.string.minute));
            movieRating.setText(rating + getString(R.string.max_rating));
            moviePoster.setImageBitmap(Utility.convertBytesToBitmap(posterPath));
            }

        else if (Utility.isNetworkStatusAvailable(getContext()) && movieID!=null) {
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
            fetchMovieTask.execute("detail", movieID);

            //fetchMovieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "detail",getActivity().getIntent().getStringExtra("movieID"));
            //fetchMovieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "trailer",getActivity().getIntent().getStringExtra("movieID"));

            FetchMovieTask fetchMovieTask2 = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
            fetchMovieTask2.execute("trailer", movieID);

            FetchMovieTask fetchMovieTask3 = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
            fetchMovieTask3.execute("review", movieID);
        }
        else if(movieID==null){

        }
        else{
            home.setVisibility(View.GONE);
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((cursor==null) || (cursor!=null && !cursor.moveToFirst())) {
                    ContentValues movieValues = new ContentValues();
                    favoriteButton.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.mipmap.ic_favorite_white_24dp)
                            , null, null, null);

                    bitmap = ((BitmapDrawable) moviePoster.getDrawable()).getBitmap();
                    test = Utility.convertBitmapToBytes(bitmap);

                    movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_RATING, rating);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_RUNTIME, runtime);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_DATE, releaseDate);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, test);

                    Uri insertedUri = getContext().getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
                    Toast.makeText(getContext(), getContext().getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
                }
                else{
                    favoriteButton.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.mipmap.ic_favorite_outline_white_24dp)
                            , null, null, null);
                    getActivity().getContentResolver().delete(
                            MovieEntry.CONTENT_URI,
                            MovieEntry.COLUMN_MOVIE_ID +" = ?",
                            new String[]{id}
                    );
                    Toast.makeText(getContext(), getContext().getString(R.string.remove_favorites), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return home;
    }

    class FetchMovieTaskListener implements AsyncTaskListener<ArrayList<String>> {
        final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";

        @Override
        public void onTaskComplete(ArrayList<String> result) {
            String[] details = new String[0];
            if (result.size() != 0) {
                details = result.get(0).split("#");
            }
            if (details != null && details.length != 0) {
                if (details[0].equalsIgnoreCase("trailer")) {
                    trailerCard.setVisibility(View.VISIBLE);
                    final ArrayList<MovieTrailerItem> trailerItems = new ArrayList<>();

                    for (int i = 1; i < details.length; i++) {
                        trailerUrl = details[1];
                        MovieTrailerItem movieTrailerItem = new MovieTrailerItem();
                        movieTrailerItem.setTrailerId(details[i]);
                        trailerItems.add(movieTrailerItem);
                    }
                    if (trailerItems.size() != 0) {
                        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), trailerItems);
                        trailerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        trailerView.setItemAnimator(new DefaultItemAnimator());
                        trailerView.setAdapter(movieTrailerAdapter);
                        movieTrailerAdapter.setOnItemClick(new OnItemClick() {
                            @Override
                            public void onItemClicked(int position) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerItems.get(position).getTrailerId()));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException ex) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(getString(R.string.youtube_link) + trailerItems.get(position).getTrailerId()));
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
                else if (details != null && details[0].equalsIgnoreCase("review")) {
                    reviewCard.setVisibility(View.VISIBLE);
                    ArrayList<MovieReviewItem> authorNames = new ArrayList<>();
                    ArrayList<MovieReviewItem> contents = new ArrayList<>();

                    for (int i = 0; i < result.size(); i++) {
                        details = result.get(i).split("#");
                        authorNames.add(new MovieReviewItem(details[1]));
                        contents.add(new MovieReviewItem(details[2]));
                    }
                    if (authorNames.size() != 0) {
                        movieReviewAdapter = new MovieReviewAdapter(getActivity(), authorNames, contents);
                        movieReviewView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        movieReviewView.setItemAnimator(new DefaultItemAnimator());
                        movieReviewView.setAdapter(movieReviewAdapter);
                    }
                }
                else {
                    title = details[0];
                    overview = details[1];
                    releaseDate = details[2];
                    runtime = details[3];
                    rating = details[4];
                    posterPath = details[5];
                    id = details[6];

                    movieTitle.setText(details[0]);
                    movieDescription.setText(details[1]);
                    try {
                        movieDate.setText(Utility.convertDate(details[2]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    movieRuntime.setText(details[3] + getString(R.string.minute));
                    movieRating.setText(details[4] + getString(R.string.max_rating));
                    if (!details[5].contains("null")) {
                        Picasso.with(getContext())
                                .load(M_POSTER_LINK + details[5])
                                .error(R.string.image_error)
                                .placeholder(R.drawable.placeholder)
                                .into(moviePoster);
                    }
                }
            }
        }
    }
}