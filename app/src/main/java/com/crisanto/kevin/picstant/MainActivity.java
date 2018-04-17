package com.crisanto.kevin.picstant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionDrawerToggle;
    NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layout variables
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mNavigationView = findViewById(R.id.main_nav_view);
        mActionDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionDrawerToggle);
        mActionDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Default fragment to be displayed
        //changeFragmentDisplay(mNavigationView.findItem(R.id.home));

        // listener for navigation view
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragmentDisplay(item);
                return true;
            }
        });
    }

    private void changeFragmentDisplay(MenuItem item){

        Fragment fragment = null;

        if (item.getItemId() == R.id.home) {
            fragment = new HomeFragment();
        } else if(item.getItemId() == R.id.search) {
            fragment = new SearchFragment();
        } else if(item.getItemId() == R.id.profile) {
            fragment = new ProfileFragment();
        } else if(item.getItemId() == R.id.likes) {
            fragment = new LikesFragment();
        } else if(item.getItemId() == R.id.camera) {
            fragment = new CameraFragment();
        } else if(item.getItemId() == R.id.logout) {
            Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }

        // Hide navigation drawer
        mDrawerLayout.closeDrawer(Gravity.START);

        if(fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_content, fragment);
            ft.commit();
        }
        //return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isUserLoggedIn = SharedPreferenceManager.getInstance(getApplicationContext()).isUserLoggedIn();
        if(!isUserLoggedIn) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else{

        }
    }
}
