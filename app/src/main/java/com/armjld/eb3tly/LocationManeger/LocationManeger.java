package com.armjld.eb3tly.LocationManeger;

import android.location.Location;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationManeger {
    private FirebaseAuth mAuth;
    private ArrayList<LocationDataType> locHolder = new ArrayList<LocationDataType>();
    DatabaseReference Bdatabase;
    private int index =0;
    public void clear(){
        locHolder.clear();
        index=0;
    }

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

    public boolean  add(LocationDataType loc , String id){
            locHolder.add(index,loc);
            index++;
            String uId = UserInFormation.getId();
            int i = 0;
          if(uId == null ||uId.isEmpty()){
              return false;
          }

          if(loc == null){
              return false;
          }

          else{
              i++;
              String location = "location";
              location +=Integer.toString(i);
              Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("locations").child(id);;
              Bdatabase.child("latitude").setValue(loc.getLattude());
              Bdatabase.child("longitude").setValue(loc.getLontude());
              Bdatabase.child("Address").setValue(loc.getAddress());
              Bdatabase.child("region").child(loc.getRegion());
              Bdatabase.child("state").child(loc.getState());
              Bdatabase.child("title").child(loc.getTitle());
              Bdatabase.child("LocationID").setValue(loc.getId());
              Bdatabase.child("name").setValue(loc.getName());
              location = "Location";
              return true;
          }
      }

      public boolean Remove(LocationDataType loc ,int i  , String id){
            locHolder.remove(loc);
            String location = "location";
            location +=Integer.toString(i);
            String uId = UserInFormation.getId();

            if(uId == null ||uId.isEmpty()){
              return false;
            }

            if(loc == null){
              return false;
            }
          else{
            Bdatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("locations").child(id);
              Bdatabase.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      Bdatabase.removeValue();
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });

              return true;
          }

      }

      public Boolean ImportLocation(){
          FirebaseUser user = mAuth.getCurrentUser();

          if(user == null){
              return false;
          }
          else{
              Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid()).child("locations");
              Bdatabase.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.exists()){
                          clear();
                          for(DataSnapshot ds : snapshot.getChildren()){
                              LocationDataType loc = new LocationDataType();
                              double m = (double) ds.child("latitude").getValue();
                              loc.setLattude(m);
                              m = (double) ds.child("longitude").getValue();
                              loc.setLontude(m);
                              loc.setAddress(ds.child("Address").getValue().toString());
                              loc.setState(ds.child("state").getValue().toString());
                              loc.setRegion(ds.child("region").getValue().toString());
                              loc.setId(ds.child("LocationID").getValue().toString());
                              loc.setName(ds.child("name").getValue().toString());
                              loc.setName(ds.child("title").getValue().toString());
                              locHolder.add(loc);
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



}
