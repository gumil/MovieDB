package com.stratpoint.moviedb.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratpoint.moviedb.R;
import com.stratpoint.moviedb.loaders.JSONParserTask;


public class MovieListFragment extends Fragment {

    private Activity mActivity;

    private RecyclerView mRecyclerView;

    private View progressBar;
    private View textViewError;
    private View progressBarContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.movie_recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        progressBarContainer = view.findViewById(R.id.progressBar_container);
        textViewError = view.findViewById(R.id.textview_error);
        mRecyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        parseJSONFromUrl();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void parseJSONFromUrl() {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new JSONParserTask(mActivity, mRecyclerView, progressBarContainer).execute();
        } else {
            progressBar.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }
    }
}
