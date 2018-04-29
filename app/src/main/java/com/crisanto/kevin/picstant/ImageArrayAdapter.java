package com.crisanto.kevin.picstant;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crisanto.kevin.picstant.models.Image;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageArrayAdapter extends ArrayAdapter<Image>{

    Context mContext;
    User user;

    public ImageArrayAdapter(@NonNull Context context, int resource, @NonNull List<Image> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Image getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Image item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ImageView imageView;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;

        if(convertView == null){

            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(screenWidth/3, screenWidth/3));
            //imageView.setLayoutParams(new GridView.LayoutParams((int) mContext.getResources().getDimension(R.dimen.grid), (int)mContext.getResources().getDimension(R.dimen.grid)));
            //imageView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)getContext().getResources().getDimension(R.dimen.grid)));
            imageView.setPadding(1,1,1,1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else{
            imageView = (ImageView) convertView;
        }
        Image image = getItem(position);
        if(image != null){
            //if(!image.getImage_url().isEmpty()) {
                Picasso.get().load(image.getImage_url()).error(R.drawable.user).into(imageView);
            //}
        }

        showSingleImage(imageView, image);
        return imageView;
    }

    private void showSingleImage(ImageView imageView, Image image){

        final String image_name = image.getImage_name();
        final String image_url = image.getImage_url();
        final int user_id = image.getUser_id();
        final int image_id = image.getId();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleImageIntent = new Intent(getContext(), SingleStoryActivity.class);
                singleImageIntent.putExtra("image_name", image_name);
                singleImageIntent.putExtra("image_url", image_url);
                singleImageIntent.putExtra("user_id", user_id);
                singleImageIntent.putExtra("image_id", image_id);
                if(user != null) {
                    singleImageIntent.putExtra("username", user.getUsername());
                    singleImageIntent.putExtra("followers", user.getFollowers());
                    singleImageIntent.putExtra("following", user.getFollowing());
                    singleImageIntent.putExtra("email", user.getEmail());
                    singleImageIntent.putExtra("posts", user.getPosts());
                    singleImageIntent.putExtra("image", user.getImage());
                }

                getContext().startActivity(singleImageIntent);
            }
        });
    }
}


