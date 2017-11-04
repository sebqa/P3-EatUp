package com.example.sebastian.eatup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Sebastian on 15-01-2017.
 */

public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler{
    Context ctx;
    Activity activity;
    String notiType;
    TextView newNoti;
    public NotificationReceivedHandler(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
    }
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("itemKey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);



        }
    }
}
