package com.conwole.main.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conwole.main.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import xyz.hanks.library.bang.SmallBangView;

public class PostViewHolder extends RecyclerView.ViewHolder{

    public CircularImageView userProfileImage;
    public TextView userProfileName;
    public TextView postCategory;
    public ImageView postMoreOption;
    public TextView postTitle;
    public TextView postDescription;
    public ImageView postImage;
    public SmallBangView postLikeIconAnimation;
    public TextView postLikeCount;
    public ImageView postCommentIcon;
    public TextView postCommentCount;
    public TextView postTimeAgo;
    public LinearLayout postLike;
    public LinearLayout postComment;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        userProfileName = itemView.findViewById(R.id.user_profile_name);
        userProfileImage = itemView.findViewById(R.id.user_profile_image);
        postCategory = itemView.findViewById(R.id.post_category);
        postMoreOption = itemView.findViewById(R.id.post_more_option);
        postTitle = itemView.findViewById(R.id.post_title);
        postDescription = itemView.findViewById(R.id.post_description);
        postImage = itemView.findViewById(R.id.post_image);
        postLike = itemView.findViewById(R.id.post_like);
        postLikeIconAnimation = itemView.findViewById(R.id.post_like_icon_animation);
        postLikeCount = itemView.findViewById(R.id.post_like_count);
        postComment = itemView.findViewById(R.id.post_comment);
        postCommentIcon = itemView.findViewById(R.id.post_comment_icon);
        postCommentCount = itemView.findViewById(R.id.post_comment_count);
        postTimeAgo = itemView.findViewById(R.id.post_time_ago);

    }
}