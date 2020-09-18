package com.armjld.eb3tly.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.armjld.eb3tly.Utilites.About;
import com.armjld.eb3tly.Adapters.NotiAdaptere;
import com.armjld.eb3tly.Utilites.Conatact;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.Utilites.HowTo;
import com.armjld.eb3tly.main.MainActivity;
import com.armjld.eb3tly.Passaword.ChangePassword;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Utilites.UserSetting;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import Model.notiData;

public class Notifications extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference nDatabase,uDatabase;
    private FirebaseAuth mAuth;
    private static ArrayList<notiData> mm;
    private long count;
    private SwipeRefreshLayout refresh;
    private TextView txtNoOrders;
    private RecyclerView recyclerView;
    public static String TAG = "Notifications";
    String uType = UserInFormation.getAccountType();
    String uId = UserInFormation.getId();


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mm.size() >= 1) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth = FirebaseAuth.getInstance();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        ImageView btnNavBar = findViewById(R.id.btnNavBar);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        refresh = findViewById(R.id.refresh);
        count =0;
        mm = new ArrayList<>();

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("الاشعارات");

        //Recycler
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);

        // NAV BAR
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        btnNavBar.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        // Navigation Bar Buttons Function
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_timeline) {
                newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(newIntentNB);
            }
            if (id==R.id.nav_profile){
                finish();
                whichProfile();
            }
            if(id == R.id.nav_info) {
                startActivity(new Intent(getApplicationContext(), UserSetting.class));

            }
            if (id == R.id.nav_changepass) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
            }
            if (id == R.id.nav_how) {
                startActivity(new Intent(getApplicationContext(), HowTo.class));
            }
            if (id == R.id.nav_contact) {
                startActivity(new Intent(getApplicationContext(), Conatact.class));
            }
            if(id==R.id.nav_share){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(Notifications.this, About.class));
            }
            if (id==R.id.nav_signout){
                signOut();
            }
            if (id==R.id.nav_exit){
                finishAffinity();
                System.exit(0);
            }
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        });


        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            getNoti();
            recyclerView.setVisibility(View.GONE);
            refresh.setRefreshing(false);
        });

        refresh.setRefreshing(true);
        getNoti();

        Menu nav_menu = navigationView.getMenu();
        if (uType != null) {
            if(!uType.equals("Supplier")) {
                nav_menu.findItem(R.id.nav_how).setVisible(false);
            }
        }
    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }

    private void clearAdapter() {
        mm.clear();
        mm.trimToSize();
        count = 0;
        recyclerView.setAdapter(null);
    }

    private void getNoti() {
        count = 0;
        clearAdapter();
        nDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String notiID = ds.getKey();
                    assert notiID != null;
                    notiData notiDB = ds.getValue(notiData.class);
                    mm.add((int) count, notiDB);
                    NotiAdaptere orderAdapter = new NotiAdaptere(Notifications.this, mm, getApplicationContext(), mm.size());
                    recyclerView.setAdapter(orderAdapter);
                    nDatabase.child(uId).child(notiID).child("isRead").setValue("true");
                    count++;
                }
                recyclerView.setVisibility(View.VISIBLE);
                refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void signOut() {
        uDatabase.child(uId).child("device_token").setValue("");
        finish();
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }
}