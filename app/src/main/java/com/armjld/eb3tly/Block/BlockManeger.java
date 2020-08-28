package com.armjld.eb3tly.Block;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BlockManeger {

    private static ArrayList<String>blockedId = new ArrayList<String>();
    private static long num_Blocked_users=0;
    private static Boolean firstTime = false;
    String uId = UserInFormation.getId();
    DatabaseReference Bdatabase;


    public static Boolean getFirstTime() {
        return firstTime;
    }

    public static void setFirstTime(Boolean firstTime) {
        BlockManeger.firstTime = firstTime;
    }

    public static long getNum_Blocked_users() {
        return num_Blocked_users;
    }

    public static void setNum_Blocked_users(long num_Blocked_users) {
        BlockManeger.num_Blocked_users = num_Blocked_users;
    }

    public ArrayList<String> getBlockedId() {
        return blockedId;
    }
    public void clear(){
        blockedId.clear();
        num_Blocked_users =0;
    }

    public Boolean addUser(String id){
        final boolean[] f = {false};
        blockedId.add((int)num_Blocked_users ,id);
        num_Blocked_users++;
        String uId = UserInFormation.getId();
        if(uId == null ||uId.isEmpty()){
            return false;

        }
        if(id == null ||id.isEmpty()){
            return false;
        }

       else {
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("Blocked").child(id);
            Bdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()&&snapshot.child("statue").toString().equals("being Blocked")) {
                        Bdatabase.child("statue").setValue("both");
                        Bdatabase= FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("Blocked").child(uId);
                        Bdatabase.child("statue").setValue("both");
                        f[0] = true;
                    } else {
                        Bdatabase.child("id").setValue(id);
                        Bdatabase.child("statue").setValue("blocker");

                        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("Blocked").child(uId);
                        Bdatabase.child("id").setValue(uId);
                        Bdatabase.child("statue").setValue("being Blocked");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (!f[0])
            {
                Bdatabase.child("id").setValue(id);
                Bdatabase.child("statue").setValue("blocker");

                Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("Blocked").child(uId);
                Bdatabase.child("id").setValue(uId);
                Bdatabase.child("statue").setValue("being Blocked");
                return true;
            }
            return false;
        }

    }





    public void add(String ids){
        blockedId.add(ids);

    }

    public Boolean removeUser(String id){
        blockedId.remove(id);
        num_Blocked_users-=1;
        String uId = UserInFormation.getId();
        if(uId == null ||uId.isEmpty()){
            return false;
        }
        if(id == null ||id.isEmpty()){
            return false;
        }
        else{
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("Blocked").child(id);
            Bdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()) {

                     if (snapshot.child("statue").getValue().toString().equals("blocker")) {
                                    Bdatabase.removeValue();
                                    Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("Blocked").child(uId);
                                    Bdatabase.removeValue();
                                    blockedId.remove(id);

                        }
                     if(snapshot.child("statue").getValue().toString().equals("both")){
                         Bdatabase.child("statue").setValue("being blocked");
                         Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("Blocked").child(uId);
                         Bdatabase.child("statue").setValue("blocker");
                         blockedId.remove(id);
                     }
                     }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return true;

        }
    }

    public int search(String id){
        String uId = UserInFormation.getId();
        if(uId == null ||uId.isEmpty()){
            return -1;
        }
        if(id == null ||id.isEmpty()){
            return -1;
        }
        else{
            final int[] value = {0};
           Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("Blocked");
           Bdatabase.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   for (DataSnapshot values:snapshot.getChildren()) {
                       if(values.getValue().equals(id))
                           value[0] =1;
                            break;
                   }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
           int v = value[0];
            return v;
        }
    }
    public Boolean check(String id){

        for (String ids:blockedId) {
            if(ids.equals(id)){
                return true;
            }
        }
        return false;
    }

}
