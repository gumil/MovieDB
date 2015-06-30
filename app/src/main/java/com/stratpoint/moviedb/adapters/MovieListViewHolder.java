package com.stratpoint.moviedb.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stratpoint.moviedb.R;
import com.stratpoint.moviedb.models.Movie;

public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView textViewMovieTitle;
    public TextView textViewMovieYear;
    public ImageView imageViewBackDrop;
    private OnClickListener mListener;
    private Movie movie;

    public MovieListViewHolder(View itemView, OnClickListener listener) {
        super(itemView);
        mListener = listener;
        itemView.setOnClickListener(this);
        imageViewBackDrop = (ImageView) itemView.findViewById(R.id.imageview_backdrop);
        textViewMovieTitle = (TextView) itemView.findViewById(R.id.textview_movie_title);
        textViewMovieYear = (TextView) itemView.findViewById(R.id.textview_movie_year);
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(movie);
    }

    public interface OnClickListener {
        void onClick(Movie movie);
    }
}
