package com.example.sebastian.eatup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sebastian.appdrawer.R;
import com.example.sebastian.eatup.fragments.AboutFragment;
import com.example.sebastian.eatup.fragments.BrowseFragment;
import com.example.sebastian.eatup.fragments.FavoriteFragment;
import com.example.sebastian.eatup.fragments.MapFragment;
import com.example.sebastian.eatup.fragments.MyFoodFragment;
import com.example.sebastian.eatup.fragments.SettingsFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.UserInfo;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OneSignal.NotificationReceivedHandler {

    Menu menu;
    private static final String TAG = "MainActivity";
    FloatingActionButton fab;
    Button signOutBtn;
    Button signInBtn;
    TextView tvEmail,tvUsername;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String username, oneSignalID;
    //FirebaseLoad
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    public Location mLastLocation; //FirebaseLoad of the client
    public static double mLatitude; //Client latitude coordinate
    public static double mLongitude; //Client longitude coordinate
    TextView newNoti;
    boolean isAdded= false;
    RequestsFragment requestsFragment = new RequestsFragment();
    BrowseFragment browseFragment = new BrowseFragment();
    Fragment currentFragment = null;
    ImageView userIcon;
    int RC_SIGN_IN = 9999;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.getDatabase();
        //Set theme to the one that shows splash screen before the super.onCreate
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        signOutBtn = (Button)findViewById(R.id.signOutBtn);

        OneSignal.setSubscription(true);

// Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc

        Boolean loggedin = getIntent().getBooleanExtra("isLoggedIn",false);

        //Gets user information if the user has created an account and logged in
        //If the user is not logged in, then user will be null.
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateUI(user);
                    startOneSignal();
                    final DatabaseReference oneSignalIDRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                    if(oneSignalID != null) {
                        oneSignalIDRef.child("oneSignalID").setValue(oneSignalID);
                    }
                    oneSignalIDRef.child("name").setValue(user.getDisplayName());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userIcon.setImageResource(R.drawable.usericon);


                }

            }
        };


        //Hide title in the toolbar to make room for logo
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Create new object of the hamburger menu and define preliminary behaviour.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        newNoti = (TextView)drawer.findViewById(R.id.newNoti);



        setDrawerLeftEdgeSize(this, drawer, 0.2f);
        //Set onClickListener to the menu, such that elements can be pressed independently.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        View headerview = navigationView.getHeaderView(0);
        tvEmail = (TextView)headerview.findViewById(R.id.tvEmail);
        tvUsername = (TextView)headerview.findViewById(R.id.tvUsername);
        userIcon = (ImageView)headerview.findViewById(R.id.userIcon);

        signInBtn = (Button)headerview.findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


        });
        signOutBtn = (Button)headerview.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseAuth.getInstance().signOut();
                OneSignal.setSubscription(false);
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                //startActivity(new Intent(MainActivity.this, LoginSignUp.class));

                            }
                        });
                updateUI(null);

            }
        });

        // Check for location permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            Log.i(TAG, "TEST MESSAGE: FirebaseLoad permission has been granted.");
        } else if (permissionCheck == -1) {
            Log.i(TAG, "TEST MESSAGE: FirebaseLoad permission has NOT been granted.");
        }

        // If permission has not been granted, ask for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS);
            //When the user responds to the request, onRequestPermissionsResult() is invoked.
        }

        //If permission has already been granted, create an instance of GoogleAPIClient
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient == null) { //check if there already exists one
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                Log.i(TAG, "TEST MESSAGE: Google API Client instance created.");

                // Create the LocationRequest object
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            }
        }

        //Initialize the main fragment's layout


        //Set onClickListener to the floating action button, and make it start new activity.
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,CreateItem.class);
                    intent.putExtra("username",username);
                    final android.support.v4.app.FragmentManager fn = getSupportFragmentManager();
                fn.beginTransaction().remove(requestsFragment);

                startActivity(intent);
                overridePendingTransition(R.anim.slidein, 0);

                //postInfo();
            }
        });

        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("food");
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                for (final DataSnapshot postSnapshot : dataSnapshot.child("confirmedReq").getChildren()) {
                    final Item item = dataSnapshot.getValue(Item.class);

                    if(user != null) {
                        String snap = postSnapshot.getValue().toString();
                        String userIDcheck = user.getUid().toString();
                        Log.d("snapshot", snap);
                        Log.d("userID", userIDcheck);
                        if (snap.equals(userIDcheck)) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                            ad.setTitle("Order confirmation");
                            ad.setMessage("Confirmation for: " + item.title + "\n" + "Exacts address is: " + item.getAddress());
                            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    DatabaseReference deleteRef = mFirebaseDatabaseReference.child(item.getKey()).child("confirmedReq");
                                    Log.d("deleteref", deleteRef.getRef().toString());
                                    deleteRef.getRef().setValue(null);

                                }
                            });


                            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {



                                    dialog.cancel();
                                }
                            });
                            ad.show();
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (final DataSnapshot postSnapshot : dataSnapshot.child("confirmedReq").getChildren()) {
                    final Item item = dataSnapshot.getValue(Item.class);

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {

                        String snap = postSnapshot.getValue().toString();
                        String userIDcheck = user.getUid().toString();
                        Log.d("snapshot", snap);
                        Log.d("userID", userIDcheck);
                        if (snap.equals(userIDcheck)) {
                            newNoti.setVisibility(View.VISIBLE);
                            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                            ad.setTitle("Order confirmation");
                            ad.setMessage("Confirmation for: " + item.title + "\n" + "Exacts address is: " + item.getAddress());
                            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    DatabaseReference deleteRef = mFirebaseDatabaseReference.child(item.getKey()).child("confirmedReq");
                                    Log.d("deleteref", deleteRef.getRef().toString());
                                    deleteRef.getRef().setValue(null);

                                }
                            });


                            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference deleteRef = mFirebaseDatabaseReference.child(item.getKey()).child("confirmedReq");
                                    Log.d("deleteref", deleteRef.getRef().toString());
                                    deleteRef.getRef().setValue(null);

                                    DatabaseReference confirmedHistory = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("confirmedRequests").push();
                                    confirmedHistory.setValue(item);

                                    DatabaseReference deleteSentRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("sentRequests");
                                    Query sentReqquery = deleteSentRef.orderByChild("requestedItem").equalTo(item.getKey());
                                    sentReqquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                                                    postSnapshot.getRef().setValue(null);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Query query = mFirebaseDatabaseReference.child(item.getKey()).child("itemRequests");
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                String snap = postSnapshot.getValue().toString();
                                                String userIDcheck = user.getUid().toString();
                                                if (snap.equals(userIDcheck)) {
                                                    dataSnapshot.getRef().setValue(null);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });



                                    dialog.cancel();
                                }
                            });
                            ad.show();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void
    startOneSignal(){
        OneSignal.startInit(this).setNotificationReceivedHandler(new NotificationReceivedHandler(getApplicationContext(),this)).setNotificationOpenedHandler(new NotificationOpenedHandler(getApplicationContext(),this))
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
                oneSignalID = userId;
            }
        });
    }

    public void postInfo() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String method = "sendNoti";
        String user_id = "AQI9HnZxPSRUL9Lwn4RSro4nZOy1";
        String tag = "kat";
        String item_key = "-K_GLZI7wgczr1YQ5DOy";

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,user_id,tag,item_key);



    }


    //Override onBackPressed such that it doesn't close the app, but only the hamburger menu if it's open.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            new MaterialDialog.Builder(this)
                    .title("Exit")
                    .content("Are you sure you want to exit?")
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            onYesClick();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            onNoClick();
                        }
                    })
                    .negativeText("No")
                    .show();
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure you want to exit?").setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    onYesClick();

                                }


                            }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onNoClick();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();*/
        }
    }
    private void onYesClick() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);

        MainActivity.this.finish();



    }private void onNoClick() {

    }

    //Initialize the menu layout






    //Handle hamburger menu clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final android.support.v4.app.FragmentManager fn = getSupportFragmentManager();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int id = item.getItemId();
        //Check which element was pressed
        if (id == R.id.nav_main) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
                fn.beginTransaction().remove(currentFragment);

                fn.beginTransaction().replace(R.id.content_frame, browseFragment).commit();


            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_map) {
            fn.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_favorites & user != null) {
            gpsdisconnect();
            //fn.beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
            FavoriteFragment fragmenttab = new FavoriteFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmenttab).commit();

            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_my_food) {
            gpsdisconnect();
            fn.beginTransaction().replace(R.id.content_frame, new MyFoodFragment()).commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_requests) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        gpsdisconnect();
                        fn.beginTransaction().replace(R.id.content_frame, requestsFragment).commit();
                        fab.setVisibility(View.INVISIBLE);
                        newNoti.setVisibility(View.INVISIBLE);
                    }

            },0);


        } else if (id == R.id.nav_settings) {
            gpsdisconnect();
            fn.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_about) {
            gpsdisconnect();
            fn.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void addFragments(Fragment fragment,
                             boolean addToBackStack, String tag) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }





    //Methods to hide/show the floating action button from fragments
    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

    private void updateUI(final FirebaseUser user) {
        final Uri[] profileUri = new Uri[1];
        final String[] pname = new String[1];
        if (user != null) {
            profileUri[0] = user.getPhotoUrl();
            tvEmail.setText(user.getEmail());

            signOutBtn.setVisibility(View.VISIBLE);

            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue().toString() != null) {

                        username = dataSnapshot.getValue().toString();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("What is your name?");

                        // Set up the input
                        final EditText input = new EditText(MainActivity.this);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_NUMBER);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                username = input.getText().toString();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        userRef.child("name").setValue(username);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

            if(pname[0] != null){
                tvUsername.setText(pname[0]);
                Log.d("USERDISPLAYNAME", pname[0]);
            }else {
                tvUsername.setText("NA");

            }

            // If the above were null, iterate the provider data
            // and set with the first non null data



            //tvUsername.setText(""+user.getUid());
            signOutBtn.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.INVISIBLE);





        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(00);
                } catch (InterruptedException e) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do some stuff
                        for (UserInfo userInfo : user.getProviderData()) {
                            if (pname[0] == null && userInfo.getDisplayName() != null){
                                pname[0] = userInfo.getDisplayName();
                                tvUsername.setText(pname[0]);

                            }
                            if (profileUri[0] == null && userInfo.getPhotoUrl() != null) {
                                profileUri[0] = userInfo.getPhotoUrl();
                            }
                        }
                        if (profileUri[0] != null) {
                            Picasso.with(MainActivity.this)
                                    .load(profileUri[0])
                                    .transform(new CircleTransform(50,0))
                                    .placeholder(R.drawable.progress_animation)
                                    .into(userIcon);
                        }

                    }
                });
            }
        };
        thread.start(); //start the thread
        } else {
            tvEmail.setText("Not logged in");
            tvUsername.setVisibility(View.INVISIBLE);
            signOutBtn.setVisibility(View.INVISIBLE);
            signInBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance(getApplicationContext());
        branch.initSession(new Branch.BranchUniversalReferralInitListener(){
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError branchError) {
                if (branchUniversalObject == null) {
                    Log.i("BRANCHDATA","DEN ER NULL");

                }
                 /*In case the clicked link has $android_deeplink_path the Branch will launch the MonsterViewer automatically since AutoDeeplinking feature is enabled.
                  Launch Monster viewer activity if a link clicked without $android_deeplink_path*/

                else if (branchUniversalObject.getMetadata().containsKey("itemKey")) {
                    Log.i("BRANCHDATA",branchUniversalObject.getMetadata().get("itemKey"));

                    Intent intent = new Intent(MainActivity.this, ItemDetails.class);
                    intent.putExtra("item_key",branchUniversalObject.getMetadata().get("itemKey"));
                    startActivity(intent);
                }
            }
        }, this.getIntent().getData(), this);


        mAuth.addAuthStateListener(mAuthListener);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


    }
    // [END on_start_add_listener]

    @Override
    protected void onResume() {
        super.onResume();
        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {

                Log.e("BRANCHITEMKEY",Branch.getInstance().getLatestReferringParams().getString("itemKey"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("nondeeplink", "Launched by normal application flow");
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        gpsdelay();


    }

    public void gpsdelay(){
        Log.d("fgpsdelay","fær");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsdisconnect();
                Log.d("egpsdelay", "efter");
            }

        },10000);


    }

    public void gpsdisconnect(){
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;

        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    // Handle answer to the location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        //--- Handle the user response to the permission request --//
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Check for location permission
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == 0) {
                    Log.i(TAG, "TEST MESSAGE: FirebaseLoad permission has been granted after request.");
                } else if (permissionCheck == -1) {
                    Log.i(TAG, "TEST MESSAGE: FirebaseLoad permission has NOT been granted after request.");
                }

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                // Create an instance of GoogleAPIClient.
                if (mGoogleApiClient == null) { //check if there already exists one
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();

                    Log.i(TAG, "TEST MESSAGE: Google API Client instance created.");
                }

                // Create the LocationRequest object
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(30 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(10 * 1000); // 1 second, in milliseconds

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //FirebaseLoad
    @Override
    public void onConnected(Bundle bundle) {

        try {
            // Check for location permission
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            //Fetch client's location coordinates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            Log.i(TAG, "TEST MESSAGE: FirebaseLoad services connected.");


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation == null || permissionCheck == -1) {
                mLatitude = 0.0;
                mLongitude = 0.0;

            } else {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                Log.i(TAG, "Client latitude: " + mLatitude); //debugging
                Log.i(TAG, "Client longitude: " + mLongitude); //debugging
                if(!isAdded) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    android.support.v4.app.FragmentManager fn = getSupportFragmentManager();
                        fn.beginTransaction().replace(R.id.content_frame, browseFragment).commit();

                    isAdded = true;
                }

            }

        } catch (SecurityException ex) {
            //handler
        }

    }

    //FirebaseLoad
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "FirebaseLoad services suspended. Please reconnect.");
    }

    //FirebaseLoad
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "FirebaseLoad services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //FirebaseLoad
    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        Log.i(TAG, "Location changed");

    }



    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    AuthUI.getInstance()
                            .signOut(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    //startActivity(new Intent(MainActivity.this, LoginSignUp.class));

                                }
                            });
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    AuthUI.getInstance()
                            .signOut(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    //startActivity(new Intent(MainActivity.this, LoginSignUp.class));

                                }
                            });
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    AuthUI.getInstance()
                            .signOut(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    //startActivity(new Intent(MainActivity.this, LoginSignUp.class));

                                }
                            });
                    return;
                }
            }

        }
    }
}