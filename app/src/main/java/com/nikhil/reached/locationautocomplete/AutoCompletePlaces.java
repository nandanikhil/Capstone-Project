package com.nikhil.reached.locationautocomplete;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nikhil on 03/05/15.
 */
public class AutoCompletePlaces {


    Activity mActivity;

    AutocompleteFinished listner;
    private ProgressDialog prog;

    public void completeString(Activity mActivity, String url, AutocompleteFinished listner) {
        this.listner = listner;
        this.mActivity = mActivity;
        AutoCompleteTask autoCompleteTask = new AutoCompleteTask();

        autoCompleteTask.execute(url);
    }


    public class AutoCompleteTask extends AsyncTask<String, Integer, String> {
        String autoCompleteData = null;
        private JSONObject autoCompleteJson;
        AutoCompleteParser autoCompleteParser = new AutoCompleteParser();
        private List<HashMap<String, String>> autoCompleteList;
        JSONArray jPlaces = null;


        @Override
        protected String doInBackground(String... url) {
            try {
                String autoCompleteUrl = url[0];
                Http http = new Http();
                autoCompleteData = http.read(autoCompleteUrl);
                autoCompleteJson = new JSONObject(autoCompleteData);
                autoCompleteList = autoCompleteParser.parse(autoCompleteJson);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            listner.autoCompleteFinished(autoCompleteList);
        }
    }
}
