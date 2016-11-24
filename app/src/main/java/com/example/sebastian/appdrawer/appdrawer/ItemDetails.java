package com.example.sebastian.appdrawer.appdrawer;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ItemDetails extends AppCompatActivity {
    ImageView d_imageView;
    TextView txTitle,txPrice,txCreator, txDistance, txServingsLeft, txDescription;
    public static final String FOOD = "food";
    private DatabaseReference mFirebaseDatabaseReference;
    String itemKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        d_imageView = (ImageView)findViewById(R.id.imagePlaceholderDetails);
        txTitle = (TextView) findViewById(R.id.txTitleDetails);
        txCreator = (TextView) findViewById(R.id.txCreator);
        txPrice = (TextView) findViewById(R.id.txPrice);
        txDistance = (TextView) findViewById(R.id.txDistance);
        txServingsLeft = (TextView) findViewById(R.id.txServingsLeft);
        txDescription = (TextView) findViewById(R.id.txDescriptionDetails);

        /*Retrieve parsed information
        d_imageView.setImageResource(getIntent().getIntExtra("item_img",00));
        txTitle.setText(getIntent().getStringExtra("item_title"));
        txCreator.setText(getIntent().getStringExtra("item_creator"));
        txPrice.setText(getIntent().getStringExtra("item_price") + " DKK");
        txDistance.setText(getIntent().getStringExtra("item_distance"));
        */
        
        itemKey = getIntent().getStringExtra("item_key");

        Utils.getDatabase();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);

        mFirebaseDatabaseReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Item item = dataSnapshot.getValue(Item.class);
                txDescription.setText(item.getDescription());
                txTitle.setText(item.getTitle());
                txPrice.setText(item.getPrice());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
