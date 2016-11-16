package com.example.sebastian.appdrawer.appdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sebastian.appdrawer.R;

public class CreateItem extends AppCompatActivity {

    EditText itemTag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        // Added tagsArea as LinearLayout view
       final View linearLayout =  findViewById(R.id.tagsArea);


        //
        itemTag = (EditText) findViewById(R.id.etTags);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Method for adding a new TextView when ENTER is pressed
        itemTag.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (actionId == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                     Toast.makeText(CreateItem.this, itemTag.getText(), Toast.LENGTH_LONG).show();
                    // Adding a TextView "tag" when ENTER is pressed
                    /* TextView tag = new TextView(CreateItem.this);
                    tag.setPadding(10, 10, 10, 10);
                    tag.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 20, 0, 0); */
                    return true;
                }
                return false;
            }
        });



    }
}
