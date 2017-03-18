package com.example.sebastian.appdrawer.appdrawer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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


import me.imid.swipebacklayout.lib.SwipeBackLayout;


public class ItemDetails extends SwipeBackActivityNew {
    ImageView d_imageView;
    TextView txTitle,txPrice,txCreator, txServingsLeft, txDescription, btnOrder,txDistance;
    public static final String FOOD = "food";
    private DatabaseReference mFirebaseDatabaseReference;
    String itemKey, amount = "23  serving(s)";
    Item item;
    String receiverSignalID;
    public double haverdistanceKM;
    SwipeBackLayout swipeBackLayout;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*SwipeBack.attach(this, Position.LEFT)
                .setDrawOverlay(true)
                .setContentView(R.layout.activity_item_details)
                .setSwipeBackView(R.layout.swipeback_default);
                */
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_return); //Exit button
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

       d_imageView = (ImageView)findViewById(R.id.imagePlaceholderDetails);
        txTitle = (TextView) findViewById(R.id.txTitleDetails);
        txCreator = (TextView) findViewById(R.id.txCreator);
        txPrice = (TextView) findViewById(R.id.txPrice);
        txServingsLeft = (TextView) findViewById(R.id.txServingsLeft);
        txDescription = (TextView) findViewById(R.id.txDescriptionDetails);
        btnOrder = (TextView) findViewById(R.id.btnOrder);
        txDistance = (TextView) findViewById(R.id.txDistance);


        swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        swipeBackLayout.setScrollThresHold(0.9f);


        itemKey = getIntent().getStringExtra("item_key");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int width = metrics.widthPixels;
        Utils.getDatabase();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    Toast.makeText(ItemDetails.this, "Please login or sign up before ordering an item",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ItemDetails.this, LogInActivity.class));
                }
                else if(user != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetails.this);
                    builder.setTitle("How many servings would you like?");

                    // Set up the input
                    final EditText input = new EditText(ItemDetails.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            amount = input.getText().toString();
                            placeOrder(user);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                }

            }
        });



        mFirebaseDatabaseReference.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                item = dataSnapshot.getValue(Item.class);
                if(item != null) {
                    txDescription.setText(item.getDescription());
                    txTitle.setText(item.getTitle());
                    if(item.getPrice().equals("10") ){
                        txPrice.setText("Free");
                    } else {
                        txPrice.setText(item.getPrice()+" kr");
                    }
                    txCreator.setText(item.getCreator());
                    txServingsLeft.setText(""+item.getAmount()+" serving(s) remaining");
                    haversine(MainActivity.mLatitude,MainActivity.mLongitude,item.getLatitude(),item.getLongitude());
                    //Reduce decimals for listview
                    int temp = (int)(haverdistanceKM*1000.0);
                    int shortDouble = (temp);
                    txDistance.setText((shortDouble)+" m");




                    if(item.getDownloadUrl() == null){
                        item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                    }
                    Picasso.with(ItemDetails.this)
                            .load(item.getDownloadUrl())
                            .centerCrop()
                            .resize(width,width)
                            .rotate(90)
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
    public void placeOrder(FirebaseUser user){
        DatabaseReference itemRequestedRef = rootRef.child("food").child(itemKey).child("itemRequests").push();
        itemRequestedRef.setValue(user.getUid());


        final DatabaseReference myRequests = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("sentRequests").push();
        myRequests.child("requestedItem").setValue(itemKey);

        DatabaseReference newRequestRef = myRequests.getRef();
        newRequestRef.child("requestedAmount").setValue(""+amount);

        DatabaseReference oneSignalRef = rootRef.child("users").child(item.getUserID()).child("oneSignalID");
        oneSignalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("OneSignalIDsnap",dataSnapshot.getValue().toString());
                    receiverSignalID = dataSnapshot.getValue().toString().trim();
                    String method = "orderNoti";

                    BackgroundTask backgroundTask = new BackgroundTask(ItemDetails.this);
                    backgroundTask.execute(method,receiverSignalID,itemKey);
                    /*try {
                       OneSignal.postNotification(new JSONObject("{'contents': {'en':'You have received a new order'}, 'include_player_ids': ['" + receiverSignalID + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        Toast.makeText(this, "Your order has been placed. Please wait for confirmation from the seller",
                Toast.LENGTH_LONG).show();
        finish();
    }
    public void haversine(double lat1, double lon1, double lat2, double lon2) {
        double Rad = 6372.8; //Earth's Radius In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        haverdistanceKM = Rad * c;

    }

   /* public void addTag(){


        final TextView tag = new TextView(ItemDetails.this);

        tag.setPadding(10,10, 10, 10);


        tag.setBackgroundColor(Color.parseColor("#BDBDBD"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final String newTag = itemTag.getText().toString().toLowerCase().trim();
        tags.add(newTag);
        tag.setMaxLines(1);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        final FlexboxLayout linearLayout = (FlexboxLayout) findViewById(R.id.tagsArea);
        tag.setId(tagCounter);
        linearLayout.addView(tag);
        final ArrayList<String> displayedTags = new ArrayList<>();
        displayedTags.add(itemTag.getText().toString().toLowerCase().trim());
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeView(tag);
                tags.remove(newTag);

            }
        });
        tag.setText("X      "+itemTag.getText().toString());


        //Flexbox yo! https://github.com/google/flexbox-layout/blob/master/README.md


        focusOnView();
        itemTag.getText().clear();
        tagCounter=tagCounter+1;




    }*/

   @Override
   public void onBackPressed() {
       super.onBackPressed();
       overridePendingTransition(0,R.anim.slideout);

   }
}
