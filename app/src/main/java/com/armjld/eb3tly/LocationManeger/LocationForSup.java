package com.armjld.eb3tly.LocationManeger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Adapters.LocationsAdapter;
import com.armjld.eb3tly.MyLocation;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.Data;
import Model.LocationDataType;

public class LocationForSup extends AppCompatActivity {

    DatabaseReference uDatabase;
    String uId;
    ListView listLocations;
    ArrayList<LocationDataType> listLoc = new ArrayList<>();
    ImageView btnBack, btnAdd;
    LinearLayout linNew,linText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_for_sup);

        listLocations = findViewById(R.id.listLocations);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);
        linText = findViewById(R.id.linText);
        uId = UserInFormation.getId();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        linNew = findViewById(R.id.linNew);
        tbTitle.setText("عناويني");

        btnBack.setOnClickListener(v-> finish());

        linText.setOnClickListener(v-> {
            MyLocation.type = "New";
            startActivity(new Intent(this, MyLocation.class));
        });

        btnAdd.setOnClickListener(v-> {
            MyLocation.type = "New";
            startActivity(new Intent(this, MyLocation.class));
        });

        uDatabase.child(uId).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    linText.setVisibility(View.GONE);
                    btnAdd.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        LocationDataType locData = ds.getValue(LocationDataType.class);
                        listLoc.add(locData);
                        LocationsAdapter adapter = new LocationsAdapter(LocationForSup.this, listLoc);
                        listLocations.setAdapter(adapter);
                    }
                } else {
                    linText.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}