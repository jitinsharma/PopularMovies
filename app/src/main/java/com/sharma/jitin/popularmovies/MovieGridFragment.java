package com.sharma.jitin.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sharma.jitin.popularmovies.Utils.NetworkCheck;
import com.sharma.jitin.popularmovies.adapter.MovieAdapter;
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

    ArrayList<MovieItem> posterPaths = new ArrayList<>();
    ArrayList<String> movieIds = new ArrayList<>();

    ProgressDialog progressDialog;
    NetworkCheck networkCheck = new NetworkCheck();

    final String M_POPULARITY = "popularity";
    final String M_RATING = "rating";
    final String LOG_TAG = this.getClass().getSimpleName();

    Parcelable state;
    int position;

    public MovieGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            posterPaths = (ArrayList<MovieItem>)savedInstanceState.get("key");
            movieIds = (ArrayList<String>)savedInstanceState.get("id");
            ((LinearLayoutManager) movieView.getLayoutManager()).scrollToPosition(position);
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
            if(networkCheck.isNetworkStatusAvailable(getContext())) {
                fetchMovieTask.execute("grid", M_POPULARITY);
            }
            else{
                Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
        if(id == R.id.action_sort_rating){
            if(networkCheck.isNetworkStatusAvailable(getContext())) {
                fetchMovieTask.execute("grid", M_RATING);
            }
            else{
                Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
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
        if(networkCheck.isNetworkStatusAvailable(getContext())) {
            //fetchMovieTask.execute("grid", M_POPULARITY);
            if(posterPaths.size()==0) {
                fetchMovieTask.execute("grid", M_POPULARITY);
        }
            else{
                movieAdapter = new MovieAdapter(getActivity(),posterPaths);
                movieView.setAdapter(movieAdapter);
                movieAdapter.setOnItemClick(new OnItemClick() {
                    @Override
                    public void onItemClicked(int position) {
                        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                        String id = movieIds.get(position);
                        intent.putExtra("movieID", id);
                        startActivity(intent);
                    }
                });
            }
        }
        else{
            Snackbar.make(getView(), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // store the data in the fragment
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", posterPaths);
        savedInstanceState.putStringArrayList("id", movieIds);
        position = ((LinearLayoutManager)movieView.getLayoutManager()).findFirstVisibleItemPosition();
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
                        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                        String id = movieIds.get(position);
                        intent.putExtra("movieID", id);
                        startActivity(intent);
                    }
                });
        }
    }

    /*class FetchMovieTask extends AsyncTask<String,Void,Void>{
        final String M_POSTER_LINK = "http://image.tmdb.org/t/p/w185/";
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.loading_movie), getString(R.string.grab_popcorn));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            movieAdapter = new MovieAdapter(getActivity(),posterPaths);
            movieView.setAdapter(movieAdapter);
            movieAdapter.setOnItemClick(new OnItemClick() {
                @Override
                public void onItemClicked(int position) {
                    Intent intent = new Intent(getActivity(),MovieDetailActivity.class);
                    String id = movieIds.get(position);
                    intent.putExtra("movieID", id);
                    startActivity(intent);
                }
            });
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJson = null;
            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String SORT_BY = ".desc";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0] + SORT_BY)
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))
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
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                try {
                    return getMovieData(movieJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public Void getMovieData(String movieJson)throws JSONException{

            final String M_RESULT = "results";
            final String M_ID = "id";
            final String M_POSTER_PATH = "poster_path";


            JSONObject forecastJson = new JSONObject(movieJson);
            JSONArray resultArray = forecastJson.getJSONArray(M_RESULT);

            movieIds.clear();
            posterPaths.clear();
            for(int i=0; i<resultArray.length(); i++){
                JSONObject movieDetails = resultArray.getJSONObject(i);
                movieIds.add(movieDetails.getString(M_ID));
                posterPaths.add(new MovieItem(M_POSTER_LINK + movieDetails.getString(M_POSTER_PATH)));
            }
            return null;
        }
    }*/
}
