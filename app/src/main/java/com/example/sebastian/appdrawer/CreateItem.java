package com.example.sebastian.appdrawer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class CreateItem extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Firebase connection and references
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();

    private static final String TAG = "CreateItem";


    EditText itemTag, etDescription;
    ScrollView scrollView;
    Button addItemBtn,addPhotoBtn,addTagBtn;
    int tagCounter;
    Bitmap bitmap;
    boolean isSet = true;
    TextView imageCounter;
    ArrayList<String> tags = new ArrayList<String>();
    ListView tagsList;
    ImageView imagePlaceholder;
    TextView tvServings, tvLocation, tvPrice,tvCurrentLocation;
    EditText edNrOfServings, etTitle;
    int maxLength = 13;
    public String downloadUrl, stringUser;
    CameraPhoto cameraPhoto;
    String stringNrOfServings;
    SwitchCompat swLocation, swPrice;
    public static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    String tagsToAdd,stringAddress;

    private FirebaseAuth mAuth;

    //FirebaseLoad
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    public Location mLastLocation; //FirebaseLoad of the client
    double mLatitude; //Client latitude coordinate
    double mLongitude; //Client longitude coordinate
    GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setDrawOverlay(true)
                .setContentView(R.layout.activity_create_item)
                .setSwipeBackView(R.layout.swipeback_default);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_return); //Exit button
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //ui elements
        tagsList = (ListView) findViewById(R.id.listView);
        imagePlaceholder = (ImageView) findViewById(R.id.imagePlaceholder);
        tvServings = (TextView) findViewById(R.id.servings);
        edNrOfServings = (EditText) findViewById(R.id.nrOfServings);
        etTitle = (EditText) findViewById(R.id.etTitle);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);
        addTagBtn = (Button)findViewById(R.id.addTagButton);
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
        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTag();
            }
        });
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
        edNrOfServings.setText("1");
        // OnClickListener for servings textview
        // husk at sætte default værdien på EditText til 1
        tvServings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edNrOfServings.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(CreateItem.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                edNrOfServings.setHint("");
                edNrOfServings.setText("");
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
                arg0.requestFocus();
            }
        });

        // ---- FirebaseLoad of the item creator ---- //

        //Check for location permission
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
                else if(user != null && validateForm()) {
                    //Display the tags which are stored in an ArrayList, as a list
                    //ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, tags);
                    //tagsList.setAdapter(adapter1);
                    uploadImage(user);
                    //insert data into Firebase

                    startActivity(new Intent(CreateItem.this, MainActivity.class));

                }

            }
        });

    }

    public void uploadItem(FirebaseUser user){
        DatabaseReference foodRef = rootRef.child("food").push(); //point to food branch

        String stringItemTitle = etTitle.getText().toString();
        String stringItemDescription = etDescription.getText().toString();

        stringNrOfServings =edNrOfServings.getText().toString();

        String stringItemKey = foodRef.getKey();
        int intServings = Integer.parseInt(stringNrOfServings);

        stringUser = getIntent().getStringExtra("username");
        String userID = user.getUid();
        DatabaseReference requestRefCheck = rootRef.child("food").child(stringItemKey).child("itemRequest");
        if(requestRefCheck == null){

        }
        //String stringUserName = user.getDisplayName();
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("CET"));

        String currentTimeString = dateFormatGmt.format(new Date())+"";

        Item newFoodItem = new Item(stringUser, stringItemTitle,
                stringItemDescription, "10", intServings,currentTimeString,
                stringItemKey,downloadUrl,mLatitude,mLongitude,userID,stringAddress);
        foodRef.setValue(newFoodItem);
        Toast.makeText(CreateItem.this, stringItemTitle +  " was added",
                Toast.LENGTH_LONG).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(foodRef.getKey(),new GeoLocation(mLatitude,mLongitude));
        foodRef.child("tags").setValue(tags);

        sendNotifications(stringItemKey);
    }

    private void sendNotifications(String itemKey) {
        tags.add(0,itemKey);
        JSONObject tagsToNotify = new JSONObject();
        for (int i = 0; i < tags.size(); i++) {

            try {
                tagsToNotify.put("Count:" + String.valueOf(i + 1), tags.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        BackgroundTask backgroundTask = new BackgroundTask(this);


        if (tags.size() == 1){
            String method = "tagsNotiOne";
            tagsToAdd = tags.get(0);
            backgroundTask.execute(method,tagsToAdd);

        } else if(tags.size() > 1){
            String method = "tagsNoti";
            tagsToAdd = tags.toString();
            backgroundTask.execute(method,tagsToAdd);

        }
        Log.d(TAG,tagsToNotify.toString() );
    }

    private boolean validateForm() {
        boolean valid = true;

        String stringItemTitle = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(stringItemTitle)) {
            etTitle.setError("Required");
            scrollView.scrollTo(0,0);
            valid = false;
        } else {
            etTitle.setError(null);
        }

        return valid;
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

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                bitmap = RotateBitmap(bitmap,90);
                final int width = metrics.widthPixels;
                Bitmap displayImage = ThumbnailUtils.extractThumbnail(bitmap,width,width);
                imagePlaceholder.setImageBitmap(displayImage);


            } catch (IOException e) {
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
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public void addTag(){


        final TextView tag = new TextView(CreateItem.this);

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


        focusOnView(linearLayout);
        itemTag.getText().clear();
        tagCounter=tagCounter+1;

    }

    public void uploadImage(final FirebaseUser user){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] byteData = baos.toByteArray();

        StorageReference filepath = mStorage.child("users").child(user.getUid()).child("food").child("food "+UUID.randomUUID());

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
                uploadItem(user);
            }
        });
    }

    public void addressFromLatLng() throws IOException {
        Geocoder geocoder;
        List<Address> yourAddresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        yourAddresses= geocoder.getFromLocation(mLatitude, mLongitude, 1);
        Log.d("Adresse","");


        if (yourAddresses.size() > 0 & isSet)
        {
            String yourAddress = yourAddresses.get(0).getAddressLine(0);
            String yourCity = yourAddresses.get(0).getAddressLine(1);
            String yourCountry = yourAddresses.get(0).getAddressLine(2);

            Log.d("Adresse",yourAddress+ " "+yourCity+ " "+yourCountry);
            stringAddress = yourAddress+ ", "+yourCity+ ", "+yourCountry;

            tvCurrentLocation.append(" "+yourAddress+ ", "+yourCity+ ", "+yourCountry);

        }
    }

    private final void focusOnView(final FlexboxLayout linearlayout){
        scrollView.post(new Runnable() {
            @Override
                public void run() {
                scrollView.smoothScrollTo(0, linearlayout.getTop());
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

    //FirebaseLoad
    @Override
    public void onConnected(Bundle bundle) {

        try {
            // Check for location permission
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            //Fetch client's location coordinates
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, "TEST MESSAGE: FirebaseLoad services connected.");

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
                addressFromLatLng();
                isSet = false;
            }

        } catch (SecurityException ex) {
            //handler
        } catch (IOException e) {
            e.printStackTrace();
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
        Log.i(TAG, "FirebaseLoad changed");
    }

    // ----- LOCATION METHODS END ----- //

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}
