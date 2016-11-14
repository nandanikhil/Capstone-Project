/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikhil.reached.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nikhil.reached.R;
import com.nikhil.reached.ReachedWidgetProvider;
import com.nikhil.reached.StatusActivity;
import com.nikhil.reached.utils.Utility;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    private String name;
    private String location;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.


        String message = remoteMessage.getData().get("message");
        name = message.split("##")[0];
        location = message.split("##")[1];

        if (remoteMessage.getData().get("data").equalsIgnoreCase("1")) {
            updateWidget();

        } else {
            sendNotification();
        }


    }


    /**
     * Create and show a simple notification containing the received GCM message.
     */
    private void sendNotification() {
        Intent intent;

        intent = new Intent(getBaseContext(), StatusActivity.class);
        intent.putExtra(Utility.IS_FROM_NOTIFICATIONS, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(name + getString(R.string.has_reached) + location)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bm)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());


    }


    public void updateWidget() {
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_reached);
        ComponentName thisWidget = new ComponentName(context, ReachedWidgetProvider.class);
        remoteViews.setTextViewText(R.id.status, name + getString(R.string.on_way) + location);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

}