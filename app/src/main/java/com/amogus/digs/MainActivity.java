package com.amogus.digs;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

//This is the java activity for main layout
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //since this is the activity for main layout this code will display the layout.
        setContentView(R.layout.activity_main);

        //getting the instance of the Singleton
        singleton = Singleton.getInstance(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        //this will display the toolbar by supporting it
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //this will run if the save instancestate is null
        if (savedInstanceState == null) {
            //this will change the content of the framelayout of the main activity layout
            //the content of the frame layout can be change by fragments
            //the frame layout will display the helpme fragment if the user start the app
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpMeFragment()).commit();
            //set the helpme item in the navigation drawer selected
            navigationView.setCheckedItem(R.id.nav_help);
            //set the title of toolbar
            actionBar.setTitle("Help");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                //if the settings item is selected in the navigation drawer, this will open the Settings Activity
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                //the content of the frame layout will display the help me fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpMeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_help);
                actionBar.setTitle("Help");
                break;
            case R.id.nav_wiki:
                //the content of the frame layout will display the wiki fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WikiFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_wiki);
                actionBar.setTitle("Wiki");
                break;
            case R.id.nav_hotlines:
                //the content of the frame layout will display the hotlines fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HotlinesFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_hotlines);
                actionBar.setTitle("Emergency Hotlines");
                break;
            case R.id.nav_rescue:
                //the content of the frame layout will display the rescue fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RescueFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_rescue);
                actionBar.setTitle("Rescue Someone");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        View navigation_header = navigationView.getHeaderView(0);

        TextView txtName = navigation_header.findViewById(R.id.text_username);
        TextView txtContact = navigation_header.findViewById(R.id.text_contactnumber);

        txtName.setText(singleton.getUser_name());
        txtContact.setText(singleton.getContact_number());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //if the navigation drawer is open and the back button is pressed, it will close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}