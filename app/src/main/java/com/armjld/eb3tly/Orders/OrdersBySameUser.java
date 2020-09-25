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

import com.armjld.eb3tly.LoginManager;
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
    private ImageView btnBack;
    String userID;
    String dName = "";

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
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
        txtNoOrders = findViewById(R.id.txtNoOrders);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

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
                            orderAdapter = new MyAdapter(OrdersBySameUser.this, mm);
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

}