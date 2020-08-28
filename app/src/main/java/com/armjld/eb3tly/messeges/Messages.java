package com.armjld.eb3tly.messeges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.armjld.eb3tly.Adapters.MessageAdapter;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
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

    DatabaseReference messageDatabase,uDatabase;
    ImageView btnSend;
    String uName = UserInFormation.getUserName();
    String uId = UserInFormation.getId();
    Toolbar toolbar_home;

    String rId = "";
    String roomId = "";

    EditText editWriteMessage;
    RecyclerView recyclerMsg;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        btnSend = findViewById(R.id.btnSend);
        toolbar_home = findViewById(R.id.toolbar_home);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        editWriteMessage = findViewById(R.id.editWriteMessage);
        recyclerMsg = findViewById(R.id.recyclerMsg);
        recyclerMsg.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerMsg.setLayoutManager(linearLayoutManager);

        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");



        btnSend.setOnClickListener(v -> {
            String msg = editWriteMessage.getText().toString().trim();
            if(msg.length() == 0) {
                editWriteMessage.requestFocus();
                return;
            }

            HashMap<String, Object> mHashmap = new HashMap<>();
            mHashmap.put("senderid", uId);
            mHashmap.put("reciverid", rId);
            mHashmap.put("msg", msg);
            mHashmap.put("timestamp", datee);
            messageDatabase.child(roomId).push().setValue(mHashmap);
        });

        uDatabase.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData uData = snapshot.getValue(userData.class);
                String uName = uData.getname();
                String uType = uData.getAccountType();
                String ppURL = uData.getPpURL();

                // ---- Set the Data in the Header
                tbTitle.setText(uName + "(" + uType + ")");
                readMessage(uId, rId, ppURL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void readMessage(String id, String userID, String imgURL) {
        mChat = new ArrayList<>();
        messageDatabase.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    mChat.add(chat);
                    messageAdapter = new MessageAdapter(Messages.this, mChat, imgURL);
                    recyclerMsg.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}