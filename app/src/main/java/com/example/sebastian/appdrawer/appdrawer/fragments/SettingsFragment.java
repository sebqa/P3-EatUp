package com.example.sebastian.appdrawer.appdrawer.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastian.appdrawer.R;
import com.example.sebastian.appdrawer.appdrawer.MyPagerAdapter;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class SettingsFragment extends Fragment {
    FragmentPagerAdapter adapterViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings,container,false);
        setHasOptionsMenu(true);

        ViewPager vpPager = (ViewPager) rootView.findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(3);


        return rootView;


    }

}

