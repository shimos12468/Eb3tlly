package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class supplierProfile extends AppCompatActivity {
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private static ArrayList<Data> listSup;
    private static ArrayList<Data> listDelv;
    private long countSup;
    private long countDelv;
    private SupplierAdapter supplierAdapter;
    private DeliveryAdapter deliveryAdapter;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP;
    private TextView txtUserDate;
    private TextView uName;
    private TextView txtNotiCount,txtTotalOrders;
    private String TAG = "Profile";
    private RecyclerView recyclerView;
    String uType = StartUp.userType;
    String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_profile);

        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = mUser.getUid();

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.GONE);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



        ImageView btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        ConstraintLayout constNoti = findViewById(R.id.constNoti);
        ImageView btnOpenNoti = findViewById(R.id.btnOpenNoti);
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        TextView txtNoOrders = findViewById(R.id.txtNoOrders);
        imgSetPP = findViewById(R.id.imgPPP);
        txtNotiCount = findViewById(R.id.txtNotiCount);


        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردراتي");
        txtNoOrders.setVisibility(View.GONE);
        txtNotiCount.setVisibility(View.GONE);

        // Adapter
        countDelv = 0;
        countSup = 0;
        listDelv = new ArrayList<>();
        listSup = new ArrayList<>();

       /* //Recycler
        recyclerView=findViewById(R.id.userRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);*/



        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_timeline, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        constNoti.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(supplierProfile.this, Notifications.class));
        });

        btnNavbarProfile.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        btnOpenNoti.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(supplierProfile.this, Notifications.class));
        });

        // Navigation Bar Buttons Function
        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_timeline) {
                newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntentNB);
                finish();
            }
            if (id==R.id.nav_profile){
                startActivity(new Intent(getApplicationContext(), NewProfile.class));
            }
            if (id == R.id.nav_info) {
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
            if (id == R.id.nav_share) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(supplierProfile.this, About.class));
            }
            if (id == R.id.nav_signout) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mAuth.signOut();
                }
                finish();
                startActivity(new Intent(supplierProfile.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
            }
            if (id==R.id.nav_exit){
                finishAffinity();
                System.exit(0);
            }
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        });

        // -------------------------- Get users Notifications Count -------------------//
        nDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int notiCount = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists() && ds.child("isRead").exists()) {
                            if(Objects.equals(ds.child("isRead").getValue(), "false")) {
                                notiCount++;
                            }
                        }
                    }
                    if(notiCount > 0) {
                        txtNotiCount.setVisibility(View.VISIBLE);
                        txtNotiCount.setText(""+notiCount);
                    } else {
                        txtNotiCount.setVisibility(View.GONE);
                    }
                } else {
                    txtNotiCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // -------------------------- Get user info for profile ------------------------------ //
        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ppURL = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                uName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                txtUserDate.setText("اشترك : " + Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                if (!isFinishing()) {
                    Log.i(TAG, "Photo " + ppURL);
                    Picasso.get().load(Uri.parse(ppURL)).into(imgSetPP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        TextView usType = findViewById(R.id.txtUserType);
        String user_type;
        if (uType.equals("Supplier")) {
            user_type = "sId";
            usType.setText("تاجر");
            txtNoOrders.setText("لم تقم باضافه اي اوردرات حتي الان");
            btnAdd.setVisibility(View.VISIBLE);
            btnNavbarProfile.setVisibility(View.VISIBLE);
        } else {
            user_type = "dId";
            usType.setText("مندوب شحن");
            txtNoOrders.setText("لم تقم بقبول اي اوردرات حتي الان");
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_how).setVisible(false);
            btnAdd.setVisibility(View.GONE);
        }

        // ---------------------- Get Ratings -------------------------//
        RatingBar rbProfile = findViewById(R.id.rbProfile);
        rDatabase.child(uId).orderByChild(user_type).equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    long total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long rating = (long) Double.parseDouble(Objects.requireNonNull(ds.child("rate").getValue()).toString());
                        total = total + rating;
                    }
                    double average = (double) total / dataSnapshot.getChildrenCount();
                    Log.i(TAG, "Average Before : " +average);
                    if(String.valueOf(average).equals("NaN")) {
                        average = 5;
                        Log.i(TAG, "Average Midel : " + average);
                    }
                    Log.i(TAG, "Average Final : " + average);
                    rbProfile.setRating((int) average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        // -------------------------- ADD Order Button --------------------------//
        btnAdd.setOnClickListener(v -> vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString().equals("false")) {
                        Toast.makeText(supplierProfile.this, "عذرا لا يمكنك اضافه اوردرات في الوقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                    } else {
                        startActivity(new Intent(supplierProfile.this, AddOrders.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        }));
    }
}