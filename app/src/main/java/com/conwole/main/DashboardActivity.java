package com.conwole.main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.conwole.main.Fragment.HomeFragment;
import com.conwole.main.Fragment.MentorFragment;
import com.conwole.main.Fragment.SettingsFragment;
import com.conwole.main.Fragment.TrainingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Conwole");
        actionBar.setElevation(0);

        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle("Conwole");
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_mentor:
                    actionBar.setTitle("Mentor");
                    selectedFragment = new MentorFragment();
                    break;
                case R.id.navigation_training:
                    actionBar.setTitle("Training");
                    selectedFragment = new TrainingFragment();
                    break;
                case R.id.navigation_settings:
                    actionBar.setTitle("Settings");
                    selectedFragment = new SettingsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            return true;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }
}