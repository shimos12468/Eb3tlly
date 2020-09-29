package com.armjld.eb3tly.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class chatListclass {

    private DatabaseReference Bdatabase;

    public void dlevarychat(String id){
        String uId = UserInFormation.getId();
        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        Bdatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFound = false;
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("uId").getValue().toString().equals(id) && ds.child("statue").getValue().toString().equals("accepted")||ds.child("statue").getValue().toString().equals("recived") ){
                        isFound = true;
                        break;
                    }
                }

                if(!isFound) {
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(id);
                    Bdatabase.child("talk").setValue("false");

                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("chats").child(uId);
                    Bdatabase.child("talk").setValue("false");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void supplierchat(String id){
        String uId = UserInFormation.getId();
        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        Bdatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFound = false;
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("uAccepted").getValue().toString().equals(id) && ds.child("statue").getValue().toString().equals("accepted")||ds.child("statue").getValue().toString().equals("recived") ){
                        isFound = true;
                        break;
                    }
                }

                if(!isFound) {
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(id);
                    Bdatabase.child("talk").setValue("false");

                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("chats").child(uId);
                    Bdatabase.child("talk").setValue("false");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void startChating(String myId, String hisId, Context context) {
        DatabaseReference Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myId).child("chats").child(hisId);
        Bdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            DatabaseReference Bdatabase;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myId).child("chats").child(hisId);
                    Bdatabase.child("talk").setValue("true");

                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(hisId).child("chats").child(myId);
                    Bdatabase.child("talk").setValue("true");

                    Intent intent = new Intent(context, Messages.class);

                    intent.putExtra("roomid", Objects.requireNonNull(snapshot.child("roomid").getValue()).toString());
                    intent.putExtra("rid", hisId);
                    ((Activity)context).startActivityForResult(intent, 1);

                } else {
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myId).child("chats");
                    String chat = Bdatabase.push().getKey();
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myId).child("chats").child(hisId);
                    Bdatabase.child("userId").setValue(hisId);
                    Bdatabase.child("roomid").setValue(chat);
                    Bdatabase.child("talk").setValue("true");
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(hisId).child("chats");
                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(hisId).child("chats").child(myId);
                    Bdatabase.child("userId").setValue(myId);
                    Bdatabase.child("roomid").setValue(chat);
                    Bdatabase.child("talk").setValue("true");

                    Intent intent = new Intent(context, Messages.class);
                    intent.putExtra("roomid",chat);
                    intent.putExtra("rid", hisId);
                    ((Activity)context).startActivityForResult(intent, 1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
