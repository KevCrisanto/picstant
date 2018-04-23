package com.crisanto.kevin.picstant;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    ImageView back_arrow;
    ListView comment_lv;
    EditText comment_et;
    ImageButton comment_send_btn;
    ArrayList<Comment> arrayListComments;
    CommentListAdapter commentListAdapter;
    ProgressDialog mProgressDialog;

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

        int story_id = getIntent().getIntExtra("story_id", 0);
        Log.i("story_id", String.valueOf(story_id));
        getAllComments(story_id);
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
                            Comment comment = new Comment(jsonObjectSingleStory.getInt("comment_id"), jsonObjectSingleStory.getInt("user_id"),
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
}
