package com.example.sebastian.appdrawer.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.sebastian.appdrawer.R;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Sebastian on 02-11-2016.
 */


public class FavoriteFragment extends Fragment {



    Button addTagBtn;
    EditText addNewTag;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        addNewTag = (EditText)rootView.findViewById(R.id.addANewTag);
        addTagBtn = (Button)rootView.findViewById(R.id.addTagButton);


        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newTag = addNewTag.getText().toString().trim();
                Log.d("TagString",newTag);
                //addTagBtn.setText(newTag);
                OneSignal.sendTag(newTag,"true");



            }
        });
        OneSignal.getTags(new OneSignal.GetTagsHandler() {
            @Override
            public void tagsAvailable(JSONObject tags) {
                if(tags != null){
                Log.d("debug", tags.toString());
                JSONArray tagsList = tags.names();
                Log.d("tagsList",tagsList.toString());
            }}
        });


            return rootView;
    }

}
