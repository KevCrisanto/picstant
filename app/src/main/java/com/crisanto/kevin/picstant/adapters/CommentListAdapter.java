package com.crisanto.kevin.picstant.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crisanto.kevin.picstant.R;
import com.crisanto.kevin.picstant.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private Context mContext;
    ArrayList<Comment> commentArrayList;

    public CommentListAdapter(@NonNull Context context, int resource, ArrayList<Comment> list) {
        super(context, resource, list);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Comment getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Comment item) {
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
            view = li.inflate(R.layout.comment_single_item, null);
        }
        Comment comment = getItem(position);
        if(comment != null){
            CircleImageView profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
            TextView username = view.findViewById(R.id.username_tv);
            TextView comment_text = view.findViewById(R.id.comment_tv);
            TextView time = view.findViewById(R.id.time_tv);

            Picasso.get().load(comment.getProfile_image()).error(R.drawable.user).into(profile_photo);

            username.setText(comment.getUsername());
            comment_text.setText(comment.getComment_text());

            time.setText(comment.getTime());

//            if(comment.getTime() != null && comment.getTime().length() > 29) {
//                String complete_time = comment.getTime();
//                String month_day = complete_time.substring(4, 9); // month, day
//                String year = complete_time.substring(29, complete_time.length());
//
//                time.setText(mContext.getResources().getString(R.string.date, month_day, year));
//            }


        }
        return view;
    }

}
