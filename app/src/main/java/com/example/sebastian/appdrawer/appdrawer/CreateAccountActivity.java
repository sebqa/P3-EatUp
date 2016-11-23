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
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/*****************************

 AUTHOR: Zanduz
 DATE: 14 NOV 2016
 DESCRIPTION:

 Enables the user to create an account.
 Inserts the user account data into Firebase.

 ****************************/

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity"; //Identifier for log entries
    private FirebaseAuth mAuth; //Variable to be used as reference to the authentication instance of Firebase
    private FirebaseAuth.AuthStateListener mAuthListener; //Variable to be used as authentication state listener (tracks login/logout status)

    //Define UI elements

    EditText editTextPassword,editTextEmail,edFirstName,edLastName;
    Button buttonSignup;
    //String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Initialize UI elements
        editTextEmail = (EditText)findViewById(R.id.editEmail);
        editTextPassword = (EditText)findViewById(R.id.editPassword);
        buttonSignup = (Button)findViewById(R.id.buttonSignup);
        edFirstName = (EditText)findViewById(R.id.edFirstName);
        edLastName = (EditText)findViewById(R.id.edLastName);

        //Call the createAccount method when the user clicks the sign up button
        //Pass the entered email and password to the method
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });

        mAuth = FirebaseAuth.getInstance(); //Create reference to the authentication instance

        //Initialize the authentication state listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                    Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //Specifies that the authentication state listener has to be tied to the instance of our Firebase connection
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //If there has been created an authentication listener, then remove it
    //A new one will be created in onStart() when the activity is created again
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email); //Create log entry
        //userEmail = editTextEmail.getText().toString();
        //If the email field or password field are empty, then the account will not be created
        if (!validateForm()) {
            return; //return terminates execution of the rest of the method
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this, "Account creation successful",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateAccountActivity.this,MainActivity.class));
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this, "Account creation failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    //Checks that the email and pw input fields are not empty
    private boolean validateForm() {
        boolean valid = true;

        String email = editTextEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Required");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Required");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }

}
