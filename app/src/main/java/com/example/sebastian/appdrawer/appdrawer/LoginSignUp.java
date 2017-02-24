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
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class LoginSignUp extends AppCompatActivity{

    //UI elements
    Button buttonBrowse;
    Button buttonSignIn;
    TextView textSignUp;
    String oneSignalID;
    String dbOneSignalID;
    //Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Log entries tag for debugging
    private static final String TAG = "LoginSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_sign_up);
        setTheme(R.style.AppTheme);
        Utils.getDatabase();



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
        OneSignal.init(this,"p3-eatup","d243bbe7-8946-41ca-86e2-05110261c1f8");
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                if(userId!=null){
                    oneSignalID = userId;
                }

                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);

            }
        });
        Log.i("ErViHer?", "oneSignal: "+oneSignalID);

        final DatabaseReference oneSignalIDRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("oneSignalID");

        oneSignalIDRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbOneSignalID = dataSnapshot.getValue().toString();

                Log.d("signalIDDD", dbOneSignalID);
                Log.d("signalIDDD", "ID ="+oneSignalID);
                if(!oneSignalID.equals(dbOneSignalID)) {
                    FirebaseAuth.getInstance().signOut();
                } else{
                    Log.d("signalMatch", "it's a match");
                    startActivity(new Intent(LoginSignUp.this,MainActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        // User is signed in
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());



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


    public boolean compareIDs(String phoneID, String dbID){
        if (phoneID.equals(dbID)) {
            return true;
        } else {
            return false;
        }
    }

}
