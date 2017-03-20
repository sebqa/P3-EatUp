package com.example.sebastian.appdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Sebastian on 30-12-2016.
 */

public class SentRequestsFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sentrequests, container, false);
        setHasOptionsMenu(true);



        final ArrayList<String> sentRequests = new ArrayList<String>();


        final ListView requestedItems = (ListView) rootView.findViewById(R.id.requestedItems);


        final DatabaseReference itemRequestsRef = rootRef.child("food");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        final DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("sentRequests");
        sentRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getValue() != null) {
                            String requestedItemKey = postSnapshot.child("requestedItem").getValue().toString();
                            final String requestedAmount = postSnapshot.child("requestedAmount").getValue().toString();
                            Log.d("sentRequests postsnap", "" + postSnapshot.child("requestedItem").getValue().toString());

                            itemRequestsRef.child(requestedItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("Add title", "SECOND CHANGED");
                                    Item item = dataSnapshot.getValue(Item.class);
                                    if (item != null) {

                                        sentRequests.add(item.title + " - " + requestedAmount + " serving(s)");
                                        ArrayAdapter sentRequestsadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sentRequests);
                                        requestedItems.setAdapter(sentRequestsadapter);
                                    } else {
                                        postSnapshot.getRef().setValue(null);
                                    }
                                    itemRequestsRef.removeEventListener(this);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;

    }

    @Override
    public void onStop() {
        super.onStop();
        //Detach listeners
    }
    public static SentRequestsFragment newInstance() {
        SentRequestsFragment fragment = new SentRequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


}

