package com.lak.tunebreaddriver;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lak.tunebreaddriver.Util.AppConfig;
//import com.example.userapp.Util.AppConfig;
import com.lak.tunebreaddriver.Util.FirebaseDB;
//import com.example.userapp.Util.FirebaseDB;
import com.lak.tunebreaddriver.Util.GeofenceHelper;
//import com.example.userapp.Util.GeofenceHelper;
import com.lak.tunebreaddriver.Util.Location;
//import com.example.userapp.Util.Location;
import com.lak.tunebreaddriver.Util.TaskLoadedCallback;
//import com.example.userapp.Util.TaskLoadedCallback;
import com.lak.tunebreaddriver.Util.User;
//import com.example.userapp.Util.User;
import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, TaskLoadedCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    FloatingToolbar floatingToolbar;
    FloatingActionButton fab;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imgEnableTracking;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    Marker userLocationMarker;
    User user;
    FirebaseDB firebaseDB;
    LocationRequest locationRequest;
    int count;
    Boolean isTrackingEnabled = false;
    //DB

    Location locationObj;
    public DatabaseReference mDatabase;
    AppConfig appConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        appConfig = new AppConfig(this);
        if(!appConfig.isUserLoggedIn())
        {
            MapsActivity.this.startActivity(new Intent(MapsActivity.this, activity_login.class));
            ((Activity) MapsActivity.this).finish();
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        imgEnableTracking = findViewById(R.id.imgEnableTracking);
        //db
        mDatabase = FirebaseDatabase.getInstance().getReference();
        floatingToolbar = findViewById(R.id.floatingToolbar);
        fab = findViewById(R.id.fab);
        floatingToolbar.setMenu(R.menu.main);
        floatingToolbar.attachFab(fab);
        firebaseDB = new FirebaseDB();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        userLocationMarker = null;
        count = 0;
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
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

//        // Add a marker in Sydney and move the camera
//        LatLng eiffel = new LatLng(48.8589, 2.29365);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);

////        mDatabase = FirebaseDatabase.getInstance().getReference().child("location").child(appConfig.getLogedUserID());
//        // meny add chiled below
//        mDatabase.child("location").addListenerForSingleValueEvent(new ValueEventListener() {
//
////        mDatabase.child("location").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    Log.d("DB", dataSnapshot.getValue().toString());
//                    locationObj = dataSnapshot.getValue(Location.class);
//                    LatLng latLng = new LatLng(Double.parseDouble(locationObj.latitude),Double.parseDouble(locationObj.longitude));
//                    handleMapLongClick(latLng);
////                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
////                    LatLng latLng = new LatLng(Double.parseDouble(locationObj.latitude),Double.parseDouble(locationObj.longitude));
//////                        LatLng latLng = new LatLng(ds.child("latitude").getValue(Double.class), ds.child("longitude").getValue(Double.class));
////                        locationObj = dataSnapshot.getValue(Location.class);
////                          handleMapLongClick(latLng);
////                    }
//                } else {
//                    Log.i("DB", "Location Doesn't Available");
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        mDatabase.child("location").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("DB", dataSnapshot.getValue().toString());
                    locationObj = dataSnapshot.getValue(Location.class);
                    LatLng latLng = new LatLng(Double.parseDouble(locationObj.latitude),Double.parseDouble(locationObj.longitude));
                    handleMapLongClick(latLng);
                } else {
                    Log.i("DB", "Location Doesn't Available");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.child("users").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("DBU", dataSnapshot.getValue().toString());
                    user = dataSnapshot.getValue(User.class);
                    appConfig.setLoggedVehicle(user.vehicleNumber);
                    appConfig.setLoggedVehicleType(user.vehicleType);
                } else {
                    Log.i("DB", "Location Doesn't Available");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setListeners();
    }
    private void setListeners() {
        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_my_qr) {
                    MapsActivity.this.startActivity(new Intent(MapsActivity.this, GenerateQRCode.class));
                    return;
                }
                if (item.getItemId() == R.id.action_my_account) {
                    MapsActivity.this.startActivity(new Intent(MapsActivity.this, AccountSetting.class));

                    return;
                }
            }

            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     //
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
             //   handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            //handleMapLongClick(latLng);
        }

    }

    private void handleMapLongClick(LatLng latLng) {
        Log.d("myLog", "Called...!");
        mMap.clear();
            addMarker(latLng);
            addCircle(latLng, GEOFENCE_RADIUS);
            addGeofence(latLng, GEOFENCE_RADIUS);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

    }

    private void addGeofence(LatLng latLng, float radius) {
        Log.d("myLog", "Adding......!"+GEOFENCE_ID);
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"Geo Fence Added..!");
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG,errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.customer));
        markerOptions.title(appConfig.getLoggedVehicle());
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onTaskDone(Object... values) {

    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            try{
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
            firebaseDB.createRealtimeLocation(latLng, appConfig.getLoggedVehicle(),appConfig.getLogedUserID());
            }catch (Exception ex){
                Log.d("Error",""+ex);
            }
            if (mMap != null) setUserLocationMarker(locationResult.getLastLocation());
        }
    };
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
    private void setUserLocationMarker(android.location.Location lastLocation) {
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        count++;
       if (count<3) {
            //Create a new marker
           Log.d("Called","InsidesetUserLocationMarker"+userLocationMarker);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latLng);
            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.greencar));
            markerOptions1.rotation(lastLocation.getBearing());
            markerOptions1.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions1);
//              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(lastLocation.getBearing());
//             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//             mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
}