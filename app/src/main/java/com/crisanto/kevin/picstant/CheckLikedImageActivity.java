package com.crisanto.kevin.picstant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CheckLikedImageActivity extends AppCompatActivity {

    TextView image_title;
    SquareImageView story_image;
    ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_liked_image);

        image_title = (TextView) findViewById(R.id.image_title);
        story_image = (SquareImageView) findViewById(R.id.story_image);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);

        String image_url =  getIntent().getStringExtra("image_url");
        String title = getIntent().getStringExtra("image_title");

        if(image_url != null && !image_url.isEmpty()){
            Picasso.get().load(image_url).error(R.drawable.user).into(story_image);
        }
        if(title != null && !title.isEmpty()){
            image_title.setText(title);
        }
    }
}
