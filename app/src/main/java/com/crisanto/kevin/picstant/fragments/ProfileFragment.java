package com.crisanto.kevin.picstant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.R;
import com.crisanto.kevin.picstant.SettingsActivity;
import com.crisanto.kevin.picstant.helpers.SharedPreferenceManager;
import com.crisanto.kevin.picstant.helpers.URLS;
import com.crisanto.kevin.picstant.helpers.VolleyHandler;
import com.crisanto.kevin.picstant.adapters.ImageArrayAdapter;
import com.crisanto.kevin.picstant.models.Image;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    TextView edit_profile, posts_num_tv, following_num_tv, followers_num_tv, display_name_tv, description_tv;
    CircleImageView user_profile_image;
    GridView images_grid_layout;
    ArrayList<Image> arrayListImages;
    ImageArrayAdapter imageArrayAdapter;
    User user;
    int user_id, posts, followers, following;
    String description, image;
    boolean firstVisit;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        edit_profile = view.findViewById(R.id.follow_this_profile);
        edit_profile.setText(getContext().getResources().getString(R.string.edit_profile));

        posts_num_tv = (TextView) view.findViewById(R.id.posts_num_tv);
        following_num_tv = (TextView) view.findViewById(R.id.following_num_tv);
        followers_num_tv = (TextView) view.findViewById(R.id.followers_num_tv);
        display_name_tv = (TextView) view.findViewById(R.id.display_name_tv);
        description_tv = (TextView) view.findViewById(R.id.description);
        user_profile_image = (CircleImageView)view.findViewById(R.id.profile_image);
        images_grid_layout = (GridView) view.findViewById(R.id.images_grid_layout);

        user = SharedPreferenceManager.getInstance(getContext()).getUserData();
        user_id = user.getId();

        arrayListImages = new ArrayList<>();
        imageArrayAdapter = new ImageArrayAdapter(getContext(), 0, arrayListImages);
        images_grid_layout.setAdapter(imageArrayAdapter);

        getUserData();
        getAllImages();
        firstVisit = true;

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstVisit){
            firstVisit = false;
        }else{
            getUserData();
            //images_grid_layout.setAdapter(null);
            arrayListImages.clear();
            imageArrayAdapter.notifyDataSetChanged();
            images_grid_layout.setAdapter(imageArrayAdapter);
            getAllImages();
        }
    }



    private void goToSettings(){
        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
        settingsIntent.putExtra("user_id", user.getId());
        settingsIntent.putExtra("username", user.getUsername());
        settingsIntent.putExtra("email", user.getEmail());
        settingsIntent.putExtra("profileImage", user.getImage());
        settingsIntent.putExtra("following", followers);
        settingsIntent.putExtra("followers", following);
        settingsIntent.putExtra("posts", posts);
        settingsIntent.putExtra("description", description);
        getContext().startActivity(settingsIntent);
    }

    private void getAllImages(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_images+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONArray jsonArrayImages = jsonObject.getJSONArray("images");

                        for (int i = 0; i < jsonArrayImages.length(); i++) {
                            JSONObject jsonObjectSingleImage= jsonArrayImages.getJSONObject(i);
                            Image image = new Image(jsonObjectSingleImage.getInt("id"),
                                    jsonObjectSingleImage.getString("image_url"),jsonObjectSingleImage.getString("image_name"),
                                    jsonObjectSingleImage.getInt("user_id"));

                            arrayListImages.add(image);
                        }
                        imageArrayAdapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getContext()).addRequestToQueue(stringRequest);

    }

    private void getUserData(){

        String username = user.getUsername();
        String email = user.getEmail();
        //String image = user.getImage();
        //final String description = user.getDescription();

        display_name_tv.setText(username);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_user_data+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                        posts = jsonObjectUser.getInt("posts");
                        followers = jsonObjectUser.getInt("followers");
                        following = jsonObjectUser.getInt("following");
                        description = jsonObjectUser.getString("description");
                        image = jsonObjectUser.getString("image");

                        if(!image.isEmpty()) {
                            Picasso.get().load(image).error(R.drawable.user).into(user_profile_image);
                        }

                        posts_num_tv.setText(String.valueOf(jsonObjectUser.getInt("posts")));
                        following_num_tv.setText(String.valueOf(jsonObjectUser.getInt("following")));
                        followers_num_tv.setText(String.valueOf(jsonObjectUser.getInt("followers")));
                        description_tv.setText(jsonObjectUser.getString("description"));

                    } else{
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueue(stringRequest);
    }

}
