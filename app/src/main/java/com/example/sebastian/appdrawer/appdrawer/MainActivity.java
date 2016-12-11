package com.example.sebastian.appdrawer.appdrawer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
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

import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Menu menu;
    private static final String TAG = "MainActivity";
    FloatingActionButton fab;
    Button signOutBtn;
    Button signInBtn;
    TextView tvEmail,tvUsername;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String username;
    //Location
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    public Location mLastLocation; //Location of the client
    public static double mLatitude; //Client latitude coordinate
    public static double mLongitude; //Client longitude coordinate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.getDatabase();

        //Set theme to the one that shows splash screen before the super.onCreate
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        signOutBtn = (Button)findViewById(R.id.signOutBtn);

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
                    tvEmail.setText(""+user.getEmail());
                    tvUsername.setText(""+user.getEmail());



                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
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

        setDrawerLeftEdgeSize(this, drawer, 0.5f);
        //Set onClickListener to the menu, such that elements can be pressed independently.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        View headerview = navigationView.getHeaderView(0);
        tvEmail = (TextView)headerview.findViewById(R.id.tvEmail);
        tvUsername = (TextView)headerview.findViewById(R.id.tvUsername);
        signInBtn = (Button)headerview.findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, LogInActivity.class));


            }
        });
        signOutBtn = (Button)headerview.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginSignUp.class));
                updateUI(null);

            }
        });

        // Check for location permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            Log.i(TAG, "TEST MESSAGE: Location permission has been granted.");
        } else if (permissionCheck == -1) {
            Log.i(TAG, "TEST MESSAGE: Location permission has NOT been granted.");
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
        android.app.FragmentManager fn = getFragmentManager();
        fn.beginTransaction().replace(R.id.content_frame, new BrowseFragment()).commit();

        //Set onClickListener to the floating action button, and make it start new activity.
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,CreateItem.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
            }
        });

        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("food");
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (final DataSnapshot postSnapshot : dataSnapshot.child("confirmedReq").getChildren()) {
                    final Item item = dataSnapshot.getValue(Item.class);

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                                DatabaseReference deleteRef = mFirebaseDatabaseReference.child(item.getKey()).child("confirmedReq");
                                Log.d("deleteref", deleteRef.getRef().toString());
                                deleteRef.getRef().setValue(null);
                                dialog.cancel();
                            }
                        });
                        ad.show();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (final DataSnapshot postSnapshot : dataSnapshot.child("confirmedReq").getChildren()) {
                    final Item item = dataSnapshot.getValue(Item.class);

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                                DatabaseReference deleteRef = mFirebaseDatabaseReference.child(item.getKey()).child("confirmedReq");
                                Log.d("deleteref", deleteRef.getRef().toString());
                                deleteRef.getRef().setValue(null);
                                dialog.cancel();
                            }
                        });
                        ad.show();
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



    //Override onBackPressed such that it doesn't close the app, but only the hamburger menu if it's open.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            alert.show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }


    //Handle the toolbar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Check which element was pressed
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sort){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Handle hamburger menu clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        android.app.FragmentManager fn = getFragmentManager();

        int id = item.getItemId();
        //Check which element was pressed
        if (id == R.id.nav_main) {
            //Replace current fragment with MainFragment
            fn.beginTransaction().replace(R.id.content_frame, new BrowseFragment()).commit();
            //Show floating action button
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_map) {
            fn.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_favorites) {
            fn.beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_my_food) {
            fn.beginTransaction().replace(R.id.content_frame, new MyFoodFragment()).commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_requests) {
            fn.beginTransaction().replace(R.id.content_frame, new RequestsFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_settings) {
            fn.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_about) {
            fn.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }










    //Methods to hide/show the floating action button from fragments
    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {

            tvEmail.setText(""+user.getEmail());
            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("name");
            mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    username = dataSnapshot.getValue().toString();
                    tvUsername.setText(username);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            //tvUsername.setText(""+user.getUid());
            signOutBtn.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.INVISIBLE);



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
        mAuth.addAuthStateListener(mAuthListener);
        mAuth.addAuthStateListener(mAuthListener);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }
    // [END on_start_add_listener]

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
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
                    Log.i(TAG, "TEST MESSAGE: Location permission has been granted after request.");
                } else if (permissionCheck == -1) {
                    Log.i(TAG, "TEST MESSAGE: Location permission has NOT been granted after request.");
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
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1 * 1000); // 1 second, in milliseconds

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Location
    @Override
    public void onConnected(Bundle bundle) {

        try {
            // Check for location permission
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            //Fetch client's location coordinates
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, "TEST MESSAGE: Location services connected.");

            if (mLastLocation == null || permissionCheck == -1) {
                mLatitude = 0.0;
                mLongitude = 0.0;
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                Log.i(TAG, "Client latitude: " + mLatitude); //debugging
                Log.i(TAG, "Client longitude: " + mLongitude); //debugging
            }

        } catch (SecurityException ex) {
            //handler
        }

    }

    //Location
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    //Location
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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //Location
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed");
    }


}
