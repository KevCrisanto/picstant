package com.crisanto.kevin.picstant;
import com.crisanto.kevin.picstant.models.Story;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryListAdapter extends ArrayAdapter<Story> {

    private Context mContext;
    ArrayList<Story> storyArrayList;

    public StoryListAdapter(Context context, int resource, ArrayList list) {
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
            TextView number_of_likes = view.findViewById(R.id.num_of_likes);
            TextView tags = view.findViewById(R.id.image_tags);
            TextView date = view.findViewById(R.id.image_time);

            profile_photo.setImageURI(Uri.parse(story.getProfile_image()));
            story_image.setImageURI(Uri.parse(story.getStory_image()));

            username.setText(story.getUsername());
            number_of_likes.setText(mContext.getResources().getString(R.string.likes, story.getLike()));
            tags.setText(story.getTitle());
            date.setText(story.getTime());

        }
        return view;
    }
}
