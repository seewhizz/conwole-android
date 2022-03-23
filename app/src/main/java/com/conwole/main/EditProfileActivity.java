package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;

    private FloatingActionButton userImageUpdate;
    private CircularImageView userImage;
    private EditText userName;
    private EditText userBio;
    private EditText userLink;
    private TextView userEmail;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;
    private Uri imageUri;
    private String destinationFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        userImageUpdate = findViewById(R.id.upload_new_profile_picture);
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userBio = findViewById(R.id.user_bio);
        userLink = findViewById(R.id.user_link);

        storageReference = FirebaseStorage.getInstance().getReference("users/info");
        databaseReference = FirebaseDatabase.getInstance().getReference("users/info");

        if(Common.currentUser!=null){
            User mainUser = Common.currentUser;
            if(mainUser.getProfile_pic_url()!= null){
                Glide.with(EditProfileActivity.this).load(mainUser.getProfile_pic_url()).into(userImage);
            }
            if(mainUser.getFull_name()!= null){
                userName.setText(mainUser.getFull_name());
            }
            if(mainUser.getUser_email()!= null){
                userEmail.setText(mainUser.getUser_email());
            }
            if(mainUser.getUser_bio()!= null){
                userBio.setText(mainUser.getUser_bio());
                userBio.setMovementMethod(new ScrollingMovementMethod());
            }
            if(mainUser.getUser_link()!= null){
                userLink.setText(mainUser.getUser_link());
            }
        }

        userImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void cropImage(Uri imageUri) {
        UCrop uCrop = UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), this.destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.start(this);
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            this.imageUri = data.getData();
            this.destinationFileName = FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + getImageExtension(imageUri);
            cropImage(imageUri);
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null && UCrop.getOutput(data) != null){
            handleCropResult(data);
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if(resultUri!=null){
            userImage.setImageURI(resultUri);
            this.imageUri = resultUri;
        }
    }

    private void updateProfile() {

        if (userName.getText().toString().length() == 0) {
            userName.setError("Required!");
            return;
        }

        if (userLink.getText().toString().length() != 0) {
            boolean isLink = URLUtil.isValidUrl(userLink.getText().toString().trim());
            if (!isLink) {
                Toast.makeText(this, "Incorrect Link", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (imageUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("Updating...");
            mDialog.show();
            StorageReference imageReference = storageReference.child(this.destinationFileName);
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            uploadTask = imageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
                            String urlString = url.toString().trim();
                            Common.currentUser.setProfile_pic_url(urlString);
                            Common.currentUser.setFull_name(userName.getText().toString().trim());
                            Common.currentUser.setUser_bio(userBio.getText().toString().trim());
                            Common.currentUser.setUser_link(userLink.getText().toString().trim());
                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Common.currentUser);
                            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();

                            startActivity(new Intent(EditProfileActivity.this, RegisterActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    });
        } else {
            Common.currentUser.setFull_name(userName.getText().toString().trim());
            Common.currentUser.setUser_bio(userBio.getText().toString().trim());
            Common.currentUser.setUser_link(userLink.getText().toString().trim());
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Common.currentUser);
            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(EditProfileActivity.this, RegisterActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_align_parent_end,menu);
        MenuItem menuItem = menu.findItem(R.id.navigation_align_parent_end_icon);
        menuItem.setVisible(true);
        menuItem.setIcon(R.drawable.ic_baseline_done_24);
        menuItem.setTitle("Update");
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
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(EditProfileActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                updateProfile();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}