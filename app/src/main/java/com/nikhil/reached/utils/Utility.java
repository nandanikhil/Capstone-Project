package com.nikhil.reached.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nikhil.reached.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by nikhil on 07/11/16.
 */

public class Utility {


    public static final String FCM_TOKEN = "FCM_TOKEN";
    public static final String SELCTED_LOCATION = "SELCTED_LOCATION";
    public static final String TRANSIT_DETAILS = "JOURNEY_STARTED";
    public static final String RECIEVERS_FIREBASEREGID = "RECIEVERS_FIREBASEREGID";
    public static final String IS_FROM_NOTIFICATIONS = "IS_FROM_NOTIFICATIONS";

    public static SharedPreferences getSharedPrefrences(Context context) {
        return context.getSharedPreferences("reachedPrefs", Context.MODE_PRIVATE);
    }

    public static void savePushToken(String token, Context context) {
        context.getSharedPreferences("reachedPrefs", Context.MODE_PRIVATE).edit().putString(FCM_TOKEN, token).apply();
    }

    public static String getPushtoken(Context context) {
        return context.getSharedPreferences("reachedPrefs", Context.MODE_PRIVATE).getString(FCM_TOKEN, "");
    }


    public static String autoCompleteTextViewUrl(Activity context, String text) {
        String input = "";
        try {
            input = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        googlePlacesUrl.append("input=" + input);
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&components=country:in");
        googlePlacesUrl.append("&key=" + context.getResources().getString(R.string.google_places_key));
        return googlePlacesUrl.toString();
    }


    public static String placeDetailsFromPlaceidUrl(Activity context, String placeid) {
        String key = "AIzaSyCI0049MaICQ7gtKLvNHM3otNY-PBlI5Tw";
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlacesUrl.append("placeid=" + placeid);
        googlePlacesUrl.append("&key=" + context.getResources().getString(R.string.google_places_key));

        return googlePlacesUrl.toString();
    }


}
