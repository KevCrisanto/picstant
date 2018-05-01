package com.crisanto.kevin.picstant.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crisanto.kevin.picstant.ProfileActivity;
import com.crisanto.kevin.picstant.R;
import com.crisanto.kevin.picstant.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchListAdapter extends ArrayAdapter<User>{

    Context mContext;

    public SearchListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable User item) {
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
            view = li.inflate(R.layout.user_single_item, null);
        }
        User user = getItem(position);
        if(user != null){
            CircleImageView profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
            TextView username = view.findViewById(R.id.username_tv);

            Picasso.get().load(user.getImage()).error(R.drawable.user).into(profile_photo);

            username.setText(user.getUsername());

            visitProfile(view, user.getId(), user);

        }
        return view;
    }

    private void visitProfile(View view, final int id, final User user){

        final String username = user.getUsername();
        final String image = user.getImage();
        final String email = user.getEmail();
        final int following = user.getFollowing();
        final int followers = user.getFollowers();
        final int posts = user.getPosts();
        final String description = user.getDescription();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                profileIntent.putExtra("user_id", id);
                profileIntent.putExtra("username", username);
                profileIntent.putExtra("email", email);
                profileIntent.putExtra("image", image);
                profileIntent.putExtra("following", following);
                profileIntent.putExtra("followers", followers);
                profileIntent.putExtra("posts", posts);
                profileIntent.putExtra("description", description);

                getContext().startActivity(profileIntent);
            }
        });

    }
}
