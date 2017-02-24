package com.example.sebastian.appdrawer.appdrawer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Sebastian on 03-02-2017.
 */


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int index)
    {
        switch (index)
        {
            case 0:
                return new SentRequestsFragment();
            case 1:
                return new ReceivedRequestsFragment();
            case 2:
                return new ConfirmedRequestsFragment();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}