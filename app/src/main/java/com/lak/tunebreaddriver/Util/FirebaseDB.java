package com.lak.tunebreaddriver.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.lak.tunebreaddriver.MapsActivity;
//import com.example.userapp.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDB {
    public DatabaseReference mDatabase;
    Location locationObj;
    RealLocation realLocation;
    Alert alert;

    public FirebaseDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        alert = new Alert();
        realLocation = new RealLocation();
    }

    public Location getRegisteredLocation(String mobile) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("location").child("0766414584").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("DB", dataSnapshot.getValue().toString());
                    locationObj = dataSnapshot.getValue(Location.class);
                } else {
                    Log.i("DB", "Location Doesn't Available");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return locationObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createAlert(String mobile, String vehicleNumber, String vehicleType) {
        mDatabase.child("users").child(mobile).child("authorize").setValue(false);
        alert = new Alert(mobile, vehicleNumber, vehicleType);
        mDatabase.child("alerts").child(alert.AlertID).setValue(alert).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "ALERT DATA ADDED");
                } else
                    Log.i("FIREBASE", "FAILED TO ADD USER");
            }
        });
    }

    public void createRealtimeLocation(LatLng position, String title, String user) {

        realLocation = new RealLocation(position, title);
        mDatabase.child("realLocation").child(user).setValue(realLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "LOCATION DATA ADDED");
                } else
                    Log.i("FIREBASE", "FAILED TO ADD LOCATION");
            }
        });
    }
}
