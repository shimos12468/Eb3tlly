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

public class WalletManeger {
    DatabaseReference Wdatabase;

    public boolean RequestNewOrder(){
        String uid = UserInFormation.getId();
        final String[] requestsnum = new String[1];
        final String[] numaccepted = new String[1];
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("notdilivared").exists() && snapshot.child("requestsToday").exists()) {
                    requestsnum[0] = snapshot.child("requestsToday").getValue().toString();
                    numaccepted[0] = snapshot.child("notdilivared").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            Boolean f=false;
            Boolean fu = false;
        for(int i = 0;i<=10;i++){
            if(requestsnum[0].equals(String.valueOf(i))){
                if(i>0){
                    Wdatabase.child("requestsToday").setValue(String.valueOf(i-=1));
                    f = true;
                }

            }
        }
        for(int i =0 ;i<=20 ; i++){
            if(numaccepted[0].equals(String.valueOf(i))){
                if(i<20){
                    fu = true;
                }
            }

        }

        if(f&&fu){
            return true;
        }
        else return false;

    }


    public void AcceptedOrder( String uid){
        final String[] requestsnum = new String[1];
        final String[] numaccepted = new String[1];
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("notdilivared").exists()) {

                    numaccepted[0] = snapshot.child("notdilivared").getValue().toString();

                    for(int i = 0;i<=20;i++){
                        if(numaccepted[0].equals(String.valueOf(i))){
                            if(i<20){
                                Wdatabase.child("notdilivared").setValue(String.valueOf(i+=1));

                            }
                            else{

                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    public void declinedOrder( String uid){
        //String uid = UserInFormation.getId();
        final String[] requestsnum = new String[1];
        final String[] numaccepted = new String[1];
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("notdilivared").exists()&&snapshot.child("requestsToday").exists()) {
                    requestsnum[0] = snapshot.child("requestsToday").getValue().toString();
                    numaccepted[0] = snapshot.child("notdilivared").getValue().toString();
                    for(int i = 0;i<=10;i++){
                        if(snapshot.child("requestsToday").getValue().toString().equals(String.valueOf(i))){
                            if(i>0&&i<10){
                                Wdatabase.child("requestsToday").setValue(String.valueOf(i+=1));
                                //f = true;
                            }

                        }
                    }
                    for(int i =0 ;i<=20 ; i++){
                        if( snapshot.child("notdilivared").getValue().toString().equals(String.valueOf(i))){
                            if(i>0&&i<20){
                                Wdatabase.child("notdilivared").setValue(String.valueOf(i+=1));
                                //fu = true;
                            }
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    public void ondelivared(){
        String uid = UserInFormation.getId();

            final String[] requestsnum = new String[1];
            final String[] numaccepted = new String[1];
            Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("notdilivared").exists()) {
                        //requestsnum[0] = snapshot.child("requestsToday").getValue().toString();
                        numaccepted[0] = snapshot.child("notdilivared").getValue().toString();
                        for(int i =0 ;i<=20 ; i++){
                            if(snapshot.child("notdilivared").getValue().toString().equals(String.valueOf(i))){
                                if(i>0&&i<20){
                                    Wdatabase.child("notdilivared").setValue(String.valueOf(i+=1));

                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }


    public  void SetWalletData(){
        String uid  = UserInFormation.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        String datee = sdf.format(new Date());
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Wdatabase.child("dateofday").setValue(datee);
    }
    public void compareDate(){

        String uid= UserInFormation.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        String datee = sdf.format(new Date());
        String d;
        Wdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Wdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("dateofday").exists()){
                    if(snapshot.child("dateofday").getValue().toString().charAt(8)!=datee.charAt(8)||snapshot.child("dateofday").getValue().toString().charAt(9)!=datee.charAt(9)){
                        Wdatabase.child("dateofday").setValue(datee);
                        Wdatabase.child("requestsToday").setValue(String.valueOf(10));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
