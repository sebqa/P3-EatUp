package com.example.sebastian.appdrawer.appdrawer.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout of the fragment
        View rootView = inflater.inflate(R.layout.fragment_about,container,false);

        //Force the method 'OnCreateOptionsMenu'
        setHasOptionsMenu(true);
        final FrameLayout flHolder = (FrameLayout) rootView.findViewById(R.id.aboutFrame);

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
                .addGooglePlayStoreLink("8002078663318221363")
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
                .addWebsiteLink("site")
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
        return rootView;


    }

    public void loadAbout(){

    }

}
