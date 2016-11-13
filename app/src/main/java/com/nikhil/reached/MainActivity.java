package com.nikhil.reached;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nikhil.reached.beans.User;
import com.nikhil.reached.utils.Utility;

/**
 * Created by nikhil on 07/11/16.
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = "##MainActivity";
    private static final String USERS = "USERS";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private GoogleApiClient mGoogleApiClient;
    private String phoneno;
    User myUser;
    private DatabaseReference mUsersReference;
    private RecyclerView userLists;
    private UserAdapter mAdapter;
    private String selectedLocation;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.getSharedPrefrences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter = new UserAdapter(this, mUsersReference, selectedLocation);
        userLists.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            selectedLocation = getIntent().getStringExtra(Utility.SELCTED_LOCATION);
        }


        setContentView(R.layout.activity_main);
        Utility.getSharedPrefrences(getApplicationContext()).registerOnSharedPreferenceChangeListener(MainActivity.this);
        mUsersReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS);
        userLists = (RecyclerView) findViewById(R.id.userLists);
        userLists.setLayoutManager(new LinearLayoutManager(this));

        myUser = User.getMyUser();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();

        }


        if (AccountKit.getCurrentAccessToken() != null) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {


                @Override
                public void onSuccess(Account account) {
                    phoneno = account.getPhoneNumber().getPhoneNumber();

                    myUser.setUserName(mUsername);
                    myUser.setMobileNo(phoneno);
                    myUser.setUserEmail(mFirebaseUser.getEmail());
                    myUser.setId(mFirebaseUser.getUid());
                    if (!Utility.getPushtoken(getApplicationContext()).isEmpty())
                        myUser.setFirebaseRegid(Utility.getPushtoken(getApplicationContext()));


                    mUsersReference.child(myUser.getId()).setValue(myUser);
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });

        }


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equalsIgnoreCase(Utility.FCM_TOKEN)) {
            if (!Utility.getPushtoken(getApplicationContext()).isEmpty())
                mUsersReference.child(User.getMyUser().getId()).child("firebaseRegid").setValue(Utility.getPushtoken(getApplicationContext()));
        }
    }
}
