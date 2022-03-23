package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.Post;
import com.conwole.main.Model.User;
import com.conwole.main.ViewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class PostCategoryActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> postAdapter;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/info");
    private DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("posts");
    private DatabaseReference postCommentReference = FirebaseDatabase.getInstance().getReference("post-comments");
    private DatabaseReference userPostReference = FirebaseDatabase.getInstance().getReference("user-posts");

    @Override
    protected void onStart() {
        super.onStart();

        if(Common.currentUser == null){
            Toast.makeText(this, "Data Refreshed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostCategoryActivity.this,SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_category);

        String category = getIntent().getStringExtra("category");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(category);
        actionBar.setElevation(0);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPost(category);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        loadPost(category);


    }

    private void loadPost(String category) {

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        Query query = postReference.orderByChild("category").equalTo(category).limitToLast(100);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();

        postAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                if(model!=null){

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.ic_logo_png);

                    if(model.getImage_url()!= null){

                        Glide.with(PostCategoryActivity.this).load(model.getImage_url()).apply(requestOptions).into(holder.postImage);
                        holder.postImage.setAdjustViewBounds(true);

                    }
                    if(model.getUser_id()!= null){
                        userReference.child(model.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                Glide.with(PostCategoryActivity.this).load(user.getProfile_pic_url()).apply(requestOptions).into(holder.userProfileImage);
                                holder.userProfileName.setText(user.getFull_name());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(model.getCategory()!= null){
                        holder.postCategory.setText(model.getCategory());
                    }
                    if(model.getTitle()!= null){
                        holder.postTitle.setText(model.getTitle());
                    }
                    if(model.getDescription()!= null){
                        holder.postDescription.setText(model.getDescription());
                    }
                    if(model.getTimestamp()!= null){
                        long time = Long.parseLong(model.getTimestamp());
                        PrettyTime p = new PrettyTime();
                        Date date = new Date(time);
                        holder.postTimeAgo.setText(p.format(date));
                    }
                    if(model.like_count == 0) {
                        holder.postLikeCount.setText("");
                    }else if(model.like_count == 1){
                        holder.postLikeCount.setText(model.like_count + " like");
                    }else if(model.like_count > 1){
                        holder.postLikeCount.setText(model.like_count + " likes");
                    }
                    holder.postLikeIconAnimation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            onLikeClicked(userPostReference.child(model.getUser_id()).child(model.getKey()));
                            onLikeClicked(postReference.child(model.getKey()));

                            if (holder.postLikeIconAnimation.isSelected()) {
                                holder.postLikeIconAnimation.setSelected(false);
                            } else {
                                // if not selected only
                                // then show animation.
                                holder.postLikeIconAnimation.setSelected(true);
                                holder.postLikeIconAnimation.likeAnimation();
                            }
                        }
                    });
                    holder.postCommentIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(PostCategoryActivity.this, PostDetailActivity.class);
                            intent.putExtra("post_key",model.getKey());
                            Common.currentPost = model;
                            startActivity(intent);
                        }
                    });
                    if(model.likes.containsKey(uid)){
                        holder.postLikeIconAnimation.setSelected(true);
                    }else {
                        holder.postLikeIconAnimation.setSelected(false);
                    }
                    if(model.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        holder.postMoreOption.setVisibility(View.VISIBLE);
                        holder.postMoreOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Check is user is owner
                                if(model.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PostCategoryActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                                    builder.setTitle("Delete This Post: ");
                                    builder.setMessage(model.getTitle());
                                    builder.setCancelable(true);
                                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialogInterface, int i) {
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference storageReference = storage.getReferenceFromUrl(model.getImage_url());
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    postReference.child(model.getKey()).removeValue();
                                                    postCommentReference.child(model.getKey()).removeValue();
                                                    userPostReference.child(model.getUser_id()).child(model.getKey()).removeValue();
                                                    dialogInterface.dismiss();
                                                }
                                            });
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
                    }else {
                        holder.postMoreOption.setVisibility(View.GONE);
                    }
                }
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_post, parent, false);
                return new PostViewHolder(itemView);
            }
        };

        postAdapter.startListening();
        recyclerView.setAdapter(postAdapter);

        progressBar.setVisibility(View.GONE);
    }

    private void onLikeClicked(DatabaseReference reference) {
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.likes.containsKey(uid)) {
                    // Unlike the post and remove self from likes
                    p.like_count = p.like_count - 1;
                    p.likes.remove(uid);
                } else {
                    // Like the post and add self to likes
                    p.like_count = p.like_count + 1;
                    p.likes.put(uid, true);
                }
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}