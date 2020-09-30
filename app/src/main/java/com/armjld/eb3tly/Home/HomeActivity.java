package com.armjld.eb3tly.Home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.CaptinProfile.capAcceptedTab;
import com.armjld.eb3tly.CaptinProfile.capDelvTab;
import com.armjld.eb3tly.CaptinProfile.captinRecived;
import com.armjld.eb3tly.Chat.ChatFragmet;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Settings.SettingFragment;
import Model.UserInFormation;
import com.armjld.eb3tly.CaptinProfile.captinFragment;
import com.armjld.eb3tly.SupplierProfile.acceptedTab;
import com.armjld.eb3tly.SupplierProfile.dilveredTab;
import com.armjld.eb3tly.SupplierProfile.placedTab;
import com.armjld.eb3tly.SupplierProfile.supplierFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;

public class HomeActivity extends AppCompatActivity  {

    public static ArrayList<Data> mm = new ArrayList<>();
    public static ArrayList<Data> delvList = new ArrayList<>();
    public static ArrayList<Data> supList = new ArrayList<>();

    private static DatabaseReference mDatabase;
    public static boolean requests = false;
    public static boolean orders = false;

    static BlockManeger block = new BlockManeger();

    public static String TAG = "Home Activity";
    // Disable the Back Button

    boolean doubleBackToExitPressedOnce = false;
    public static String whichFrag = "Home";
    public static BottomNavigationView bottomNavigationView;

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Home");
        if(fragment != null && fragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        } else {
            whichFrag = "Home";
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), whichFrag).addToBackStack("Home").commit();
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        makeChecks();

        if(UserInFormation.getAccountType().equals("Supplier")) {
            getSupOrders();
        } else {
            getDeliveryOrders();
        }

        getOrdersByLatest();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag(), whichFrag).addToBackStack("Home").commit();

    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Home" : {
                frag = new HomeFragment();
                bottomNavigationView.setSelectedItemId(R.id.home);

                break;
            }
            case "Profile" : {
                if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                    frag = new captinFragment();
                } else {
                    frag = new supplierFragment();
                }
                bottomNavigationView.setSelectedItemId(R.id.profile);

                break;
            }

            case "Chats" : {
                frag = new ChatFragmet();
                bottomNavigationView.setSelectedItemId(R.id.chats);

                break;
            }

            case "Settings" : {
                frag = new SettingFragment();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
            }
        }
        return frag;
    }

    private void makeChecks() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        String datee = sdf.format(new Date());

        if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            DatabaseReference Wdatabase =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("requests");
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

            mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count2 = 0;
                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("accepted")){
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
        String fragTag = "";
        switch (item.getItemId()) {
            case R.id.home : {
                fragment = new HomeFragment();
                fragTag = "Home";
                break;
            }

            case R.id.settings : {
                fragment = new SettingFragment();
                fragTag = "Settings";
                break;
            }

            case R.id.profile : {
                if(UserInFormation.getAccountType().equals("Supplier")) {
                    fragment = new supplierFragment();
                } else {
                    fragment = new captinFragment();
                }
                fragTag = "Profile";
                break;
            }

            case R.id.chats : {
                fragment = new ChatFragmet();
                fragTag = "Chats";
                break;
            }
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragTag).addToBackStack("Home").commit();
        return true;
    };



    public static void getSupOrders() {
        supList.clear();
        supList.trimToSize();
        delvList.clear();
        delvList.trimToSize();

        Log.i(TAG, "Getting Supplier Orders From Database");

        mDatabase.orderByChild("uId").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Data orderData = ds.getValue(Data.class);
                    assert orderData != null;
                    supList.add(orderData);
                }
                placedTab.getOrders();
                acceptedTab.getOrders();
                dilveredTab.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void getDeliveryOrders() {
        supList.clear();
        supList.trimToSize();
        delvList.clear();
        delvList.trimToSize();
        Log.i(TAG, "Getting Captain Orders From Database");
        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Data orderData = ds.getValue(Data.class);
                    assert orderData != null;
                    delvList.add(orderData);
                }
                capAcceptedTab.getOrders();
                capDelvTab.getOrders();
                captinRecived.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void getOrdersByLatest() {
        mm.clear();
        mm.trimToSize();
        Log.i(TAG, "Getting All Orders From Database");

        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                            myDate =  format.parse(sdf.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed") && !block.check(orderData.getuId())) {
                            mm.add(orderData);
                        }

                        Collections.sort(mm, (o1, o2) -> {
                            String one = o1.getDate();
                            String two = o2.getDate();
                            return one.compareTo(two);
                        });
                    }

                    HomeFragment.getOrders();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


}