package com.gmail.yurapapapa.anylocator2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by yurap_000 on 004 04 сен.
 */
public class LocationReceiver extends BroadcastReceiver{
    private static final String TAG = "myLogs";

    @Override
    public void onReceive(Context context, Intent intent) {

        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if(location != null) {
            onLocationReceived(context, location);
            return;
        }

        if(intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enabled);
        }
    }
    protected void onLocationReceived(Context context, Location location){
        Log.d(TAG, this + " Got location from " + location.getProvider() + ": " +
                location.getLatitude() + ", " + location.getLongitude());
        if (10f >= location.getAccuracy()) {
            Log.d(TAG, String.valueOf(location.getAccuracy()));

        }
    }
    protected void onProviderEnabledChanged(boolean enabled) {
       // Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
    }

}
