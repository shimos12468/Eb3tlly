package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import Model.Data;
import Model.notiData;
import Model.replyAdmin;
import Model.replyAdmin;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class ReplyByAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference cDatabase;
    private static ArrayList<replyAdmin> mm;
    private long count;
    private RecyclerView recyclerView;
    String TAG = "ReplyByAdmin";

    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, Admin.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_by_admin);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Reply to Messages");

        count =0;
        mm = new ArrayList<replyAdmin>();

        //Recycler
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        cDatabase = getInstance().getReference().child("Pickly").child("messages");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ---------------------- GET ALL THE Opened Messages -------------------//
        cDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot snap : ds.getChildren()) {
                        if(snap.child("statue").getValue().toString().equals("opened")) {
                            Log.i(TAG, " Message " + snap.getValue().toString());
                            replyAdmin replyAdmins = snap.getValue(replyAdmin.class);
                            mm.add((int) count, replyAdmins);
                            count++;
                            replyAdapter rep = new replyAdapter(ReplyByAdmin.this, mm, getApplicationContext(), count);
                            recyclerView.setAdapter(rep);
                        }
                    }
                }
                count = 0;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
}