package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Objects;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MainActivity extends AppCompatActivity {

    private TextView signup,txtForgetPass;
    @SuppressLint("StaticFieldLeak")
    public static EditText email,pass;
    private Button btnlogin;
    //FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private ProgressDialog mdialog;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Connect to Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        uDatabase = getInstance().getReference().child("Pickly").child("users");


        mdialog = new ProgressDialog(this);
        signup = findViewById(R.id.signup_text);
        email = findViewById(R.id.txtEditName);
        pass = findViewById(R.id.txtEditPassword);
        btnlogin = findViewById(R.id.btnEditInfo);
        txtForgetPass = findViewById(R.id.txtForgetPass);

        final Intent signIntent = new Intent(this, Terms.class);
        signup.setOnClickListener(v -> {
            finish();
            signIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIntent);

        });

        final Intent forgetIntent = new Intent(this, ForgetPass.class);
        txtForgetPass.setOnClickListener(v -> {
            finish();
            forgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(forgetIntent);
        });

        String uMail = getIntent().getStringExtra("umail");
        String uPass = getIntent().getStringExtra("upass");

        if(uMail != null && uPass != null) {
            email.setText(uMail);
            pass.setText(uPass);
        }

        btnlogin.setOnClickListener(v -> {
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

            mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                        String deviceToken = instanceIdResult.getToken();
                        Log.i("Token : ", deviceToken);
                        FirebaseDatabase.getInstance().getReference("Pickly").child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() && mAuth.getCurrentUser() != null){
                                    //uDatabase.child(userID).child("device_token").setValue(deviceToken);
                                    uDatabase.child(userID).child("mpass").setValue(mpass);
                                    String isCompleted = Objects.requireNonNull(snapshot.child("completed").getValue()).toString();

                                    if (isCompleted.equals("true")) {
                                        String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                                        String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();
                                        StartUp.userType = uType;
                                        StartUp.userName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                        StartUp.userDate = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                                        StartUp.userURL = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                                        if (isActive.equals("true")) { // Check if the account is Disabled
                                            // --------------------- check account types and send each type to it's activity --------------//
                                            switch (uType) {
                                                case "Supplier":
                                                    mdialog.dismiss();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), supplierProfile.class));
                                                    break;
                                                case "Delivery Worker":
                                                    mdialog.dismiss();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                    break;
                                                case "Admin":
                                                    mdialog.dismiss();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), Admin.class));
                                                    break;
                                            }
                                        } else {
                                            mdialog.dismiss();
                                            Toast.makeText(MainActivity.this, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                    } else {
                                        mdialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Please clear the app data and signon again", Toast.LENGTH_SHORT).show();
                                    }
                                } else{
                                    Toast.makeText(getApplicationContext(), "سجل حسابك مرة اخري", Toast.LENGTH_LONG).show();
                                    mdialog.dismiss();
                                    finish();
                                    startActivity(new Intent(MainActivity.this, Signup.class));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "تأكد من بيانات الحساب", Toast.LENGTH_LONG).show();
                    mdialog.dismiss();
                }
            });
        });
    }
}
