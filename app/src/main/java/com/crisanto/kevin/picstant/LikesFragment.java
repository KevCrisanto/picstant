package com.crisanto.kevin.picstant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.Like;
import com.crisanto.kevin.picstant.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LikesFragment extends Fragment {

    ListView likes_lv;
    ArrayList<Like> arrayLikesList;
    LikeArrayAdapter likeArrayAdapter;
    ProgressDialog mProgressDialog;

    public LikesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        likes_lv = view.findViewById(R.id.likes_lv);
        arrayLikesList = new ArrayList<Like>();
        likeArrayAdapter = new LikeArrayAdapter(getContext(), R.layout.like_single_item, arrayLikesList);

        likes_lv.setAdapter(likeArrayAdapter);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading feed");
        mProgressDialog.setMessage("Loading the stories you liked...");

        // get the id of all the stories that the user has liked
        getAllStoryIds();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        likes_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Like like = arrayLikesList.get(position);

                if(like != null) {
                    String username = like.getStory_username();
                    String image = like.getStory_image();
                    String title = like.getStory_title();
                    int story_id = like.getStory_id();

                    Intent intent = new Intent(getContext(), CheckLikedImageActivity.class);
                    intent.putExtra("image_url", image);
                    intent.putExtra("image_title", title);
                    intent.putExtra("story_id", story_id);

                    startActivity(intent);
                }
            }
        });
    }

    private void getAllStoryIds(){

        User user = SharedPreferenceManager.getInstance(getContext()).getUserData();
        int user_id = user.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_story_ids+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONArray jsonArrayIds = jsonObject.getJSONArray("ids");
                        Log.i("arrayids", jsonArrayIds.toString());

                        String ids = jsonArrayIds.toString();
                        ids = ids.replace("[", "");
                        ids = ids.replace("]", "");

                        if(ids == null || ids.isEmpty()) {

                        } else{
                            getAllStoriesThatWeLiked(ids);
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
                    }
                }
        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void getAllStoriesThatWeLiked(String ids){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.all_stories_we_liked+ids, new Response.Listener<String>() {
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

                            Like like = new Like(jsonObjectSingleStory.getInt("id"), jsonObjectSingleStory.getString("image_url"),
                                    jsonObjectSingleStory.getString("username"), jsonObjectSingleStory.getString("title"));
                            arrayLikesList.add(like);
                        }
                        likeArrayAdapter.notifyDataSetChanged();

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

        VolleyHandler.getInstance(getContext()).addRequestToQueue(stringRequest);
    }

}
