package com.armjld.eb3tly.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.armjld.eb3tly.Chat.ChatFragmet;
import com.armjld.eb3tly.Home.HomeFragment;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Settings.SettingFragment;
import com.armjld.eb3tly.Home.StartUp;
import Model.UserInFormation;
import com.armjld.eb3tly.CaptinProfile.captinFragment;
import com.armjld.eb3tly.SupplierProfile.supplierFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Model.Data;


public class HomeActivity extends AppCompatActivity  {

    public static ArrayList<Data> mm = new ArrayList<Data>();
    public static ArrayList<Data> delvList = new ArrayList<Data>();
    public static ArrayList<Data> supList = new ArrayList<Data>();

    private static DatabaseReference mDatabase;
    public static boolean requests = false;
    public static boolean orders = false;

    private String TAG = "Home Activity";
    String uType = UserInFormation.getAccountType();
    static String uId = UserInFormation.getId();
    // Disable the Back Button

    boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView bottomNavigationView;
    public static String whichFrag = "Home";

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

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    // On Create Fun
    @SuppressLint("RtlHardcoded")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        makeChecks();

        if(UserInFormation.getAccountType().equals("Supplier")) {
            getSupOrders();
        } else {
            getDeliveryOrders();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag()).commit();

    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Home" : {
                frag = new HomeFragment();
                break;
            }
            case "Profile" : {
                if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                    frag = new captinFragment();
                } else {
                    frag = new supplierFragment();
                }
                break;
            }

            case "Chats" : {
                frag = new ChatFragmet();
                break;
            }

            case "Settings" : {
                frag = new SettingFragment();
                break;
            }
        }
        return frag;
    }

    private void makeChecks() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        String datee = sdf.format(new Date());

        if(uType.equals("Delivery Worker")) {
            DatabaseReference Wdatabase =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("requests");
            Wdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(ds.child("statue").exists()) {
                                if(ds.child("statue").getValue().toString().equals("N/A") && ds.child("date").getValue().toString().substring(0 , 10).equals(datee.substring(0 ,10))){
                                    count++;
                                }
                            }
                        }

                        if(count >= 10) {
                            requests = true;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count2 = 0;
                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(ds.child("statue").getValue().toString().equals("accepted")){
                                count2++;
                            }
                        }

                        if(count2 >= 20) {
                            orders = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home : {
                fragment = new HomeFragment();
                break;
            }

            case R.id.settings : {
                fragment = new SettingFragment();
                break;
            }

            case R.id.profile : {
                if(UserInFormation.getAccountType().equals("Supplier")) {
                    fragment = new supplierFragment();
                } else {
                    fragment = new captinFragment();
                }
                break;
            }

            case R.id.chats : {
                fragment = new ChatFragmet();
                break;
            }
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        return true;
    };



    public static void getSupOrders() {
        supList.clear();
        supList.trimToSize();
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Data orderData = ds.getValue(Data.class);
                    assert orderData != null;
                    supList.add(orderData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void getDeliveryOrders() {
        delvList.clear();
        delvList.trimToSize();
        mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Data orderData = ds.getValue(Data.class);
                    assert orderData != null;
                    delvList.add(orderData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


}