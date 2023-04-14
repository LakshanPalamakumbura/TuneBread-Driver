package com.lak.tunebreaddriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.lak.tunebreaddriver.Util.AppConfig;
//import com.example.userapp.Util.AppConfig;
//import com.lak.tunebreaddriver.Util.DirectLocation;
//import com.example.userapp.Util.DirectLocation;
//import com.lak.tunebreaddriver.Util.FetchURL;
//import com.example.userapp.Util.FetchURL;
import com.lak.tunebreaddriver.Util.Validator;
//import com.example.userapp.Util.Validator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_login extends AppCompatActivity {
    ImageView imgViewBgLogin;
    ImageView imgLogo;
    LinearLayout layoutMiddle;
    LinearLayout layoutTop;
    LinearLayout viewCenterBottom;
    Animation bgAnim, centerLayoutAnim, fromBottom;
    Display display;
    RelativeLayout viewCenter;
    RelativeLayout viewBottom;

    EditText txtPhoneNo;
    EditText txtPassword;
    TextView txtForgotPassword;
    Button btnSignIn;
    TextView txtSignUp;

    Vibrator vibrate;
    Animation shakeEditText;
    AppConfig appConfig;
    public MarkerOptions place1, place2;
//    DirectLocation locationObj;
    public DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      //  this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // getSupportActionBar().hide();
        centerLayoutAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_enter);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_up_enter);

        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtPassword = findViewById(R.id.txtPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);


        imgViewBgLogin = findViewById(R.id.imgViewBgLogin);
        imgLogo = findViewById(R.id.imgLogo);
        display = getWindowManager().getDefaultDisplay();
        layoutMiddle = findViewById(R.id.layoutMiddle);
        layoutTop = findViewById(R.id.layoutTop);
        viewCenter = findViewById(R.id.viewCenter);
        viewBottom = findViewById(R.id.viewBottom);
        viewCenterBottom = findViewById(R.id.viewCenterBottom);
        appConfig = new AppConfig(this);
        bgAnim = AnimationUtils.loadAnimation(this, R.anim.bg_welcome_anim);

        imgViewBgLogin.animate().translationY(-display.getHeight()).setDuration(800).setStartDelay(1500);
        imgLogo.animate().alpha(0).setDuration(800).setStartDelay(1600);
        layoutMiddle.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(1600);
        layoutTop.startAnimation(bgAnim);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCenter.startAnimation(centerLayoutAnim);
                btnSignIn.startAnimation(fromBottom);
                viewCenterBottom.startAnimation(fromBottom);
                viewCenter.setVisibility(View.VISIBLE);
                viewBottom.setVisibility(View.VISIBLE);
            }
        }, 1800);
/*
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this, activity_reset_password.class));
                Bungee.zoom(activity_login.this);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this, activity_signup.class));
                Bungee.zoom(activity_login.this);
            }
        });*/
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validator.validatePhone(txtPhoneNo.getText().toString())) {
                   // showAlertDialog("Please enter a valid Mobile No").show();
                    initEditTextErrorAnimation(txtPhoneNo);
                    return;
                }
                if (txtPassword.getText().toString().length() < 6) {
                    //showAlertDialog("Please enter a valid Password").show();
                    initEditTextErrorAnimation(txtPassword);
                    return;
                }
                if(txtPhoneNo.getText().toString().equals("0766414584") && txtPassword.getText().toString().equals("Test@123"))
                {
                    activity_login.this.startActivity(new Intent(activity_login.this, MapsActivity.class));
                    ((Activity) activity_login.this).finish();
                    appConfig.setUserLoggedIn();
                    appConfig.setLoggedUserID(txtPhoneNo.getText().toString());
                }
//                if(txtPhoneNo.getText().toString().equals("0712321122") && txtPassword.getText().toString().equals("Test@123"))
//                {
//                    activity_login.this.startActivity(new Intent(activity_login.this, DirectMapActivity.class));
//                    ((Activity) activity_login.this).finish();
//                    appConfig.setUserLoggedIn();
//                    appConfig.setLoggedUserID(txtPhoneNo.getText().toString());
//                }



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
    private void initEditTextErrorAnimation(EditText editText) {
        shakeEditText = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);
        vibrate.vibrate(20);
        editText.startAnimation(shakeEditText);
    }

    private Flashbar showAlertDialog(String message) {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(1000)
                .message(message)
                .messageColor(ContextCompat.getColor(this, R.color.white))
                .backgroundColor(ContextCompat.getColor(this, R.color.errorMessageBackgroundColor))
                .showIcon()
                .iconColorFilterRes(R.color.errorMessageIconColor)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(600)
                        .slideFromLeft()
                        .accelerate())
                .build();
    }
}