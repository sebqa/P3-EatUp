package com.example.sebastian.appdrawer.appdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Sebastian on 30-12-2016.
 */

public class ConfirmedRequestsFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    TextView txFragment;
    String key;
    String username;
    String receiverSignalID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmedrequests,container,false);
        setHasOptionsMenu(true);

        final ArrayList<String> confirmedRequests = new ArrayList<String>();

        final ListView confirmedRequestsList = (ListView)rootView.findViewById(R.id.confirmedItems);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




        DatabaseReference confirmedRequestsRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("confirmedRequests");
        final ArrayAdapter confirmedRequestsadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, confirmedRequests);

        confirmedRequestsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Item item = dataSnapshot.getValue(Item.class);
                Log.d("ItemValue",item.getTitle());
                confirmedRequests.add(0,item.getTitle()+": "+item.getAddress());

                getActivity().runOnUiThread(new Runnable(){
                    public void run(){
                        confirmedRequestsadapter.notifyDataSetChanged();

                    }
                });


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        confirmedRequestsList.setAdapter(confirmedRequestsadapter);



        return rootView;

    }


    @Override
    public void onStop() {
        super.onStop();
        //Detach listeners
    }

    public static ConfirmedRequestsFragment newInstance() {
        ConfirmedRequestsFragment fragment = new ConfirmedRequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


}
