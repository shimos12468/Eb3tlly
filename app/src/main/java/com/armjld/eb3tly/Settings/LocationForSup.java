package com.armjld.eb3tly.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.armjld.eb3tly.SupplierProfile.LocationsAdapter;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import Model.LocationDataType;

public class LocationForSup extends AppCompatActivity {

    DatabaseReference uDatabase;
    String uId;
    SwipeMenuListView listLocations;
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

        btnBack.setOnClickListener(v-> {
            finish();
            startActivity(new Intent(this, SettingsActivity.class));
        });

        linText.setOnClickListener(v-> {
            AddLocation.type = "New";
            startActivity(new Intent(this, AddLocation.class));
        });

        btnAdd.setOnClickListener(v-> {
            AddLocation.type = "New";
            startActivity(new Intent(this, AddLocation.class));
        });

        SwipeMenuCreator creator = menu -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(90);
            deleteItem.setIcon(R.drawable.ic_close_red);
            menu.addMenuItem(deleteItem);
        };

        listLocations.setMenuCreator(creator);
        listLocations.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

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

        listLocations.setOnMenuItemClickListener((position, menu, index) -> {
            String id = listLoc.get(position).getId();
            return false;
        });
    }
}