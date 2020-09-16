package com.armjld.eb3tly.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.eb3tly.Adapters.MyAdapter;
import com.armjld.eb3tly.Adapters.chatsAdapter;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.ChatsData;

public class Chats extends AppCompatActivity {

    DatabaseReference messageDatabase;
    String uId = UserInFormation.getId();

    private chatsAdapter _chatsAdapter;

    RecyclerView recyclerChat;
    ArrayList<ChatsData> mChat;
    ImageView btnBack;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        recyclerChat = findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        btnBack = findViewById(R.id.btnBack);
        tbTitle.setText("المحادثات");

        btnBack.setOnClickListener(v-> {
            finish();
        });

        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        mChat = new ArrayList<ChatsData>();
        final int[] count = {0};
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if(ds.child("roomid").exists()) {
                        ChatsData cchatData = ds.getValue(ChatsData.class);
                        String talk = "true";
                        if(ds.child("talk").exists()) {
                            talk = ds.child("talk").getValue().toString();
                        }

                        if(ds.child("timestamp").exists() && talk.equals("true")) {
                            mChat.add(cchatData);
                            count[0] +=1;
                            _chatsAdapter = new chatsAdapter(Chats.this, mChat);
                            _chatsAdapter.addItem(count[0],cchatData);
                            recyclerChat.setAdapter(_chatsAdapter);
                        }
                    }

                }
                Log.i("Chats", mChat.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void whichProfile () {
        if(UserInFormation.getAccountType().equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}