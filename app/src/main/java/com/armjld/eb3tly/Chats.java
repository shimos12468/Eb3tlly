package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.armjld.eb3tly.Adapters.MessageAdapter;
import com.armjld.eb3tly.Adapters.chatsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.ChatsData;

public class Chats extends AppCompatActivity {

    DatabaseReference messageDatabase;
    String uId = UserInFormation.getId();

    chatsAdapter chatsAdapter;
    RecyclerView recyclerChat;
    List<ChatsData> mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        recyclerChat = findViewById(R.id.recyclerChat);
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");

        messageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if((int) ds.getChildrenCount() > 1) {
                        if(ds.child("senderid").getValue().toString().equals(uId) || ds.child("reciverid").getValue().toString().equals(uId)) {
                            // --- add to adapter
                            ChatsData cchatData = ds.getValue(ChatsData.class);
                            mChat.add(cchatData);
                            chatsAdapter = new chatsAdapter(Chats.this, mChat);
                            recyclerChat.setAdapter(chatsAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}