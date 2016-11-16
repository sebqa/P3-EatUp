package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Menu menu;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FloatingActionButton fab;
    Button signOutBtn;
    NavigationView navigationView;
    String userEmail;
    TextView tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set theme to the one that shows splash screen before the super.onCreate
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        signOutBtn = (Button)findViewById(R.id.signOutBtn);

        Boolean loggedin = getIntent().getBooleanExtra("isLoggedIn",false);
        userEmail = getIntent().getStringExtra("userEmail");


        //Hide title in the toolbar to make room for logo
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Create new object of the hamburger menu and define preliminary behaviour.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Set onClickListener to the menu, such that elements can be pressed independently.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        View headerview = navigationView.getHeaderView(0);
        tvEmail = (TextView)headerview.findViewById(R.id.tvEmail);
        tvEmail.setText(getIntent().getStringExtra("userEmail"));
        signOutBtn = (Button)headerview.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,CreateAccountActivity.class));
            }
        });

        //Initialize the main fragment's layout
        android.app.FragmentManager fn = getFragmentManager();
        fn.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        //Set onClickListener to the floating action button, and make it start new activity.
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,CreateItem.class));
            }
        });


    }



    //Override onBackPressed such that it doesn't close the app, but only the hamburger menu if it's open.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //Initialize the menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }


    //Handle the toolbar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Check which element was pressed
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sort){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Handle hamburger menu clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        android.app.FragmentManager fn = getFragmentManager();


        int id = item.getItemId();
        //Check which element was pressed
        if (id == R.id.nav_main) {
            //Replace current fragment with MainFragment
            fn.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

            //Show floating action button
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_map) {
            fn.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_favorites) {
            fn.beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_my_food) {
            fn.beginTransaction().replace(R.id.content_frame, new MyFoodFragment()).commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_requests) {
            fn.beginTransaction().replace(R.id.content_frame, new RequestsFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_settings) {
            fn.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_about) {
            fn.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Methods to hide/show the floating action button from fragments
    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }


}
