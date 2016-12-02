package com.example.sebastian.appdrawer.appdrawer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class RequestsFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    TextView txFragment;
    String key;
    String username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests,container,false);
        setHasOptionsMenu(true);


        final ListView ownItems = (ListView)rootView.findViewById(R.id.ownItems);
        final ArrayList<String> itemList = new ArrayList<String>();
        final ArrayList<String> keys = new ArrayList<String>();
        final ArrayList<String> userRequests = new ArrayList<String>();
        final ArrayList<String> sentRequests = new ArrayList<String>();
        final ListView requestedItems = (ListView)rootView.findViewById(R.id.requestedItems);





        final DatabaseReference itemRequestsRef = rootRef.child("food");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Query query = itemRequestsRef.orderByChild("userID").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Data change","FIRST CHANGED");
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {Log.d("FOR","CHANGED");
                    if(postSnapshot.getValue() != null) {
                        key = postSnapshot.getKey();
                        keys.add(key);

                        itemRequestsRef.child("" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("Add title", "SECOND CHANGED");
                                Item item = dataSnapshot.getValue(Item.class);
                                long itemCount = postSnapshot.child("itemRequests").getChildrenCount();
                                if(item.title != null) {

                                    itemList.add(item.title + ": " + itemCount);
                                    ArrayAdapter ownItemsadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemList);

                                    ownItems.setAdapter(ownItemsadapter);
                                }
                                itemRequestsRef.removeEventListener(this);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //txFragment.setText(key);

                    }
                }
                query.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
            });





        ownItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Log.d("############","Items " +  keys.get(arg2) );
                if(keys.get(arg2) != null) {
                    key = keys.get(arg2);



                    DatabaseReference ItemRequestsRef = FirebaseDatabase.getInstance().getReference("food").child(key);
                    ItemRequestsRef.child("itemRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    final DatabaseReference usersNameRef = FirebaseDatabase.getInstance().getReference("users").child(postSnapshot.getValue().toString());
                                    usersNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            username = dataSnapshot.child("name").getValue().toString();
                                            userRequests.add(username+":   1 serving(s)");
                                            Log.d("Username",username);
                                            usersNameRef.removeEventListener(this);


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });

                                }
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = LayoutInflater.from(getActivity());
                                View convertView = (View) inflater.inflate(R.layout.custom, null);
                                alertDialog.setView(convertView);
                                alertDialog.setTitle("Order requests for "+itemList.get(arg2));
                                alertDialog.setMessage("Click on a user to confirm their order");
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                                final ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,userRequests);
                                lv.setAdapter(dialogAdapter);
                                final AlertDialog ad = alertDialog.show();
                                lv.deferNotifyDataSetChanged();


                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Toast toast = Toast.makeText(getActivity(),"Order has been confirmed for "+"\n"+userRequests.get(i), Toast.LENGTH_LONG);
                                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                        if( v != null) v.setGravity(Gravity.CENTER);
                                        toast.show();
                                        ad.dismiss();



                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    userRequests.clear();


                }else{
                    Toast.makeText(getActivity(), "Item no longer exists",
                            Toast.LENGTH_SHORT).show();

                }



            }


        });

        final DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("sentRequests");
        sentRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if(postSnapshot.getValue() != null) {
                            String requestedItemKey = postSnapshot.child("requestedItem").getValue().toString();
                            final String requestedAmount = postSnapshot.child("requestedAmount").getValue().toString();
                            Log.d("sentRequests postsnap",""+postSnapshot.child("requestedItem").getValue().toString());

                            itemRequestsRef.child(requestedItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("Add title", "SECOND CHANGED");
                                    Item item = dataSnapshot.getValue(Item.class);
                                    if(item.title != null) {

                                        sentRequests.add(item.title+" - "+requestedAmount+" serving(s)");
                                        ArrayAdapter sentRequestsadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sentRequests);
                                        requestedItems.setAdapter(sentRequestsadapter);
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

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {

        MenuItem item= menu.findItem(R.id.action_sort);
        item.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.action_settings);
        item2.setVisible(false);
    }


}

