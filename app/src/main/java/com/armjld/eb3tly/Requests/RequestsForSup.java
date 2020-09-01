package com.armjld.eb3tly.Requests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Adapters.RequestsAdapter;
import com.armjld.eb3tly.Adapters.confirm_adapter;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.admin.Admin_Confirm;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.ConfirmationData;
import Model.requestsData;

public class RequestsForSup extends AppCompatActivity {

    private static ArrayList<requestsData> mm;
    private long count;
    private RecyclerView requestRecycler;
    private DatabaseReference uDatabase, mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_for_sup);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        requestRecycler = findViewById(R.id.requestRecycler);

        String orderID = getIntent().getStringExtra("orderid");

        //Recycler
        requestRecycler = findViewById(R.id.requestRecycler);
        requestRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        requestRecycler.setLayoutManager(layoutManager);

        tbTitle.setText("اقتراحات الاسعار");

        count =0;
        mm = new ArrayList<requestsData>();

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");

        mDatabase.child(orderID).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        requestsData rData = ds.getValue(requestsData.class);
                        mm.add((int) count, rData);
                        count++;
                        RequestsAdapter req = new RequestsAdapter(RequestsForSup.this, mm, count, orderID);
                        requestRecycler.setAdapter(req);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}