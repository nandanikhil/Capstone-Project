package com.nikhil.reached.locationautocomplete;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nikhil on 04/05/15.
 */
public class PlacesDetail {


    Activity mActivity;
    PlacesDetailFetched listner;
    private ProgressDialog prog;
    private Double latitude;
    private Double longitude;
    private String name = null;

    public void details(Activity mActivity, String url, PlacesDetailFetched listner) {
        this.listner = listner;
        this.mActivity = mActivity;
        FetchDetailTask fetchDetailTask = new FetchDetailTask();

        fetchDetailTask.execute(url);
    }


    public class FetchDetailTask extends AsyncTask<String, Integer, String> {
        String placeDetailData = null;
        private JSONObject placeDetailJson;
        //        placeDetailParser placeDetailParser = new placeDetailParser();
        private List<HashMap<String, String>> placeDetailList;
        JSONArray jPlaces = null;


        @Override
        protected String doInBackground(String... url) {
            try {
                String placeDetailUrl = url[0];
                Http http = new Http();
                placeDetailData = http.read(placeDetailUrl);
                placeDetailJson = new JSONObject(placeDetailData);
                parseJson(placeDetailJson);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            listner.detailsFetched(latitude,longitude, name);
        }
    }

    private void parseJson(JSONObject placeDetailJson) {
        JSONObject result = null;
        JSONObject geometry = null;
        JSONObject location = null;

        try {
            if (placeDetailJson.getJSONObject("result") != null) {
                result = placeDetailJson.getJSONObject("result");
                if (result.getJSONObject("geometry") != null) {
                    geometry = result.getJSONObject("geometry");
                    name = result.getString("name");
                    if (geometry.getJSONObject("location") != null) {
                        location = geometry.getJSONObject("location");

                        if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                            latitude = location.getDouble("lat");
                            longitude = location.getDouble("lng");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
