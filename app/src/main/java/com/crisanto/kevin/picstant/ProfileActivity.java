package com.crisanto.kevin.picstant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.adapters.ImageArrayAdapter;
import com.crisanto.kevin.picstant.helpers.SharedPreferenceManager;
import com.crisanto.kevin.picstant.helpers.URLS;
import com.crisanto.kevin.picstant.helpers.VolleyHandler;
import com.crisanto.kevin.picstant.models.Image;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/* This activity runs when you visit other user's profile */
/* It takes the user id passed with  the intent to this activity */

public class ProfileActivity extends AppCompatActivity {

    TextView follow_this_profile, posts_num_tv, following_num_tv, followers_num_tv, display_name_tv, description_tv;
    CircleImageView other_user_profile_image;
    int other_user_id, user_id;
    GridView images_grid_layout;
    ArrayList<Image> arrayListImages;
    ImageArrayAdapter imageArrayAdapter;

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        posts_num_tv = (TextView) findViewById(R.id.posts_num_tv);
        following_num_tv = (TextView) findViewById(R.id.following_num_tv);
        followers_num_tv = (TextView) findViewById(R.id.followers_num_tv);
        display_name_tv = (TextView) findViewById(R.id.display_name_tv);
        description_tv = (TextView) findViewById(R.id.description);
        other_user_profile_image = (CircleImageView)findViewById(R.id.profile_image);
        images_grid_layout = (GridView) findViewById(R.id.images_grid_layout);

        follow_this_profile = (TextView) findViewById(R.id.follow_this_profile);
        follow_this_profile.setEnabled(false);

        other_user_id = getIntent().getIntExtra("user_id", 0);
        User user =  SharedPreferenceManager.getInstance(getApplicationContext()).getUserData();
        user_id = user.getId();

        arrayListImages = new ArrayList<Image>();
        imageArrayAdapter = new ImageArrayAdapter(this, 0, arrayListImages);
        images_grid_layout.setAdapter(imageArrayAdapter);

        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String image = getIntent().getStringExtra("image");
        int following = getIntent().getIntExtra("following", 0);
        int followers = getIntent().getIntExtra("followers", 0);
        int posts = getIntent().getIntExtra("posts", 0);
        String description = getIntent().getStringExtra("description");

        User userObject  = new User(user_id, email, username, image, following, followers, posts, description);

        IsCurrentUserFollowingThisUser();

        getOtherUsersProfileData(userObject);

        getAllImages();
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        getAllImages();
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();
        getAllImages();
    }*/

    private void getAllImages(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_images+other_user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONArray jsonArrayImages = jsonObject.getJSONArray("images");

                        for (int i = 0; i < jsonArrayImages.length(); i++) {
                            JSONObject jsonObjectSingleImage = jsonArrayImages.getJSONObject(i);

                            Image image = new Image(jsonObjectSingleImage.getInt("id"),
                                    jsonObjectSingleImage.getString("image_url"),jsonObjectSingleImage.getString("image_name"),
                                    jsonObjectSingleImage.getInt("user_id"));

                            arrayListImages.add(image);
                        }
                        imageArrayAdapter.notifyDataSetChanged();

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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20*1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);

    }

    private void getOtherUsersProfileData(User user){

        Picasso.get().load(user.getImage()).error(R.drawable.user).into(other_user_profile_image);
        posts_num_tv.setText(String.valueOf(user.getPosts()));
        following_num_tv.setText(String.valueOf(user.getFollowing()));
        followers_num_tv.setText(String.valueOf(user.getFollowers()));
        display_name_tv.setText(user.getUsername());
        description_tv.setText(user.getDescription());

        /*StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_other_user_data+other_user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){

                        JSONObject jsonObjectUserData = jsonObject.getJSONObject("user");

                        Picasso.get().load(jsonObjectUserData.getString("profile_image")).error(R.drawable.user).into(other_user_profile_image);
                        posts_num_tv.setText(String.valueOf(jsonObjectUserData.getInt("posts")));
                        following_num_tv.setText(String.valueOf(jsonObjectUserData.getInt("following")));
                        followers_num_tv.setText(String.valueOf(jsonObjectUserData.getInt("followers")));
                        display_name_tv.setText(jsonObjectUserData.getString("name"));
                        description.setText(jsonObjectUserData.getString("description"));

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

        VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequestToQueue(stringRequest);*/

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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20*1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

                                //  Immediately decrease the number of followers by 1
                                String text_num_of_followers = followers_num_tv.getText().toString();
                                int num_of_followers = 100;
                                try {
                                    num_of_followers = Integer.parseInt(text_num_of_followers);
                                }catch(NumberFormatException nfe) {
                                    nfe.printStackTrace();
                                }
                                followers_num_tv.setText(String.format("%d", num_of_followers - 1));

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

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20*1000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

                                //  Immediately increase the number of followers by 1
                                String text_num_of_followers = followers_num_tv.getText().toString();
                                int num_of_followers = 100;
                                try {
                                    num_of_followers = Integer.parseInt(text_num_of_followers);
                                }catch(NumberFormatException nfe) {
                                    nfe.printStackTrace();
                                }
                                followers_num_tv.setText(String.format("%d", num_of_followers + 1));

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

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20*1000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequestToQueue(stringRequest);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        int user_id = getIntent().getIntExtra("user_id", 0);

        if(user_id == 0){
            startActivity(new Intent(this, SearchActivity.class));
        }
    }
}
