package com.lak.tunebreaddriver;
//package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lak.tunebreaddriver.Util.AppConfig;
//import com.example.userapp.Util.AppConfig;
import com.lak.tunebreaddriver.Util.FirebaseDB;
//import com.example.userapp.Util.FirebaseDB;
import com.lak.tunebreaddriver.Util.User;
//import com.example.userapp.Util.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
public class OTPLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mCountryCode;
    private EditText mPhoneNumber;
    AppConfig appConfig;
    private Button mGenerateBtn;
    private ProgressBar mLoginProgress;

    private TextView mLoginFeedbackText;
    User user;
    public DatabaseReference mDatabase;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
      //  mCountryCode = findViewById(R.id.country_code_text);
        mPhoneNumber = findViewById(R.id.phone_number_text);
        mGenerateBtn = findViewById(R.id.generate_btn);
        mLoginProgress = findViewById(R.id.login_progress_bar);
        mLoginFeedbackText = findViewById(R.id.login_form_feedback);
        appConfig = new AppConfig(this);
        mGenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    String country_code = mCountryCode.getText().toString();
                String phone_number = mPhoneNumber.getText().toString();

                String complete_phone_number = "+94" + phone_number;
                if(phone_number.isEmpty()){
                    mLoginFeedbackText.setText("Please fill in the form to continue.");
                    mLoginFeedbackText.setVisibility(View.VISIBLE);
                } else {
                    mDatabase.child("users").child("0" + phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Log.d("DBU", dataSnapshot.getValue().toString());
                                user = dataSnapshot.getValue(User.class);
                                appConfig.setLoggedVehicle(user.vehicleNumber);
                                appConfig.setLoggedVehicleType(user.vehicleType);
                                appConfig.setMapType(user.mapType);
                                sendVerificationCode(complete_phone_number);
                                appConfig.setUserLoggedIn();
                                appConfig.setLoggedUserID("0"+phone_number);
                            } else {
                                mLoginFeedbackText.setText("Please fill registered phone number.");
                                mLoginFeedbackText.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //login page authoris da?
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mLoginFeedbackText.setText("Verification Failed, please try again.");
                Log.d("Issue",""+e);
                mLoginFeedbackText.setVisibility(View.VISIBLE);
                mLoginProgress.setVisibility(View.INVISIBLE);
                mGenerateBtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(OTPLogin.this, OTPActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);
            }
        };
    }
    private void sendVerificationCode(String complete_phone_number)
    {
        mLoginProgress.setVisibility(View.VISIBLE);
        mGenerateBtn.setEnabled(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                complete_phone_number,
                60,
                TimeUnit.SECONDS,
                OTPLogin.this,
                mCallbacks
        );
    }

    //plese chek below code wanted
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPLogin.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mLoginFeedbackText.setVisibility(View.VISIBLE);
                                mLoginFeedbackText.setText("There was an error verifying OTP");
                            }
                        }
                        mLoginProgress.setVisibility(View.INVISIBLE);
                        mGenerateBtn.setEnabled(true);
                    }
                });
    }
    private void sendUserToHome() {
        if(1==appConfig.getMapType()) {
            appConfig.setUserLoggedIn();
            Intent homeIntent = new Intent(OTPLogin.this, MapsActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
//        else if(2==appConfig.getMapType()){
//            appConfig.setUserLoggedIn();
//            Intent homeIntent = new Intent(OTPLogin.this, DirectMapActivity.class);
//            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(homeIntent);
//            finish();
//        }
        else {

        }
    }
}