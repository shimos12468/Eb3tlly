package com.armjld.eb3tly.coustmerService;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class custmur_service_maneger {

    private DatabaseReference Bdatabase;
    private ArrayList<String> ActiveServiceId = new ArrayList<String>();
    private String roomId;
    public void Importcoustmourservice(){

        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        Bdatabase.orderByChild("accountType").equalTo("customer Service").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        if (ds.child("statue").getValue().toString().equals("Active")) {
                            ActiveServiceId.add(ds.child("id").getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void makechatroom(){
        if(ActiveServiceId.size()>0){
            String myid = UserInFormation.getId();
            String chat = Bdatabase.push().getKey();
            roomId = chat;
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myid).child("chats").child(ActiveServiceId.get(0));
            Bdatabase.child("roomid").setValue(chat);
            Bdatabase.child("userId").setValue(ActiveServiceId.get(0));
            Bdatabase.child("talk").setValue("true");

            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(ActiveServiceId.get(0)).child("chats").child(myid);
            Bdatabase.child("roomid").setValue(chat);
            Bdatabase.child("userId").setValue(myid);
            Bdatabase.child("talk").setValue("true");
        }

    }
    public void deleteroom(){
        if(ActiveServiceId.size()>0) {
            String myid = UserInFormation.getId();
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myid).child("chats").child(ActiveServiceId.get(0));
            Bdatabase.removeValue();

            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(ActiveServiceId.get(0)).child("chats").child(myid);
            Bdatabase.removeValue();

            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId);
            Bdatabase.removeValue();
        }
    }



}

