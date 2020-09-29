package com.armjld.eb3tly.Orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Home.MyAdapter;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Home.StartUp;
import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class OneOrder extends AppCompatActivity {

    private static final String TAG = "One Order";
    String uId = UserInFormation.getId();
    private MyAdapter orderAdapter;
    private static ArrayList<Data> mm;
    private long count;
    DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    TextView toolbar_title;
    FirebaseAuth mAuth;



    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

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
        setContentView(R.layout.activity_one_order);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("اوردر جديد");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = getInstance().getReference("Pickly").child("orders");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

        String orderID = getIntent().getStringExtra("oID");
        assert orderID != null;
        mm = new ArrayList<>();
        Log.i(TAG, orderID);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Data orderData = snapshot.getValue(Data.class);
                    mm.add((int) count, orderData);
                    count++;
                    orderAdapter = new MyAdapter(OneOrder.this, mm);
                    recyclerView.setAdapter(orderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}