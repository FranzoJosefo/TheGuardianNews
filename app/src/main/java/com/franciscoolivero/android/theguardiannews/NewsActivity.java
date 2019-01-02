package com.franciscoolivero.android.theguardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();
    @BindView(R.id.list)
    ListView newsListView;
    @BindView(R.id.text_empty_state)
    TextView emptyStateView;
    @BindView(R.id.loading_spinner)
    View loadingSpinner;

    // Create a new {@link ArrayAdapter} of newsList
    private NewsAdapter mAdapter;

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?q=politics";
    private static final String API_TEST_KEY = "ab50e098-6b60-4613-b96e-504bf21b2d4b";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "News Activity, onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            loadingSpinner.setVisibility(View.GONE);
            newsListView.setEmptyView(emptyStateView);
            emptyStateView.setText(R.string.no_inet);

        } else {
            // Create a new adapter that takes an empty list of newsList as input

            mAdapter = new NewsAdapter(this, new ArrayList<News>());
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            newsListView.setAdapter(mAdapter);

            //Set the Empty View to the ListView.
            newsListView.setEmptyView(emptyStateView);

            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News currentNews = mAdapter.getItem(position);
                    openWebPage(currentNews);
                }
            });

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "Loader will be initialized. If it doesn't exist, create loader, if else reuse.");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.i(LOG_TAG, "Loader Initialized.");
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            String selectTopic = sharedPrefs.getString(
                    getString(R.string.settings_select_topic_key),
                    getString(R.string.settings_select_topic_default)
            );

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("search?q", selectTopic);
                uriBuilder.appendQueryParameter("show-tags", "contributor");
                uriBuilder.appendQueryParameter("section", selectTopic);
                uriBuilder.appendQueryParameter("api-key", API_TEST_KEY);

        Log.i(LOG_TAG, "No Loader was previously created, creating new NewsLoader.");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        loadingSpinner.setVisibility(View.GONE);
        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        Log.i(LOG_TAG, "Loading finished, add all News to adapter so they can be displayed");

        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
        }

        emptyStateView.setText(R.string.empty_state);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "Loader reset, clear the data from adapter");

        mAdapter.clear();
    }

    public void openWebPage(News news) {
        Uri newsUri = Uri.parse(news.getArticleWebUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

