package com.example.sebastian.appdrawer.appdrawer;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sebastian on 24-11-2016.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}
