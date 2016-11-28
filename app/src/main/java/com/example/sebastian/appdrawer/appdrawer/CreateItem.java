package com.example.sebastian.appdrawer.appdrawer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;


import com.example.sebastian.appdrawer.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class CreateItem extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Firebase connection and references
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();

    private static final String TAG = "CreateItem";

    LinearLayout imageLayout;
    HorizontalScrollView imageScroll;
    EditText itemTag, etDescription;
    ScrollView scrollView;
    Button addItemBtn,addPhotoBtn;
    int iCounter = 0;
    int tagCounter;
    Bitmap bitmap;
    TextView imageCounter;
    List<String> tags = new ArrayList<String>();
    ListView tagsList;
    ImageView imagePlaceholder;
    TextView tvServings, tvLocation, tvPrice;
    EditText edNrOfServings, etTitle;
    int maxLength = 13;
    public String downloadUrl, stringUserID;
    CameraPhoto cameraPhoto;

    SwitchCompat swLocation, swPrice;
    public static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    //Location
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    public Location mLastLocation; //Location of the client
    double mLatitude; //Client latitude coordinate
    double mLongitude; //Client longitude coordinate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_close); //Exit button
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //ui elements
        tagsList = (ListView) findViewById(R.id.listView);
        imagePlaceholder = (ImageView) findViewById(R.id.imagePlaceholder);
        tvServings = (TextView) findViewById(R.id.servings);
        edNrOfServings = (EditText) findViewById(R.id.nrOfServings);
        etTitle = (EditText) findViewById(R.id.etTitle);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        swLocation = (SwitchCompat) findViewById(R.id.swLocation);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        swPrice = (SwitchCompat) findViewById(R.id.swPrice);
        etDescription = (EditText) findViewById(R.id.etDesc);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        itemTag = (EditText) findViewById(R.id.etTags);

        mProgress = new ProgressDialog(this);

        final MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        imagePlaceholder.setRotation(90);
        addPhotoBtn = (Button)findViewById(R.id.btnAddPhoto);

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!marshMallowPermission.checkPermissionForCamera()) {
                    marshMallowPermission.requestPermissionForCamera();
                } else {
                    if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    } else {
                        cameraPhoto = new CameraPhoto(getApplicationContext());

                        //call it to open the camera
                        try {
                            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST_CODE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        edNrOfServings.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        // OnClickListener for servings textview
        // husk at sætte default værdien på EditText til 1
        tvServings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edNrOfServings.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(CreateItem.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                edNrOfServings.setHint("");
            }
        });

        // Added tagsArea as LinearLayout view

        /*final LinearLayout imageLayout = (LinearLayout)findViewById(R.id.imageLayout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView image = new ImageView(CreateItem.this);
                image.setImageResource(R.drawable.lasagne);
                image.setScaleType(ImageView.ScaleType.CENTER);
                imageLayout.removeView(imagePlaceholder);
                imageLayout.addView(image);
                iCounter = iCounter+1;


                imageCounter.setText(""+iCounter+" image(s)");
            }

        });
        */

        itemTag.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        //Exit button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        itemTag.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addTag();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        itemTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                arg0.requestFocus();
            }
        });

        // ---- Location of the item creator ---- //

        //Check for location permission
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

        // ---- On click listener for the 'add item' button ---- //

        addItemBtn = (Button) findViewById(R.id.btnAddItem);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if user has logged in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    Toast.makeText(CreateItem.this, "Please login or sign up before adding an item",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateItem.this, LogInActivity.class));
                }
                else if(user != null) {
                    //Display the tags which are stored in an ArrayList, as a list
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, tags);
                    tagsList.setAdapter(adapter1);

                    //insert data into Firebase
                    DatabaseReference foodRef = rootRef.child("food").push(); //point to food branch
                    edNrOfServings.setText("1");
                    String stringItemTitle = etTitle.getText().toString();
                    String stringItemDescription = etDescription.getText().toString();
                    String stringNrOfServings = edNrOfServings.getText().toString();
                    String stringItemKey = foodRef.getKey();
                    int intServings = Integer.parseInt(stringNrOfServings);
                    stringUserID = user.getUid();
                    //String stringUserName = user.getDisplayName();
                    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm");
                    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("CET"));

                    String currentTimeString = dateFormatGmt.format(new Date())+"";

                    Item newFoodItem = new Item(stringUserID, stringItemTitle,
                            stringItemDescription, "10", intServings,currentTimeString,
                            stringItemKey,downloadUrl,mLatitude,mLongitude);
                    foodRef.setValue(newFoodItem);
                    Toast.makeText(CreateItem.this, stringItemTitle +  " was added",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateItem.this, MainActivity.class));
                    finish();

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

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


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            String photoPath = cameraPhoto.getPhotoPath();
            try {

                bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                StorageReference filepath = mStorage.child("food").child("food "+UUID.randomUUID());
                imagePlaceholder.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] byteData = baos.toByteArray();

                UploadTask uploadTask = filepath.putBytes(byteData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            mProgress.setMessage("Uploading Image....");





           /* StorageReference filepath = mStorage.child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateItem.this,"Uploaded...",Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    mProgress.dismiss();
                }
            });*/
        }
    }


    public void addTag(){


        final TextView tag = new TextView(CreateItem.this);

        tag.setPadding(10,10, 10, 10);


        tag.setBackgroundColor(Color.parseColor("#BDBDBD"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tag.setMaxLines(1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        tag.setText("X      "+itemTag.getText().toString());


        //Flexbox yo! https://github.com/google/flexbox-layout/blob/master/README.md
        final FlexboxLayout linearLayout = (FlexboxLayout) findViewById(R.id.tagsArea);
        tag.setId(tagCounter);
        linearLayout.addView(tag);

        focusOnView();
        itemTag.getText().clear();
        tagCounter=tagCounter+1;
        tags.add(tag.getText().toString());
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeView(tag);

            }
        });


    }
    private final void focusOnView(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    // ---- LOCATION METHODS START ---- //

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

    // ----- LOCATION METHODS END ----- //

}
