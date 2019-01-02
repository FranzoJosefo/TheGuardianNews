package com.franciscoolivero.android.theguardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of NewsList by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
        Log.v(LOG_TAG, "New NewsLoader has been constructed");

    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading, forceLoad so loadinBackground is executed");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.v(LOG_TAG, "Url wasn't null, fetchEartquakeData(mUrl) is triggered");


        // Perform the network request, parse the response, and extract a list of NewsList.
        List<News> NewsList = QueryUtils.fetchNewsData(mUrl);
        Log.v(LOG_TAG, "Newss where correctly fetched, return NewsList so that onLoadFinished can add them to adapter");

        return NewsList;
    }
}