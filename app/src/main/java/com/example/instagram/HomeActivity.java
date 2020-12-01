package com.example.instagram;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        bottomNavigationView = findViewById(R.id.bottomNav);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
    }

//    private BottomNavigationView.OnNavigationItemReselectedListener bottomNavMethod =
//            new BottomNavigationView.OnNavigationItemReselectedListener() {
//        @Override
//        public void onNavigationItemReselected(@NonNull MenuItem item) {
//
//        }
//    };
}