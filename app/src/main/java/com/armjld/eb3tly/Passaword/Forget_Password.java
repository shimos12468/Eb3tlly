package com.armjld.eb3tly.Passaword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Login.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forget_Password extends AppCompatActivity {

    private Button sendEmail;
    private FirebaseAuth firebaseAuth;
    private EditText email;
    private DatabaseReference uDatabase;
    private ImageView btnBack;
    private ProgressDialog mdialog;

    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);
        mdialog = new ProgressDialog(this);
        sendEmail = findViewById(R.id.btnSendEmail);
        email = findViewById(R.id.txtEmail);
        btnBack = findViewById(R.id.btnBack);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("استعادة الرقم السري");

        firebaseAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v-> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        sendEmail.setOnClickListener(view -> {
            String strEmail = email.getText().toString();
            if(strEmail.isEmpty()){
                Toast.makeText(Forget_Password.this, "الرداء ادخال البريد الالكتروني", Toast.LENGTH_SHORT).show();
                return;
            }
            mdialog.setMessage("جاري التحقق من بريدك ..");
            mdialog.show();

            uDatabase.orderByChild("email").equalTo(strEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        firebaseAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(Forget_Password.this, "تم ارسال رساله الي بريدك الالكتروني", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(Forget_Password.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(Forget_Password.this, "عفوا, هذا البريد ليس مسجل لدي اي حساب", Toast.LENGTH_SHORT).show();
                    }
                    mdialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

    }
}