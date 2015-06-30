package com.stratpoint.moviedb.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratpoint.moviedb.R;
import com.stratpoint.moviedb.loaders.ImageLoader;
import com.stratpoint.moviedb.models.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListViewHolder> {

    private final OnItemSelectedCallback mCallback;
    private ImageLoader imageLoader;
    private Activity mActivity;
    private Movie[] movieArray;

    public interface OnItemSelectedCallback {
        void onItemSelected(Movie movie);
    }

    public MovieListAdapter(Activity activity, Movie[] movies) {
        mActivity = activity;
        movieArray = movies;
        imageLoader = new ImageLoader(activity);
        mCallback = (OnItemSelectedCallback) activity;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_layout, parent, false);
        return new MovieListViewHolder(view, new MovieListViewHolder.OnClickListener() {
            @Override
            public void onClick(Movie movie) {
                mCallback.onItemSelected(movie);
            }
        });
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        Movie movie = movieArray[position];
        holder.setMovie(movie);
        holder.textViewMovieTitle.setText(movie.getTitle());
        holder.textViewMovieYear.setText(movie.getYear());
        imageLoader.loadBitmap(movie.getBackdropUrlString(), holder.imageViewBackDrop);
    }

    @Override
    public int getItemCount() {
        return movieArray.length;
    }

}