package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

public class LoginSignUp extends AppCompatActivity{

    //UI elements
    Button buttonBrowse;
    Button buttonSignIn;
    TextView textSignUp;
    String oneSignalID;
    //Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Log entries tag for debugging
    private static final String TAG = "LoginSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        Utils.getDatabase();
        setTheme(R.style.AppTheme);

        //Initialize UI elements
        buttonBrowse = (Button)findViewById(R.id.buttonBrowse);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        //buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        textSignUp = (TextView)findViewById(R.id.textSignUp);
        textSignUp.setPaintFlags(textSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //When the user taps the BROWSE button, he is taken to the main screen
        //Define that the user is NOT  logged in when he clicks the BROWSE button
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSignUp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //When the user taps the SIGN IN button, he is taken to the sign in activity
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSignUp.this, LogInActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        //When the user taps the Create account text at the bottom, the sign up activity is initialized
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSignUp.this, CreateAccountActivity.class));
            }
        });

        //Checks whether the user is already signed in
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    oneSignal(user);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    /*public void postInfo() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String method = "register";
        String user_id = user.getUid();
        String user_token = oneSignalID;
        Log.d(user_id,user_token);
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,user_id,user_token);


    }*/
    public void oneSignal(FirebaseUser user){
        OneSignal.startInit(this);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                oneSignalID = userId;
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
            }
        });
        // User is signed in
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        final DatabaseReference oneSignalIDRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("oneSignalID");
        if(oneSignalID != null) {
            oneSignalIDRef.setValue(oneSignalID);

        }
        Intent intent = new Intent(LoginSignUp.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //These two methods checks whether the user is already signed in
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
