package com.crisanto.kevin.picstant;

import android.app.ProgressDialog;
import android.content.Intent;
//import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.Comment;
import com.crisanto.kevin.picstant.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    ImageView back_arrow;
    ListView comment_lv;
    EditText comment_et;
    ImageButton comment_send_btn;
    ArrayList<Comment> arrayListComments;
    CommentListAdapter commentListAdapter;
    ProgressDialog mProgressDialog;
    String profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        comment_lv = (ListView) findViewById(R.id.comment_lv);
        comment_et = (EditText) findViewById(R.id.comment_et);
        comment_send_btn = (ImageButton) findViewById(R.id.comment_send_btn);

        arrayListComments = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(getApplicationContext(), R.layout.comment_single_item, arrayListComments);
        comment_lv.setAdapter(commentListAdapter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("News Feed");
        mProgressDialog.setMessage("Updating News Feed...");

        final int story_id = getIntent().getIntExtra("story_id", 0);
        Log.i("story_id", String.valueOf(story_id));
        getAllComments(story_id);

        comment_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataToSendToDB(story_id);
                //sendCommentToDB(story_id);
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goBackToFeed();
                CommentsActivity.super.finish();
            }
        });
    }

    private void getDataToSendToDB(final int story_id){

        User user = SharedPreferenceManager.getInstance(this).getUserData();
        final int user_id = user.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_user_data+user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                        profile_image = jsonObjectUser.getString("image");
                        sendCommentToDB(story_id);

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

        VolleyHandler.getInstance(getApplicationContext().getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void getAllComments(int story_id){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_comments+story_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        mProgressDialog.dismiss();
                        JSONArray jsonArrayComments = jsonObject.getJSONArray("comments");

                        for (int i = 0; i < jsonArrayComments.length(); i++) {
                            JSONObject jsonObjectSingleStory= jsonArrayComments.getJSONObject(i);
                            Comment comment = new Comment(jsonObjectSingleStory.getInt("id"), jsonObjectSingleStory.getInt("user_id"),
                                    jsonObjectSingleStory.getInt("story_id"), jsonObjectSingleStory.getString("username"),
                                    jsonObjectSingleStory.getString("profile_image"), jsonObjectSingleStory.getString("comment_text"),
                                    jsonObjectSingleStory.getString("time"));

                            arrayListComments.add(comment);
                        }

                        commentListAdapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(CommentsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        );

        VolleyHandler.getInstance(this).addRequestToQueue(stringRequest);
    }

    private void sendCommentToDB(final int story_id){

        final String comment_text = comment_et.getText().toString();

        if(comment_text.isEmpty() || comment_text == ""){
            return;
        }

        User user = SharedPreferenceManager.getInstance(this).getUserData();
        final String username = user.getUsername();
        final int user_id = user.getId();
        //final String profile_image = user.getImage();
        final String currentTime = currentReadableTime();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.send_comment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){
                        mProgressDialog.dismiss();
                        JSONObject jsonObjectUser = jsonObject.getJSONObject("comment");

                        Comment comment = new Comment(jsonObjectUser.getInt("comment_id"), jsonObjectUser.getInt("user_id"),
                                jsonObjectUser.getInt("story_id"), jsonObjectUser.getString("username"),
                                jsonObjectUser.getString("profile_image"), jsonObjectUser.getString("comment_text"),
                                jsonObjectUser.getString("time"));

                        arrayListComments.add(comment);
                        commentListAdapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(CommentsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map commentData = new HashMap<>();
                commentData.put("user_id", String.valueOf(user_id));
                commentData.put("story_id", String.valueOf(story_id));
                commentData.put("username", username);
                commentData.put("profile_image", profile_image);
                commentData.put("comment_text", comment_text);
                commentData.put("time", currentTime);
                return commentData;
            }

        }; // end of StringRequest

        comment_et.setText("");

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);

    }

    private void goBackToFeed(){
        startActivity(new Intent(this, MainActivity.class));
    }

    private String currentReadableTime(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date initial_date = new Date(timestamp.getTime());
        String input = initial_date.toString();

        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss ZZZZ yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy");
        Date date;
        String formattedDate = null;
        try {
            date = parser.parse(input);
            formattedDate = formatter.format(date);
        } catch(java.text.ParseException e){
            e.printStackTrace();
        }
        return formattedDate;
    }

}
