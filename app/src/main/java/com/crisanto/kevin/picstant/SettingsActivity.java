package com.crisanto.kevin.picstant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    TextView edit_image, done_edit, username_tv, email_tv;
    EditText desc_et;
    CircleImageView profile_image;
    ImageView back_arrow;
    ProgressDialog mProgressDialog;

    final int CHANGE_PROFILE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edit_image = findViewById(R.id.edit_image);
        done_edit = findViewById(R.id.done_edit);
        username_tv = findViewById(R.id.username_tv);
        desc_et = findViewById(R.id.desc_et);
        email_tv = findViewById(R.id.email_tv);
        profile_image = findViewById(R.id.profile_image);
        back_arrow = findViewById(R.id.back_arrow);

        mProgressDialog = new ProgressDialog(this);

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewProfileImage();
            }
        });

        done_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        String profileImage = getIntent().getStringExtra("profileImage");
        String email = getIntent().getStringExtra("email");
        String description = getIntent().getStringExtra("description");
        String username = getIntent().getStringExtra("username");

        if(!profileImage.isEmpty()){
            Picasso.get().load(profileImage).error(R.drawable.user).into(profile_image);
        }

        if(!email.isEmpty()){
            email_tv.setText(email);
        }

        if(!username.isEmpty()){
            username_tv.setText(email);
        }

        if(!description.isEmpty()){
            desc_et.setText(description);
        }
    }

    private  void updateUserData(){
        final String description = desc_et.getText().toString();
        User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUserData();
        final int user_id = user.getId();

        mProgressDialog.setTitle("Updating profile");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.update_user_data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                mProgressDialog.dismiss();
                                String descriptionString = jsonObject.getString("description");
                                //JSONObject jsonObjectImage = jsonObject.getJSONObject("image");

                                desc_et.setText(descriptionString);
                                // Update email
                                SharedPreferenceManager.getInstance(getApplicationContext()).updateDescription(descriptionString);
                                // Return to profile
                                SettingsActivity.super.finish();

                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> imageMap = new HashMap<>();
                imageMap.put("description", description);
                imageMap.put("user_id", String.valueOf(user_id));
                return imageMap;
            }

        }; // end of StringRequest

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
    }

    private void getNewProfileImage(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHANGE_PROFILE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHANGE_PROFILE_IMAGE){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    if(bitmap != null) {
                        sendImageToDB(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(SettingsActivity.this, "Couldn't get bitmap of image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendImageToDB(Bitmap bitmap){
        final String imageString = convertImageToString(bitmap);
        User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUserData();
        final int user_id = user.getId();
        String date = dateOfImage();
        final String image_name =  String.valueOf(user_id) + "-" + date;

        if(imageString != null){
            mProgressDialog.setTitle("Uploading profile image");
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.update_profile_image,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (!jsonObject.getBoolean("error")) {
                                    mProgressDialog.dismiss();
                                    String imageString = jsonObject.getString("image");
                                    //JSONObject jsonObjectImage = jsonObject.getJSONObject("image");

                                    if(!imageString.isEmpty()) {
                                        Picasso.get().load(imageString).error(R.drawable.user).into(profile_image);
                                        // Update profile image
                                        SharedPreferenceManager.getInstance(getApplicationContext()).updateDescription(imageString);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    mProgressDialog.dismiss();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> imageMap = new HashMap<>();
                    imageMap.put("image_encoded", imageString);
                    imageMap.put("image_name", image_name);
                    imageMap.put("user_id", String.valueOf(user_id));
                    return imageMap;
                }

            }; // end of StringRequest

            VolleyHandler.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
        }
    }

    private String dateOfImage(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.toString();
        return timestamp.toString();
    }

    private String convertImageToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageByteArray = baos.toByteArray();
        String result = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return result;
    }
}
