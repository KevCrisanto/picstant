package com.crisanto.kevin.picstant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* This activity runs when you visit other user's profile */
/* It takes the user id passed with  the intent to this activity */

public class ProfileActivity extends AppCompatActivity {

    TextView follow_this_profile;
    int other_user_id, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        follow_this_profile = (TextView) findViewById(R.id.follow_this_profile);
        follow_this_profile.setEnabled(false);

        other_user_id = getIntent().getIntExtra("user_id", 0);
        User user =  SharedPreferenceManager .getInstance(getApplicationContext()).getUserData();
        user_id = user.getId();

        IsCurrentUserFollowingThisUser();
    }

    private void IsCurrentUserFollowingThisUser(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.check_following_state+other_user_id+"&user_id="+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){

                        boolean isFollowing = jsonObject.getBoolean("state");

                        follow_this_profile.setEnabled(true);

                        if(isFollowing){

                            follow_this_profile.setText(getApplicationContext().getResources().getString(R.string.unfollow));
                            unfollowThisUser();

                        }else{

                            follow_this_profile.setText(getApplicationContext().getResources().getString(R.string.follow));
                            followThisUser();

                        }

                    } else{
                        Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequestToQueue(stringRequest);

    }

    private void unfollowThisUser(){
        follow_this_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.unfollow_this_person+other_user_id+"&user_id="+user_id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                boolean state = jsonObject.getBoolean("state");

                                follow_this_profile.setEnabled(false);
                                follow_this_profile.setText(getApplicationContext().getResources().getString(R.string.follow));

                            } else{
                                Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );

                VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequestToQueue(stringRequest);


            }
        });
    }

    private void followThisUser(){
        follow_this_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.follow_this_person+other_user_id+"&user_id="+user_id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                boolean state = jsonObject.getBoolean("state");

                                follow_this_profile.setEnabled(false);
                                follow_this_profile.setText(getApplicationContext().getResources().getString(R.string.unfollow));

                            } else{
                                Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );

                VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequestToQueue(stringRequest);


            }
        });
    }
}
