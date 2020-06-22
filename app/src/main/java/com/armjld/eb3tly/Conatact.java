package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import Model.notiData;
import Model.replyAdmin;

public class Conatact extends AppCompatActivity {

    Button btnSend;
    EditText txtContact;
    FirebaseAuth mAuth;
    String strName, strPhone, strEmail = "";
    String userID;
    DatabaseReference uDatabase, cDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conatact);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخزل", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تواصل معنا");

        btnSend = findViewById(R.id.btnSend);
        txtContact = findViewById(R.id.txtContact);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        cDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("messages");

        uDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                strName = dataSnapshot.child("name").getValue().toString();
                strEmail = dataSnapshot.child("email").getValue().toString();
                strPhone = dataSnapshot.child("phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(txtContact.getText().toString().trim())) {
                    Toast.makeText(Conatact.this, "الرجاء كتابه رسالتك بالكامل", Toast.LENGTH_SHORT).show();
                    return;
                }

                String version = "";
                try {
                    PackageInfo pInfo = Conatact.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String id = cDatabase.push().getKey().toString();
                replyAdmin Noti = new replyAdmin(strEmail,txtContact.getText().toString().trim(),strName,strPhone,"opened",datee,id,version,mAuth.getCurrentUser().getUid());
                cDatabase.child(userID).child(id).setValue(Noti);

                Toast.makeText(Conatact.this, "شكرا لك تم استلام رسالتك و سيتم الرد عليك في اقرب وقت", Toast.LENGTH_LONG).show();

                finish();
                startActivity(new Intent(Conatact.this, profile.class));
            }
        });



    }
}