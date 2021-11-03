package com.example.geofen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.geofen.databinding.ActivityMainBinding;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.provider.Settings.System.getString;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG ="..Read" ;
    String  info ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: -----");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.i(TAG, "onReceive: geofenceTransition  " +geofenceTransition);

        // Get the geofences that were triggered. A single event can trigger
        // multiple geofences.
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        Log.i(TAG, "onReceive : trigerGeofence :" + triggeringGeofences.size());

        Location location =geofencingEvent.getTriggeringLocation();
        double lat =  location.getLatitude();
        double lon =  location.getLongitude();

        // Test that the reported transition was of interest.
            Log.i(TAG, "onReceive: If");
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                info ="GEOFENCE_TRANSITION_ENTER ( Device Entered in the zone)";
                Toast.makeText(context.getApplicationContext(), info , Toast.LENGTH_LONG).show();
                Log.i(TAG, "GEOFENCE_TRANSITION_ENTER : " + geofenceTransition + "  " + triggeringGeofences);
                sendTransition(context,info,lat,lon);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                info="GEOFENCE_TRANSITION_DWELL (Device within the zone) ";
                Toast.makeText(context.getApplicationContext(),info , Toast.LENGTH_LONG).show();
                Log.i(TAG, "GEOFENCE_TRANSITION_EXIT : " + geofenceTransition + "  " + triggeringGeofences);
                sendTransition(context,info,lat,lon);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                info ="GEOFENCE_TRANSITION_EXIT (Device out of the zone) ";
                Toast.makeText(context.getApplicationContext(), "GEOFENCE_TRANSITION_EXIT (Device out of the zone) ", Toast.LENGTH_LONG).show();
                Log.i(TAG, "GEOFENCE_TRANSITION_EXIT : " + geofenceTransition + "  " + triggeringGeofences);
                sendTransition(context,info,lat,lon);
            default:
                // Log the error.
                Log.e(TAG, "Transition Type " +
                        geofenceTransition);

                break;
        }
    }

    void sendTransition(Context c ,String s,double lat,double lon)
    {
        Intent i = new Intent("GeofenceBroadcastReceiver");
        i.putExtra("sendData", s);
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        c.sendBroadcast(i);
    }
}