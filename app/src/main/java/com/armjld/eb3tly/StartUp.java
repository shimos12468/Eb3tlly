package com.armjld.eb3tly;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
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
    private ConstraintLayout startConst;
    public UserInFormation userInfo = new UserInFormation();

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

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_startup);
        startConst = findViewById(R.id.startConst);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        // ---------------- Check for Updates ----------------------//
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(StartUp.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            10001);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "يوجد تحديث جديد متاح", Toast.LENGTH_SHORT).show();
            } else {
                whatToDo();
            }
        });
    }

    private void whatToDo() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //ImportBlockedUsers();
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

    private void ImportBlockedUsers() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Database = FirebaseDatabase.getInstance().getReference().child("Pickly").child("Blocked");
        Database.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                       blocedUsers.adduser(ds.getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void reRoute () {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        uDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.child("id").exists()) {

                    // ------------------ Set Device Token ----------------- //
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(StartUp.this, instanceIdResult -> {
                        String deviceToken = instanceIdResult.getToken();
                        uDatabase.child(user.getUid()).child("device_token").setValue(deviceToken);
                    });

                    String isComplete = Objects.requireNonNull(snapshot.child("completed").getValue()).toString();
                    String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();

                    if(isComplete.equals("true")) {
                        String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                        UserInFormation.setAccountType(uType);
                        UserInFormation.setUserName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                        UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                        UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString());
                        if(isActive.equals("true")) {
                            if(!snapshot.child("userState").exists()) {
                                Toast.makeText(StartUp.this, "لا تنسي اضافه محافظتك في بياناتك الشخصيه", Toast.LENGTH_LONG).show();
                            }

                            try {
                                PackageInfo pInfo = StartUp.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                                String version = pInfo.versionName;
                                uDatabase.child(user.getUid()).child("app_version").setValue(version);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if (resultCode != RESULT_OK) {
                whatToDo();
                Toast.makeText(this, "لم يتم تحديث التطبيق", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
