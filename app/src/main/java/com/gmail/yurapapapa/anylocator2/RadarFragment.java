package com.gmail.yurapapapa.anylocator2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by yurap_000 on 004 04 сен.
 */
public class RadarFragment extends Fragment{
    private static final String TAG = "myLogs";
    public static final String PREFS_FIRST_LOCATION = "first_locaiton";
    public static final String PREFS_FIRST_LOCATION_LONGITUDE = "first_location_longitude";
    public static final String PREFS_FIRST_LOCATION_ALTITUDE = "first_location_longitude";
    public static final String PREFS_FIRST_LOCATION_LATITUDE = "first_location_latitude";

    // define the display assembly compass picture
    private ImageView imageArrow;
    // record the compass picture angle turned
    private float currentDegree = 0f;
    // device sensorManager
    private SensorManager mSensorManager;
    private Sensor accelerometr;
    private Sensor magnetometer;
    private TextView tv, tvDistance;

    private Location mLastLocation;
    private Location mFirstLocation;
    private static boolean isFirst = true;

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location location) {

            mLastLocation = location;

            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_FIRST_LOCATION, Context.MODE_PRIVATE);
            if (isFirst && location.getAccuracy() <= 5) {
                Log.d(TAG, "isFirst&&mFirstLocation.getAccruracy = " + location.getAccuracy());
                prefs.edit().putFloat(PREFS_FIRST_LOCATION_ALTITUDE, (float)location.getAltitude()).apply();
                prefs.edit().putFloat(PREFS_FIRST_LOCATION_LATITUDE, (float)location.getLatitude()).apply();
                prefs.edit().putFloat(PREFS_FIRST_LOCATION_LONGITUDE, (float)location.getLongitude()).apply();
                mFirstLocation = location;
                //mFirstLocation.setLatitude(41.27988);
                //mFirstLocation.setLongitude(69.212484);
                isFirst = false;
            }
            if (!isFirst && mFirstLocation == null) {
                Log.d(TAG, "mFirstLocation = " + null);
                mFirstLocation = location;
                location.setLongitude(prefs.getFloat(PREFS_FIRST_LOCATION_LONGITUDE, -1));
                location.setAltitude(prefs.getFloat(PREFS_FIRST_LOCATION_ALTITUDE, -1));
                location.setLatitude(prefs.getFloat(PREFS_FIRST_LOCATION_LATITUDE, -1));

            }
            if(isVisible() && mLastLocation != null && mFirstLocation != null) {
                updateUI(String.valueOf(mLastLocation.distanceTo(mFirstLocation)));
            }
            Log.d(TAG, "RadarFragment " + "onLocationReceived");
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            String toastText = enabled ? "enabled" : "disabled";
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometr = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_radar, container, false);
        tv = (TextView) v.findViewById(R.id.fragment_radar_tv);
        tvDistance = (TextView) v.findViewById(R.id.fragment_radar_tvDistance);
        imageArrow = (ImageView) v.findViewById(R.id.fragment_radar_iv);
        return v;
    }
    private void updateUI(String s) {
        tvDistance.setText("Distance: " + s + " m.");
    }



    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }
    private SensorListener mSensorListener = new SensorListener();
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private class SensorListener implements SensorEventListener {
        private float[] mGravity;
        private float[] mGeomagnetic;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mLastLocation == null || mFirstLocation == null) {
                return;
            }

            GeomagneticField geoField = new GeomagneticField((float)mLastLocation.getLatitude(),
                    (float)mLastLocation.getLongitude(), (float)mLastLocation.getAltitude()
                    , mLastLocation.getTime());
            float azimuth = Math.round(event.values[0]);
            azimuth += geoField.getDeclination();
            float bearing = mLastLocation.bearingTo(mFirstLocation);
            float direction = azimuth - bearing;
            //tv.setText("Heading: " + Float.toString(direction) + " degrees");
            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(currentDegree, -direction, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            // how long the animation will take place
            ra.setDuration(210);
            // set the animation after the end of the reservation status
            ra.setFillAfter(true);
            // start the animation
            imageArrow.startAnimation(ra);
            currentDegree = -direction;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "RadarFragment onAccuracyChanged");
        }
    }
}
