package com.example.sebastian.eatup;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.Arrays;

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
    int RC_SIGN_IN = 9999;

    //Log entries tag for debugging
    private static final String TAG = "LoginSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread splashThread = new Thread()
        {
            @Override
            public void run() {
                Utils.getDatabase();
                mAuth = FirebaseAuth.getInstance();
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Log.i("CHECK USER", "Før");
                            OneSignal.init(LoginSignUp.this,"p3-eatup","d243bbe7-8946-41ca-86e2-05110261c1f8");

                            oneSignal(user);


                        } else {
                            // User is signed out
                            Log.i("CHECK USER", "IKKE LOGGET IND");

                            uiElements();
                            Log.d(TAG, "onAuthStateChanged:signed_out");

                        }
                    }

                };

            }
        };
        splashThread.start();
        super.onCreate(savedInstanceState);



        //Checks whether the user is already signed in


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

        final DatabaseReference oneSignalIDRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        oneSignalIDRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*dbOneSignalID = dataSnapshot.child("oneSignalID").getValue().toString();

                Log.d("signalIDDD", dbOneSignalID);
                Log.d("signalIDDD", "ID ="+oneSignalID);*/
                if(false/*!oneSignalID.equals(dbOneSignalID)*/) {
                    FirebaseAuth.getInstance().signOut();
                    OneSignal.setSubscription(false);
                    AuthUI.getInstance()
                            .signOut(LoginSignUp.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    //startActivity(new Intent(MainActivity.this, LoginSignUp.class));

                                }
                            });
                } else{
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(LoginSignUp.this,MainActivity.class));
                            finish();
                            Toast.makeText(LoginSignUp.this, "Logged in!", Toast.LENGTH_LONG).show();
                        }

                    },0);Log.i("CHECK USER", "EFTER");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        // User is signed in
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());



    }

    public void uiElements(){
        Log.i("UIELEMENTS", "Før");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_login_sign_up);

                //Initialize UI elements
                buttonBrowse = (Button)findViewById(R.id.buttonBrowse);
                buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
                //buttonSignUp = (Button)findViewById(R.id.buttonSignUp);


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
                        /*Intent intent = new Intent(LoginSignUp.this, LogInActivity.class);
                        startActivity(intent);*/
                        //finish();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                        .setTheme(R.style.AppTheme)
                                        .build(),
                                RC_SIGN_IN);
                    }

                });

                //When the user taps the Create account text at the bottom, the sign up activity is initialized


            }
        });

    }
    //These two methods checks whether the user is already signed in
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // slow code goes here.
                 mAuth.addAuthStateListener(mAuthListener);

            }
        }).start();

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
