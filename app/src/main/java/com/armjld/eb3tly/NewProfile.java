package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;
import Model.Data;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NewProfile extends AppCompatActivity {

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

    public NewProfile() {
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
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

        //Recycler
        recyclerView=findViewById(R.id.userRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);



        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_timeline, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        constNoti.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(NewProfile.this, Notifications.class));
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
            startActivity(new Intent(NewProfile.this, Notifications.class));
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
                startActivity(new Intent(NewProfile.this, About.class));
            }
            if (id == R.id.nav_signout) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mAuth.signOut();
                }
                finish();
                startActivity(new Intent(NewProfile.this, MainActivity.class));
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
                        Toast.makeText(NewProfile.this, "عذرا لا يمكنك اضافه اوردرات في الوقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                    } else {
                        startActivity(new Intent(NewProfile.this, AddOrders.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        }));

        if(uType.equals("Supplier")) {
            getSupOrders();
            getOrderCountSup();
        } else {
            getDelvOrders();
            getOrderCountDel();
        }

    }

    public void getSupOrders() {
        clearAdapter();
        Log.i(TAG, "Getting Supplier Orders");
        // ---------------------- GET ALL THE ORDERS -------------------//
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            listSup.add((int) countSup, orderData);
                            countSup++;
                            Log.i(TAG, "Inside the Query");

                            supplierAdapter = new SupplierAdapter(NewProfile.this, listSup, getApplicationContext(), countSup);
                            recyclerView.setAdapter(supplierAdapter);                     }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.orderByChild("uId").equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listSup.size();i++){
                    if(listSup.get(i).getId().equals(orderData.getId())) {
                        if(supplierAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            supplierAdapter  = new SupplierAdapter(NewProfile.this, listSup, getApplicationContext(), countSup);
                        }
                        supplierAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listSup.size();i++){
                    if(listSup.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(supplierAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            supplierAdapter  = new SupplierAdapter(NewProfile.this, listSup, getApplicationContext(), countSup);
                        }
                        supplierAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void getDelvOrders() {
        clearAdapter();
        mDatabase.orderByChild("acceptedTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.exists() && Objects.requireNonNull(ds.child("uAccepted").getValue()).toString().equals(uId)) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            listDelv.add((int) countDelv, orderData);
                            countDelv++;


                            deliveryAdapter = new DeliveryAdapter(NewProfile.this, listDelv, getApplicationContext(), countDelv);
                            recyclerView.setAdapter(deliveryAdapter);
                            //updateNone(mm.size());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.orderByChild("uAccepted").equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listDelv.size();i++){
                    if(listDelv.get(i).getId().equals(orderData.getId())) {
                        if(deliveryAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            deliveryAdapter  = new DeliveryAdapter(NewProfile.this, listDelv, getApplicationContext(), countDelv);
                        }
                        deliveryAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listDelv.size();i++){
                    if(listDelv.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(deliveryAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            deliveryAdapter  = new DeliveryAdapter(NewProfile.this, listDelv, getApplicationContext(), countDelv);
                        }
                        deliveryAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void clearAdapter() {
        listSup.clear();
        listDelv.clear();
        listDelv.trimToSize();
        listSup.trimToSize();
        countSup = 0;
        countDelv = 0;
        recyclerView.setAdapter(null);
    }

    private void getOrderCountDel() {
        mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    String strCount = String.valueOf(count);
                    txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                } else {
                    txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOrderCountSup() {
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    String strCount = String.valueOf(count);
                    txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                } else {
                    txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}