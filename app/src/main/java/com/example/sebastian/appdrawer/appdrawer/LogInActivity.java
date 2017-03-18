package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import java.util.Arrays;

public class LogInActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button buttonSignIn;
    TextView tvCreateAccount;
    private static final String TAG = "LogInActivity";
    String oneSignalID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //Tracks user sign in/out
    int RC_SIGN_IN = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Utils.getDatabase();
        //Initialize UI elements
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        tvCreateAccount = (TextView) findViewById(R.id.tvCreateAccount);

        // Getting the firebase reference url
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(editEmail.getText().toString(), editPassword.getText().toString());
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, CreateAccountActivity.class));
            }
        });


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
                // ...
            }
        };

    }
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


    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(LogInActivity.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();
                            OneSignal.setSubscription(true);

                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LogInActivity.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Required.");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        String password = editPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Required.");
            valid = false;
        } else {
            editPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Back", "TEST");
    }
}
