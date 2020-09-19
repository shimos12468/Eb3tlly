package com.armjld.eb3tly.Wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.armjld.eb3tly.Adapters.WalletAdapter;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Model.Data;

public class MyWallet extends AppCompatActivity {

    DatabaseReference mDatabase,uDatabase;
    ImageView btnBack;
    public static int TotalMoney = 0;
    TextView txtTotal,btnPay,txtEmpty;
    int count;
    ArrayList<Data> mm;
    private WalletAdapter walletAdapter;
    RecyclerView walletRecycler;
    int Final;
    float precnt = (float) 0.2;



    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        TotalMoney = 0;
        count = 0;
        mm = new ArrayList<>();

        txtTotal = findViewById(R.id.txtTotal);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);
        txtEmpty = findViewById(R.id.txtEmpty);
        walletRecycler = findViewById(R.id.walletRecycler);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        walletRecycler.setLayoutManager(layoutManager);


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



        if(UserInFormation.getCurrentdate().equals("none")) {
            txtTotal.setText("فاتورتك : " + TotalMoney + " ج");
            txtEmpty.setVisibility(View.VISIBLE);
            return;
        }

        uDatabase.child(UserInFormation.getId()).child("wallet").child(UserInFormation.getCurrentdate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        String orderid = ds.getKey();
                        assert orderid != null;
                        mDatabase.child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                Data orderData = snapshot2.getValue(Data.class);
                                assert orderData != null;
                                Log.i("My Wallet", "Adding order to Array List : " + orderData.toString());

                                int orderMoney = Integer.parseInt(orderData.getGGet());
                                TotalMoney = TotalMoney + orderMoney;
                                mm.add(count, orderData);

                                Final = (int) (TotalMoney * precnt);
                                txtTotal.setText("فاتورتك : " + Final + " ج");

                                walletAdapter = new WalletAdapter(mm, MyWallet.this);
                                walletRecycler.setAdapter(walletAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }



                } else {
                    txtTotal.setText("فاتورتك : " + TotalMoney + " ج");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
}