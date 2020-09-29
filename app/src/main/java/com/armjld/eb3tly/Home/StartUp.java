package com.armjld.eb3tly.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.SignUp.Intros.IntroFirstRun;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Login.Login_Options;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import Model.UserInFormation;

public class StartUp extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    private FirebaseAuth mAuth;
    private String TAG = "StartUp";
    private ConstraintLayout startConst;
    public UserInFormation userInfo = new UserInFormation();
    int codee = 10001;
    static DatabaseReference uDatabase;
    LinearLayout linLogo;
    DatabaseReference Database;
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
        linLogo = findViewById(R.id.linLogo);

        Animation animSlide = AnimationUtils.loadAnimation(this, R.anim.slide);
        linLogo.startAnimation(animSlide);

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("com.armjld.eb3tly", MODE_PRIVATE);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    LoginManager _lgnMn = new LoginManager();
                    _lgnMn.setMyInfo(StartUp.this);
                } else {
                    if(sharedPreferences.getBoolean("firstrun", true)) {
                        sharedPreferences.edit().putBoolean("firstrun", false).apply();
                        finish();
                        startActivity(new Intent(StartUp.this, IntroFirstRun.class));
                    } else {
                        finish();
                        startActivity(new Intent(StartUp.this, Login_Options.class));
                    }
                }
            }
        }, 2500);
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
}
