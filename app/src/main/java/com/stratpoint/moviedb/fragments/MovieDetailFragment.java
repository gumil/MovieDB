package com.stratpoint.moviedb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stratpoint.moviedb.R;
import com.stratpoint.moviedb.loaders.ImageLoader;
import com.stratpoint.moviedb.models.Movie;

public class MovieDetailFragment extends Fragment {

    public static final String ARG_EXTRA_MOVIE = "movie";
    private Movie movie;
    private TextView textViewOverview;
    private TextView textViewRating;
    private TextView textViewYear;
    private TextView textViewTitle;
    private ImageView imageViewCover;
    private ImageView imageViewBackdrop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_EXTRA_MOVIE)) {
            this.movie = getArguments().getParcelable(ARG_EXTRA_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        textViewOverview = (TextView) view.findViewById(R.id.textview_overview);
        textViewRating = (TextView) view.findViewById(R.id.textview_rating);
        textViewYear = (TextView) view.findViewById(R.id.textview_year);
        textViewTitle = (TextView) view.findViewById(R.id.textview_title);
        imageViewBackdrop = (ImageView) view.findViewById(R.id.imageview_backdrop);
        imageViewCover = (ImageView) view.findViewById(R.id.imageview_cover);

        textViewOverview.setText(movie.getOverview());
        textViewOverview.clearComposingText();
        textViewRating.setText("Rating: " + movie.getRating());
        textViewYear.setText(movie.getYear());
        textViewTitle.setText(movie.getTitle());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageLoader imageLoader = new ImageLoader(getActivity());
        imageLoader.loadBitmap(movie.getBackdropUrlString(), imageViewBackdrop);
        imageLoader.loadBitmap(movie.getCoverUrlString(), imageViewCover);
    }
}
