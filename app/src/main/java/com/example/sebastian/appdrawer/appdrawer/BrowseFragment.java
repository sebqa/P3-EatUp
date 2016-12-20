package com.example.sebastian.appdrawer.appdrawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    ArrayList<Item> arrayList = new ArrayList<Item>();
    FloatingActionButton toTop;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout specific to this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Cast the recyclerView such that we can manipulate it
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        //Add a layout manager to control layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Utils.getDatabase();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);




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
        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(MainActivity.mLatitude, MainActivity.mLongitude),500000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("geoKeyItem",key.toString());
                //Load items, and constructs instances of the Item class with them

                mFirebaseDatabaseReference.child(key.toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(dataSnapshot.child("title").toString(),"itemgeoKey");
                        //Load items, and constructs instances of the Item class with them
                        final Item item = dataSnapshot.getValue(Item.class);
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



                        //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                        //at the top.
                        if (item.getDownloadUrl() == null) {
                            item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                        }

                        //Check if item is more than 5 hours old
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
                        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis

                        Log.d("Time difference", "" + diff / (1000 * 60 * 60));

                        //If time difference is more than 5 hours
                        if (diff/(1000 * 60 * 60) < 4) {
                            //Add item to list
                            arrayList.add(0,item);
                            adapter.notifyDataSetChanged();
                        } else {
                            //Delete item from database
                            dataSnapshot.getRef().setValue(null);
                            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });*/


        if(isAdded()) {
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                //Load items, and constructs instances of the Item class with them
                final Item item = dataSnapshot.getValue(Item.class);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



                //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                //at the top.
                if (item.getDownloadUrl() == null) {
                    item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                }

                //Check if item is more than 5 hours old
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
                long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis

                Log.d("Time difference", "" + diff / (1000 * 60 * 60));

                //If time difference is more than 5 hours
                if (diff/(1000 * 60 * 60) < 4) {
                    //Add item to list
                    arrayList.add(0, item);
                    adapter.notifyDataSetChanged();
                } else {
                    //Delete item from database
                    dataSnapshot.getRef().setValue(null);
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getUpdates(dataSnapshot);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load items.",
                        Toast.LENGTH_SHORT).show();

            }

        });
    }


        //New instance of our adapter class, which shows the arrayList.
        //That instance is tied to the recyclerView.
        adapter = new RecyclerAdapter(arrayList, getActivity());
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void getUpdates(DataSnapshot dataSnapshot){



        if (dataSnapshot.getChildrenCount() > 0) {

            Item item = dataSnapshot.getValue(Item.class);
            arrayList.remove(item);



        }

    }

}
