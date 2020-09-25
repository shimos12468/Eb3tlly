package com.armjld.eb3tly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.DatabaseClasses.Ratings;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Login.Login_Options;
import com.armjld.eb3tly.admin.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Objects;
import Model.UserInFormation;

public class LoginManager {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    public static boolean dataset = false;


    public void setMyInfo(Context mContext) {

        uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).keepSynced(true);
        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) mContext, instanceIdResult -> {
                        String deviceToken = instanceIdResult.getToken();
                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(deviceToken);
                    });

                    UserInFormation.setAccountType(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString());
                    UserInFormation.setUserName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                    UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString());
                    UserInFormation.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    UserInFormation.setEmail(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                    UserInFormation.setPass(Objects.requireNonNull(snapshot.child("mpass").getValue()).toString());
                    UserInFormation.setPhone(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                    UserInFormation.setisConfirm("false");

                    Ratings _ratings = new Ratings();
                    _ratings.setMyRating();

                    if(snapshot.child("isConfirmed").exists()) {
                        UserInFormation.setisConfirm(Objects.requireNonNull(snapshot.child("isConfirmed").getValue()).toString());
                    }

                    if(snapshot.child("currentDate").exists()) {
                        UserInFormation.setCurrentdate(Objects.requireNonNull(snapshot.child("currentDate").getValue()).toString());
                    }

                    if(snapshot.child("sendOrderNoti").exists()) {
                        UserInFormation.setSendGovNoti(Objects.requireNonNull(snapshot.child("sendOrderNoti").getValue()).toString());
                    }

                    if(snapshot.child("sendOrderNotiCity").exists()) {
                        UserInFormation.setSendCityNoti(Objects.requireNonNull(snapshot.child("sendOrderNoti").getValue()).toString());
                    }

                    dataset = true;


                    if(isActive.equals("true")) {
                        try {
                            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                            String version = pInfo.versionName;
                            uDatabase.child(mAuth.getCurrentUser().getUid()).child("app_version").setValue(version);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        ImportBlockedUsers();
                        switch (UserInFormation.getAccountType()) {
                            case "Supplier":
                            case "Delivery Worker":
                                mContext.startActivity(new Intent(mContext, HomeActivity.class));
                                break;
                            case "Admin":
                                mContext.startActivity(new Intent(mContext, Admin.class));
                                break;
                        }

                    } else {
                        Toast.makeText(mContext, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                        clearInfo(mContext);
                        mContext.startActivity(new Intent(mContext, Login_Options.class));
                    }
                } else {
                    clearInfo(mContext);
                    mContext.startActivity(new Intent(mContext, Login_Options.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        
    }

    private void ImportBlockedUsers() {
        uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        blocedUsers.add(Objects.requireNonNull(ds.child("id").getValue()).toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    
    public void clearInfo(Context mContext) {
        uDatabase.child(Objects.requireNonNull(mAuth.getUid())).child("device_token").setValue("");
        if(Login_Options.mGoogleSignInClient != null) {
            Login_Options.mGoogleSignInClient.signOut();
        }
        Login_Options.disconnectFromFacebook();
        mAuth.signOut();
        mContext.startActivity(new Intent(mContext, Login_Options.class));
        UserInFormation.clearUser();
        dataset = false;
        Toast.makeText(mContext, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }
}
