package com.conwole.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conwole.main.Common.Common;
import com.conwole.main.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;

public class RegisterActivity extends AppCompatActivity {

    private String token;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/info");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String newToken) {
                token = newToken;
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    User user = snapshot.child(firebaseUser.getUid()).getValue(User.class);
                    databaseReference.child(firebaseUser.getUid()).child("token").setValue(token);
                    Common.currentUser = user;
                }else{
                    User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            String.valueOf(firebaseUser.getPhotoUrl()),
                            firebaseUser.getDisplayName(),
                            String.valueOf(System.currentTimeMillis()),
                            token
                    );
                    databaseReference.child(firebaseUser.getUid()).setValue(user);
                    Common.currentUser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(firebaseUser!= null){
            //Todo before Main Activity Now Dashboard
            startActivity(new Intent(this,DashboardActivity.class));
        }

    }
}