package com.armjld.eb3tly.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

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

import java.util.List;
import java.util.Objects;

import Model.ChatsData;

public class Chats extends AppCompatActivity {

    DatabaseReference messageDatabase;
    String uId = UserInFormation.getId();

    chatsAdapter chatsAdapter;
    RecyclerView recyclerChat;
    List<ChatsData> mChat;

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

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("roomid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    String roomID = ds.child("roomid").getValue().toString();
                    messageDatabase.child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ss) {
                            ChatsData cchatData = ss.getValue(ChatsData.class);
                            mChat.add(cchatData);
                            chatsAdapter = new chatsAdapter(Chats.this, mChat);
                            recyclerChat.setAdapter(chatsAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}