package com.crisanto.kevin.picstant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.authentication.LoginActivity;
import com.crisanto.kevin.picstant.fragments.CameraFragment;
import com.crisanto.kevin.picstant.fragments.HomeFragment;
import com.crisanto.kevin.picstant.fragments.LikesFragment;
import com.crisanto.kevin.picstant.fragments.ProfileFragment;
import com.crisanto.kevin.picstant.fragments.SearchFragment;
import com.crisanto.kevin.picstant.helpers.SharedPreferenceManager;
import com.crisanto.kevin.picstant.helpers.URLS;
import com.crisanto.kevin.picstant.helpers.VolleyHandler;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionDrawerToggle;
    NavigationView mNavigationView;
    User user;
    String mProfileImage, mEmail, mDescription, mUsername;

    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("FRAGMENT_HOME");;
        if(currentFragment != null && currentFragment.isVisible())
        {
            super.finish();
        }
        else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar((Toolbar)findViewById(R.id.my_toolbar));
        getSupportActionBar().setTitle("Picstant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_menu);

        user = SharedPreferenceManager.getInstance(this).getUserData();

        // Default fragment to be displayed
        changeFragmentDisplay(R.id.home);

        // listener for navigation view
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragmentDisplay(item.getItemId());
                return true;
            }
        });
    }

    private void changeFragmentDisplay(int item){

        Fragment fragment = null;

        if (item == R.id.home) {
            //fragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_content, new HomeFragment(), "FRAGMENT_HOME");
            ft.commit();
        } else if(item == R.id.search) {
            fragment = new SearchFragment();
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
        } else if(item == R.id.profile) {
            fragment = new ProfileFragment();
        } else if(item == R.id.likes) {
            fragment = new LikesFragment();
        } else if(item == R.id.camera) {
            fragment = new CameraFragment();
        } else if(item == R.id.logout) {
            logUserOutIfTheyWant();
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

    private void logUserOutIfTheyWant(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out");
        builder.setMessage("Are you sure that you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getApplicationContext());
                sharedPreferenceManager.logUserOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }else if(item.getItemId() == R.id.settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.putExtra("profileImage", mProfileImage);
            settingsIntent.putExtra("email", mEmail);
            settingsIntent.putExtra("description", mDescription);
            settingsIntent.putExtra("username", mUsername);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isUserLoggedIn = SharedPreferenceManager.getInstance(getApplicationContext()).isUserLoggedIn();
        if(!isUserLoggedIn) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else{
            getUserData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isUserLoggedIn = SharedPreferenceManager.getInstance(getApplicationContext()).isUserLoggedIn();
        if(!isUserLoggedIn) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else{
            getUserData();
        }
    }

    private void getUserData(){
        View navHeader = mNavigationView.getHeaderView(0);
        final ImageView user_iv = navHeader.findViewById(R.id.user_iv);
        final TextView user_name = navHeader.findViewById(R.id.user_name);
        final TextView user_email = navHeader.findViewById(R.id.user_email);

        int user_id = user.getId();
        //String username = user.getUsername();
        //user_name.setText(username);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_user_data+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                        mUsername = jsonObjectUser.getString("username");
                        mEmail = jsonObjectUser.getString("email");
                        mProfileImage = jsonObjectUser.getString("image");
                        mDescription = jsonObjectUser.getString("description");

                        user_email.setText(mEmail);
                        user_name.setText(mUsername);


                        if(!mProfileImage.isEmpty()) {
                            Picasso.get().load(mProfileImage).error(R.drawable.user).into(user_iv);
                        }

                    } else{
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }
}
