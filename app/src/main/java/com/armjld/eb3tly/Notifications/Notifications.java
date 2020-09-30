package com.armjld.eb3tly.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.DatabaseClasses.rquests;
import com.armjld.eb3tly.Home.StartUp;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;
import Model.notiData;

public class Notifications extends AppCompatActivity {

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
    private ImageView btnBack, btnClear;


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        mAuth = FirebaseAuth.getInstance();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

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

        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            getNoti();
            recyclerView.setVisibility(View.GONE);
        });

        // ----------- Clear All Noti ------------ //
        btnClear.setOnClickListener(v-> {
            MaterialDialog materialDialog = new MaterialDialog.Builder(this).setMessage("هل تريد الغاء كل الاشعارات ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {
                nDatabase.child(uId).removeValue();
                getNoti();
                txtNoOrders.setVisibility(View.VISIBLE);
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            materialDialog.show();
        });

        refresh.setRefreshing(true);
        getNoti();

        btnBack.setOnClickListener(v->finish());

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
                if(snapshot.exists()) {
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
                    checkCount();
                    recyclerView.setVisibility(View.VISIBLE);
                    refresh.setRefreshing(false);
                } else {
                    refresh.setRefreshing(false);
                    txtNoOrders.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void checkCount() {
        if(mm.size() >= 1) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }
}