package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextView signup,txtForgetPass;
    private EditText email,pass;
    private Button btnlogin;

    //FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private ProgressDialog mdialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Connect to Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        mdialog = new ProgressDialog(this);
        signup = findViewById(R.id.signup_text);
        email = findViewById(R.id.txtEditName);
        pass = findViewById(R.id.txtEditPassword);
        btnlogin = findViewById(R.id.btnEditInfo);
        txtForgetPass = findViewById(R.id.txtForgetPass);

        final Intent signIntent = new Intent(this, Terms.class);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                signIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signIntent);

            }
        });

        final Intent forgetIntent = new Intent(this, ForgetPass.class);
        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                forgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(forgetIntent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String memail = email.getText().toString().trim();
                    String mpass = pass.getText().toString().trim();

                    if (TextUtils.isEmpty(memail)) {
                        email.setError("يجب ادخال اسم المستخدم");
                        return;
                    }
                    if (TextUtils.isEmpty(mpass)) {
                        pass.setError("يجب ادخال كلمه المرور");
                        return;
                    }
                    mdialog.setMessage("جاري تسجيل الدخول..");
                    mdialog.show();

                    mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        String deviceToken = instanceIdResult.getToken();
                                        Log.i("Token : ", deviceToken);
                                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(userID).child("device_token").setValue(deviceToken);
                                        FirebaseDatabase.getInstance().getReference("Pickly").child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                                                String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();
                                                if(isActive.equals("true")) { // Check if the account is Disabled
                                                    // --------------------- check account types and send each type to it's activity --------------//
                                                    switch (uType) {
                                                        case "Supplier":
                                                            startActivity(new Intent(getApplicationContext(), profile.class));
                                                            break;
                                                        case "Delivery Worker":
                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                            break;
                                                        case "Admin":
                                                            startActivity(new Intent(getApplicationContext(), Admin.class));
                                                            break;
                                                    }
                                                } else {
                                                    Toast.makeText(MainActivity.this, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        mdialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "تأكد من بيانات الحساب", Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();
                            }
                        }
                    });
                }
            });
    }
}
