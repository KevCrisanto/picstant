package com.crisanto.kevin.picstant.adapters;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.CommentsActivity;
import com.crisanto.kevin.picstant.R;
import com.crisanto.kevin.picstant.helpers.SharedPreferenceManager;
import com.crisanto.kevin.picstant.helpers.SquareImageView;
import com.crisanto.kevin.picstant.helpers.URLS;
import com.crisanto.kevin.picstant.helpers.VolleyHandler;
import com.crisanto.kevin.picstant.models.Story;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryListAdapter extends ArrayAdapter<Story> {

    private Context mContext;
    ArrayList<Story> storyArrayList;
    TextView number_of_likes;

    public StoryListAdapter(@NonNull Context context, int resource, ArrayList<Story> list) {
        super(context, resource, list);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Story getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Story item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            LayoutInflater li =LayoutInflater.from(mContext);
            view = li.inflate(R.layout.feed_single_item, null);
        }
        Story story = getItem(position);
        if(story != null){
            CircleImageView profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
            SquareImageView story_image = view.findViewById(R.id.story_image);
            TextView username = view.findViewById(R.id.username_tv);
            number_of_likes = view.findViewById(R.id.num_of_likes);
            TextView tags = view.findViewById(R.id.image_tags);
            TextView date = view.findViewById(R.id.image_time);
            TextView view_all_comments = view.findViewById(R.id.view_all_comments);
            final ImageView redHeart = view.findViewById(R.id.read_heart_like);
            final ImageView whiteHeart = view.findViewById(R.id.white_heart_like);

            if(story.getProfile_image().isEmpty()){
                profile_photo.setImageResource(R.drawable.user);
            }else{
                Picasso.get().load(story.getProfile_image()).error(R.drawable.user).into(profile_photo);
            }
            if(story.getStory_image().isEmpty()){
                profile_photo.setImageResource(R.drawable.user);
            }else{
                Picasso.get().load(story.getStory_image()).error(R.drawable.user).into(story_image);
            }

            username.setText(story.getUsername());
            number_of_likes.setText(mContext.getResources().getString(R.string.num_likes, story.getLike()));
            tags.setText(story.getTitle());
            date.setText(story.getTime());

            viewAllComments(view_all_comments, story.getId());

            int story_id = story.getId();
            didCurrentUserLikeThisStory(story_id, redHeart, whiteHeart);
        }
        return view;
    }

    private void didCurrentUserLikeThisStory(final int story_id, final ImageView redHeart, final ImageView whiteHeart){

        User user = SharedPreferenceManager.getInstance(getContext()).getUserData();
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

                        String text_with_num_of_likes = number_of_likes.getText().toString();
                        String string_num_of_likes = "";
                        int int_num_of_likes = 10;
                        String pattern = "[0-9]+";  // Any number
                        Pattern regEx = Pattern.compile(pattern);

                        // Find number of likes
                        Matcher m = regEx.matcher(text_with_num_of_likes);
                        if (m.find()) {
                            string_num_of_likes = m.group(0);
                            try {
                                int_num_of_likes = Integer.parseInt(string_num_of_likes);
                            }catch(NumberFormatException nfe) {
                                nfe.printStackTrace();
                            }
                        }
                        final int likes = int_num_of_likes;

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
                                // Immediately increase number of likes
                                number_of_likes.setText(getContext().getResources().getString(R.string.num_likes, likes + 1));
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
                                // Immediately decrease number of likes
                                number_of_likes.setText(getContext().getResources().getString(R.string.num_likes, likes - 1));
                            }
                        });
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

    private void viewAllComments(TextView view_all_comments, final int id){
        view_all_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewAllCommentsIntent = new Intent(getContext(), CommentsActivity.class);
                viewAllCommentsIntent.putExtra("story_id", id);
                getContext().startActivity(viewAllCommentsIntent);

            }
        });
    }
}
