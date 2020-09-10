package com.armjld.eb3tly.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Adapters.confirm_adapter;
import com.armjld.eb3tly.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.ConfirmationData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Admin_Confirm extends AppCompatActivity {

    private DatabaseReference nDatabase,cDatabase,uDatabase,mDatabase,reportDatabase,confirmDatabase;
    private static ArrayList<ConfirmationData> mm;
    private long count;
    private RecyclerView recyclerView;
    String TAG = "Admin Reports";

    public void onBackPressed() {
        Intent i = new Intent(this, Admin.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__confirm);

        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
        cDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("messages");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        reportDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("reports");
        confirmDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("confirms");


        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Confirm Accounts");

        count =0;
        mm = new ArrayList<ConfirmationData>();

        //Recycler
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        confirmDatabase.orderByChild("isConfirmed").equalTo("pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ConfirmationData confirmationData = ds.getValue(ConfirmationData.class);
                        mm.add((int) count, confirmationData);
                        count++;
                        confirm_adapter rep = new confirm_adapter(Admin_Confirm.this, mm, getApplicationContext(), count);
                        recyclerView.setAdapter(rep);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}