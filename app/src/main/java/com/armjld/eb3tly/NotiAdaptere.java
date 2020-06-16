package com.armjld.eb3tly;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NotiAdaptere extends RecyclerView.Adapter<NotiAdaptere.MyViewHolder> {

    Context context, context1;
    long count;
    notiData [] notiData;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList datalist,filterList;
    private DatabaseReference mDatabase,uDatabase,nDatabase;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<String>();
    private String TAG = "Notification Adapter";

    public NotiAdaptere(SwipeRefreshLayout mSwipeRefreshLayout) {
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public NotiAdaptere(Context context, notiData[] notiData, Context context1, long count, SwipeRefreshLayout mSwipeRefreshLayout) {
        this.count = count;
        this.context = context;
        this.notiData = notiData;
        this.context1 = context1;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.item_data,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.setDate(notiData[position].getDDate());
        holder.setUsername(notiData[position].getuId());
        holder.setOrdercash(notiData[position].getGMoney());
        holder.setOrderFrom(notiData[position].reStateP());
        holder.setOrderto(notiData[position].reStateD());
        holder.setFee(notiData[position].getGGet().toString());
        holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        holder.setType(notiData[position].getIsCar(), notiData[position].getIsMotor(), notiData[position].getIsMetro(), notiData[position].getIsTrans());

        //Hide this order Button
        holder.btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Still working on this", Toast.LENGTH_SHORT).show();
            }
        });

        String PAddress =notiData[position].getmPAddress();
        String DAddress = notiData[position].getDAddress();
        String rateUID = notiData[position].getuId();
        String notes = notiData[position].getNotes();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
    public int getItemCount() {
        int Count = (int) count;
        return Count;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        Button btnAccept, btnHide, btnMore ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
            btnAccept = myview.findViewById(R.id.btnAccept);
            btnHide = myview.findViewById(R.id.btnHide);
            btnMore = myview.findViewById(R.id.btnMore);
        }

        void setUsername(String userID){
            uDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String mName = snapshot.child("name").getValue().toString();
                    TextView mtitle = myview.findViewById(R.id.txtUsername);
                    mtitle.setText(mName);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        public void setOrderFrom(String orderFrom){
            TextView mtitle=myview.findViewById(R.id.OrderFrom);
            mtitle.setText(orderFrom);
        }
        public void setOrderto(String orderto){
            TextView mtitle=myview.findViewById(R.id.orderto);
            mtitle.setText(orderto);
        }

        public void setDate (String date){
            TextView mdate= myview.findViewById(R.id.date);
            mdate.setText(date);
        }
        public void setOrdercash(String ordercash){
            TextView mtitle=myview.findViewById(R.id.ordercash);
            mtitle.setText(ordercash + " ج");
        }
        public void setFee(String fees) {
            TextView mtitle=myview.findViewById(R.id.fees);
            mtitle.setText(fees + " ج");
        }

        public void setType(String car, String motor, String metro, String trans) {
            ImageView icnCar = myview.findViewById(R.id.icnCar);
            ImageView icnMotor = myview.findViewById(R.id.icnMotor);
            ImageView icnMetro = myview.findViewById(R.id.icnMetro);
            ImageView icnTrans = myview.findViewById(R.id.icnTrans);
            if (car.equals("سياره")) {
                icnCar.setVisibility(View.VISIBLE);
            } else {
                icnCar.setVisibility(View.INVISIBLE);
            }

            if(motor.equals("موتسكل")) {
                icnMotor.setVisibility(View.VISIBLE);
            } else {
                icnMotor.setVisibility(View.INVISIBLE);
            }

            if(metro.equals("مترو")) {
                icnMetro.setVisibility(View.VISIBLE);
            } else {
                icnMetro.setVisibility(View.INVISIBLE);
            }

            if (trans.equals("مواصلات")) {
                icnTrans.setVisibility(View.VISIBLE);
            } else {
                icnTrans.setVisibility(View.INVISIBLE);
            }
        }

        public void setPostDate(int dS, int dM, int dH, int dD) {
            String finalDate = "";
            TextView mtitle = myview.findViewById(R.id.txtPostDate);
            if (dS < 60) {
                finalDate = "منذ " + String.valueOf(dS) + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + String.valueOf(dM) + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + String.valueOf(dH) + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " + String.valueOf(dD) + " ايام";
            }
            mtitle.setText(finalDate);
        }
    }

    private void refresh() {
        new Handler() {
            public void postDelayed(Runnable runnable, int i) {
            }

            @Override
            public void publish(LogRecord record) {

            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.postDelayed(new Runnable() {

            @Override
            public void run() {
                NotiAdaptere.this.notifyDataSetChanged();
                Log.i(TAG, "Data Refreshed");

            }
        },3000);
        NotiAdaptere.this.notifyDataSetChanged();
        Log.i(TAG, "Data Refreshed");
        mSwipeRefreshLayout.setRefreshing(false);
    }
}