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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
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
    boolean isImageFitToScreen;

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

       Branch.getAutoInstance(this);
       Branch branch = Branch.getInstance(getApplicationContext());
       branch.initSession(new Branch.BranchReferralInitListener(){
           @Override
           public void onInitFinished(JSONObject referringParams, BranchError error) {
               if (error == null) {
                   // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                   // params will be empty if no data found
                   // ... insert custom logic here ...
               } else {
                   Log.i("MyApp", error.getMessage());
               }
           }
       }, this.getIntent().getData(), this);
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

       d_imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               d_imageView.setScaleType(ImageView.ScaleType.FIT_XY);




           }
       });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    createDeepLink();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailsmenu, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                createDeepLink();
                return false;
            }
        });

        return true;
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


   public void createDeepLink(){
       BranchUniversalObject branchUniversalObject = new BranchUniversalObject()

               // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
               .setCanonicalIdentifier("item/"+item.getKey())

               // The canonical URL for SEO purposes (optional)
               .setCanonicalUrl("https://branch.io/deepviews")

               // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
               .setTitle(item.getTitle())
               .setContentDescription(item.getDescription())
               .setContentImageUrl(item.getDownloadUrl())

               // You use this to specify whether this content can be discovered publicly - default is public
               .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

               // Here is where you can add custom keys/values to the deep link data
               .addContentMetadata("itemKey", item.getKey());


       LinkProperties linkProperties = new LinkProperties()
               .setChannel("facebook")
               .setFeature("sharing")
               .addControlParameter("$desktop_url", "http://example.com/home")
               .addControlParameter("$ios_url", "http://example.com/ios");

       branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
           @Override
           public void onLinkCreate(String url, BranchError error) {
               if (error == null) {
                   Log.i("MyApp", "got my Branch link to share: " + url);
               }
           }
       });
       ShareSheetStyle shareSheetStyle = new ShareSheetStyle(ItemDetails.this, "","")
               .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
               .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
               .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
               .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
               .setAsFullWidthStyle(true)
               .setSharingTitle("Share With");
       branchUniversalObject.showShareSheet(this,
               linkProperties,
               shareSheetStyle,
               new Branch.BranchLinkShareListener() {
                   @Override
                   public void onShareLinkDialogLaunched() {
                   }
                   @Override
                   public void onShareLinkDialogDismissed() {
                   }
                   @Override
                   public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                   }
                   @Override
                   public void onChannelSelected(String channelName) {
                   }
               });
   }
}
