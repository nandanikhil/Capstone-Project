package com.nikhil.reached;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nikhil.reached.utils.Utility;

public class StatusActivity extends AppCompatActivity {
    private TextView status;
    private boolean isFromNotifications = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        status = (TextView) findViewById(R.id.status);

        if (getIntent().getExtras() != null) {
            isFromNotifications = getIntent().getBooleanExtra(Utility.IS_FROM_NOTIFICATIONS, false);
        }
        String transit_details = Utility.getSharedPrefrences(getApplicationContext()).getString(Utility.TRANSIT_DETAILS, "");
        if (isFromNotifications) {
            if (!transit_details.isEmpty() && transit_details.contains("##"))
                status.setText(transit_details.split("##")[0] + getResources().getString(R.string.has_reached) + transit_details.split("##")[1]);
        } else {
            if (!transit_details.isEmpty()) {
                if (transit_details.contains("##"))
                    status.setText(transit_details.split("##")[0] + getResources().getString(R.string.on_way) + transit_details.split("##")[1]);
            } else {
                status.setText("No one is traveling in your connections");
            }
        }

    }
}
