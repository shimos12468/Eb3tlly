package com.armjld.eb3tly.Settings.Wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import Model.Data;

public class MyWallet extends AppCompatActivity {

    DatabaseReference mDatabase,uDatabase;
    ImageView btnBack;
    public static int TotalMoney = 0;
    TextView txtMyMoney,btnPay,txtEmpty;
    int count;
    ArrayList<Data> mm;
    private WalletAdapter walletAdapter;
    RecyclerView walletRecycler;
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

        txtMyMoney = findViewById(R.id.txtMyMoney);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);
        txtEmpty = findViewById(R.id.txtEmpty);
        walletRecycler = findViewById(R.id.walletRecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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

        txtMyMoney.setText(UserInFormation.getWalletmoney() + " ج");


        btnPay.setOnClickListener(v-> {
            AlertDialog.Builder myDialogMore = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View pay = inflater.inflate(R.layout.payment, null);
            myDialogMore.setView(pay);
            final AlertDialog dialog = myDialogMore.create();
            dialog.show();

            TextView txtQuickerCode = pay.findViewById(R.id.txtQuickerCode);
            ImageView btnBack = pay.findViewById(R.id.btnBack);
            TextView tbTitle2 = pay.findViewById(R.id.toolbar_title);
            tbTitle2.setText("خطوات الدفع");

            btnBack.setOnClickListener(v1-> {
                dialog.dismiss();
            });

            txtQuickerCode.setText("010136249624");
        });

        if(UserInFormation.getCurrentdate().equals("none")) {
            txtEmpty.setVisibility(View.VISIBLE);
        }

        uDatabase.child(UserInFormation.getId()).child("wallet").child(UserInFormation.getCurrentdate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String orderid = ds.getKey();
                        assert orderid != null;

                        if(HomeActivity.delvList.size() == 0) {
                            return;
                        }

                        Data c = HomeActivity.delvList.stream().filter(x -> x.getId().equals(orderid)).findFirst().get();
                        orderList.add(c);
                    }

                    walletAdapter = new WalletAdapter(orderList, MyWallet.this);
                    walletRecycler.setAdapter(walletAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}