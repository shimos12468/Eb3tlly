package com.armjld.eb3tly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NotiAdaptere extends RecyclerView.Adapter<NotiAdaptere.MyViewHolder> {

    Context context, context1;
    long count;
    notiData[] notiData;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase, uDatabase, nDatabase;
    private String TAG = "Notification Adapter";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    public NotiAdaptere(Context context, notiData[] notiData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.notiData = notiData;
        this.context1 = context1;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_notification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        String From = notiData[position].getFrom();
        String To = notiData[position].getTo();
        String Datee = notiData[position].getDatee();
        String Statue = notiData[position].getStatue();
        String OrderID = notiData[position].getOrderid();

        holder.setBody(From, Statue, OrderID, To);
        holder.setDate(Datee);

        holder.myview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, profile.class));
            }
        });
    }


    @Override
    public int getItemCount() {
        return (int) count;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        TextView txtBody, txtNotidate;
        ImageView imgEditPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setBody(String sendby, String message, String OrderID, String To) {
            txtBody = myview.findViewById(R.id.txtBody);
            imgEditPhoto = myview.findViewById(R.id.imgEditPhoto);

            uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameFrom = Objects.requireNonNull(dataSnapshot.child(sendby).child("name").getValue()).toString();
                    String URL = Objects.requireNonNull(dataSnapshot.child(sendby).child("ppURL").getValue()).toString();
                    String ToType = Objects.requireNonNull(dataSnapshot.child(To).child("accountType").getValue()).toString();
                    Picasso.get().load(Uri.parse(URL)).into(imgEditPhoto);
                    mDatabase.child(OrderID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                String orderTo = Objects.requireNonNull(dataSnapshot.child("dname").getValue()).toString();
                                String body = "";
                                switch (message) {
                                    case "edited": {
                                        body = " قام " + nameFrom + " بتعديل بعض بيانات الاوردر الذي قبلته ";
                                        break;
                                    }
                                    case "deleted": {
                                        if (ToType.equals("Supplier")) {
                                            body = " قام " + nameFrom + " بالغاء الاوردر " + orderTo + " الذي قام بقبولة ";
                                        } else {
                                            body = " قام " + nameFrom + " بالغاء الاوردر ";
                                        }
                                        break;
                                    }
                                    case "delivered": {
                                        body = " قام " + nameFrom + " بتوصيل اوردر " + orderTo;
                                        break;
                                    }
                                    case "accepted": {
                                        body = " قام " + nameFrom + " بقبول اوردر " + orderTo;
                                        break;
                                    }
                                    case "recived": {
                                        body = "قام" + nameFrom + " بتسليمك الاوردر";
                                        break;
                                    }
                                    case "welcome": {
                                        body = "اهلا بيك في برنامج ابعتلي, اول منصة مهمتها توصيل التاجر بمندوب الشحن";
                                        break;
                                    }
                                    default: {
                                        body = message;
                                        break;
                                    }
                                }
                                txtBody.setText(body);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }});
                }
                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        public void setDate(String date) {
            txtNotidate = myview.findViewById(R.id.txtNotidate);
            txtNotidate.setText(date);
        }

    }
}