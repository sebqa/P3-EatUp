package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;


public class ItemDetails extends AppCompatActivity {
    ImageView d_imageView;
    TextView txTitle,txPrice,txCreator, txDistance, txServingsLeft, txDescription;
    public static final String FOOD = "food";
    private DatabaseReference mFirebaseDatabaseReference;
    String itemKey;
    String itemImageUrl;
    Button btnOrder;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
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
        btnOrder = (Button) findViewById(R.id.btnOrder);

        /*Retrieve parsed information
        d_imageView.setImageResource(getIntent().getIntExtra("item_img",00));
        txTitle.setText(getIntent().getStringExtra("item_title"));
        txCreator.setText(getIntent().getStringExtra("item_creator"));
        txPrice.setText(getIntent().getStringExtra("item_price") + " DKK");
        txDistance.setText(getIntent().getStringExtra("item_distance"));
        */

        itemKey = getIntent().getStringExtra("item_key");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        final int width = metrics.widthPixels;
        Utils.getDatabase();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    Toast.makeText(ItemDetails.this, "Please login or sign up before ordering an item",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ItemDetails.this, LogInActivity.class));
                }
                else if(user != null) {
                    DatabaseReference itemRequestedRef = rootRef.child("food").child(itemKey).child("itemRequests").push();
                    itemRequestedRef.setValue(user.getUid());

                    final DatabaseReference myRequests = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("sentRequests").push();
                    myRequests.child("requestedItem").setValue(itemKey);

                    DatabaseReference newRequestRef = myRequests.getRef();
                    newRequestRef.child("requestedAmount").setValue("1 serving");
                    newRequestRef.child("requestConfirmed").setValue("false");

                }

            }
        });



        mFirebaseDatabaseReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Item item = dataSnapshot.getValue(Item.class);
                if(item != null) {
                    txDescription.setText(item.getDescription());
                    txTitle.setText(item.getTitle());
                    txPrice.setText(item.getPrice());
                    if(item.getDownloadUrl() == null){
                        item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                    }
                    Picasso.with(ItemDetails.this)
                            .load(item.getDownloadUrl())
                            .centerCrop()
                            .resize(width,width)
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.progress_animation )
                            .into(d_imageView);



                } else{
                    Toast.makeText(ItemDetails.this, "Item no longer exists",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
