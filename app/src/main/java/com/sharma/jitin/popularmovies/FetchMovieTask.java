package com.sharma.jitin.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sharma.jitin.popularmovies.model.AsyncTaskListener;
import com.sharma.jitin.popularmovies.model.MovieAPIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jitin on 06-12-2015.
 */
public class FetchMovieTask extends AsyncTask<String,Void,ArrayList<String>> {
    Context context;
    public AsyncTaskListener<ArrayList<String>> asyncTaskListener;
    ProgressDialog progressDialog;

    public FetchMovieTask(Context context, AsyncTaskListener<ArrayList<String>> asyncTaskListener){
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }
    @Override
    protected void onPreExecute(){
        //progressDialog = ProgressDialog.show(context, context.getString(R.string.loading_movie), context.getString(R.string.grab_popcorn));
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<String> arrayList) {
        asyncTaskListener.onTaskComplete(arrayList);
       /* if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }*/
        super.onPostExecute(arrayList);
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJson = null;
        Uri builtUri = Uri.EMPTY;
        MovieAPIItem movieAPIItem = new MovieAPIItem();
        switch (params[0]){
            case "grid":
                builtUri = Uri.parse(movieAPIItem.getMOVIE_BASE_URL()).buildUpon()
                        .appendQueryParameter(movieAPIItem.getSORT_PARAM(), params[1] + movieAPIItem.getSORT_BY())
                        .appendQueryParameter(movieAPIItem.getAPI_KEY(), context.getString(R.string.api_key))
                        .build();
                break;
            case "detail":
                builtUri = Uri.parse(movieAPIItem.getMOVIE_DETAILS_BASE_URL()).buildUpon()
                        .appendPath(params[1])
                        .appendQueryParameter(movieAPIItem.getAPI_KEY(),context.getString(R.string.api_key))
                        .build();
                break;
            case "trailer":
                builtUri = Uri.parse(movieAPIItem.getMOVIE_DETAILS_BASE_URL()).buildUpon()
                        .appendPath(params[1])
                        .appendQueryParameter(movieAPIItem.getAPI_KEY(), context.getString(R.string.api_key))
                        .appendPath(movieAPIItem.getMOVIE_TRAILER())
                        .build();
                break;
            case "review":
                builtUri = Uri.parse(movieAPIItem.getMOVIE_DETAILS_BASE_URL()).buildUpon()
                        .appendPath(params[1])
                        .appendQueryParameter(movieAPIItem.getAPI_KEY(),context.getString(R.string.api_key))
                        .appendPath(movieAPIItem.getMOVIE_REVIEW())
                        .build();
                break;
        }
        try {
            /*final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String SORT_BY = ".desc";
            final String API_KEY = "api_key";

            builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0] + SORT_BY)
                    .appendQueryParameter(API_KEY, context.getString(R.string.api_key))
                    .build();*/

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

        }
        catch (IOException e){

        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(this.getClass().getSimpleName(), "Error closing stream", e);
                }
            }
            try {
                return getMovieData(params[0], movieJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public ArrayList<String> getMovieData(String callType, String movieJson)throws JSONException {

        ArrayList<String> data = new ArrayList<>();

        final String M_RESULT = "results";
        final String M_ID = "id";
        final String M_POSTER_PATH = "poster_path";

        final String M_OVERVIEW = "overview";
        final String M_TITLE = "original_title";
        final String M_RELEASE_DATE = "release_date";
        final String M_RUNTIME = "runtime";
        final String M_RATING = "vote_average";

        final String M_TRAILER_KEY = "key";

        final String M_REVIEW_AUTHOR = "author";
        final String M_REVIEW_CONTENT = "content";

        JSONObject movieDetailJson = new JSONObject(movieJson);

        data.clear();
        switch (callType){
            case "grid":
                JSONArray resultArray = movieDetailJson.getJSONArray(M_RESULT);
                for(int i=0; i<resultArray.length(); i++){
                    JSONObject movieDetails = resultArray.getJSONObject(i);
                    data.add(movieDetails.getString(M_ID) + "," + movieDetails.getString(M_POSTER_PATH));
                }
                break;
            case "detail":
                data.add(movieDetailJson.getString(M_TITLE) + "#" +
                        movieDetailJson.getString(M_OVERVIEW) + "#" +
                        movieDetailJson.getString(M_RELEASE_DATE) + "#" +
                        movieDetailJson.getString(M_RUNTIME) + "#" +
                        movieDetailJson.getString(M_RATING) + "#" +
                        movieDetailJson.getString(M_POSTER_PATH));
                break;
            case "trailer":
                JSONArray resultArray2 = movieDetailJson.getJSONArray(M_RESULT);
                for(int i=0; i<resultArray2.length(); i++){
                    JSONObject movieDetails = resultArray2.getJSONObject(i);
                    data.add("trailer" + "#" + movieDetails.getString(M_TRAILER_KEY));
                }
                break;
            case "review":
                JSONArray resultArray3 = movieDetailJson.getJSONArray(M_RESULT);
                for(int i=0; i<resultArray3.length(); i++){
                    JSONObject movieDetails = resultArray3.getJSONObject(i);
                    data.add("review" + "#" + movieDetails.getString(M_REVIEW_AUTHOR) + "#" + movieDetails.getString(M_REVIEW_CONTENT));
                }
                break;
        }
        return data;
    }
}