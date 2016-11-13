package com.nikhil.reached;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nikhil.reached.locationautocomplete.AutoCompletePlaces;
import com.nikhil.reached.locationautocomplete.AutocompleteFinished;
import com.nikhil.reached.locationautocomplete.PlacesDetail;
import com.nikhil.reached.locationautocomplete.PlacesDetailFetched;
import com.nikhil.reached.utils.GeofenceErrorMessages;
import com.nikhil.reached.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeoFenceActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AutocompleteFinished, PlacesDetailFetched {

    private static final int ACCESS_FINE_LOCATION = 1000;
    private Geofence geofence;
    protected GoogleApiClient mGoogleApiClient;


    private AutoCompleteTextView autoCompleteEditText;
    private List<HashMap<String, String>> autoCompleteList;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            4 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 0.1f;
    private TextInputLayout autoCompleteEditTextWrapper;
    private Toolbar toolbar;
    private String locationSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }


        buildGoogleApiClient();
        autoCompleteEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteEditText);
        autoCompleteEditTextWrapper = (TextInputLayout) findViewById(R.id.autoCompleteEditTextWrapper);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_geofences);


        autoCompleteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (autoCompleteEditTextWrapper.getError() != null) {
                    autoCompleteEditTextWrapper.setError(null);
                }

                if (autoCompleteEditText.isPerformingCompletion()) {
                    return;
                }
                AutoCompletePlaces autoCompletePlaces = new AutoCompletePlaces();
                autoCompletePlaces.completeString(GeoFenceActivity.this, Utility.autoCompleteTextViewUrl(GeoFenceActivity.this, s.toString()), GeoFenceActivity.this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public Geofence createGeofence(Double latitude, Double longitude, String name) {
        return new Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(
                        latitude,
                        longitude,
                        GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }


    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.e("###", "####");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do something with result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {

        mGoogleApiClient.connect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    @Override
    public void autoCompleteFinished(final List<HashMap<String, String>> autoCompleteList) {


        if (autoCompleteList != null && autoCompleteList.size() > 0) {
            this.autoCompleteList = autoCompleteList;
            ArrayList<String> autoCompleteListArray = new ArrayList<String>();
            for (int i = 0; i < autoCompleteList.size(); i++) {
                autoCompleteListArray.add(autoCompleteList.get(i).get("description"));
            }
            final ArrayAdapter adapter = new ArrayAdapter<String>(GeoFenceActivity.this, R.layout.default_list_item, R.id.name, autoCompleteListArray);

            autoCompleteEditText.setThreshold(1);
            autoCompleteEditText.showDropDown();
            autoCompleteEditText.setAdapter(adapter);
            autoCompleteEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PlacesDetail placesDetail = new PlacesDetail();
                    placesDetail.details(GeoFenceActivity.this, Utility.placeDetailsFromPlaceidUrl(GeoFenceActivity.this, autoCompleteList.get(position).get("place_id")), GeoFenceActivity.this);
                    adapter.clear();
                    autoCompleteEditText.dismissDropDown();
                    autoCompleteEditText.removeTextChangedListener(null);
                }
            });
        }

    }

    @Override
    public void detailsFetched(Double latitude, Double longitude, String name) {
        if (latitude > 0 && longitude > 0) {
            locationSelected = name;
            geofence = createGeofence(latitude, longitude, name);
            if (!mGoogleApiClient.isConnected()) {
                Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(GeoFenceActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GeoFenceActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION);
            } else {
                addGeofence();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {

            case ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addGeofence();
                } else {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void addGeofence() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Toast.makeText(
                                GeoFenceActivity.this,
                                R.string.geofence_added,
                                Toast.LENGTH_SHORT
                        ).show();


                        Intent intent = new Intent(GeoFenceActivity.this, MainActivity.class);
                        intent.putExtra(Utility.SELCTED_LOCATION, locationSelected);
                        startActivity(intent);
                        finish();

                    } else {
                        // Get the status code for the error and log it using a myUser-friendly message.
                        String errorMessage = GeofenceErrorMessages.getErrorString(GeoFenceActivity.this,
                                status.getStatusCode());
                    }
                }
            });
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }

    }

}