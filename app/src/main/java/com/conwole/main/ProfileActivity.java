package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private CircularImageView userImage;
    private TextView userName,userEmail;
    private TextView userBio, userLink;
    private ImageView userCoverImage;
    boolean owner,visitor;
    private FloatingActionButton floatingActionButton;
    private String loadUserPost = "";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> postAdapter;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/info");
    private DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("posts");
    private DatabaseReference userPostReference = FirebaseDatabase.getInstance().getReference("user-posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        userCoverImage = findViewById(R.id.user_cover_image);
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userBio = findViewById(R.id.user_bio);
        userLink = findViewById(R.id.user_link);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        loadPost();


    }

    private void loadPost() {

        if(Common.currentProfile!=null){
            if(Common.currentProfile.getUser_email().equals(Common.currentUser.getUser_email())){
                //Owner
                loadUserPost = uid;
                owner = true; visitor = false;
                User mainUser = Common.currentUser;
                if(mainUser.getProfile_pic_url()!= null){
                    Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userCoverImage);
                    Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userImage);
                }
                if(mainUser.getFull_name()!= null){
                    userName.setText(mainUser.getFull_name());
                }
                if(mainUser.getUser_email()!= null){
                    userEmail.setVisibility(View.VISIBLE);
                    userEmail.setText(mainUser.getUser_email());
                }
                if(mainUser.getUser_bio()!= null){
                    if(!mainUser.getUser_bio().equals("")){
                        userBio.setVisibility(View.VISIBLE);
                        userBio.setText(mainUser.getUser_bio());
                        userBio.setMovementMethod(new ScrollingMovementMethod());
                    }else {
                        userBio.setVisibility(View.GONE);
                    }
                }else {
                    userBio.setVisibility(View.GONE);
                }
                if(mainUser.getUser_link()!= null){
                    if(!mainUser.getUser_link().equals("")) {
                        userLink.setVisibility(View.VISIBLE);
                        userLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mainUser.getUser_link())));
                            }
                        });
                        userLink.setText(mainUser.getUser_link());
                    }else {
                        userLink.setVisibility(View.GONE);
                    }
                }else {
                    userLink.setVisibility(View.GONE);
                }
            }
            else {
                //Visitor
                owner = false; visitor = true;
                User mainUser = Common.currentProfile;
                loadUserPost = Common.currentProfile.getUser_id();
                if(mainUser.getProfile_pic_url()!= null){
                    Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userCoverImage);
                    Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userImage);
                }
                if(mainUser.getFull_name()!= null){
                    userName.setText(mainUser.getFull_name());
                }
                if(mainUser.getUser_email()!= null){
                    userEmail.setVisibility(View.GONE);
                    userEmail.setText(mainUser.getUser_email());
                }
                if(mainUser.getUser_bio()!= null){
                    if(!mainUser.getUser_bio().equals("")){
                        userBio.setVisibility(View.VISIBLE);
                        userBio.setText(mainUser.getUser_bio());
                        userBio.setMovementMethod(new ScrollingMovementMethod());
                    }else {
                        userBio.setVisibility(View.GONE);
                    }
                }else {
                    userBio.setVisibility(View.GONE);
                }
                if(mainUser.getUser_link()!= null){
                    if(!mainUser.getUser_link().equals("")) {
                        userLink.setVisibility(View.VISIBLE);
                        userLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mainUser.getUser_link())));
                            }
                        });
                        userLink.setText(mainUser.getUser_link());
                    }else {
                        userLink.setVisibility(View.GONE);
                    }
                }else {
                    userLink.setVisibility(View.GONE);
                }
            }
        }else if(Common.currentUser!=null){
            //Owner
            loadUserPost = uid;
            owner = true; visitor = false;
            User mainUser = Common.currentUser;
            if(mainUser.getProfile_pic_url()!= null){
                Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userCoverImage);
                Glide.with(ProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userImage);
            }
            if(mainUser.getFull_name()!= null){
                userName.setText(mainUser.getFull_name());
            }
            if(mainUser.getUser_email()!= null){
                userEmail.setVisibility(View.VISIBLE);
                userEmail.setText(mainUser.getUser_email());
            }
            if(mainUser.getUser_bio()!= null){
                if(!mainUser.getUser_bio().equals("")){
                    userBio.setVisibility(View.VISIBLE);
                    userBio.setText(mainUser.getUser_bio());
                    userBio.setMovementMethod(new ScrollingMovementMethod());
                }else {
                    userBio.setVisibility(View.GONE);
                }
            }else {
                userBio.setVisibility(View.GONE);
            }
            if(mainUser.getUser_link()!= null){
                if(!mainUser.getUser_link().equals("")) {
                    userLink.setVisibility(View.VISIBLE);
                    userLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mainUser.getUser_link())));
                        }
                    });
                    userLink.setText(mainUser.getUser_link());
                }else {
                    userLink.setVisibility(View.GONE);
                }
            }else {
                userLink.setVisibility(View.GONE);
            }
        }

        floatingActionButton = findViewById(R.id.add_user_post);

        if(owner && !visitor){
            floatingActionButton.setVisibility(View.VISIBLE);
        }else if(!owner && visitor){
            floatingActionButton.setVisibility(View.GONE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,NewPostActivity.class));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        DatabaseReference reference = userPostReference.child(loadUserPost);
        Query query = reference.limitToLast(100);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();


        postAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                progressBar.setVisibility(View.INVISIBLE);
                if(model!=null){

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.ic_logo_png);

                    if(model.getImage_url()!= null){
                        Glide.with(ProfileActivity.this).load(model.getImage_url()).apply(requestOptions).into(holder.postImage);
                        holder.postImage.setAdjustViewBounds(true);
                        holder.postImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new StfalconImageViewer.Builder<String>(ProfileActivity.this,
                                        new ArrayList<>(Arrays.asList(model.getImage_url())),
                                        new ImageLoader<String>() {
                                            @Override
                                            public void loadImage(ImageView imageView, String image) {
                                                Glide.with(ProfileActivity.this).load(image).into(imageView);
                                            }
                                        }).withHiddenStatusBar(false)
                                        .withTransitionFrom(holder.postImage)
                                        .show();
                            }
                        });
                    }

                    if(model.getTitle()!= null){
                        holder.postTitle.setText(model.getTitle());
                    }

                    if(model.getCategory()!= null){
                        holder.postCategory.setText(model.getCategory());
                        holder.postCategory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ProfileActivity.this, PostCategoryActivity.class)
                                        .putExtra("category",model.getCategory()));
                            }
                        });
                    }

                    if(model.getTimestamp()!= null){
                        long time = Long.parseLong(model.getTimestamp());
                        PrettyTime p = new PrettyTime();
                        Date date = new Date(time);
                        holder.postTimeAgo.setText(p.format(date));
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProfileActivity.this, PostDetailActivity.class);
                            intent.putExtra("post_key",model.getKey());
                            Common.currentPost = model;
                            startActivity(intent);
                        }
                    });

                }
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_post_profile, parent, false);
                return new PostViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(postAdapter.getItemCount()==0){
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        postAdapter.startListening();
        recyclerView.setAdapter(postAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_align_parent_end,menu);
        MenuItem menuItem = menu.findItem(R.id.navigation_align_parent_end_icon);
        if(owner && !visitor){
            menuItem.setVisible(true);
        }else if(!owner && visitor){
            menuItem.setVisible(false);
        }
        menuItem.setIcon(R.drawable.ic_baseline_edit_24);
        menuItem.setTitle("Edit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if(item.getItemId() == R.id.navigation_align_parent_end_icon){
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.currentProfile = null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Common.currentUser == null){
            Toast.makeText(this, "Data Refreshed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this,SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
}