package com.example.sebastian.appdrawer.appdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Sebastian on 15-01-2017.
 */

public class NotificationOpenedHandler extends Activity implements OneSignal.NotificationOpenedHandler {
    Context ctx;
    public NotificationOpenedHandler(Context ctx){
        this.ctx = ctx;
    }
    String customKey;
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;


        if (data != null) {
            customKey = data.optString("item_key", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.

        Intent intent = new Intent(ctx, ItemDetails.class);
        intent.putExtra("item_key", customKey);

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
