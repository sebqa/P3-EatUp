package com.example.sebastian.appdrawer.appdrawer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.example.sebastian.appdrawer.appdrawer.FirebaseLoad;
import com.example.sebastian.appdrawer.appdrawer.Item;
import com.example.sebastian.appdrawer.appdrawer.MainActivity;
import com.example.sebastian.appdrawer.appdrawer.RecyclerAdapter;
import com.example.sebastian.appdrawer.appdrawer.Utils;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class BrowseFragment extends Fragment {

    private Spinner spinner2;
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
    boolean hasRun = false;
    int currentOrdering;

    FirebaseLoad firebaseLoad = new FirebaseLoad();

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
        noItems = (TextView)rootView.findViewById(R.id.noItemsText);
        setHasOptionsMenu(true);
        Utils.getDatabase();

        addItemsOnSpinner2(rootView);
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

        if(listIsAtTop()){
            toTop.hide();
        }

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
                    if(listIsAtTop()){
                        toTop.hide();
                    }
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

        adapter.notifyDataSetChanged();



        return rootView;

    }
    private boolean listIsAtTop()   {
        if(recyclerView.getChildCount() == 0) return true;
        return recyclerView.getChildAt(0).getTop() == 0;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                Log.d("SearchText",s.trim());
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
                geoFire = new GeoFire(ref);
                double radius = 1000;
                geoQuery = geoFire.queryAtLocation(new GeoLocation(MainActivity.mLatitude,MainActivity.mLongitude), radius);
                arrayList.clear();

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

                                Thread t1 = new Thread(new Runnable(){

                                    @Override
                                    public void run() {
                                        getTime(item);
                                    }
                                });
                                t1.start();


                                Log.d("Time difference", "" + diff / (1000 * 60 * 60));
                                if (arrayList.size() < maxListSize && !arrayList.contains(item) && item.creator.toLowerCase().contains(s.toLowerCase().trim()) || item.title.toLowerCase().contains(s.toLowerCase().trim()) ){
                                    //If time difference is more than 5 hours
                                    arrayList.add(0, item);

                                    Log.d("arrayList",arrayList.toString());
                                    Thread t2 = new Thread(new Runnable(){

                                        @Override
                                        public void run() {
                                            haversine(MainActivity.mLatitude,MainActivity.mLongitude,item.getLatitude(),item.getLongitude());
                                        }
                                    });
                                    t2.start();
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
                geoQuery.addGeoQueryEventListener(query);
                adapter.notifyDataSetChanged();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                arrayList.clear();
                startgeoQuery();
                return false;
            }
        });*/

    }
    //Handle the toolbar clicks


    @Override
    public void onStart() {
        super.onStart();
        Log.d("CurrentLOC", "" + MainActivity.mLongitude + MainActivity.mLatitude);
        if (!hasRun) {
            noItems.setVisibility(View.VISIBLE);
            firebaseLoad.getClosestItems(arrayList,adapter);
            currentOrdering = 2;
            hasRun = true;

        }



    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void addItemsOnSpinner2(View view) {

        spinner2 = (Spinner) view.findViewById(R.id.sortSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Nearest");
        list.add("Newest");
        list.add("Cost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
        spinner2.setPrompt("Sort By:");

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int disOrdering = 2;
                int newOrdering = 1;
                //Check which element was pressed
                if (i == 0) {

                    if (currentOrdering != disOrdering) {
                        arrayList.clear();
                        firebaseLoad.getClosestItems(arrayList, adapter);
                        adapter.notifyDataSetChanged();
                        currentOrdering = 2;
                    }

                } else if (i == 2) {
                    Collections.sort(arrayList, new Comparator<Item>() {
                                public int compare(Item object1, Item object2) {
                                    return Integer.parseInt(object1.price) - Integer.parseInt(object2.price);
                                }
                            }
                    );
                    adapter.notifyDataSetChanged();

                } else if (i == 1) {
                    if (currentOrdering != newOrdering) {
                        arrayList.clear();
                        firebaseLoad.getNewestItems(arrayList, adapter);
                        currentOrdering = 1;
                    }

                }


                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
