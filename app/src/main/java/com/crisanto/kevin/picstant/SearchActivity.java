package com.crisanto.kevin.picstant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.adapters.SearchListAdapter;
import com.crisanto.kevin.picstant.helpers.SharedPreferenceManager;
import com.crisanto.kevin.picstant.helpers.URLS;
import com.crisanto.kevin.picstant.helpers.VolleyHandler;
import com.crisanto.kevin.picstant.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {

    EditText search_et;
    ListView search_results_lv;
    ArrayList<User> arrayListUsers;
    SearchListAdapter searchListAdapter;
    int user_id;

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        startActivity(new Intent(SearchActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_et = (EditText) findViewById(R.id.search_et);
        search_results_lv = (ListView) findViewById(R.id.search_results_lv);

        arrayListUsers = new ArrayList<User>();
        searchListAdapter = new SearchListAdapter(SearchActivity.this, R.layout.user_single_item, arrayListUsers);
        search_results_lv.setAdapter(searchListAdapter);

        User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUserData();
        user_id = user.getId();

        search_et.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                // user is typing: reset already started timer (if existing)
                if (timer != null) {
                    timer.cancel();
                }

            }

            @Override

            public void afterTextChanged(final Editable s) {
                // user typed: start the timer
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // do your actual work here
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arrayListUsers.clear();
                                searchListAdapter.clear();
                                if(!s.toString().equals("")) {
                                    getSimilarUSers(s.toString());
                                }
                            }
                        });
                    }
                }, 500);
            }
        });
    }

    private void getSimilarUSers(String text){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_similar_users+text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.getBoolean("error")){

                        JSONArray jsonArrayUsers = jsonObject.getJSONArray("users");
                        //JSONObject jsonObjectStories = jsonObject.getJSONObject("stories");
                        //JSONArray jsonArrayBigArray = jsonObjectStories.getJSONArray("big array");

                        for (int i = 0; i < jsonArrayUsers.length(); i++) {
                            JSONObject jsonObjectSingleUser= jsonArrayUsers.getJSONObject(i);

                            if(user_id != jsonObjectSingleUser.getInt("id")) {
                                User user = new User(jsonObjectSingleUser.getInt("id"), jsonObjectSingleUser.getString("email"),
                                        jsonObjectSingleUser.getString("username"), jsonObjectSingleUser.getString("image"),
                                        jsonObjectSingleUser.getInt("following"), jsonObjectSingleUser.getInt("followers"),
                                        jsonObjectSingleUser.getInt("posts"), jsonObjectSingleUser.getString("description"));

                                //arrayListUsers.add(user);
                                searchListAdapter.add(user);
                            }
                        }
                        //searchListAdapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(SearchActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20*1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);

    }
}
