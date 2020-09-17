package com.armjld.eb3tly.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.caculateTime;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import Model.Data;
import Model.notiData;


public class NotiAdaptere extends RecyclerView.Adapter<NotiAdaptere.MyViewHolder> {

    Context context, context1;
    long count;
    ArrayList<notiData>notiData;
    private DatabaseReference mDatabase, uDatabase;
    private String TAG = "Notification Adapter";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static caculateTime _cacu = new caculateTime();


    public NotiAdaptere(Context context, ArrayList<notiData> notiData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.notiData = notiData;
        this.context1 = context1;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
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
        String From = notiData.get(position).getFrom();
        String To = notiData.get(position).getTo();
        String Datee = notiData.get(position).getDatee();
        String Statue = notiData.get(position).getStatue();
        String OrderID =notiData.get(position).getOrderid();
        String uName = notiData.get(position).getuName();
        String action = notiData.get(position).getAction();
        String ppURL = notiData.get(position).getPpURL();

        holder.setNoti(uName, Statue, Datee,ppURL);
        holder.myview.setOnClickListener(v-> {
            switch (action) {
                case "": {

                    break;
                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return (int) count;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtBody, txtNotidate,txtName;
        public ImageView imgEditPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            txtBody = myview.findViewById(R.id.txtBody);
            txtNotidate = myview.findViewById(R.id.txtNotidate);
            imgEditPhoto = myview.findViewById(R.id.imgEditPhoto);
            txtName = myview.findViewById(R.id.txtName);
        }

        public void setNoti(String uName, String statue, String datee, String ppURL) {
            txtName.setText(uName);
            txtBody.setText(statue);
            txtNotidate.setText(_cacu.setPostDate(datee));
            Picasso.get().load(Uri.parse(ppURL)).into(imgEditPhoto);
        }

    }

    public boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            assert date != null;
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        if (date == null) {
            return false;
        } else {
            return true;
        }
    }
}