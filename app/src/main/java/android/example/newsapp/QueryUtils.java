package android.example.newsapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link } objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Article> extractFeatureFromJson(String articleJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            Log.i(LOG_TAG, "TEST: QueryUtils Activity extractFeatureFromJson() called and quit if articleJSON is empty.");
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            // Extract the JSONObject associated with the key called "response",
            // which represents a list of response (or articles).
            JSONObject jsonObjectResponse = baseJsonResponse.getJSONObject("response");

            //we need the results array from the  base object
            JSONArray articleArray = jsonObjectResponse.getJSONArray("results");

            // For each article in the articleArray, create an {@link Article} object
            for (int i = 0; i < articleArray.length(); i++) {
                // Get a single article at position i within the list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                // For a given article, extract the JSONObject associated with the
                // key called "fields", which represents a list of all fields
                // for that article.
                JSONObject fields = currentArticle.getJSONObject("fields");

                // Extract the value for the key called "sectionName","webPublicationDate","headline","shortUrl","byline","thumbnail"
                String sectionName = currentArticle.getString("sectionName");
                String webPublicationDate = currentArticle.getString("webPublicationDate");
                String headline = fields.getString("headline");
                String shortUrl = fields.getString("shortUrl");
                String author;
                String image;

                //Checking JSONObject has author field or not
                if (fields.has("byline")) {
                    author = fields.getString("byline");
                    //Log.i(LOG_TAG, "TEST: QueryUtils Activity , I am inside equals stuff and test my author value:  " + author);
                } else {
                    //if I don't have data about author, set it up as Unknown
                    author = "Unknown";
                    //Log.i(LOG_TAG, "TEST: QueryUtils Activity , I am inside the UNKNOWN stuff and test my author value:  " + author);
                }

                //Checking JSONObject has imagine link string or not
                if (fields.has("thumbnail")) {
                    image = fields.getString("thumbnail");
                } else {
                    //if I don't have imagine for the article, choose a Guardian picture
                    image = "https://scontent-lht6-1.xx.fbcdn.net/v/t1.0-9/46153917_2215538038459186_9131443579053408256_n.png?_nc_cat=105&_nc_sid=dd9801&_nc_oc=AQl_VKi5WOMclNWJeS58wWFpDApBo59vERuTIsVZEpSuM4FEXWT1Q0FNmnBdshrSeNU&_nc_ht=scontent-lht6-1.xx&oh=1d2a00e91adc2d88fa64b56bf5c63ba1&oe=5EB88452";
                }


                // Create a new {@link Article} object with the section name, publication date, headline, link, author name(s)
                // and an image link from the JSON response.
                Article article = new Article(sectionName, webPublicationDate, headline, shortUrl, author, getBitmapFromURL(image));

                // Add the new {@link Article} to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    protected static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.i(LOG_TAG, "TEST: QueryUtils Activity readFromStream() called");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the USGS dataset and return a list of {@link } object
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: QueryUtils Activity fetchArticleData() called");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            Thread.sleep(2000);
            jsonResponse = makeHttpRequest(url);
        } catch (IOException | InterruptedException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            e.printStackTrace();
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Article> article = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return article;
    }

    private static Bitmap getBitmapFromURL(String src) {

        try {
            //Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

}


