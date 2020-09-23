package com.armjld.eb3tly.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Login.MainActivity;
import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Model.replyAdmin;

public class Conatact extends AppCompatActivity {

    Button btnSend;
    EditText txtContact;
    FirebaseAuth mAuth;
    String strName, strPhone, strEmail = "";
    String userID;
    DatabaseReference uDatabase, cDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    String uType = UserInFormation.getAccountType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conatact);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تواصل معنا");

        btnSend = findViewById(R.id.btnSend);
        txtContact = findViewById(R.id.txtContact);
        mAuth = FirebaseAuth.getInstance();
        userID = UserInFormation.getId();
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

        btnSend.setOnClickListener(v -> {

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

            String id = cDatabase.push().getKey();
            replyAdmin Noti = new replyAdmin(strEmail,txtContact.getText().toString().trim(),strName,strPhone,"opened",datee,id,version,userID);
            assert id != null;
            cDatabase.child(userID).child(id).setValue(Noti);

            Toast.makeText(Conatact.this, "شكرا لك تم استلام رسالتك و سيتم الرد عليك في اقرب وقت", Toast.LENGTH_LONG).show();
            finish();
            whichProfile();
        });



    }

    private void whichProfile () {
        finish();
    }
}