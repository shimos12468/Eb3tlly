package com.armjld.eb3tly.DatabaseClasses;

import androidx.annotation.NonNull;

import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.requestsData;

public class rquests {

    //private static int  num_of_requests =0;
    private static ArrayList<String>requests= new ArrayList<String>();
    private String uId = UserInFormation.getId();
    private DatabaseReference Bdatabase;
    int count;

    public static ArrayList<String> getRequests() {
        return requests;
    }

    public static void setRequests(ArrayList<String> requests) {
        rquests.requests = requests;
    }

    public boolean addrequest(String orderid ,String date){
        String uId = UserInFormation.getId();

        if(uId == null){
            return false;
        } else{
            Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests").child(uId);
            Bdatabase.child("id").setValue(uId);
            Bdatabase.child("date").setValue(date);
            Bdatabase.child("statue").setValue("N/A");

            Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("requests").child(orderid);
            Bdatabase.child("orderId").setValue(orderid);
            Bdatabase.child("date").setValue(date);
            Bdatabase.child("statue").setValue("N/A");

            return true;
        }
    }

    public void deleteReq(String accID, String orderid){
           FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests").child(accID).removeValue();
           FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(accID).child("requests").child(orderid).removeValue();
    }

    public void  deletedOrder(String orderid){
        count =0;
        ArrayList<requestsData>mm = new ArrayList<requestsData>();
        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests");
        Bdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        requestsData rData = ds.getValue(requestsData.class);
                        String dlivaryId = rData.getId();
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests").child(dlivaryId).child("statue").setValue("declined");
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(dlivaryId).child("requests").child(orderid).child("statue").setValue("declined");
                    }
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }
}
