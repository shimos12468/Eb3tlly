package com.armjld.eb3tly.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.armjld.eb3tly.Adapters.MyAdapter;
import com.armjld.eb3tly.Adapters.chatsAdapter;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
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
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        mChat = new ArrayList<ChatsData>();

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if(ds.child("roomid").exists() && ds.child("orderid").exists()) {
                        ChatsData cchatData = ds.getValue(ChatsData.class);

                        String roomID = cchatData.getRoomid();
                        Log.i("Chats", "Room id : " + roomID);

                        mChat.add(cchatData);
                    }
                    _chatsAdapter = new chatsAdapter(Chats.this, mChat);
                    recyclerChat.setAdapter(_chatsAdapter);
                }
                Log.i("Chats", mChat.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}