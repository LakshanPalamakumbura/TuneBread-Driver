package com.lak.tunebreaddriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lak.tunebreaddriver.Util.AppConfig;
//import com.example.userapp.Util.AppConfig;
import com.lak.tunebreaddriver.Util.User;
//import com.example.userapp.Util.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountSetting extends AppCompatActivity {
    TextView txtEmail;
    TextView txtNIC;
    TextView txtMobile;
    TextView txtVehicleType;
    TextView txtVehicleNumber;
    TextView txtName;
    TextView txtTop;
    TextView txtAfter;
    public DatabaseReference mDatabase;
    User user;
Button btnLogOut;
    AppConfig appConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account_setting);
        txtName = (TextView)findViewById(R.id.txtChangeName);
        txtEmail = (TextView)findViewById(R.id.txtChangeEmail);
        txtNIC = (TextView)findViewById(R.id.txtNIC);
        txtMobile = (TextView)findViewById(R.id.txtMobileNumber);
        txtVehicleType = (TextView)findViewById(R.id.txtVehicleType);
        txtVehicleNumber = (TextView)findViewById(R.id.txtVehicleNumber);
        txtTop = (TextView)findViewById(R.id.txt_top);
        txtAfter = (TextView)findViewById(R.id.txt_topafter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        appConfig = new AppConfig(this);
        btnLogOut = (Button)findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSetting.this.startActivity(new Intent(AccountSetting.this, OTPLogin.class));
                ((Activity) AccountSetting.this).finish();
                appConfig.setUserLoggedOut();
            }
        });
        mDatabase.child("users").child(appConfig.getLogedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("DBU", dataSnapshot.getValue().toString());
                        user = dataSnapshot.getValue(User.class);
                        txtTop.setText(user.fname + " " + user.lname);
                        txtAfter.setText(""+user.email);
                        txtName.setText(user.fname + " " + user.lname);
                        txtEmail.setText(""+user.email);
                        txtMobile.setText(appConfig.getLogedUserID());
                        txtVehicleType.setText(""+user.vehicleType);
                        txtVehicleNumber.setText(""+user.vehicleNumber);
                        txtNIC.setText(user.NIC);
                    } else Log.i("DB", "Details Doesn't Available");
                }catch (Exception ex){
                    Log.i("DB", "Details Doesn't Available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ImageView imgback = (ImageView)findViewById(R.id.img_back);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}