package com.armjld.eb3tly.messeges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.armjld.eb3tly.Adapters.MessageAdapter;
import com.armjld.eb3tly.Chat.Chats;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.Login_Options;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Model.Chat;
import Model.userData;

public class Messages extends AppCompatActivity {

    private DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
    private DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    ImageView btnSend;
    String uName = UserInFormation.getUserName();
    private String uId = UserInFormation.getId();
    boolean f = true;
    private String rId = "";
    private String roomId ;
    ImageView btnBack;

    EditText editWriteMessage;
    RecyclerView recyclerMsg;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Chats.class));
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        roomId = getIntent().getStringExtra("roomid");
        rId = getIntent().getStringExtra("rid");

        btnSend = findViewById(R.id.btnSend);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        editWriteMessage = findViewById(R.id.editWriteMessage);
        recyclerMsg = findViewById(R.id.recyclerMsg);
        btnBack = findViewById(R.id.btnBack);


        recyclerMsg.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerMsg.setLayoutManager(linearLayoutManager);


        btnBack.setOnClickListener(v-> {
            startActivity(new Intent(this, Chats.class));
        });

        uDatabase.child(rId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tbTitle.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnSend.setOnClickListener(v -> {
            String msg = editWriteMessage.getText().toString().trim();

            if(msg.length() == 0) {
                editWriteMessage.requestFocus();
                Toast.makeText(this, "Fuck", Toast.LENGTH_SHORT).show();
                return;
            }

            String msgID =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).push().getKey();
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgID).child("senderid").setValue(uId);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgID).child("reciverid").setValue(rId);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgID).child("msg").setValue(msg);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgID).child("timestamp").setValue(datee);

            messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(roomId);
            messageDatabase.child("timestamp").setValue(datee);

            messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(roomId);
            messageDatabase.child("timestamp").setValue(datee);

            Log.i("KOSMY", roomId);

            editWriteMessage.setText("");
        });

        readMessage();
    }

    private void readMessage() {
        mChat = new ArrayList<>();
        messageDatabase.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Chat chat = ds.getValue(Chat.class);
                        mChat.add(chat);
                        messageAdapter = new MessageAdapter(Messages.this, mChat);
                        recyclerMsg.setAdapter(messageAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}