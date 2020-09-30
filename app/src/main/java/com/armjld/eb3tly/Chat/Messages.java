package com.armjld.eb3tly.Chat;

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

import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Orders.OrderInfo;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import Model.Chat;

public class Messages extends AppCompatActivity {

    private DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
    private DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    ImageView btnSend;
    private String uId = UserInFormation.getId();
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
                HomeActivity.whichFrag = "Chats";
                startActivity(new Intent(this, HomeActivity.class));
                break;
            }
            case "Profile" : {
                //HomeActivity.whichFrag = "Profile";
                finish();
                break;
            }
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


        btnBack.setOnClickListener(v-> getBack());

        btnCall.setOnClickListener(v-> {
            if(!phoneNumb.equals("")) {

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(Messages.this).setMessage("هل تريد الاتصال ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {

                    checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumb));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                }).build();
                mBottomSheetDialog.show();



            } else {
                Toast.makeText(this, "التاجر لم يضع رقم هاتف", Toast.LENGTH_SHORT).show();
            }
        });

        uDatabase.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                String dataPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();

                if(dataPhone.length() == 11) {
                    phoneNumb = dataPhone;
                } else if (dataPhone.length() == 10) {
                    phoneNumb = "0" + dataPhone;
                } else {
                    phoneNumb = "";
                }

                if(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString().equals("Supplier")) {
                    txtType.setText("تاجر");
                } else if(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString().equals("Delivery Worker")){
                    txtType.setText("كابتن");
                } else {
                    txtType.setText("خدمة العملاء");
                }
                Picasso.get().load(Uri.parse(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString())).into(imgPPP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnSend.setOnClickListener(v -> {
            String msg = editWriteMessage.getText().toString().trim();

            if(msg.length() == 0) {
                editWriteMessage.requestFocus();
                return;
            }

            HashMap<String, Object> newMsg = new HashMap<>();
            newMsg.put("reciverid", rId);
            newMsg.put("senderid", uId);
            newMsg.put("msg", msg);
            newMsg.put("timestamp", datee);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).push().setValue(newMsg);

            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(rId).child("timestamp").setValue(datee);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId).child("timestamp").setValue(datee);

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
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
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