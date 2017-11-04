package com.example.sebastian.eatup.fragments;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.sebastian.appdrawer.R;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class AboutFragment extends Fragment {
    @Nullable
    FrameLayout flHolder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout of the fragment
        View rootView = inflater.inflate(R.layout.fragment_about,container,false);

        //Force the method 'OnCreateOptionsMenu'
        setHasOptionsMenu(true);
        flHolder = (FrameLayout) rootView.findViewById(R.id.aboutFrame);




        return rootView;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAbout();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadAbout(){

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AboutBuilder builder = AboutBuilder.with(getActivity())
                                .setAppIcon(R.mipmap.ic_launcher)
                                .setAppName(R.string.app_name)
                                .setPhoto(R.mipmap.profile_picture)
                                .setCover(R.mipmap.profile_cover)
                                .setLinksAnimated(true)
                                .setDividerDashGap(13)
                                .setName("ConceptCreators")
                                .setSubTitle("WIP")
                                .setLinksColumnsCount(4)
                                .setBrief("'Description'")
                        /*.addGooglePlayStoreLink("8002078663318221363")
                        .addGitHubLink("jrvansuita")
                        .addBitbucketLink("jrvansuita")
                        .addFacebookLink("user")
                        .addTwitterLink("user")
                        .addInstagramLink("jnrvans")
                        .addGooglePlusLink("+JuniorVansuita")
                        .addYoutubeChannelLink("CaseyNeistat")
                        .addDribbbleLink("user")
                        .addLinkedInLink("arleu-cezar-vansuita-júnior-83769271")
                        .addEmailLink("vansuita.jr@gmail.com")
                        .addWhatsappLink("Jr", "+554799650629")
                        .addSkypeLink("user")
                        .addGoogleLink("user")
                        .addAndroidLink("user")
                        .addWebsiteLink("site")*/
                                .addFiveStarsAction()
                                .addMoreFromMeAction("Vansuita")
                                .setVersionNameAsAppSubTitle()
                                .addShareAction(R.string.app_name)
                                .addUpdateAction()
                                .setActionsColumnsCount(2)
                                .addFeedbackAction("vansuita.jr@gmail.com")
                                .addIntroduceAction((Intent) null)
                                .addHelpAction((Intent) null)
                                .addChangeLogAction((Intent) null)
                                .addRemoveAdsAction((Intent) null)
                                .addDonateAction((Intent) null)
                                .setWrapScrollView(true)
                                .setShowAsCard(false);

                        AboutView view = builder.build();

                        flHolder.addView(view);
                    }
                });
            }
        }, 300 );


            }


}
