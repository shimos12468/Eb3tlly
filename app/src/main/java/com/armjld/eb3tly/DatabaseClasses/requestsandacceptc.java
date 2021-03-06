package com.armjld.eb3tly.DatabaseClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        return count <= 20;

    }

    public Boolean requestNewOrder(){
        String id = UserInFormation.getId();
        count2 = -1;
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

        count3 = -1;
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

        Log.i("LOOOOOG", ""+count2 + count3);


        return count2 < 20 && count3 < 10;

    }
}
