package com.gmail.yurapapapa.anylocator2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Created by yurap_000 on 004 04 сен.
 */
public class MainFragment extends Fragment{

    private static final String TAG = "myLogs";
    private RunManager mRunManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ImageButton start = (ImageButton) v.findViewById(R.id.fragment_main_ibShowArrow);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.startLocationUpdates();
                Intent i = new Intent(getActivity(), RadarActivity.class);
                startActivity(i);
            }
        });
        ImageButton stop = (ImageButton) v.findViewById(R.id.fragment_main_ibStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopLocationUpdates();
            }
        });
        return v;
    }



    private class TrackingStarter extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Wait please")
                    .setTitle("Getting locaton from GPS")
                    .setView(new ProgressBar(getActivity()));
            builder.create().show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
