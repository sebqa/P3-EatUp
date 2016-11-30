package com.example.sebastian.appdrawer.appdrawer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by Sebastian on 02-11-2016.
 */

public class RequestsFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    TextView txFragment;
    String key;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests,container,false);
        setHasOptionsMenu(true);
        txFragment = (TextView) rootView.findViewById(R.id.textView7);


        ListView ownItems = (ListView)rootView.findViewById(R.id.ownItems);
        final ArrayList<String> list = new ArrayList<String>();


        final DatabaseReference itemRequestsRef = rootRef.child("food");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = itemRequestsRef.orderByChild("userID").equalTo(user.getUid());
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    key = postSnapshot.getKey();
                    list.add(""+key);
                    //txFragment.setText(key);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };

        query.addValueEventListener(valueEventListener);
        list.add(key);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);

        ownItems.setAdapter(adapter);







        return rootView;

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

