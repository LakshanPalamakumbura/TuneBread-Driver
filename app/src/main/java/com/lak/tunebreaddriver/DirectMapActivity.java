package com.lak.tunebreaddriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lak.tunebreaddriver.Util.AppConfig;
//import com.example.userapp.Util.AppConfig;
//import com.lak.tunebreaddriver.Util.DirectLocation;
//import com.example.userapp.Util.DirectLocation;
//import com.lak.tunebreaddriver.Util.FetchURL;
//import com.example.userapp.Util.FetchURL;
import com.lak.tunebreaddriver.Util.FirebaseDB;
//import com.example.userapp.Util.FirebaseDB;
import com.lak.tunebreaddriver.Util.Location;
//import com.example.userapp.Util.Location;
import com.lak.tunebreaddriver.Util.TaskLoadedCallback;
//import com.example.userapp.Util.TaskLoadedCallback;
import com.lak.tunebreaddriver.Util.User;
//import com.example.userapp.Util.User;
import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class DirectMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "DirectMapsActivity";
    private GoogleMap mMap;
    FloatingToolbar floatingToolbar;
    FloatingActionButton fab;
//    DirectLocation locationObj;
    public DatabaseReference mDatabase;
    AppConfig appConfig;
    private Polyline currentPolyline;
    User user;
    public MarkerOptions place1, place2;
    ImageView imgEnableTracking;
    Marker userLocationMarker;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    FirebaseDB firebaseDB;
    Boolean isTrackingEnabled = false;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_map);
        firebaseDB = new FirebaseDB();
        appConfig = new AppConfig(this);
        if (!appConfig.isUserLoggedIn()) {
            DirectMapActivity.this.startActivity(new Intent(DirectMapActivity.this, OTPLogin.class));
            ((Activity) DirectMapActivity.this).finish();
            return;
        }else{
            if(1==appConfig.getMapType()){
                DirectMapActivity.this.startActivity(new Intent(DirectMapActivity.this, MapsActivity.class));
                ((Activity) DirectMapActivity.this).finish();
                return;
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        floatingToolbar = findViewById(R.id.floatingToolbar);
        fab = findViewById(R.id.fab);
        floatingToolbar.setMenu(R.menu.main);
        floatingToolbar.attachFab(fab);

        imgEnableTracking = findViewById(R.id.imgEnableTracking);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //get User Details
        mDatabase.child("users").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("DBU", dataSnapshot.getValue().toString());
                    user = dataSnapshot.getValue(User.class);
                    appConfig.setLoggedVehicle(user.vehicleNumber);
                    appConfig.setLoggedVehicleType(user.vehicleType);
                    if(user.authorize)
                        appConfig.setUserAuthorize();
                    else appConfig.setUserUnAuthorize();
                } else Log.i("DB", "Details Doesn't Available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setListeners();
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                // your code here...
                CheckAuthorize();
                //
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 1000*1*15);
    }
    private void CheckAuthorize() {
        if(!appConfig.getUserAuthorize())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                firebaseDB.createAlert(appConfig.getLogedUserID(), appConfig.getLoggedVehicle(), appConfig.getLoggedVehicleType());
            }
        else{
                mDatabase.child("users").child(appConfig.getLogedUserID()).child("authorize").setValue(true);
            }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mDatabase.child("directLocation").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
//            //  MarkerOptions markerOption = new MarkerOptions();
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //mMap.clear();
//                if (dataSnapshot.getValue() != null) {
//                    Log.d("DB", dataSnapshot.getValue().toString());
//                    locationObj = dataSnapshot.getValue(DirectLocation.class);
//                    place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(locationObj.startlatitude), Double.parseDouble(locationObj.startlongitude)));
//                    LatLng latLng = new LatLng(Double.parseDouble(locationObj.startlatitude), Double.parseDouble(locationObj.startlongitude));
////                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//                    place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(locationObj.endlatitude), Double.parseDouble(locationObj.endlongitude)));
//                    new FetchURL(DirectMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//                    mMap.addMarker(place1);
//                    mMap.addMarker(place2);
//                } else Log.i("DB", "Location Doesn't Available");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        imgEnableTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackingEnabled) {
                    isTrackingEnabled = false;
                    imgEnableTracking.setImageDrawable(getDrawable(R.drawable.tracking_off));
                } else {
                    isTrackingEnabled = true;
                    imgEnableTracking.setImageDrawable(getDrawable(R.drawable.tracking_on));
                }
            }
        });
        enableUserLocation();

    }

    private void setListeners() {
        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_my_qr) {
                    DirectMapActivity.this.startActivity(new Intent(DirectMapActivity.this, GenerateQRCode.class));
                    return;
                }
                if (item.getItemId() == R.id.action_my_account) {
                    DirectMapActivity.this.startActivity(new Intent(DirectMapActivity.this, AccountSetting.class));

                    return;
                }
            }

            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private void enableUserLocation() {
        //Ask for permission
        //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        Log.d("Poly", "PolyLine" + values[0]);

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            checkLocation(locationResult);
            LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            firebaseDB.createRealtimeLocation(latLng, appConfig.getLoggedVehicle(),appConfig.getLogedUserID());
            if (mMap != null) setUserLocationMarker(locationResult.getLastLocation());
        }
    };
private void checkLocation(LocationResult locationResult){
    try{
        String[] arrlng = appConfig.getlng().split(",");
        String[] arrlat = appConfig.getlat().split(",");

        Log.d("CheckLang", "-------------------------------");
        Boolean check = false;
        LatLng latLnge = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
        for (int i = 0; i < arrlng.length; i++) {
            LatLng latLngs = new LatLng(Double.parseDouble(arrlat[i]), Double.parseDouble(arrlng[i]));
            double x = CalculationByDistance(latLngs, latLnge);
            if (x <= 1) {
                check = true;
                Log.d("UserLocationC", "" + x);
            } else Log.d("UserLocation", "" + x);
        }
        if (check) {
            appConfig.setUserAuthorize();
            Log.d("UserLocation", "Authorize");
        } else {
            appConfig.setUserUnAuthorize();
            Log.d("UserLocation", "UnAuthorize");
        }

    }catch (NullPointerException e){
        Log.d("Error", "Still Loading...!");
    }
}
    private void setUserLocationMarker(android.location.Location lastLocation) {
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        if(appConfig.getUserAuthorize()) if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.greencar));
            markerOptions.rotation(lastLocation.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
          //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(lastLocation.getBearing());
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        else if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));
            markerOptions.rotation(lastLocation.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(lastLocation.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }
    private void startLocationUpdates() {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            startLocationUpdates();
        else {
            // you need to request permissions...
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d("Radius", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meter);

        return meter;
    }
}