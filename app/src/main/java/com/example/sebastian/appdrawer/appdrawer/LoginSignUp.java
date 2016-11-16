package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;

import org.w3c.dom.Text;

public class LoginSignUp extends AppCompatActivity {

    Button buttonBrowse;
    Button buttonSignIn;
    //Button buttonSignUp;
    TextView textSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        setTheme(R.style.AppTheme);

        buttonBrowse = (Button)findViewById(R.id.buttonBrowse);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        //buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        textSignUp = (TextView)findViewById(R.id.textSignUp);

        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSignUp.this, MainActivity.class));
            }
        });

        /*
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginSignUp.this, MainActivity.class));
            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSignUp.this, CreateAccountActivity.class));
            }
        });
        */

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSignUp.this, MainActivity.class));
            }
        });

    }

}
