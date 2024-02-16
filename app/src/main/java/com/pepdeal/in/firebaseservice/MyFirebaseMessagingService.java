package com.pepdeal.in.firebaseservice;


import static com.pepdeal.in.firebaseservice.Config.TOPIC_GLOBAL;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pepdeal.in.R;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.activity.MessageUsersListActivity;
import com.pepdeal.in.activity.SellerTicketListActivity;
import com.pepdeal.in.firebaseservice.util.NotificationHelper;
import com.pepdeal.in.firebaseservice.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Firebase-Notification
 * https://github.com/quintuslabs/Firebase-Notification
 * Created on 28/04/19..
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    String offer_id, issue, location;
    Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }


    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
            Uri alarmSound = Uri.parse("android.resource://" +
                    getPackageName() + "/" + R.raw.notification_sound);
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        }
    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
//            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            String msg_flag = data.getString("msg_flag");
//            String offer_id = data.getString("offer_id");
            /*if(data.has("result")) {
                JSONArray jsonArray = data.getJSONArray("result");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
            }*/
//            JSONObject payload = data.getJSONObject("payload");


            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

 /*           if (!offer_id.equals("0")) {
                intent = new Intent(getApplicationContext(), OffersProductListActivity.class);
                intent.putExtra("offer_id", offer_id);
            } else {*/
            /*0-admin, 1- chat, 2- ticket status change,3- ticket raised*/
            if (msg_flag.equals("0")) {
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                HomeActivity.pos = 1;
            } else if (msg_flag.equals("1")) {
                intent = new Intent(getApplicationContext(), MessageUsersListActivity.class);
            } else if (msg_flag.equals("2")) {
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                HomeActivity.pos = 2;
            } else if (msg_flag.equals("3")) {
                intent = new Intent(getApplicationContext(), SellerTicketListActivity.class);
            }
//                intent.putExtra("status",status);
//            }

            if (imageUrl != null) {
                notificationHelper.createNotification(title, message, R.drawable.main_logo, imageUrl, timestamp, intent);
            } else {
                notificationHelper.createNotification(title, message, timestamp, intent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
