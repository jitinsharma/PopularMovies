package com.sharma.jitin.popularmovies;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharma.jitin.popularmovies.adapter.MovieReviewAdapter;
import com.sharma.jitin.popularmovies.adapter.MovieTrailerAdapter;
import com.sharma.jitin.popularmovies.model.AsyncTaskListener;
import com.sharma.jitin.popularmovies.model.MovieReviewItem;
import com.sharma.jitin.popularmovies.model.MovieTrailerItem;
import com.sharma.jitin.popularmovies.model.OnItemClick;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    ImageView moviePoster;
    TextView movieTitle;
    TextView movieDate;
    TextView movieRuntime;
    TextView movieRating;
    TextView movieDescription;

    RecyclerView trailerView;
    MovieTrailerAdapter movieTrailerAdapter;

    RecyclerView movieReviewView;
    MovieReviewAdapter movieReviewAdapter;

    ProgressDialog progressDialog;
    final String LOG_TAG = this.getClass().getSimpleName();
    Parcelable state;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        movieTitle = (TextView)home.findViewById(R.id.movie_title);
        movieDate = (TextView)home.findViewById(R.id.movie_date);
        movieRuntime = (TextView)home.findViewById(R.id.movie_runtime);
        movieRating = (TextView)home.findViewById(R.id.movie_rating);
        moviePoster = (ImageView)home.findViewById(R.id.movie_detail_image);
        movieDescription = (TextView)home.findViewById(R.id.movie_description);

        trailerView = (RecyclerView)home.findViewById(R.id.movie_trailer_list);
        movieReviewView = (RecyclerView)home.findViewById(R.id.movie_review_list);

        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
        fetchMovieTask.execute("detail",getActivity().getIntent().getStringExtra("movieID"));

        //fetchMovieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "detail",getActivity().getIntent().getStringExtra("movieID"));
        //fetchMovieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "trailer",getActivity().getIntent().getStringExtra("movieID"));

        FetchMovieTask fetchMovieTask2 = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
        fetchMovieTask2.execute("trailer",getActivity().getIntent().getStringExtra("movieID"));

        FetchMovieTask fetchMovieTask3 = new FetchMovieTask(getContext(), new FetchMovieTaskListener());
        fetchMovieTask3.execute("review",getActivity().getIntent().getStringExtra("movieID"));
        return home;
    }

    class FetchMovieTaskListener implements AsyncTaskListener<ArrayList<String>> {
        final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";

        @Override
        public void onTaskComplete(ArrayList<String> result) {
            String[] details;
            details = result.get(0).split("#");
            if(details[0].equalsIgnoreCase("trailer")){
                final ArrayList<MovieTrailerItem> trailerItems = new ArrayList<>();

                for(int i=1; i<details.length; i++){
                    MovieTrailerItem movieTrailerItem = new MovieTrailerItem();
                    movieTrailerItem.setTrailerId(details[i]);
                    trailerItems.add(movieTrailerItem);
                }
                movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), trailerItems);
                trailerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                trailerView.setItemAnimator(new DefaultItemAnimator());
                trailerView.setAdapter(movieTrailerAdapter);
                movieTrailerAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void onItemClicked(int position) {
                        try{
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerItems.get(position).getTrailerId()));
                            startActivity(intent);
                        }catch (ActivityNotFoundException ex){
                            Intent intent=new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(getString(R.string.youtube_link) + trailerItems.get(position).getTrailerId()));
                            startActivity(intent);
                        }
                    }
                });
            }
            else if(details[0].equalsIgnoreCase("review")){
                ArrayList<MovieReviewItem> authorNames = new ArrayList<>();
                ArrayList<MovieReviewItem> contents = new ArrayList<>();

                for(int i=0; i<result.size(); i++){
                    details = result.get(i).split("#");
                    authorNames.add(new MovieReviewItem(details[1]));
                    contents.add(new MovieReviewItem(details[2]));
                }
                movieReviewAdapter = new MovieReviewAdapter(getActivity(), authorNames, contents);
                movieReviewView.setLayoutManager(new LinearLayoutManager(getActivity()));
                movieReviewView.setItemAnimator(new DefaultItemAnimator());
                movieReviewView.setAdapter(movieReviewAdapter);
            }
            else {
                movieTitle.setText(details[0]);
                movieDescription.setText(details[1]);
                try {
                    movieDate.setText(convertDate(details[2]));
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

    /*class FetchMovieDetails extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.loading_movie_details), getString(R.string.hang_on));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            movieTitle.setText(title);
            try {
                movieDate.setText(convertDate(releaseDate));
            } catch (Exception e) {
                e.printStackTrace();
            }

            runtime = runtime + getString(R.string.minute);
            rating = rating + getString(R.string.max_rating);

            movieRuntime.setText(runtime);
            movieRating.setText(rating);
            movieDescription.setText(overview);
            Picasso.with(getContext())
                    .load(M_POSTER_LINK + posterPath)
                    .error(R.string.image_error)
                    .placeholder(R.drawable.placeholder)
                    .into(moviePoster);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJson = null;
            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY,getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJson = buffer.toString();

            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
                try {
                    return getMovieDetails(movieJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
        }
        public Void getMovieDetails(String movieJson)throws JSONException {

            final String M_OVERVIEW = "overview";
            final String M_TITLE = "original_title";
            final String M_RELEASE_DATE = "release_date";
            final String M_RUNTIME = "runtime";
            final String M_RATING = "vote_average";
            final String M_POSTER_PATH = "poster_path";

            JSONObject movieDetailJson = new JSONObject(movieJson);
            title = movieDetailJson.getString(M_TITLE);
            releaseDate = movieDetailJson.getString(M_RELEASE_DATE);
            runtime = movieDetailJson.getString(M_RUNTIME);
            rating = movieDetailJson.getString(M_RATING);
            overview = movieDetailJson.getString(M_OVERVIEW);
            posterPath = movieDetailJson.getString(M_POSTER_PATH);
            return null;
        }
    }*/

    public String convertDate(String value)throws Exception{
        String convertedDate;
        Date date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        SimpleDateFormat sdfFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        date = formatter.parse(value);
        convertedDate = sdfFormat.format(date);
        return convertedDate;
    }
}
