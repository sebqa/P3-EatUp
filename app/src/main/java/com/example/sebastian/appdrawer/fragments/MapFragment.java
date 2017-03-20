package com.example.sebastian.appdrawer.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastian.appdrawer.R;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class MapFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map,container,false);
            setHasOptionsMenu(true);
            return rootView;


        }

}
