package com.example.sebastian.appdrawer.appdrawer;

import android.app.Fragment;
import android.content.Intent;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class MainFragment extends Fragment {


    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    public static final String FOOD = "food";
    private DatabaseReference mFirebaseDatabaseReference;
    //Arrays to draw dummy data from.(Here we would use data from the database).
    //String arrays are found in values/strings
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

        //Database initialize
       /* if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        } */
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

        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener()  {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                //Load items, and constructs instances of the Item class with them
                Item item = dataSnapshot.getValue(Item.class);
                //Add those instances to the arrayList shown in the Recyclerview, and makes sure it's
                //at the top.

                arrayList.add(0,item);

                adapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getUpdates(dataSnapshot);
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
