package com.armjld.eb3tly.Chat;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

}
