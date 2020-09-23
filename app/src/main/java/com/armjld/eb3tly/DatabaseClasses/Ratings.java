package com.armjld.eb3tly.DatabaseClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ratings {

    private DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

    public void setMyRating() {
        uDatabase.child(UserInFormation.getId()).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if(snapshot.child("one").exists()) {
                    one = snapshot.child("one").getValue().toString();
                }
                if(snapshot.child("two").exists()) {
                    two = snapshot.child("two").getValue().toString();
                }
                if(snapshot.child("three").exists()) {
                    three = snapshot.child("three").getValue().toString();
                }
                if(snapshot.child("four").exists()) {
                    four = snapshot.child("four").getValue().toString();
                }
                if(snapshot.child("five").exists()) {
                    five = snapshot.child("five").getValue().toString();
                }

                UserInFormation.setRating(calculateRating(one,two,three,four,five));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void setRating(String userID, int Rating) {
        uDatabase.child(userID).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch (Rating)  {
                    case 1 : {
                        if(snapshot.child("one").exists()) {
                            int rate1 = Integer.parseInt(snapshot.child("one").getValue().toString());
                            uDatabase.child(userID).child("rating").child("one").setValue(rate1+1);
                        } else {
                            uDatabase.child(userID).child("rating").child("one").setValue(1);

                        }
                        break;
                    }

                    case 2 : {
                        if(snapshot.child("two").exists()) {
                            int rate2 = Integer.parseInt(snapshot.child("two").getValue().toString());
                            uDatabase.child(userID).child("rating").child("two").setValue(rate2+1);
                        } else {
                            uDatabase.child(userID).child("rating").child("two").setValue(1);
                        }
                        break;
                    }

                    case 3 : {
                        if(snapshot.child("three").exists()) {
                            int rate3 = Integer.parseInt(snapshot.child("three").getValue().toString());
                            uDatabase.child(userID).child("rating").child("three").setValue(rate3+1);
                        } else {
                            uDatabase.child(userID).child("rating").child("three").setValue(1);
                        }

                        break;
                    }

                    case 4 : {
                        if(snapshot.child("four").exists()) {
                            int rate4 = Integer.parseInt(snapshot.child("four").getValue().toString());
                            uDatabase.child(userID).child("rating").child("four").setValue(rate4+1);
                        } else {
                            uDatabase.child(userID).child("rating").child("four").setValue(1);

                        }
                        break;
                    }

                    case 5 : {
                        if(snapshot.child("five").exists()) {
                            int rate5 = Integer.parseInt(snapshot.child("five").getValue().toString());
                            uDatabase.child(userID).child("rating").child("five").setValue(rate5+1);
                        } else {
                            uDatabase.child(userID).child("rating").child("five").setValue(1);
                        }
                        break;
                    }
                }

                Log.i("Rating System", "Successsfully Added Ratitng to " + userID + " Rating : " + Rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public int calculateRating(String R1, String R2, String R3, String R4, String R5) {
        int intR1 = Integer.parseInt(R1);
        int intR2 = Integer.parseInt(R2);
        int intR3 = Integer.parseInt(R3);
        int intR4 = Integer.parseInt(R4);
        int intR5 = Integer.parseInt(R5);

        int TotalCount = intR1 + intR2 + intR3 + intR4 + intR5;
        if(TotalCount == 0) {
            TotalCount = 1;
        }

        int Rating5 = 5 * intR5;
        int Rating4 = 4 * intR4;
        int Rating3 = 3 * intR3;
        int Rating2 = 2 * intR2;

        int FinalRate = ((intR1 + Rating2 + Rating3 + Rating4 + Rating5) / TotalCount);

        if(FinalRate == 0) {
            FinalRate = 5;
        }

        return FinalRate;
    }
}
