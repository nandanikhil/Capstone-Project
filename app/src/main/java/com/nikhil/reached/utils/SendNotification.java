package com.nikhil.reached.utils;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

/**
 * Created by nikhil on 13/11/16.
 */

public class SendNotification extends AsyncTask<Void, Void, Void> {
    private static final String URL = "http://reached.azurewebsites.net/PushServer-0.0.1-SNAPSHOT/api/PushService";

    private JSONObject object;

    public SendNotification(String firebaseRegid, String name, int type, String location) {
        try {
            object = new JSONObject();
            object.put("regID", firebaseRegid);
            object.put("message", name + "##" + location);
            object.put("data", String.valueOf(type));

            Utility.getSharedPrefrences(getApplicationContext()).edit().putString(Utility.RECIEVERS_FIREBASEREGID, firebaseRegid).commit();
            Utility.getSharedPrefrences(getApplicationContext()).edit().putString(Utility.TRANSIT_DETAILS, name + "##" + location).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Void doInBackground(Void... voids) {

        makeApiCall();
        return null;
    }


    public void makeApiCall() {
        try {

            java.net.URL url = new URL(URL);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(object.toString());
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while (in.readLine() != null) {
                stringBuffer.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}