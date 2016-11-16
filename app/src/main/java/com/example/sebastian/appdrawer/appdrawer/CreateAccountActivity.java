package com.example.sebastian.appdrawer.appdrawer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.sebastian.appdrawer.R;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();

    }
}
