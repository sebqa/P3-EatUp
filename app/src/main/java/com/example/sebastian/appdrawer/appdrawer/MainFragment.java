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

import com.example.sebastian.appdrawer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class MainFragment extends Fragment{


    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    //Arrays to draw dummy data from.(Here we would use data from the database).
    //String arrays are found in values/strings
    String[] itemtitle,itemcreator,itemprice;
    int[] itemdistance;
    int[] Img_res = {R.drawable.hjemmelavetburger,
            R.drawable.hakkeboef,
            R.drawable.flaeskesteg,
            R.drawable.lasagne,R.drawable.hjemmelavetburger,
            R.drawable.hakkeboef,
            R.drawable.flaeskesteg,
            R.drawable.lasagne,R.drawable.hjemmelavetburger,
            R.drawable.hakkeboef,
            R.drawable.flaeskesteg,
            R.drawable.lasagne,R.drawable.hjemmelavetburger,
            R.drawable.hakkeboef,
            R.drawable.flaeskesteg,
            R.drawable.lasagne,R.drawable.hjemmelavetburger,
            R.drawable.hakkeboef,
            R.drawable.flaeskesteg,
            R.drawable.lasagne};
    ArrayList<Item> arrayList = new ArrayList<Item>();


    FloatingActionButton toTop;

    //Firebase connection
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout specific to this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        //Cast the recyclerView such that we can manipulate it
        final RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        //Add a layout manager to control layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Tie the new strings to the String arrays we prepared.
        itemtitle = getResources().getStringArray(R.array.itemTitles);
        itemcreator = getResources().getStringArray(R.array.itemCreators);
        itemprice = getResources().getStringArray(R.array.itemPrices);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        //This for loop creates items with one element from each of the four arrays.
        // It adds the item to the arraylist, that will be shown in the recyclerView.
        int i = 0;
        for (String title : itemtitle) {
            Item item = new Item(title, itemcreator[i]+" nr "+(i+1), itemprice[i], Img_res[i]);
            arrayList.add(item);
            i++;
        }

        //New instance of our adapter class, which shows the arrayList.
        //That instance is tied to the recyclerView.
        adapter = new RecyclerAdapter(arrayList,getActivity());
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        //Casting the button that takes the user to the top.
        toTop = (FloatingActionButton)rootView.findViewById(R.id.toTop);
        //The onClickListener that scrolls to position '0'.
        toTop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        //Adds an onScrollListener.
        //Hides the 'toTop' button and the floating action button, when scrolling.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && toTop.isShown())
                {
                    toTop.hide();
                    ((MainActivity) getActivity()).hideFloatingActionButton();

                }
            }
            //Shows the 'toTop' button and the floating action button when not scrolling.
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    toTop.show();
                    ((MainActivity) getActivity()).showFloatingActionButton();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Supposed to clear the arrayList and add one new item.
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        int i = 0;
                        for (String title : itemtitle) {
                            Item item = new Item(title, itemcreator[i]+" nr "+(i+21), itemprice[i], Img_res[i]);
                            arrayList.add(item);
                            i++;
                        }
                            recyclerView.scrollToPosition(0);

                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }
        );


        return rootView;

    }

}
