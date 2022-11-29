package com.amogus.digs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpMeFragment()).commit();
            actionBar.setTitle("Help");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpMeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_help);
                actionBar.setTitle("Help");
                break;
            case R.id.nav_wiki:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WikiFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_wiki);
                actionBar.setTitle("Wiki");
                break;
            case R.id.nav_hotlines:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HotlinesFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_hotlines);
                actionBar.setTitle("Emergency Hotlines");
                break;
            case R.id.nav_rescue:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RescueFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_rescue);
                actionBar.setTitle("Rescue Someone");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}