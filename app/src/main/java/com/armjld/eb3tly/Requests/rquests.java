package com.armjld.eb3tly.Requests;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class rquests {

    //private static int  num_of_requests =0;
    private static ArrayList<String>requests= new ArrayList<String>();
    private String uId = UserInFormation.getId();
    private DatabaseReference Bdatabase;

    //public static int getNum_of_requests() {
      //  return num_of_requests;
    //}

    //public static void setNum_of_requests(int num_of_requests) {
      //  rquests.num_of_requests = num_of_requests;
    //}

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
        }
        else{
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

    public boolean deleteReq(String orderid){
        String uId = UserInFormation.getId();
        if(uId == null){
            return false;
        } else{
           FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests").child(uId).removeValue();
           FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("requests").child(orderid).removeValue();
            return true;
        }
    }
}
