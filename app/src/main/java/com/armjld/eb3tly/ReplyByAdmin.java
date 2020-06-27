package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    Button btnToDel,btnToSupplier,btnResetUser;
    EditText txtUserNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase,mDatabase,rDatabase,vDatabase,nDatabase;

    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, Admin.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_by_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        vDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("values");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Reply to Messages");

        btnToDel = findViewById(R.id.btnToDel);
        btnToSupplier = findViewById(R.id.btnToSupplier);
        txtUserNumber = findViewById(R.id.txtUserNumber);
        btnResetUser = findViewById(R.id.btnResetUser);

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

        btnToDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = txtUserNumber.getText().toString().trim();
                uDatabase.orderByChild("phone").equalTo(num).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                if(ds.exists()) {
                                    String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                    uDatabase.child(userID).child("accountType").setValue("Delivery Worker");
                                    Toast.makeText(ReplyByAdmin.this, "Changed Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

        btnToSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = txtUserNumber.getText().toString().trim();
                uDatabase.orderByChild("phone").equalTo(num).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                if(ds.exists()) {
                                    String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                    uDatabase.child(userID).child("accountType").setValue("Supplier");
                                    Toast.makeText(ReplyByAdmin.this, "Changed Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

        btnResetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = txtUserNumber.getText().toString().trim();
                uDatabase.orderByChild("phone").equalTo(num).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                if(ds.exists()) {
                                    String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                    uDatabase.child(userID).child("accountType").child("canceled").setValue("0");
                                    Toast.makeText(ReplyByAdmin.this, "Reseted Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ReplyByAdmin.this, "Wrong Number", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
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