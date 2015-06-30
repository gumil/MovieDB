package com.stratpoint.moviedb.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    private static final String URL_IMAGE_PREFIX = "https://dl.dropboxusercontent.com/u/5624850/movielist/images/";
    private static  final String URL_BACKDROP_SUFFIX = "-backdrop.jpg";
    private static  final String URL_COVER_SUFFIX = "-cover.jpg";

    private String slug;
    private String title;
    private String rating;
    private String year;
    private String overview;

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(String title, String rating, String year, String slug, String overview) {
        this.title = title;
        this.rating = rating;
        this.year = year;
        this.slug = slug;
        this.overview = overview;
    }

    private Movie(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
    this.title = data[0];
    this.rating = data[1];
    this.year = data[2];
    this.slug = data[3];
    this.overview = data[4];
}

    public String getOverview() {
        return overview;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getBackdropUrlString() {
        return URL_IMAGE_PREFIX + slug + URL_BACKDROP_SUFFIX;
    }

    public String getCoverUrlString() {
        return URL_IMAGE_PREFIX + slug + URL_COVER_SUFFIX;
    }

    @Override
    public String toString() {
        return "Title = " + title + " Rating = " + rating + " Year = " + year + " slug = " +
                slug + " Overview = " + overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {title, rating, year, slug, overview});
    }
}
