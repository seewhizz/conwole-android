package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conwole.main.Common.Common;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CircularImageView userImage;
    private TextView userName,userEmail;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();

        if(Common.currentUser == null){
            Toast.makeText(this, "Data Refreshed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        Glide.with(MainActivity.this).load(firebaseUser.getPhotoUrl()).into(userImage);
        userName.setText(firebaseUser.getDisplayName());
        userEmail.setText(firebaseUser.getEmail());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                // Google revoke access
                googleSignInClient.revokeAccess().addOnCompleteListener(MainActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(MainActivity.this, SplashActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                        });
            }
        });

    }
}