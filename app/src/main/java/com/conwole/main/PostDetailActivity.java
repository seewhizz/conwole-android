package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.Comment;
import com.conwole.main.Model.Post;
import com.conwole.main.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    private CircularImageView userProfileImage;
    private TextView userProfileName;
    private TextView postCategory;
    private TextView postTitle;
    private TextView postDescription;
    private ImageView postImage;
    private TextView postLikeCount;
    private TextView postCommentCount;
    private TextView postTimeAgo;
    private LinearLayout postLike;
    private LinearLayout postComment;
    private String postKey;
    private EditText fieldCommentText;
    private Button buttonPostComment;
    private DatabaseReference postReference;
    private DatabaseReference commentsReference;
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/info");
    private DatabaseReference userPostReference = FirebaseDatabase.getInstance().getReference("user-posts");
    private DatabaseReference postCommentReference = FirebaseDatabase.getInstance().getReference("post-comments");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Comments");

        postKey = getIntent().getStringExtra("post_key");

        // Initialize Database
        postReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postKey);
        commentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(postKey);

        userProfileName = findViewById(R.id.user_profile_name);
        userProfileImage = findViewById(R.id.user_profile_image);
        postCategory = findViewById(R.id.post_category);
        postTitle = findViewById(R.id.post_title);
        postDescription = findViewById(R.id.post_description);
        postImage = findViewById(R.id.post_image);
        postLike = findViewById(R.id.post_like);
        postLikeCount = findViewById(R.id.post_like_count);
        postComment = findViewById(R.id.post_comment);
        postCommentCount = findViewById(R.id.post_comment_count);
        postTimeAgo = findViewById(R.id.post_time_ago);

        fieldCommentText = findViewById(R.id.fieldCommentText);
        buttonPostComment = findViewById(R.id.buttonPostComment);

        fieldCommentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.equals("")) {
                    buttonPostComment.setEnabled(false);
                } else {
                    buttonPostComment.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment(fieldCommentText.getText().toString().trim());
            }
        });

        Post model = Common.currentPost;
        if (model != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_logo_png);
            if (model.getImage_url() != null) {
                Glide.with(PostDetailActivity.this).load(model.getImage_url()).apply(requestOptions).into(postImage);
                postImage.setAdjustViewBounds(true);
                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new StfalconImageViewer.Builder<String>(PostDetailActivity.this,
                                new ArrayList<>(Arrays.asList(model.getImage_url())),
                                new ImageLoader<String>() {
                                    @Override
                                    public void loadImage(ImageView imageView, String image) {
                                        Glide.with(PostDetailActivity.this).load(image).into(imageView);
                                    }
                                }).withHiddenStatusBar(false)
                                .withTransitionFrom(postImage)
                                .show();
                    }
                });
            }
            if (model.getUser_id() != null) {
                userReference.child(model.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Glide.with(PostDetailActivity.this).load(user.getProfile_pic_url()).apply(requestOptions).into(userProfileImage);
                        userProfileName.setText(user.getFull_name());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            if (model.getCategory() != null) {
                postCategory.setText(model.getCategory());
            }
            if (model.getTitle() != null) {
                postTitle.setText(model.getTitle());
            }
            if (model.getDescription() != null) {
                postDescription.setText(model.getDescription());
            }
            if (model.getTimestamp() != null) {
                long time = Long.parseLong(model.getTimestamp());
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(time);
                String date = DateFormat.format("EEE, dd MMM yyyy\nh:mm a", cal).toString();
                postTimeAgo.setText(date);
            }
            if (model.like_count == 0) {
                postLikeCount.setText("no like");
            } else if (model.like_count == 1) {
                postLikeCount.setText(model.like_count + " like");
            } else if (model.like_count > 1) {
                postLikeCount.setText(model.like_count + " likes");
            }
            commentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postCommentCount.setText(snapshot.getChildrenCount() + " Comments");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        getAllComments();

    }

    private void getAllComments() {

        RecyclerView recyclerView = findViewById(R.id.recyclerPostComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter adapter = new CommentAdapter(this,commentsReference);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Common.currentUser == null){
            Toast.makeText(this, "Data Refreshed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostDetailActivity.this,SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void postComment(String commentText) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users/info").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorId = user.getUser_id();

                        // Create new comment object
                        Comment comment = new Comment(uid, commentText);

                        // Push the comment, it will appear in the list
                        commentsReference.push().setValue(comment);

                        // Clear the field
                        fieldCommentText.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public CircularImageView commentPhoto;
        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            commentPhoto = itemView.findViewById(R.id.commentPhoto);
            authorView = itemView.findViewById(R.id.commentAuthorName);
            bodyView = itemView.findViewById(R.id.commentBodyText);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;
        private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/info");
        private DatabaseReference postCommentReference = FirebaseDatabase.getInstance().getReference("post-comments");

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            userReference.child(comment.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getProfile_pic_url() != null) {
                            Glide.with(mContext).load(user.getProfile_pic_url()).into(holder.commentPhoto);
                        }
                        if (user.getFull_name() != null) {
                            holder.authorView.setText(user.getFull_name());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.bodyView.setText(comment.getCommentText());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(comment.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || Common.currentPost.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                        builder.setTitle("Delete This Comment: ");
                        builder.setMessage(comment.getCommentText());
                        builder.setCancelable(true);
                        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                postCommentReference.child(Common.currentPost.getKey()).child(mCommentIds.get(position)).removeValue();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create().show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}