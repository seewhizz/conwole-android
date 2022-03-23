package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.Post;
import com.conwole.main.Utils.CheckConnection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class NewPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 3;

    private Button chooseImageButton;
    private Spinner postCategory;
    private EditText postTitle;
    private EditText postDescription;
    private ImageView postDisplay;
    private ProgressBar progressBar;
    private String category;
    private StorageReference storagePostReference;
    private DatabaseReference databasePostReference;
    private DatabaseReference databaseUserPostReference;

    private StorageTask uploadTask;
    private String destinationFileName;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        getSupportActionBar().setTitle("Create New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

//        Timer timer = new Timer();
//        final int MILLISECONDS = 5000; //5 seconds
//        timer.schedule(new CheckConnection(NewPostActivity.this,NewPostActivity.this), 0, MILLISECONDS);

        chooseImageButton = findViewById(R.id.button_choose_image);
        postCategory = findViewById(R.id.post_category);
        postTitle = findViewById(R.id.post_title);
        postDescription = findViewById(R.id.post_description);
        postDisplay = findViewById(R.id.post_image_display);
        progressBar = findViewById(R.id.progress_bar);

        storagePostReference = FirebaseStorage.getInstance().getReference("posts");
        databasePostReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseUserPostReference = FirebaseDatabase.getInstance().getReference("user-posts");

        String[] items = new String[]{"Category", "Business", "Marketing", "Designing", "Coding", "Entrepreneurship", "Finance", "Stock Market"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, items);
        postCategory.setAdapter(adapter);
        postCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));

                if (adapterView.getItemAtPosition(i).equals("Category")) {
                    category = null;
                } else category = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        postDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null){
                    ((TextView) findViewById(R.id.no_image_selected)).setText("Image Selected");
                    new StfalconImageViewer.Builder<String>(NewPostActivity.this,
                            new ArrayList<>(Arrays.asList(imageUri.getPath())),
                            new ImageLoader<String>() {
                                @Override
                                public void loadImage(ImageView imageView, String image) {
                                    Glide.with(NewPostActivity.this).load(image).into(imageView);
                                }
                            }).withHiddenStatusBar(false)
                            .withTransitionFrom(postDisplay)
                            .show();

                }else {
                    Toast.makeText(NewPostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            this.destinationFileName = System.currentTimeMillis() + "." + getImageExtension(data.getData());
            cropImage(data.getData());
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            handleCropResult(data);
        }
    }

    private void cropImage(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        options.setAspectRatioOptions(0,
                new AspectRatio(null, 1, 1),
                new AspectRatio(null, 3, 4));
        UCrop uCrop = UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(), this.destinationFileName))).withOptions(options);
        uCrop.start(NewPostActivity.this);
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if(resultUri!=null){
            postDisplay.setImageURI(resultUri);
            this.imageUri = resultUri;
            chooseImageButton.setText("Change Image");
        }
    }

    private String getImageExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_align_parent_end, menu);
        MenuItem menuItem = menu.findItem(R.id.navigation_align_parent_end_icon);
        menuItem.setVisible(true);
        menuItem.setIcon(R.drawable.ic_baseline_done_24);
        menuItem.setTitle("Post");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if (item.getItemId() == R.id.navigation_align_parent_end_icon) {
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(NewPostActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadPost();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadPost() {

        if (postTitle.getText().toString().length() == 0) {
            postTitle.setError("Required!");
            return;
        }

        if (postDescription.getText().toString().length() == 0) {
            postDescription.setError("Required!");
            return;
        }

        if(category == null){
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_DarkActionBar));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("Uploading...");
            mDialog.show();
            StorageReference imageReference = storagePostReference
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(this.destinationFileName);
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            uploadTask = imageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
                            String urlString = url.toString().trim();
                            String key = databasePostReference.push().getKey();
                            Post post = new Post(
                                    key,
                                    category,
                                    postTitle.getText().toString().trim(),
                                    postDescription.getText().toString().trim(),
                                    urlString,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    String.valueOf(System.currentTimeMillis())
                            );

                            databasePostReference
                                    .child(key)
                                    .setValue(post);

                            databaseUserPostReference
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(key).setValue(post);

                            Toast.makeText(NewPostActivity.this, "Post Upload", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();

                            startActivity(new Intent(NewPostActivity.this, RegisterActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    });
        } else {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        }
    }

}