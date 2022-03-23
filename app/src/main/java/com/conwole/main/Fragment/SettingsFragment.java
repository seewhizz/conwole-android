package com.conwole.main.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.conwole.main.AboutUsActivity;
import com.conwole.main.ContactUsActivity;
import com.conwole.main.FAQActivity;
import com.conwole.main.ProfileActivity;
import com.conwole.main.Utils.AppUpdateChecker;
import com.conwole.main.BuildConfig;
import com.conwole.main.Common.Common;
import com.conwole.main.PrivacyPolicyActivity;
import com.conwole.main.R;
import com.conwole.main.SplashActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData(view);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        loadData(view);

        return view;
    }

    private void loadData(View view){

        CircularImageView settingsProfileImage = view.findViewById(R.id.settings_profile_image);
        TextView settingsProfileName = view.findViewById(R.id.settings_profile_name);
        LinearLayout aboutUs = view.findViewById(R.id.about_us);
        LinearLayout goToProfile = view.findViewById(R.id.go_to_profile);
        LinearLayout contactUs = view.findViewById(R.id.contact_us);
        LinearLayout checkUpdate = view.findViewById(R.id.check_update);
        LinearLayout logout = view.findViewById(R.id.logout);
        LinearLayout share = view.findViewById(R.id.share);
        LinearLayout visitUs = view.findViewById(R.id.visit_us);
        LinearLayout frequentlyAskedQuestion = view.findViewById(R.id.frequentlyAskedQuestion);
        LinearLayout privacyPolicy = view.findViewById(R.id.privay_policy);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ((TextView)view.findViewById(R.id.build_version)).setText("Build Version - " + BuildConfig.VERSION_NAME);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        if(Common.currentUser!=null){
            Glide.with(getActivity()).load(Common.currentUser.getProfile_pic_url()).into(settingsProfileImage);
            settingsProfileName.setText(Common.currentUser.getFull_name());
        }else{
            settingsProfileName.setText("Loading");
        }

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingsProfileName.getText().equals("Loading")){
                    Toast.makeText(getContext(), "Swipe to Refresh", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                }
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUpdateChecker appUpdateChecker = new AppUpdateChecker(getActivity());
                appUpdateChecker.checkForUpdate(true);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                // Google revoke access
                Common.currentUser = null;
                googleSignInClient.revokeAccess().addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(getActivity(), SplashActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                        });
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Conwole");
                String shareMessage= "*"+ getString(R.string.app_name)+"*\n"+ getString(R.string.slogan)+"\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share to"));
            }
        });
        visitUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.conwole.com/")));
            }
        });
        frequentlyAskedQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FAQActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        });

    }
}