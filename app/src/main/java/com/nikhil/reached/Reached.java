package com.nikhil.reached;

import android.app.Application;

import com.facebook.accountkit.AccountKit;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by nikhil on 12/11/16.
 */

public class Reached extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AccountKit.initialize(getApplicationContext());


    }
}
