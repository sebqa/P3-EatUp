package com.example.sebastian.appdrawer.appdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;
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

            Intent intent = new Intent(ctx, ItemDetails.class);
            intent.putExtra("itemkey","-KaXrUyX0hIyiNcxDUJR");
            ctx.startActivity(intent);
            if (notiType.equals("order")) {
                DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                newNoti = (TextView) drawer.findViewById(R.id.newNoti);
                newNoti.setVisibility(View.VISIBLE);
            }
        }
    }
}
