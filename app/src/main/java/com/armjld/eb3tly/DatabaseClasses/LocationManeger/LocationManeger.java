package com.armjld.eb3tly.DatabaseClasses.LocationManeger;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import Model.Data;
import Model.UserInFormation;

import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.LocationDataType;

public class LocationManeger {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static ArrayList<LocationDataType> locHolder = new ArrayList<LocationDataType>();
    DatabaseReference Bdatabase;
    private int index =0;

    public ArrayList<LocationDataType> getLocHolder() {
        return locHolder;
    }

    public void setLocHolder(ArrayList<LocationDataType> locHolder) {
        this.locHolder = locHolder;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean add(LocationDataType loc , String id){

          if(loc == null){
              return false;
          } else{
              Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("locations").child(id);
              Bdatabase.child("lattude").setValue(loc.getLattude());
              Bdatabase.child("lontude").setValue(loc.getLontude());
              Bdatabase.child("address").setValue(loc.getAddress());
              Bdatabase.child("region").child(loc.getRegion());
              Bdatabase.child("state").child(loc.getState());
              Bdatabase.child("title").child(loc.getTitle());
              Bdatabase.child("id").setValue(loc.getId());
              Log.i("Location Manager", "Added Location Successfully");
              return true;
          }
      }

      @RequiresApi(api = Build.VERSION_CODES.N)
      public boolean Remove(String id){
        LocationDataType loc = null;
        loc = locHolder.stream().filter(x -> x.getId().equals(id)).findFirst().get();

        locHolder.remove(loc);
        String uId = UserInFormation.getId();

        if(uId == null ||uId.isEmpty()){
            return false;
        }

        if(loc == null){
            return false;
        }

        Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("locations").child(id);
        Bdatabase.removeValue();
        return true;

      }

      public Boolean ImportLocation(){
        locHolder.clear();
        locHolder.trimToSize();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            return false;
        } else {
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid()).child("locations");
            Bdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.exists()){
                          for(DataSnapshot ds : snapshot.getChildren()){
                              LocationDataType loc = ds.getValue(LocationDataType.class);
                              locHolder.add(loc);
                              Log.i("Location Manager", " Added New Location");
                          }

                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) { }
              });
              return true;
          }
      }
}
