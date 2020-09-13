package com.armjld.eb3tly.Wallet;

import android.icu.util.TimeZone;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class requestsandacceptc {
    DatabaseReference Wdatabase;
    int count = 0;
    int count2 = 0;
    int count3 = 0;

    public boolean acceptdlivaryworker(String id){
        count = 0;
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        Wdatabase.orderByChild("uAccepted").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if(ds.child("statue").getValue().toString().equals("accepted")){
                            count++;
                        }
                    }
                    Log.i("acceptdlivaryworker", "mDatabase" + count + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(count <= 20){
            return true;
        }
        else
            return false;

    }

    public Boolean requestNewOrder(){
        String id = UserInFormation.getId();
        count2 = 0;
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        Wdatabase.orderByChild("uAccepted").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if(ds.child("statue").getValue().toString().equals("accepted")){
                            count2++;
                        }
                    }
                    Log.i("requestNewOrder", "MDatabase" + count2 + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        count3 = 0;
        Wdatabase =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("requests");
        Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if(ds.child("statue").getValue().toString().equals("N/A")){
                            count3++;

                        }
                    }
                    Log.i("requestNewOrder", "uDatabase" + count3 + "");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if(count2<20&&count3<10){
            return true;
        }
        else
            return false;
    }
}
