package com.stoiclife.app.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.stoiclife.app.R;
import com.stoiclife.app.view.fragments.AccueilFragment;
import com.stoiclife.app.view.fragments.PlanningFragment;
import com.stoiclife.app.view.fragments.ProfilFragment;
import com.stoiclife.app.view.fragments.ProgressionFragment;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            chargerFragment(new AccueilFragment());
            bottomNav.setSelectedItemId(R.id.nav_accueil);
        }

        configurerNavigation();
    }

    private void configurerNavigation() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                int id = item.getItemId();

                if (id == R.id.nav_accueil) {
                    fragment = new AccueilFragment();
                } else if (id == R.id.nav_planning) {
                    fragment = new PlanningFragment();
                } else if (id == R.id.nav_progression) {
                    fragment = new ProgressionFragment();
                } else if (id == R.id.nav_profil) {
                    fragment = new ProfilFragment();
                } else {
                    fragment = new AccueilFragment();
                }

                chargerFragment(fragment);
                return true;
            }
        });
    }

    private void chargerFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}