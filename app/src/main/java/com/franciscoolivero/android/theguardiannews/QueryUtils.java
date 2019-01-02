package com.franciscoolivero.android.theguardiannews;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from The Guardian.
 */
public final class QueryUtils {

    // http://content.guardianapis.com/search?q=politics&show-tags=contributor&api-key=test

    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Query the USGS dataset and return an {@link List<News>} object to represent a list of NewsList.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        //The following try catch block generates a 2 second delay until we make the request so that we can see the Loading Spinner.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a {@link List<News>} object
        List<News> NewsList = extractNews(jsonResponse);

        // Return the {@link List<News>}
        return NewsList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());

            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
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
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<News> extractNews(String jsonResponse) {

        //If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<News> NewsList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            Log.v("extractNews", "jsonRootObject created");
            JSONObject jsonObjectResponse = jsonRootObject.optJSONObject("response");
            Log.v("extractNews", "jsonResponseObject created");
            JSONArray jsonArrayResults = jsonObjectResponse.optJSONArray("results");

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject currentResult = jsonArrayResults.getJSONObject(i);
                Log.v("NewsResult: " + i, "JSONObject created");

                //Declare the variables that may not be available in the response.
                String publicationDate = null;
                String authorName = null;

                String sectionName = currentResult.optString("sectionName");
                Log.i("NewsResult " + i + " sectionName", String.valueOf(sectionName));
                String articleTitle = currentResult.optString("webTitle");
                Log.i("NewsResult " + i + " articleTitle", articleTitle);
                String articleWebUrl = currentResult.optString("webUrl");
                Log.i("NewsResult " + i + " webUrl", articleWebUrl);
                publicationDate = currentResult.optString("webPublicationDate");
                Log.i("NewsResult " + i + " publicationDate", publicationDate);

                JSONArray jsonArrayTags = currentResult.optJSONArray("tags");
                if(jsonArrayTags!=null && jsonArrayTags.length() > 0){
                    //Index is 0 for the following line, since we are only fetching ONE tag.
                    JSONObject jsonObjectTag = jsonArrayTags.getJSONObject(0);
                    authorName = jsonObjectTag.optString("webTitle");
                    Log.i("NewsResult " + i + " authorName", authorName);
                }

                NewsList.add(new News(sectionName, articleTitle, articleWebUrl, publicationDate, authorName));
                Log.i("News array", "News added " + i);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of NewsList
        return NewsList;
    }

}

