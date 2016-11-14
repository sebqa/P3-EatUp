package com.example.sebastian.appdrawer.appdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.sebastian.appdrawer.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    boolean showSort = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        android.app.FragmentManager fn = getFragmentManager();
        fn.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CreateItem.class));
            }
        });

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sort){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        android.app.FragmentManager fn = getFragmentManager();


        int id = item.getItemId();

        if (id == R.id.nav_main) {
            fn.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_map) {
            fn.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            fab.setVisibility(View.INVISIBLE);


        } else if (id == R.id.nav_favorites) {
            fn.beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
            fab.setVisibility(View.INVISIBLE);
            showSort = false;

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
    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }


}
