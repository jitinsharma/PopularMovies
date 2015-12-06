package com.sharma.jitin.popularmovies;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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

    ProgressDialog progressDialog;
    final String LOG_TAG = this.getClass().getSimpleName();

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

        FetchMovieDetails fetchMovieDetails = new FetchMovieDetails();
        fetchMovieDetails.execute(getActivity().getIntent().getStringExtra("movieID"));
        return home;
    }

    class FetchMovieDetails extends AsyncTask<String,Void,Void> {
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
    }

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
