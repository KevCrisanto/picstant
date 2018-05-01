package com.crisanto.kevin.picstant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckLikedImageActivity extends AppCompatActivity {

    TextView image_title, view_all_comments;
    SquareImageView story_image;
    ImageView back_arrow, redHeart, whiteHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_liked_image);

        image_title = (TextView) findViewById(R.id.image_title);
        story_image = (SquareImageView) findViewById(R.id.story_image);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        view_all_comments = (TextView)findViewById(R.id.view_all_comments);
        redHeart = (ImageView) findViewById(R.id.read_heart_like);
        whiteHeart = (ImageView) findViewById(R.id.white_heart_like);

        String image_url =  getIntent().getStringExtra("image_url");
        String title = getIntent().getStringExtra("image_title");
        final int story_id = getIntent().getIntExtra("story_id", 0);

        if(image_url != null && !image_url.isEmpty()){
            Picasso.get().load(image_url).error(R.drawable.user).into(story_image);
        }
        if(title != null && !title.isEmpty()){
            image_title.setText(title);
        }

        view_all_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAllCommentsIntent = new Intent(CheckLikedImageActivity.this, CommentsActivity.class);
                viewAllCommentsIntent.putExtra("story_id", story_id);
                CheckLikedImageActivity.this.startActivity(viewAllCommentsIntent);
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLikedImageActivity.super.finish();
            }
        });

        didCurrentUserLikeThisStory(story_id, redHeart, whiteHeart);
    }

    private void didCurrentUserLikeThisStory(final int story_id, final ImageView redHeart, final ImageView whiteHeart){

        User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUserData();
        final int user_id = user.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.did_user_like_story+story_id+"&user_id="+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        boolean didUserLikeThisStory = jsonObject.getBoolean("state");
                        if(didUserLikeThisStory){
                            redHeart.setVisibility(View.VISIBLE);
                            whiteHeart.setVisibility(View.INVISIBLE);
                        }else{
                            redHeart.setVisibility(View.INVISIBLE);
                            whiteHeart.setVisibility(View.VISIBLE);
                        }

                        whiteHeart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 1. hide white heart and show red heart
                                redHeart.setVisibility(View.VISIBLE);
                                whiteHeart.setVisibility(View.INVISIBLE);
                                // 2. increase number of likes in DB
                                increaseLikesForThisStory(story_id);
                                // 3. Add this user to likes db indicating he/she liked this story
                                addCurrentUserToLikesDB(story_id, user_id);
                            }
                        });

                        redHeart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 1. hide red heart and show white heart
                                redHeart.setVisibility(View.INVISIBLE);
                                whiteHeart.setVisibility(View.VISIBLE);
                                // 2. decrease number of likes in DB
                                decreaseLikesForThisStory(story_id);
                                // 3. Remove this user from liked db
                                removeCurrentUserFromLikesDB(story_id, user_id);
                            }
                        });
                    } else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void decreaseLikesForThisStory(int story_id){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.decrease_likes+story_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        String message = jsonObject.getString("message");
                        Log.i("decreaseLikes", message);

                    } else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void increaseLikesForThisStory(int story_id){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.increase_likes+story_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        String message = jsonObject.getString("message");
                        Log.i("increaseLikes", message);

                    } else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void removeCurrentUserFromLikesDB(int story_id, int user_id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.remove_user_from_likes_db+story_id+"&user_id="+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        String message = jsonObject.getString("message");
                        Log.i("removeUserFromLikes", message);

                    } else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void addCurrentUserToLikesDB(int story_id, int user_id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.add_user_to_likes_db+story_id+"&user_id="+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        String message = jsonObject.getString("message");
                        Log.i("addUserToLikes", message);

                    } else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }
}
