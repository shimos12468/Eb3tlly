package com.armjld.eb3tly.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Chat.Chats;
import com.armjld.eb3tly.Orders.MapsActivity;
import com.armjld.eb3tly.Utilites.About;
import com.armjld.eb3tly.Adapters.MyAdapter;
import com.armjld.eb3tly.Utilites.Conatact;
import com.armjld.eb3tly.Utilites.Filters;
import com.armjld.eb3tly.Utilites.HowTo;
import com.armjld.eb3tly.Passaword.ChangePassword;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Utilites.UserSetting;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;


@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private ImageView btnNavBar, btnSort,btnMaps;
    private ImageView filtrs_btn;
    private LinearLayout footer;
    private static ArrayList<Data> mm;
    private long count;
    BlockManeger block = new BlockManeger();
    // import firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, uDatabase ,Database;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txtNoOrders;
    public static boolean requests = false;
    public static boolean orders = false;
    private String TAG = "Home Activity";
    private MyAdapter orderAdapter;
    String filterDate;
    String uType = UserInFormation.getAccountType();
    String uId = UserInFormation.getId();
    public static boolean sortDate = false;
    private Thread t = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //Recycler view
    private RecyclerView recyclerView;

    // Disable the Back Button
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    // On Create Fun
    @SuppressLint("RtlHardcoded")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }


        filterDate = format.format(Calendar.getInstance().getTime());
        //Find View
        count =0;
        mm = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar_home);
        filtrs_btn = findViewById(R.id.filters_btn);
        btnSort = findViewById(R.id.btnSort);
        btnNavBar = findViewById(R.id.btnNavBar);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        btnMaps = findViewById(R.id.btnMaps);
        footer = findViewById(R.id.footer);
        txtNoOrders.setVisibility(View.GONE);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("جميع الاوردرات المتاحة");

        mSwipeRefreshLayout = findViewById(R.id.refresh);

        // ToolBar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Database
        mAuth= FirebaseAuth.getInstance();
        uDatabase  = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        mDatabase.keepSynced(true);
        uDatabase.keepSynced(true);

        //Recycler
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);


        btnMaps.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MapsActivity.class));
        });

        // ----------- check for Requests ----------- //
        if(uType.equals("Delivery Worker")) {
            DatabaseReference Wdatabase =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("requests");
            Wdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(ds.child("statue").exists()) {
                                if(ds.child("statue").getValue().toString().equals("N/A")){
                                    count++;
                                }
                            }

                        }
                        if(count >= 10) {
                            requests = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count2 = 0;
                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(ds.child("statue").getValue().toString().equals("accepted")){
                                count2++;
                            }
                        }
                        if(count2 >= 20) {
                            orders = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        // ----------- sort button
        btnSort.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.sort_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.by_date:
                        sortDate = true;
                        mm.clear();
                        mm.trimToSize();
                        count = 0;
                        recyclerView.setAdapter(null);
                        mSwipeRefreshLayout.setRefreshing(true);
                        getOrdersByDate();
                        break;
                    case R.id.by_latest:
                        sortDate = false;
                        mm.clear();
                        mm.trimToSize();
                        count = 0;
                        recyclerView.setAdapter(null);
                        mSwipeRefreshLayout.setRefreshing(true);
                        getOrdersByLatest();
                        break;
                }
                return false;
            });
            popup.show();
        });

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
            if (id == R.id.nav_changepass) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
            }
            if (id==R.id.nav_profile){
                finish();
                whichProfile();
            }
            if(id == R.id.nav_info) {
                startActivity(new Intent(getApplicationContext(), UserSetting.class));

            }
            if (id == R.id.nav_how) {
                startActivity(new Intent(getApplicationContext(), HowTo.class));
            }
            if (id==R.id.nav_signout){
                signOut();
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(HomeActivity.this, About.class));
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

        // ----------------- Hide the How to For Delivery
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_how).setVisible(false);

        footer.setVisibility(View.GONE);

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mm.clear();
            mm.trimToSize();
            count = 0;
            recyclerView.setAdapter(null);
            if(sortDate) { getOrdersByDate(); } else { getOrdersByLatest(); }
            mSwipeRefreshLayout.setRefreshing(false);
        });

        // ------------------------ CHECK FOR REALTIME CHANGES IN ORDERS --------------------------- //
        mDatabase.orderByChild("ddate").startAt(filterDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<mm.size();i++){
                    if(mm.get(i).getId().equals(orderData.getId())) {
                        if(orderAdapter!=null){
                            orderAdapter.addItem(i, orderData);
                        }
                        else{
                            Log.i(TAG,"adapter is null here");
                            orderAdapter  = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
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
                            orderAdapter  = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
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

        // ---------------------- GET ALL THE ORDERS -------------------//
        mSwipeRefreshLayout.setRefreshing(true);
        if(sortDate) {
            getOrdersByDate();
        } else {
            getOrdersByLatest();
        }

        // Filter Button
        filtrs_btn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(HomeActivity.this, Filters.class));
        });

    }

    private void checkForAdvice() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();

                int lastVisible = layoutManager.findLastVisibleItemPosition();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                boolean endHasBeenReached;
                if(!sortDate) {
                    endHasBeenReached = firstVisible >= totalItemCount;
                } else {
                    endHasBeenReached = lastVisible + 2 >= totalItemCount;
                }

                if (totalItemCount > 0 && endHasBeenReached) {
                    footer.setVisibility(View.VISIBLE);
                } else {
                    footer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getOrdersByDate() {
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        mDatabase.orderByChild("ddate").startAt(filterDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //ImportBlockedUsers();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.exists()) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            Date orderDate = null;
                            Date myDate = null;
                            try {
                                orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString().replaceAll("(^\\h*)|(\\h*$)",""));
                                myDate =  format.parse(sdf.format(Calendar.getInstance().getTime()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            assert orderDate != null;
                            assert myDate != null;
                            Log.i(TAG, "Order Data : " + orderDate + " /My Data : " + myDate);
                            if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")&&!block.check(orderData.getuId())) {
                                mm.add((int) count, orderData);
                                count++;
                            }
                            orderAdapter = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
                            recyclerView.setAdapter(orderAdapter);
                            checkForAdvice();
                            updateNone(mm.size());
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void ImportBlockedUsers() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Database = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid());
        Database.child("Blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        blocedUsers.add(ds.child("id").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }

    private void getOrdersByLatest() {
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        mDatabase.orderByChild("datee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                   // ImportBlockedUsers();
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
                            if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")&&!block.check(orderData.getuId())) {
                                mm.add((int) count, orderData);
                                count++;
                            }
                            orderAdapter = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
                            recyclerView.setAdapter(orderAdapter);
                            updateNone(mm.size());
                            checkForAdvice();
                            mSwipeRefreshLayout.setRefreshing(false);
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
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }


    private void signOut() {
        uDatabase.child(uId).child("device_token").setValue("");
        finish();
        mAuth.signOut();
        if(Login_Options.mGoogleSignInClient != null) {
            Login_Options.mGoogleSignInClient.signOut();
        }
        startActivity(new Intent(this, Login_Options.class));
        Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }
}