package com.example.sebastian.eatup;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.sebastian.appdrawer.R;
import com.squareup.picasso.Picasso;


public class ImageDialog extends Activity {

    private ImageView mDialog;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dialog);


        url = getIntent().getStringExtra("image");

        mDialog = (ImageView)findViewById(R.id.your_image);
        mDialog.setClickable(true);


        Picasso.with(this)
                .load(url)
                .error(R.drawable.placeholder)
                .into(mDialog);


        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
