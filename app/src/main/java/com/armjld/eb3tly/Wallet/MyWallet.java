package com.armjld.eb3tly.Wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyWallet extends AppCompatActivity {

    DatabaseReference mDatabase,uDatabase;
    ImageView btnBack;
    public static int TotalMoney = 0;
    TextView txtTotal,btnPay;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        txtTotal = findViewById(R.id.txtTotal);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);


        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("محفظتي");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        btnBack.setOnClickListener(v-> {
            finish();
        });

        btnPay.setOnClickListener(v-> {
            Toast.makeText(this,  "ستقوم بدفع " + TotalMoney + " ج بعد اتمام الربط بفوري.", Toast.LENGTH_SHORT).show();
        });

        TotalMoney = 0;
        uDatabase.child(UserInFormation.getId()).child("wallet").child(UserInFormation.getCurrentdate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    String orderid = ds.getKey();
                    Log.i("My Wallet", "Order ID : " + orderid);
                    assert orderid != null;
                    mDatabase.child(orderid).child("gget").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            int orderMoney = Integer.parseInt(snapshot2.getValue().toString());
                            float precnt = (float) 0.2;
                            TotalMoney = TotalMoney + orderMoney;
                            Log.i("My Wallet", "New Money : " + orderMoney + " Total : " + TotalMoney);
                            int Final = (int) (TotalMoney * precnt);
                            txtTotal.setText("فاتورتك : " + Final + " ج");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
}