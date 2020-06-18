package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HowTo extends AppCompatActivity {

    private ImageView btnNavBar;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("الاوردرات");
        mAuth = FirebaseAuth.getInstance();

        // NAV BAR
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        btnNavBar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        // ------------------ Show or Hide Buttons depending on the User Type
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                if (uType.equals("Supplier")) {
                    Menu nav_menu = navigationView.getMenu();
                    nav_menu.findItem(R.id.nav_timeline).setVisible(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        // Navigation Bar Buttons Function
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_timeline) {
                    newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(newIntentNB);
                }
                if (id == R.id.nav_changepass) {
                    startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                }
                if (id == R.id.nav_how) {
                    startActivity(new Intent(getApplicationContext(), HowTo.class));
                }
                if (id==R.id.nav_profile){
                    startActivity(new Intent(getApplicationContext(), profile.class));
                }
                if(id == R.id.nav_info) {
                    startActivity(new Intent(getApplicationContext(), UserSetting.class));

                }
                if (id==R.id.nav_signout){
                    finish();
                    startActivity(new Intent(HowTo.this, MainActivity.class));
                    mAuth.signOut();
                }
                if (id==R.id.nav_about){
                    startActivity(new Intent(HowTo.this, About.class));
                }
                if(id==R.id.nav_share){
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Play Store Link");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
                }
                if (id == R.id.nav_contact) {
                    startActivity(new Intent(getApplicationContext(), Conatact.class));
                }
                drawer.closeDrawer(Gravity.LEFT);
                return true;
            }
        });
    }
}