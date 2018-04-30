package com.crisanto.kevin.picstant;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crisanto.kevin.picstant.models.Story;
import com.crisanto.kevin.picstant.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    ListView feed_lv;
    ArrayList<Story> arrayListStories;
    StoryListAdapter storyListAdapter;
    ProgressDialog mProgressDialog;
    JSONArray jsonArrayIds;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        feed_lv = view.findViewById(R.id.feed_lv);

        arrayListStories = new ArrayList<Story>();

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("News Feed");
        mProgressDialog.setMessage("Updating News Feed...");

        storyListAdapter = new StoryListAdapter(getContext(), R.layout.feed_single_item, arrayListStories);

        feed_lv.setAdapter(storyListAdapter);

        getFollowingIds();

        return view;
    }

    private void getFollowingIds(){

        mProgressDialog.show();

        User user = SharedPreferenceManager.getInstance(getContext()).getUserData();
        final int user_id = user.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_following_ids+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        //mProgressDialog.dismiss();
                        //JSONObject jsonObjecFollowing = jsonObject.getJSONObject("following");
                        //jsonArrayIds = jsonObjecFollowing.getJSONArray("ids");
                        JSONArray jsonArrayIds = jsonObject.getJSONArray("ids");
                        Log.i("arrayids", jsonArrayIds.toString());

                        String ids = jsonArrayIds.toString();
                        ids = ids.replace("[", "");
                        ids = ids.replace("]", "");


                        Log.i("arrayidsNew", ids);
                        if(ids == null|| ids.isEmpty()) {
                            mProgressDialog.dismiss();
                        } else{
                            getLatestNewsFeed(ids);
                        }

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
                        mProgressDialog.dismiss();
                    }
                }
        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueue(stringRequest);

    }

    private void getLatestNewsFeed(String ids){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.latest_news_feed+ids, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        mProgressDialog.dismiss();
                        JSONArray jsonArrayStories = jsonObject.getJSONArray("stories");
                        //JSONObject jsonObjectStories = jsonObject.getJSONObject("stories");
                        //JSONArray jsonArrayBigArray = jsonObjectStories.getJSONArray("big array");

                        for (int i = 0; i < jsonArrayStories.length(); i++) {
                            JSONObject jsonObjectSingleStory= jsonArrayStories.getJSONObject(i);
                            Story story = new Story(jsonObjectSingleStory.getInt("id"), jsonObjectSingleStory.getInt("user_id"),
                                    jsonObjectSingleStory.getInt("num_of_likes"), jsonObjectSingleStory.getString("image_url"),
                                    jsonObjectSingleStory.getString("title"), jsonObjectSingleStory.getString("time"),
                                    jsonObjectSingleStory.getString("profile_image"), jsonObjectSingleStory.getString("username"));
                            arrayListStories.add(story);
                        }
                        storyListAdapter.notifyDataSetChanged();

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
                        mProgressDialog.dismiss();
                    }
                }
        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueue(stringRequest);
        //Volley.newRequestQueue(getContext().getApplicationContext()).add(stringRequest);

    }

}
