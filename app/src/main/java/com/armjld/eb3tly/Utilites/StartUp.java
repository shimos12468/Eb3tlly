package com.armjld.eb3tly.Utilites;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Intros.IntroFirstRun;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.admin.Admin;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.main.MainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class StartUp extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    private FirebaseAuth mAuth;
    private String TAG = "StartUp";
    private ConstraintLayout startConst;
    public UserInFormation userInfo = new UserInFormation();
    int codee = 10001;

    DatabaseReference uDatabase , Database;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_startup);
        startConst = findViewById(R.id.startConst);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        // ---------------- Check for Updates ----------------------//
       /* AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(StartUp.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            codee);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    whatToDo();
                }
                Toast.makeText(this, "يوجد تحديث جديد متاح", Toast.LENGTH_SHORT).show();
            } else {
                whatToDo();
            }
        });*/
        whatToDo();
    }

    private void whatToDo() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            reRoute();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sharedPreferences = getSharedPreferences("com.armjld.eb3tly", MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("firstrun", true)) {
                        Log.i("c" ,IntroFirstRun.class.toString());
                        startActivity(new Intent(StartUp.this, IntroFirstRun.class));
                        sharedPreferences.edit().putBoolean("firstrun", false).apply();
                    } else {
                        Log.i("AZA" ,MainActivity.class.toString());
                        startActivity(new Intent(StartUp.this, MainActivity.class));
                    }
                }
            }, 2500);
        }
    }

    public void reRoute () {
        mAuth = FirebaseAuth.getInstance();
        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.child("id").exists()) {
                    String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();
                    String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();

                    // ------------------ Set Device Token ----------------- //
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(StartUp.this, instanceIdResult -> {
                        String deviceToken = instanceIdResult.getToken();
                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(deviceToken);
                    });

                    UserInFormation.setAccountType(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString());
                    UserInFormation.setUserName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                    UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString());
                    UserInFormation.setId(mAuth.getCurrentUser().getUid());
                    UserInFormation.setEmail(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                    UserInFormation.setPass(Objects.requireNonNull(snapshot.child("mpass").getValue()).toString());
                    UserInFormation.setPhone(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                    UserInFormation.setisConfirm("false");

                    if(snapshot.child("isConfirmed").exists()) {
                        UserInFormation.setisConfirm(Objects.requireNonNull(snapshot.child("isConfirmed").getValue()).toString());
                    }

                    setUserData(mAuth.getCurrentUser().getUid());

                    if(isActive.equals("true")) {
                        if(!snapshot.child("userState").exists()) {
                            Toast.makeText(StartUp.this, "لا تنسي اضافه محافظتك في بياناتك الشخصيه", Toast.LENGTH_LONG).show();
                        }

                        try {
                            PackageInfo pInfo = StartUp.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            String version = pInfo.versionName;
                            uDatabase.child(mAuth.getCurrentUser().getUid()).child("app_version").setValue(version);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        ImportBlockedUsers();
                        switch (UserInFormation.getAccountType()) {
                            case "Supplier":
                                finish();
                                startActivity(new Intent(StartUp.this, supplierProfile.class));
                                break;
                            case "Delivery Worker":
                                finish();
                                startActivity(new Intent(StartUp.this, HomeActivity.class));
                                break;
                            case "Admin":
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
                } else {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(StartUp.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == codee) {
            if (resultCode != RESULT_OK) {
                whatToDo();
                Toast.makeText(this, "لم يتم تحديث التطبيق", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ImportBlockedUsers() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Database = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid());
        Database.child("Blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        blocedUsers.add(ds.child("id").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void setUserData(String uid) {
        uDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInFormation.setAccountType(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString());
                UserInFormation.setUserName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString());
                UserInFormation.setId(uid);
                UserInFormation.setEmail(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                UserInFormation.setPass(Objects.requireNonNull(snapshot.child("mpass").getValue()).toString());
                UserInFormation.setPhone(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                UserInFormation.setisConfirm("false");

                if(snapshot.child("isConfirmed").exists()) {
                    UserInFormation.setisConfirm(Objects.requireNonNull(snapshot.child("isConfirmed").getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
