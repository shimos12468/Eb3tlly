package com.armjld.eb3tly.messeges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.armjld.eb3tly.Adapters.MessageAdapter;
import com.armjld.eb3tly.Chat.Chats;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.Login_Options;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    ImageView btnBack,imgPPP, btnCall;
    TextView txtName, txtType;
    private static final int PHONE_CALL_CODE = 100;
    public static String cameFrom = "Chats";


    EditText editWriteMessage;
    RecyclerView recyclerMsg;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    private void getBack() {
        switch (cameFrom) {
            case "Chats" : {
                startActivity(new Intent(this, Chats.class));
                break;
            }
            case "Profile" : {
                finish();
                break;
            }
        }
    }

    private void whichProfile () {
        if(UserInFormation.getAccountType().equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }

    @Override
    public void onBackPressed() {
        getBack();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    String phoneNumb = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        roomId = getIntent().getStringExtra("roomid");
        rId = getIntent().getStringExtra("rid");

        btnSend = findViewById(R.id.btnSend);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("");
        editWriteMessage = findViewById(R.id.editWriteMessage);
        recyclerMsg = findViewById(R.id.recyclerMsg);
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        imgPPP = findViewById(R.id.imgPPP);
        btnCall = findViewById(R.id.btnCall);

        recyclerMsg.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerMsg.setLayoutManager(linearLayoutManager);


        btnBack.setOnClickListener(v-> {
            getBack();
        });

        btnCall.setOnClickListener(v-> {
            if(!phoneNumb.equals("")) {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumb));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "التاجر لم ضع رقم هاتف", Toast.LENGTH_SHORT).show();
            }
        });

        uDatabase.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtName.setText(snapshot.child("name").getValue().toString());
                String dataPhone = snapshot.child("phone").getValue().toString();

                if(dataPhone.length() == 11) {
                    phoneNumb = dataPhone;
                } else if (dataPhone.length() == 10) {
                    phoneNumb = "0" + dataPhone;
                } else {
                    phoneNumb = "";
                }

                if(snapshot.child("accountType").getValue().toString().equals("Supplier")) {
                    txtType.setText("تاجر");
                } else if(snapshot.child("accountType").getValue().toString().equals("Delivery Worker")){
                    txtType.setText("مندوب شحن");
                } else {
                    txtType.setText("خدمة العملاء");
                }
                Picasso.get().load(Uri.parse(snapshot.child("ppURL").getValue().toString())).into(imgPPP);
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

            messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(rId);
            messageDatabase.child("timestamp").setValue(datee);

            messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId);
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
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}