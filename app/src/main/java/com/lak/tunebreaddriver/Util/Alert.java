package com.lak.tunebreaddriver.Util;
//package com.example.userapp.Util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class Alert {
    public String AlertID;
    public String Date;
    public String UserMob;
    public String VehicleType;
    public String VehicleNumber;
    Random random;
    public DatabaseReference mDatabase;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Alert(String userMob, String vehicleNumber, String vehicleType) {
        random = new Random();
        AlertID = Integer.toString(random.nextInt(10000000));
        Date = "Date : "+LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonth()+"/"+LocalDateTime.now().getDayOfMonth()+"  Time : "+LocalDateTime.now().getHour()+":"+ LocalDateTime.now().getMinute();
        UserMob = userMob;
        VehicleNumber = vehicleNumber;
        VehicleType = vehicleType;
    }
    public Alert() {

    }


}
