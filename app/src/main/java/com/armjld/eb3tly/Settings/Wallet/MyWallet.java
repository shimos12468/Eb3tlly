package com.armjld.eb3tly.Settings.Wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
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
    ArrayList<Data> orderList = new ArrayList<>();



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
            Toast.makeText(this,  "ستقوم بدفع " + Final + " ج بعد اتمام الربط بفوري.", Toast.LENGTH_SHORT).show();
        });



        if(UserInFormation.getCurrentdate().equals("none")) {
            txtTotal.setText("فاتورتك : " + TotalMoney + " ج");
            txtEmpty.setVisibility(View.VISIBLE);
            return;
        }

        uDatabase.child(UserInFormation.getId()).child("wallet").child(UserInFormation.getCurrentdate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderList.clear();
                    int OrdersCount = (int) snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String orderid = ds.getKey();
                        assert orderid != null;

                        if(HomeActivity.delvList.size() == 0) {
                            return;
                        }

                        Data c = HomeActivity.delvList.stream().filter(x -> x.getId().equals(orderid)).findFirst().get();
                        orderList.add(c);
                        if(orderList.size() == OrdersCount) {
                            cacuculte();
                            break;
                        }
                    }

                    walletAdapter = new WalletAdapter(orderList, MyWallet.this);
                    walletRecycler.setAdapter(walletAdapter);
                } else {
                    txtTotal.setText("فاتورتك : " + TotalMoney + " ج");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void cacuculte() {
        if(orderList.size() > 0) {
            for (int i = 0; i < orderList.size(); i++) {
                Data oneOrder = HomeActivity.delvList.get(i);
                TotalMoney = TotalMoney + Integer.parseInt(oneOrder.getGGet());
                Final = (int) (TotalMoney * precnt);
                txtTotal.setText("فاتورتك : " + Final + " ج");
            }
        }
    }
}