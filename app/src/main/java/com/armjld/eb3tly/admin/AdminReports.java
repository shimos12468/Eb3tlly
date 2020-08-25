package com.armjld.eb3tly.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Adapters.reportsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.reportData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class AdminReports extends AppCompatActivity {

    private DatabaseReference nDatabase,cDatabase,uDatabase,mDatabase,reportDatabase;
    private static ArrayList<reportData> mm;
    private long count;
    private RecyclerView recyclerView;
    String TAG = "Admin Reports";

    public void onBackPressed() {
        Intent i = new Intent(this, Admin.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        cDatabase = getInstance().getReference().child("Pickly").child("messages");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Reports");

        count =0;
        mm = new ArrayList<reportData>();

        //Recycler
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        reportDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot snap : ds.getChildren()) {
                        reportData reportData = snap.getValue(reportData.class);
                        mm.add((int) count, reportData);
                        count++;
                        reportsAdapter rep = new reportsAdapter(AdminReports.this, mm, getApplicationContext(), count);
                        recyclerView.setAdapter(rep);
                        Toast.makeText(AdminReports.this, "Data Loaded", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}