package com.example.sebastian.appdrawer.appdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Sebastian on 15-01-2017.
 */

public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler{
    Context ctx;
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("itemKey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);

            Intent intent = new Intent(ctx, ItemDetails.class);
            intent.putExtra("itemkey","-KaXrUyX0hIyiNcxDUJR");
            ctx.startActivity(intent);

        }
    }
}
