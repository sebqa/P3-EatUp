package com.example.sebastian.appdrawer.appdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class BrowseFragment extends Fragment {
    private static final int DATASET_COUNT = 60;
    ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);


        return rootView;
    }

    private void initDataset() {



    }

}
