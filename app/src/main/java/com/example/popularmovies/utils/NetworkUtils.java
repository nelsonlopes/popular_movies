package com.example.popularmovies.utils;

import android.content.Context;
import android.example.popularmovies_stage1.BuildConfig;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static Context mContext;

    /**
     * Could be in a method, if the user could choose the dimension of the poster
     */
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public final static String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";
    private final static String PARAM_API_KEY = "api_key";

    // Movies
    public static URL buildUrl(String sortMethod) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL + sortMethod).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.TmdbApiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Trailers
    public static URL buildUrl(int movieId) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL + movieId + "/videos").buildUpon()
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.TmdbApiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /** Checks if there is internet connection
     * Reference: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/2001824#2001824
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}