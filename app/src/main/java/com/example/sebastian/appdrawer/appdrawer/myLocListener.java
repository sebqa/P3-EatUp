package com.example.sebastian.appdrawer.appdrawer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by mitna on 22-11-2016.
 */

public class myLocListener implements LocationListener {

    Location location;
    double latitude;
    double longitude;

    public double getLatitude() {
        if (location != null){
            latitude = location.getLatitude();

        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e("Latitude :", "" + location.getLatitude());
            Log.e("Longitude :", "" + location.getLongitude());

        }
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
