package com.example.sebastian.appdrawer.appdrawer;

import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;

/**
 * Created by Sebastian on 26-01-2017.
 */

public class NotificationOverride extends NotificationExtenderService {
        @Override
        protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
            OverrideSettings overrideSettings = new OverrideSettings();
            overrideSettings.extender = new NotificationCompat.Extender() {
                @Override
                public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {


                    builder.setAutoCancel(true);
                    builder.setColor(Color.RED);

                    return builder;
                }
            };

            OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
            Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

            return true;
        }
}
