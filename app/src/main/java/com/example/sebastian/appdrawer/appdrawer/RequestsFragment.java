package com.example.sebastian.appdrawer.appdrawer;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class RequestsFragment extends Fragment {
    private FragmentTabHost mTabHost;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);


        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Sent"),
                SentRequestsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Received"),
                ReceivedRequestsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentd").setIndicator("Confirmed"),
                ConfirmedRequestsFragment.class, null);

        int tabCount = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final View view = mTabHost.getTabWidget().getChildTabViewAt(i);
            if (view != null) {
                // reduce height of the tab
                view.getLayoutParams().height *= 1;

                //  get title text view
                final View textView = view.findViewById(android.R.id.title);
                if (textView instanceof TextView) {
                    // just in case check the type

                    // center text
                    ((TextView) textView).setGravity(Gravity.CENTER);
                    // wrap text
                    ((TextView) textView).setSingleLine(false);
                    ((TextView) textView).setAllCaps(true);
                    ((TextView) textView).setTextColor(Color.parseColor("#FFFFFF"));
                    // explicitly set layout parameters
                    textView.getLayoutParams().height = ViewGroup.LayoutParams.FILL_PARENT;
                    textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }

        }
        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#b71c1c"));
        }
        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#801212"));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String s) {
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#b71c1c"));
                }

                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#801212"));
            }
        });


        return rootView;
    }

}

