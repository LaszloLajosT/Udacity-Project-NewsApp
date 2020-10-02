package android.example.newsapp;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class ArticleActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<android.example.newsapp.Article>> {

    public static final String LOG_TAG = ArticleActivity.class.getName();
    /**
     * URL for Article/News data from the Guardian dataset
     */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";
    private static final String SHOW_FIELDS_OF_GUARDIAN_URL = "show-fields=headline,trailText,byline,shortUrl,thumbnail&";

    /**Insert your Guardian API KEY here
     * Eg:
     * private static final String apiKey = "YourApiKey";
     */
    private static final String apiKey = BuildConfig.GUARDIAN_API_KEY;


    /**
     * Constant value for the article loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int ARTICLE_ID_LOADER = 1;

    /**
     * Adapter for the list of articles
     */
    private ArticleAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: Article Activity onCreateLoader() called");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(GUARDIAN_REQUEST_URL);

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),//"order_by"
                getString(R.string.settings_order_by_default)//"most recent value"
        );

        String section_category = sharedPrefs.getString(getString(R.string.settings_section_category_key),
                getString(R.string.settings_section_default)
        );
        Log.i(LOG_TAG, "TEST: Article Activity onCreateLoader() Check the location value:: " + section_category);


        urlBuilder.append(SHOW_FIELDS_OF_GUARDIAN_URL);
        // If the orderBy isn't null then add that
        urlBuilder.append("order-by=").append(orderBy);

        //user wants to choose one news category to read
        if (!section_category.equals(getString(R.string.all))) {
            Log.i(LOG_TAG, "TEST: Article Activity section_category !equal `all`?  then... so append...: ");
            urlBuilder.append("&section=").append(section_category);
        }

        urlBuilder.append("&");
        urlBuilder.append(apiKey);

        Log.i(LOG_TAG, "TEST: Article Activity onCreateLoader() called and test the URL: " + urlBuilder.toString());
        return new ArticleLoader(this, urlBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

        //progress.setVisibility(View.GONE);
        // Hide loading indicator because the data has been loaded
        // View loadingIndicator = findViewById(R.id.loading_indicator);
        //   loadingIndicator.setVisibility(View.GONE);
        ProgressBar progress = findViewById(R.id.loading_indicator);
        progress.setVisibility(View.GONE);

        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(R.string.no_articles);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.i(LOG_TAG, "TEST: Article Activity onLoaderReset() called");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: ArticleActivity  onCreate() called");


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);

        ListView articleListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_ID_LOADER, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getWebUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
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
}
