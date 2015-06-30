package com.stratpoint.moviedb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.stratpoint.moviedb.R;
import com.stratpoint.moviedb.adapters.MovieListAdapter;
import com.stratpoint.moviedb.fragments.MovieDetailFragment;
import com.stratpoint.moviedb.models.Movie;


public class MovieActivity extends Activity implements MovieListAdapter.OnItemSelectedCallback{

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.ARG_EXTRA_MOVIE, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailFragment.ARG_EXTRA_MOVIE, movie);
            startActivity(detailIntent);
        }
    }
}
