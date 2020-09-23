package com.armjld.eb3tly.Orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Settings.About;
import com.armjld.eb3tly.Settings.ChangePassword;
import com.armjld.eb3tly.Settings.Conatact;
import com.armjld.eb3tly.Home.StartUp;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.Home.MyAdapter;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Settings.UserInfo;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;

public class OrdersBySameUser extends AppCompatActivity {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //Recycler view
    private RecyclerView recyclerView;
    String uType = UserInFormation.getAccountType();
    private TextView txtNoOrders;
    private String TAG = "Other Orders";
    private MyAdapter orderAdapter;
    private static ArrayList<Data> mm;
    private long count;
    // import firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private ImageView btnNavBar;
    String userID;
    String dName = "";

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(R.layout.activity_orders_by_same_user);
        userID = getIntent().getStringExtra("userid");
        dName = getIntent().getStringExtra("name");
        Log.i(TAG, "User ID : " + userID);
        count =0;
        mm = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar_home);
        btnNavBar = findViewById(R.id.btnNavBar);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردرات " + dName);
        
        
        // ToolBar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Database
        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        mDatabase.keepSynced(true);

        //Recycler
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        // NAV BAR
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

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
            if (id == R.id.nav_changepass) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
            }
            if (id==R.id.nav_profile){
                finish();
                whichProfile();
            }
            if(id == R.id.nav_info) {
                startActivity(new Intent(getApplicationContext(), UserInfo.class));

            }
            if (id == R.id.nav_how) {
                startActivity(new Intent(getApplicationContext(), HowTo.class));
            }
            if (id==R.id.nav_signout){
                finish();
                startActivity(new Intent(OrdersBySameUser.this, MainActivity.class));
                mAuth.signOut();
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(OrdersBySameUser.this, About.class));
            }
            if(id==R.id.nav_share){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
            }
            if (id == R.id.nav_contact) {
                startActivity(new Intent(getApplicationContext(), Conatact.class));
            }
            if (id==R.id.nav_exit){
                finishAffinity();
                System.exit(0);
            }
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        });

        mDatabase.orderByChild("uId").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<mm.size();i++){
                    if(mm.get(i).getId().equals(orderData.getId())) {
                        if(orderAdapter!=null)
                            orderAdapter.addItem(i, orderData);
                        else{
                            Log.i(TAG,"adapter is null here");
                            orderAdapter  = new MyAdapter(OrdersBySameUser.this, mm, getApplicationContext(), count);
                            orderAdapter.addItem(i, orderData);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<mm.size();i++){
                    if(mm.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(orderAdapter!=null)
                            orderAdapter.addItem(i, orderData);
                        else{
                            Log.i(TAG,"adapter is null here");
                            orderAdapter  = new MyAdapter(OrdersBySameUser.this, mm, getApplicationContext(), count);
                            orderAdapter.addItem(i, orderData);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        getOrdersByLatest(userID);
    }

    private void getOrdersByLatest(String uIDD) {
        Log.i(TAG, "Getting Orders");
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        assert uIDD != null;
        mDatabase.orderByChild("uId").equalTo(uIDD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.exists() && ds.child("ddate").exists()) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            Date orderDate = null;
                            Date myDate = null;
                            try {
                                orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                                myDate =  format.parse(sdf.format(Calendar.getInstance().getTime()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            assert orderDate != null;
                            assert myDate != null;
                            Log.i(TAG, "Order Data : " + orderDate + " /My Data : " + myDate);
                            if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")) {
                                mm.add((int) count, orderData);
                                count++;
                            }
                            orderAdapter = new MyAdapter(OrdersBySameUser.this, mm, getApplicationContext(), count);
                            recyclerView.setAdapter(orderAdapter);
                            updateNone(mm.size());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateNone(int listSize) {
        Log.i(TAG, "List size is now : " + listSize);
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}