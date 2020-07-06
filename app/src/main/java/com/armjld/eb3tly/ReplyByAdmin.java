package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import Model.replyAdmin;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class ReplyByAdmin extends AppCompatActivity {

    private DatabaseReference cDatabase, uDatabase;
    private static ArrayList<replyAdmin> mm;
    private long count;
    private RecyclerView recyclerView;
    String TAG = "ReplyByAdmin";
    Button btnToDel,btnToSupplier,btnResetUser,btnDeactive,btnInfo;
    EditText txtUserNumber;

    public void onBackPressed() {
        Intent i = new Intent(this, Admin.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_by_admin);

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Reply to Messages");

        btnToDel = findViewById(R.id.btnToDel);
        btnToSupplier = findViewById(R.id.btnToSupplier);
        txtUserNumber = findViewById(R.id.txtUserNumber);
        btnResetUser = findViewById(R.id.btnResetUser);
        btnDeactive = findViewById(R.id.btnDeactive);
        btnInfo = findViewById(R.id.btnInfo);

        count = 0;
        mm = new ArrayList<>();

        //Recycler
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        cDatabase = getInstance().getReference().child("Pickly").child("messages");

        btnInfo.setOnClickListener(v -> {
            String num = txtUserNumber.getText().toString().trim();
            uDatabase.orderByChild("phone").equalTo(num).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(ds.exists()) {
                                String mpass = Objects.requireNonNull(ds.child("mpass").getValue()).toString();
                                String memail = Objects.requireNonNull(ds.child("email").getValue()).toString();
                                String accountType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();
                                Log.i(TAG, "Account Login Info : " + memail + " : " + mpass);
                                ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData cl = ClipData.newPlainText("Info", memail + " : " + mpass);
                                assert clip != null;
                                clip.setPrimaryClip(cl);
                                Toast.makeText(ReplyByAdmin.this, memail + " : " + mpass + " Type : " + accountType, Toast.LENGTH_LONG).show();
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
        });

        btnDeactive.setOnClickListener(v -> {
            String num = txtUserNumber.getText().toString().trim();
            uDatabase.orderByChild("phone").equalTo(num).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(ds.exists()) {
                                String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                uDatabase.child(userID).child("active").setValue("false");
                                Toast.makeText(ReplyByAdmin.this, "Deactiveted Successfully", Toast.LENGTH_SHORT).show();
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
        });

        btnToDel.setOnClickListener(v -> {
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
        });

        btnToSupplier.setOnClickListener(v -> {
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
        });

        btnResetUser.setOnClickListener(v -> {
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
                        if(Objects.requireNonNull(snap.child("statue").getValue()).toString().equals("opened")) {
                            Log.i(TAG, " Message " + Objects.requireNonNull(snap.getValue()).toString());
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
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}