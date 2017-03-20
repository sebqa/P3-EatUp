package com.example.sebastian.appdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Sebastian on 15-01-2017.
 */

public class NotificationOpenedHandler extends Activity implements OneSignal.NotificationOpenedHandler {
    Context ctx;
    Activity activity;
    public NotificationOpenedHandler(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
    }
    String itemKey;
    String notiType;
    TextView newNoti;
    String itemName;

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;


        if (data != null) {
            itemKey = data.optString("item_key", null);
            notiType = data.optString("notiType", null);
            if (itemKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + itemKey);

        }


        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        if (notiType != null) {
            if (notiType.equals("tag")) {
                Intent intent = new Intent(ctx, ItemDetails.class);
                intent.putExtra("item_key", itemKey);

                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }

            if (notiType.equals("order")) {

                /*Intent intent = new Intent(ctx, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);*/

                DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                newNoti = (TextView) drawer.findViewById(R.id.newNoti);
                newNoti.setVisibility(View.VISIBLE);
            }
            if (notiType.equals("confirmation")) {
                DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                newNoti = (TextView) drawer.findViewById(R.id.newNoti);
                newNoti.setVisibility(View.VISIBLE);
                newNoti.setText("2");
            }
        }
    }
}

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
