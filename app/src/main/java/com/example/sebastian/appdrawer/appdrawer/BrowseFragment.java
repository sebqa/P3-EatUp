package com.example.sebastian.appdrawer.appdrawer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class BrowseFragment extends Fragment {

    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    public static final String FOOD = "food";
    private DatabaseReference mFirebaseDatabaseReference;
    RecyclerView recyclerView;
    ArrayList<Item> arrayList = new ArrayList<Item>();
    FloatingActionButton toTop;
    GeoQuery geoQuery;
    GeoFire geoFire;
    boolean allowRefresh;
    Activity mActivity;
    public double haverdistanceKM;
    long diff;
    int maxListSize = 20;
    TextView noItems;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout specific to this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Cast the recyclerView such that we can manipulate it
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        setHasOptionsMenu(true);

        Utils.getDatabase();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);
        noItems = (TextView)rootView.findViewById(R.id.noItemsText);
        //Casting the button that takes the user to the top.
        toTop = (FloatingActionButton) rootView.findViewById(R.id.toTop);
        //The onClickListener that scrolls to position '0'.
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);


            }
        });

        //Adds an onScrollListener.
        //Hides the 'toTop' button and the floating action button, when scrolling.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && toTop.isShown()) {
                    toTop.hide();
                    ((MainActivity) getActivity()).hideFloatingActionButton();

                }
            }

            //Shows the 'toTop' button and the floating action button when not scrolling.
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    toTop.show();
                    ((MainActivity) getActivity()).showFloatingActionButton();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });





        //New instance of our adapter class, which shows the arrayList.
        //That instance is tied to the recyclerView.

        adapter = new RecyclerAdapter(arrayList, getActivity());
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Log.d(recyclerView.getAdapter().toString(),"recyclerVIew onCreate");
        startgeoQuery();
        adapter.notifyDataSetChanged();
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);


    }
    //Handle the toolbar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Check which element was pressed
        if (id == R.id.sortDistance) {
            Collections.sort(arrayList, new Comparator<Item>() {
                        public int compare(Item object1, Item object2) {
                            return Double.compare(object1.distance,object2.distance);
                        }
                    }
            );
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.sortPrice){
            Collections.sort(arrayList, new Comparator<Item>() {
                        public int compare(Item object1, Item object2) {
                            return object1.price.compareTo(object2.price);
                        }
                    }
            );
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.sortNewest){
            arrayList.clear();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
            this.geoFire = new GeoFire(ref);
            MainActivity activity = (MainActivity) getActivity();
            double radius = 20;
            this.geoQuery = geoFire.queryAtLocation(new GeoLocation(MainActivity.mLatitude,MainActivity.mLongitude), radius);

            GeoQueryEventListener query = new GeoQueryEventListener(){
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.d("startgeoQuery", "1 ");
                    Log.d("geoKeyItem", key.toString());
                    //Load items, and constructs instances of the Item class with them

                    mFirebaseDatabaseReference.child(key.toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(dataSnapshot.child("title").toString(), "itemgeoKey");
                            //Load items, and constructs instances of the Item class with them
                            final Item item = dataSnapshot.getValue(Item.class);


                            //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                            //at the top.
                            if (item.getDownloadUrl() == null) {
                                item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                            }
                            getTime(item);

                            Log.d("Time difference", "" + diff / (1000 * 60 * 60));
                            if (arrayList.size() < maxListSize && !arrayList.contains(item)){
                                //If time difference is more than 5 hours
                        if (diff / (1000 * 60 * 60) < 4) {
                                //Add item to list
                                arrayList.add(0, item);

                                Log.d("arrayList",arrayList.toString());

                        } else {
                            //Delete item from database
                            dataSnapshot.getRef().setValue(null);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("sentRequests");
                                final Query query = mFirebaseDatabaseReference.orderByChild("requestedItem").equalTo(item.getKey());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("Data change", "FIRST CHANGED");
                                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Log.d("FOR", "CHANGED");
                                            if (postSnapshot.getValue() != null) {
                                                Log.d("sentRequests", postSnapshot.getValue().toString());
                                                postSnapshot.getRef().setValue(null);

                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }



                                adapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }

            };
            this.geoQuery.addGeoQueryEventListener(query);


            adapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("CurrentLOC", "" + MainActivity.mLongitude + MainActivity.mLatitude);



    }

    @Override
    public void onResume() {
        super.onResume();
    if (arrayList.isEmpty()){
        noItems.setVisibility(View.VISIBLE);
    }else {
        noItems.setVisibility(View.INVISIBLE);

    }

    }

    public void getUpdates(DataSnapshot dataSnapshot) {


        if (dataSnapshot.getChildrenCount() > 0) {

            Item item = dataSnapshot.getValue(Item.class);
            arrayList.remove(item);


        }

    }



    public void startgeoQuery() {
        Log.d("startgeoQuery", "1 ");


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        this.geoFire = new GeoFire(ref);
        MainActivity activity = (MainActivity) getActivity();
        double radius = 20;
        this.geoQuery = geoFire.queryAtLocation(new GeoLocation(MainActivity.mLatitude,MainActivity.mLongitude), radius);

        GeoQueryEventListener query = new GeoQueryEventListener(){
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("startgeoQuery", "1 ");
                Log.d("geoKeyItem", key.toString());
                //Load items, and constructs instances of the Item class with them

                mFirebaseDatabaseReference.child(key.toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(dataSnapshot.child("title").toString(), "itemgeoKey");
                        //Load items, and constructs instances of the Item class with them
                        final Item item = dataSnapshot.getValue(Item.class);


                        //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                        //at the top.
                        if (item.getDownloadUrl() == null) {
                            item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                        }
                        noItems.setVisibility(View.INVISIBLE);

                        getTime(item);

                        Log.d("Time difference", "" + diff / (1000 * 60 * 60));
                        if (arrayList.size() < maxListSize && !arrayList.contains(item)){
                        //If time difference is more than 5 hours
                        /*if (diff / (1000 * 60 * 60) < 4 && diff > 0) {*/
                            //Add item to list
                            arrayList.add(0, item);

                            Log.d("arrayList",arrayList.toString());

                        /*} else {
                            //Delete item from database
                            dataSnapshot.getRef().setValue(null);
                            ref.child(dataSnapshot.getRef().getKey()).setValue(null);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("sentRequests");
                                final Query query = mFirebaseDatabaseReference.orderByChild("requestedItem").equalTo(item.getKey());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("Data change", "FIRST CHANGED");
                                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Log.d("FOR", "CHANGED");
                                            if (postSnapshot.getValue() != null) {
                                                Log.d("sentRequests", postSnapshot.getValue().toString());
                                                postSnapshot.getRef().setValue(null);

                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }*/
                        haversine(MainActivity.mLatitude,MainActivity.mLongitude,item.getLatitude(),item.getLongitude());
                        item.setDistance(haverdistanceKM);
                        Collections.sort(arrayList, new Comparator<Item>() {
                                    public int compare(Item object1, Item object2) {
                                        return Double.compare(object1.distance,object2.distance);
                                    }
                                }
                        );


                        adapter.notifyDataSetChanged();

                    }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }

        };
        this.geoQuery.addGeoQueryEventListener(query);
        adapter.notifyDataSetChanged();


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
    public void getTime(Item item){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date itemDate = null;
        try {
            itemDate = formatter.parse(item.getCurrentTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("CET"));

        String currentTimeString = dateFormatGmt.format(new Date()) + "";
        Date currentDate = null;
        try {
            currentDate = formatter.parse(currentTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(itemDate);
        Calendar today = Calendar.getInstance();
        today.setTime(currentDate);
        diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
    }
}
