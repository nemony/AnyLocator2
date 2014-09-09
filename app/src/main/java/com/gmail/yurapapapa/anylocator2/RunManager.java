package com.gmail.yurapapapa.anylocator2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by yurap_000 on 004 04 сен.
 */
public class RunManager {
    private static final String TAG = "myLogs";

    public static final String ACTION_LOCATION = "com.gmail.yurapapapa.anylocator2.ACTION_LOCATION";

    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }
    public static RunManager get(Context c) {
        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }
    public void startLocationUpdates() {
        Log.d(TAG, "RunManager startLocationUpdates");
        String provider = LocationManager.GPS_PROVIDER;

        PendingIntent pi = getLocationPendingIntent(true);

        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    public void stopLocationUpdates() {
        Log.d(TAG, "RunManager stopLocationUpdates");
        PendingIntent pi = getLocationPendingIntent(false);
        if(pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }
}
