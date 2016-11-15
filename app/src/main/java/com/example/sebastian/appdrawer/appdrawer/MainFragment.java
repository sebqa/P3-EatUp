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

import java.util.ArrayList;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class MainFragment extends Fragment{


    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String[] itemtitle,itemcreator,itemprice;
    int[] itemdistance;
    int[] Img_res = {R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send,R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send,R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send,R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send,R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send};
    ArrayList<Item> arrayList = new ArrayList<Item>();

    String[] posts = new String[]{
            "post1","post2","post3","post4"};
    FloatingActionButton toTop;
    FloatingActionButton fab;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        final RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemtitle = getResources().getStringArray(R.array.itemTitles);
        itemcreator = getResources().getStringArray(R.array.itemCreators);
        itemprice = getResources().getStringArray(R.array.itemPrices);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        int i = 0;
        for (String title : itemtitle) {
            Item item = new Item(title, itemcreator[i]+" nr "+(i+1), itemprice[i], Img_res[i]);
            arrayList.add(item);
            i++;
        }

        adapter = new RecyclerAdapter(arrayList,getActivity());

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        toTop = (FloatingActionButton)rootView.findViewById(R.id.toTop);
        toTop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);


            }
        });

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


        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        arrayList.clear();


                            Item item = new Item(itemtitle[1], itemcreator[1], itemprice[1], Img_res[1]);
                            arrayList.add(item);
                            recyclerView.scrollToPosition(0);

                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }
        );


        return rootView;

    }

}
