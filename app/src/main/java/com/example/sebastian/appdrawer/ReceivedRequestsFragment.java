package com.example.sebastian.appdrawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;

/**
 * Created by Sebastian on 30-12-2016.
 */

public class ReceivedRequestsFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    private ProgressDialog progress;

    String key;
    String username;
    String receiverSignalID;
    String address;
    final ArrayList<String> itemList = new ArrayList<String>();
    final ArrayList<String> keys = new ArrayList<String>();
    final ArrayList<String> userRequests = new ArrayList<String>();

    final ArrayList<String> userRequestsKeys = new ArrayList<String>();
    Query query;
    ChildEventListener CEL;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receivedrequests,container,false);
        setHasOptionsMenu(true);

        OneSignal.startInit(getActivity());


        final ListView ownItems = (ListView)rootView.findViewById(R.id.ownItems);





        final DatabaseReference itemRequestsRef = rootRef.child("food");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        query = itemRequestsRef.orderByChild("userID").equalTo(user.getUid());

        CEL = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    key = dataSnapshot.getKey();
                    keys.add(key);
                    final ArrayAdapter ownItemsadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemList);

                    itemRequestsRef.child("" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("Add title", "SECOND CHANGED");
                            Item item = dataSnapshot.getValue(Item.class);
                            long itemCount = dataSnapshot.child("itemRequests").getChildrenCount();
                            if (item.title != null) {

                                itemList.add(item.title + ": " + itemCount+ " requests!");
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ownItemsadapter.notifyDataSetChanged();
                                    }
                                });

                            }
                            itemRequestsRef.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    ownItems.setAdapter(ownItemsadapter);

                }
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
        };
        query.addChildEventListener(CEL);




        ownItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                Log.d("############","Items " +  keys.get(arg2) );
                if(keys.get(arg2) != null) {
                    key = keys.get(arg2);



                    DatabaseReference ItemRequestsRef = FirebaseDatabase.getInstance().getReference("food").child(key);
                    ItemRequestsRef.child("itemRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    final DatabaseReference usersNameRef = FirebaseDatabase.getInstance().getReference("users")
                                            .child(postSnapshot.getValue().toString());
                                    usersNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            username = dataSnapshot.child("name").getValue().toString();
                                            userRequestsKeys.add(dataSnapshot.getKey());
                                            userRequests.add(username);
                                            Log.d("Username",dataSnapshot.getKey());

                                            usersNameRef.removeEventListener(this);



                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });
                                }
                                showLoadingDialog();
                                showAlertDialog(userRequests,itemList.get(arg2));
                                dismissLoadingDialog();


                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else{
                    Toast.makeText(getActivity(), "Item no longer exists",
                            Toast.LENGTH_SHORT).show();

                }



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
    public void onPause() {
        super.onPause();

        itemList.clear();
        keys.clear();
        userRequests.clear();
        userRequestsKeys.clear();
        query.removeEventListener(CEL);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissLoadingDialog();

    }

    public void showAlertDialog(final ArrayList userRequests, String itemName){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View convertView = (View) inflater.inflate(R.layout.custom, null);
        final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        final ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,userRequests);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setView(convertView);
        alertDialog.setTitle("Order requests for "+itemName);
        alertDialog.setMessage("Click on a user to confirm their order");
        lv.setAdapter(dialogAdapter);
        lv.invalidateViews();
        dialogAdapter.notifyDataSetChanged();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                userRequests.clear();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userRequests.clear();

                dialog.cancel();
            }
        });

        final AlertDialog ad = alertDialog.show();



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(getActivity(),"Order has been confirmed for "+"\n"+userRequests.get(i), Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
                toast.show();

                //final DatabaseReference requestConfirmationRef = FirebaseDatabase.getInstance().getReference("users").child(key).child("sentRequests").push();
                DatabaseReference confirmReqKey = FirebaseDatabase.getInstance().getReference("food").child(key).child("confirmedReq");
                confirmReqKey.push().setValue(userRequestsKeys.get(i));

                DatabaseReference oneSignalRef = FirebaseDatabase.getInstance().getReference("users").child(userRequestsKeys.get(i)).child("oneSignalID");
                oneSignalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            Log.d("OneSignalIDsnap",dataSnapshot.getValue().toString());
                            receiverSignalID = dataSnapshot.getValue().toString().trim();
                            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("food").child(key).child("address");
                            addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    address = dataSnapshot.getValue().toString();
                                    String method = "confNoti";

                                    BackgroundTask backgroundTask = new BackgroundTask(getActivity());
                                    backgroundTask.execute(method,receiverSignalID,key,address);
                                    ad.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                                                    /*try {
                                                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'An order has been confirmed'}, 'include_player_ids': ['" + receiverSignalID + "']}"), null);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }*/

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //setUser.setValue("NEW NOTIFICATION");


                Log.d("itemKey",key);

                dismissLoadingDialog();


            }
        });
    }

    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(getContext());
            progress.setTitle("Loading");
            progress.setMessage("Please wait...");
        }
        progress.show();
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
    public static ReceivedRequestsFragment newInstance() {
        ReceivedRequestsFragment fragment = new ReceivedRequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

}
