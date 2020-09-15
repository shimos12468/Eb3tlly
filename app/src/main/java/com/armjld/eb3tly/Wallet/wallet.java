package com.armjld.eb3tly.Wallet;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Model.Data;

public class wallet {

    private int num;
    private DatabaseReference walletDS;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());


    public boolean workerbid(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        String datee = sdf.format(new Date());
        Date start = null;
        Date end = null;
        String id = UserInFormation.getId();
        String currentdate = UserInFormation.getCurrentdate();
        if(currentdate.equals("none")){
            return true;
        }
        try {
             start =sdf.parse(currentdate);
             end = sdf.parse(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = end.getTime() - start.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        Log.d("sss" , diffHours+" ");
        if(diffHours<48){
            return true;
        }
        else {
            return false;
        }

    }

    public void SupsetDilivared(String orderid){
        String id = UserInFormation.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        String datee = sdf.format(new Date());
        String currentdate = UserInFormation.getCurrentdate();
        walletDS = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id);
                if(!currentdate.equals("none")){
                    walletDS =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("wallet").child(currentdate).child(orderid);
                    walletDS.child("payed").setValue("false");

                }
                else{
                    walletDS =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id);
                    walletDS.child("currentDate").setValue(datee);
                    UserInFormation.setCurrentdate(datee);
                    walletDS =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("wallet").child(datee).child(orderid);
                    walletDS.child("payed").setValue("false");
                }
    }

    public void presspay(){
        String id = UserInFormation.getId();
        walletDS = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id);
        walletDS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentDate = snapshot.child("currentDate").getValue().toString();
                walletDS = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("wallet").child(currentDate);
                walletDS.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String orderID = ds.getKey();
                            walletDS.child(orderID).child("payed").setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                walletDS = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id);
                walletDS.child("currentDate").setValue("none");
                UserInFormation.setCurrentdate("none");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
