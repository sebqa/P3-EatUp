package com.example.sebastian.appdrawer.appdrawer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sebastian on 02-02-2017.
 */

public class FirebaseLoad {
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String FOOD = "food";
    GeoQuery geoQuery;
    GeoFire geoFire;
    public double haverdistanceKM;
    long diff;
    int maxListSize = 20;
    boolean hasRun,isEmpty = true;


    public void getClosestItems(final ArrayList arrayList, final RecyclerView.Adapter adapter){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FOOD);
        Log.d("startgeoQuery", "1 ");


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        this.geoFire = new GeoFire(ref);
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
                        if (item != null) {
                            isEmpty = false;

                            //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                            //at the top.
                            if (item.getDownloadUrl() == null) {
                                item.setDownloadUrl("https://firebasestorage.googleapis.com/v0/b/p3-eatup.appspot.com/o/placeholder-320.png?alt=media&token=a89c2343-682a-41cc-95c2-6f896faeb2c5");
                            }
                            Thread t1 = new Thread(new Runnable(){

                                @Override
                                public void run() {
                                    getTime(item);
                                }
                            });
                            t1.start();
                        }
                        Log.d("Time difference", "" + diff / (1000 * 60 * 60));
                        if (arrayList.size() < maxListSize && !arrayList.contains(item)){
                            //If time difference is more than 5 hours
                            if (true) {
                                //diff / (1000 * 60 * 60) < 4 && diff > 0
                                //Add item to list
                                arrayList.add(0, item);

                                Log.d("arrayList",arrayList.toString());

                            } else {
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
                            }

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
        hasRun = true;


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
    public boolean isEmpty (){
        return isEmpty;
    }
    public void getNewestItems(final ArrayList arrayList, final RecyclerView.Adapter adapter){
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                //Load items, and constructs instances of the Item class with them
                final Item item = dataSnapshot.getValue(Item.class);

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
                /*if (diff/(1000 * 60 * 60) < 5) {*/
                //Add item to list
                if (arrayList.size() < maxListSize && !arrayList.contains(item)) {
                    //If time difference is more than 5 hours
                    if (true) {
                        //diff / (1000 * 60 * 60) < 4 && diff > 0
                        //Add item to list
                        arrayList.add(0, item);

                        Log.d("arrayList", arrayList.toString());

                    }
                    adapter.notifyDataSetChanged();
                }
               /* } else {
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
                }*/
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

}
