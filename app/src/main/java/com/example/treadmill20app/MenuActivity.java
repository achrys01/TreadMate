package com.example.treadmill20app;
/*
Set the menu activity for all activities
From: https://github.com/jitseve/XFran
*/

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu accountMenu;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*------ HOOKS ------*/
        navigationView = findViewById(R.id.menu_navigation_view);
        drawerLayout = findViewById(R.id.menu_drawer_layout);
        toolbar = findViewById(R.id.menu_toolbar);

        /*------ INIT ------*/
        setSupportActionBar(toolbar);

        accountMenu = navigationView.getMenu();
        accountMenu.findItem(R.id.menu_logout).setVisible(false);
        accountMenu.findItem(R.id.menu_profile).setVisible(false);

        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            accountMenu.findItem(R.id.menu_login).setVisible(false);
            accountMenu.findItem(R.id.menu_profile).setVisible(true);
            accountMenu.findItem(R.id.menu_logout).setVisible(true);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (firebaseAuth.getCurrentUser() != null) {
            accountMenu.findItem(R.id.menu_login).setVisible(false);
            accountMenu.findItem(R.id.menu_profile).setVisible(true);
            accountMenu.findItem(R.id.menu_logout).setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_start:
                startActivity(new Intent(getApplicationContext(), StartTrainingActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                drawerLayout.closeDrawers();
                break;
/*
            case R.id.menu_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                drawerLayout.closeDrawers();
                break;
 */
            case R.id.menu_logout:
                //firebaseAuth.signOut();
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                accountMenu.findItem(R.id.menu_logout).setVisible(false);
                                accountMenu.findItem(R.id.menu_profile).setVisible(false);
                                accountMenu.findItem(R.id.menu_login).setVisible(true);
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                drawerLayout.closeDrawers();
                                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                            }
                        });
        }
        return false;
    }
}

