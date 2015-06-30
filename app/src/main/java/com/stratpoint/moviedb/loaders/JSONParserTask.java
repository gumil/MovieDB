package com.stratpoint.moviedb.loaders;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.stratpoint.moviedb.adapters.MovieListAdapter;
import com.stratpoint.moviedb.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParserTask extends AsyncTask<Void, Void, Movie[]> {

    private static final String JSON_URL = "https://dl.dropboxusercontent.com/u/5624850/movielist/list_movies_page1.json";
    private static final String TAG_DATA = "data";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_TITLE = "title";
    private static final String TAG_YEAR = "year";
    private static final String TAG_RATING = "rating";
    private static final String TAG_SLUG = "slug";
    private static final String TAG_OVERVIEW = "overview";
    private static final String LOG_TAG = "JSONParserTask";

    private final View progressBar;
    private final RecyclerView mRecyclerView;
    private final Activity mActivity;

    public JSONParserTask(Activity activity, RecyclerView recyclerView, View view) {
        progressBar = view;
        mActivity = activity;
        mRecyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        try {
            String json = downloadUrl(JSON_URL);
            return createListFromJSON(json);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    private String downloadUrl(String urlString) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = new BufferedInputStream(conn.getInputStream());

            return readStream(is);

        } finally {
            if (is != null) {
                is.close();
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    public String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuffer result = new StringBuffer();

        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    private Movie[] createListFromJSON(String jsonString) {
        Movie[] movieArray = null;
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray movies = json.getJSONObject(TAG_DATA).getJSONArray(TAG_MOVIES);
            int length = movies.length();
            movieArray = new Movie[length];
            JSONObject jsonObject;
            String title, year, rating, slug, overview;
            for (int i = 0; i < length; i++) {
                jsonObject = movies.getJSONObject(i);
                title = jsonObject.getString(TAG_TITLE);
                year = jsonObject.getString(TAG_YEAR);
                rating = jsonObject.getString(TAG_RATING);
                slug = jsonObject.getString(TAG_SLUG);
                overview = jsonObject.getString(TAG_OVERVIEW);
                movieArray[i] = new Movie(title, rating, year, slug, overview);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return movieArray;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        progressBar.setVisibility(View.GONE);
        MovieListAdapter adapter = new MovieListAdapter(mActivity, movies);
        mRecyclerView.setAdapter(adapter);
    }
}
