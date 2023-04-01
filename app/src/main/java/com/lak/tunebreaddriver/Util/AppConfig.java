package com.lak.tunebreaddriver.Util;
//package com.example.userapp.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppConfig {

    private SharedPreferences sharedPreferences;
    Context context;
    static SharedPreferences.Editor editor;

    public AppConfig() {
    }

    public AppConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("USER_APP", Context.MODE_PRIVATE);
    }

    public void setAppIntroFinished() {
        editor = sharedPreferences.edit();
        editor.putBoolean("INTRO_FINISHED", true);
        editor.apply();
    }

    public boolean isAppIntroFinished() {
        return sharedPreferences.getBoolean("INTRO_FINISHED", false);
    }

    public void setUserLoggedIn() {
        editor = sharedPreferences.edit();
        editor.putBoolean("LOGGED_IN", true);
        editor.apply();
    }

    public void setUserLoggedOut() {
        editor = sharedPreferences.edit();
        editor.putBoolean("LOGGED_IN", false);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("LOGGED_IN", false);
    }

    public void setLoggedUserID(String userID) {
        editor = sharedPreferences.edit();
        editor.putString("LOGGED_USER_ID", userID);
        editor.apply();
    }
    public String getLogedUserID() {
        return sharedPreferences.getString("LOGGED_USER_ID", null);
    }
    public void setLoggedVehicle(String VehicleNumber) {
        editor = sharedPreferences.edit();
        editor.putString("LOGGED_VEHICLE_NUMBER", VehicleNumber);
        editor.apply();
    }
    public String getLoggedVehicle() {
        return sharedPreferences.getString("LOGGED_VEHICLE_NUMBER", null);
    }
    public void setLoggedVehicleType(String VehicleNumber) {
        editor = sharedPreferences.edit();
        editor.putString("LOGGED_VEHICLE_TYPE", VehicleNumber);
        editor.apply();
    }
    public String getLoggedVehicleType() {
        return sharedPreferences.getString("LOGGED_VEHICLE_TYPE", null);
    }
    public void setUserAuthorize() {
        editor = sharedPreferences.edit();
        editor.putBoolean("USER_AUTHORIZE", true);
        editor.apply();
    }
    public void setUserUnAuthorize() {
        editor = sharedPreferences.edit();
        editor.putBoolean("USER_AUTHORIZE", false);
        editor.apply();
    }
    public boolean getUserAuthorize(){return sharedPreferences.getBoolean("USER_AUTHORIZE",false);}
    public void setLng(String points) {
        editor = sharedPreferences.edit();
        editor.putString("LNG",  points);
        editor.apply();
    }
    public void setLat(String points) {
        editor = sharedPreferences.edit();
        editor.putString("LAT",  points);
        editor.apply();
    }
    public String getlat() {
        return sharedPreferences.getString("LAT", null);
    }
    public String getlng() {
        return sharedPreferences.getString("LNG", null);
    }
    public void setMapType(int type) {
        editor = sharedPreferences.edit();
        editor.putInt("MapType",  type);
        editor.apply();
    }
    public int getMapType() {
        return sharedPreferences.getInt("MapType", 0);
    }
}
