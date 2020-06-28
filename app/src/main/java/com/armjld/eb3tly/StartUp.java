package com.armjld.eb3tly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class StartUp extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    private FirebaseAuth mAuth;
    public static String userType;
    DatabaseReference uDatabase;
    public void onBackPressed() { }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_startup);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            reRoute();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sharedPreferences = getSharedPreferences("com.armjld.eb3tly", MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("firstrun", true)) {
                        startActivity(new Intent(StartUp.this, IntroFirstRun.class));
                        sharedPreferences.edit().putBoolean("firstrun", false).apply();
                    } else {
                        startActivity(new Intent(StartUp.this, MainActivity.class));
                    }
                }
                }, 2500);
        }
    }

    public void reRoute () {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        uDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.child("id").exists()) {
                    String isComplete = Objects.requireNonNull(snapshot.child("completed").getValue()).toString();
                    String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();
                    if(isComplete.equals("true")) {
                        if(isActive.equals("true")) {
                            String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                            switch (uType) {
                                case "Supplier":
                                    userType = uType;
                                    finish();
                                    startActivity(new Intent(StartUp.this, profile.class));
                                    break;
                                case "Delivery Worker":
                                    userType = uType;
                                    finish();
                                    startActivity(new Intent(StartUp.this, HomeActivity.class));
                                    break;
                                case "Admin":
                                    userType = uType;
                                    finish();
                                    startActivity(new Intent(StartUp.this, Admin.class));
                                    break;
                            }
                        } else {
                            Toast.makeText(StartUp.this, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(StartUp.this, MainActivity.class));
                        }
                    }
                } else {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(StartUp.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
