package com.lak.tunebreaddriver.Util;
//package com.example.userapp.Util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.lak.tunebreaddriver.MapsActivity;
//import com.example.userapp.MapsActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";
    public DatabaseReference mDatabase;
    FirebaseDB firebaseDB = new FirebaseDB();
    AppConfig appConfig ;
    public GeofenceBroadcastReceiver() {
        appConfig = new AppConfig();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        appConfig = new AppConfig(context);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                Log.d("Testing",""+ appConfig.getLogedUserID());
                String mobile = appConfig.getLogedUserID();
                //   Toast.makeText(context, appConfig.getLogedUserID(), Toast.LENGTH_SHORT).show();
                mDatabase.child("users").child(mobile).child("authorize").setValue(true);
                appConfig.setUserAuthorize();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    firebaseDB.createAlert(appConfig.getLogedUserID(),appConfig.getLoggedVehicle(), appConfig.getLoggedVehicleType());
                    appConfig.setUserUnAuthorize();
                }
                break;
        }

    }
}