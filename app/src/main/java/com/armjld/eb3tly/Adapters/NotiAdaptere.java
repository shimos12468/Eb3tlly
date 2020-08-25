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

import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.firebase.auth.FirebaseAuth;
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


import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NotiAdaptere extends RecyclerView.Adapter<NotiAdaptere.MyViewHolder> {

    Context context, context1;
    long count;
    String uType = UserInFormation.getAccountType();
    ArrayList<notiData>notiData;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase, uDatabase, nDatabase;
    private String TAG = "Notification Adapter";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


    public NotiAdaptere(Context context, ArrayList<notiData> notiData, Context context1, long count) {
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
        String From = notiData.get(position).getFrom();
        String To = notiData.get(position).getTo();
        String Datee = notiData.get(position).getDatee();
        String Statue = notiData.get(position).getStatue();
        String OrderID =notiData.get(position).getOrderid();

        holder.setBody(From, Statue, OrderID, To);
        if(isValidFormat("yyyy.MM.dd HH:mm:ss", Datee)) {
            String startDate = notiData.get(position).getDatee();
            String stopDate = datee;
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);

            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(startDate);
                d2 = format.parse(stopDate);
            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
            }
            assert d2 != null;
            assert d1 != null;
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffDays = diff / (24 * 60 * 60 * 1000);

            int idiffSeconds = (int) diffSeconds;
            int idiffMinutes = (int) diffMinutes;
            int idiffHours = (int) diffHours;
            int idiffDays = (int) diffDays;
            holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        } else {
            holder.setDate(Datee);
        }
        holder.myview.setOnClickListener(v -> {
            if(UserInFormation.getAccountType().equals("Supplier")) {
                switch (Statue) {
                    case "deleted": {
                        context.startActivity(new Intent(context, supplierProfile.class));
                        break;
                    }
                    case "delivered": {
                        context.startActivity(new Intent(context, supplierProfile.class));
                        break;
                    }
                    case "accepted": {
                        context.startActivity(new Intent(context, supplierProfile.class));
                        break;
                    }
                    case "welcome": {
                        context.startActivity(new Intent(context, AddOrders.class));
                        break;
                    }
                    case "متنساش تعمل لايك لصفحتنا علي الفيس بوك": {
                        String fbLink = "https://www.facebook.com/Eb3tlyy/";
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(fbLink));
                        context.startActivity(browse);
                        break;
                    }
                    case "يوجد تحديث جديد للبرنامج": {
                        String psLink = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(psLink));
                        context.startActivity(browse);
                        break;
                    }
                    default: {
                        // ---- do nothing
                        break;
                    }
                }
            } else {
                switch (Statue) {
                    case "edited": {
                        context.startActivity(new Intent(context, NewProfile.class));
                        break;
                    }
                    case "deleted": {
                        context.startActivity(new Intent(context, NewProfile.class));
                        break;
                    }
                    case "recived": {
                        context.startActivity(new Intent(context, NewProfile.class));
                        break;
                    }
                    case "welcome": {
                        context.startActivity(new Intent(context, HomeActivity.class));
                        break;
                    }

                    case "يوجد اوردر جديد في منطقتك" : {
                        mDatabase.child(OrderID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if((int) snapshot.getChildrenCount() > 1) {
                                    Data orderData = snapshot.getValue(Data.class);
                                    assert orderData != null;
                                    Log.i(TAG, orderData.getId() + " : " +orderData.getDDate());
                                    if(!orderData.getStatue().equals("placed")) {
                                        Toast.makeText(context, "نعتذر, لقد تم قبول الاوردر بالفعل من مندوب اخر", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Date orderDate = null;
                                        Date myDate = null;
                                        try {
                                            orderDate = format.parse(orderData.getDDate());
                                            myDate =  format.parse(format.format(Calendar.getInstance().getTime()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        assert orderDate != null;
                                        assert myDate != null;
                                        if(orderDate.compareTo(myDate) >= 0) {
                                            Log.i(TAG, orderData.getId());
                                            Intent OneOrder = new Intent(context, com.armjld.eb3tly.Orders.OneOrder.class);
                                            OneOrder.putExtra("oID", orderData.getId().toString());
                                            context.startActivity(OneOrder);
                                        } else {
                                            Toast.makeText(context, "معاد تسليم الاوردر قد فات", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "تم حذف هذا الاوردر", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                        break;
                    }

                    case "متنساش تعمل لايك لصفحتنا علي الفيس بوك" : {
                        String fbLink = "https://www.facebook.com/Eb3tlyy/";
                        Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(fbLink));
                        context.startActivity(browse);
                        break;
                    }
                    case "يوجد تحديث جديد للبرنامج": {
                        String psLink = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(psLink));
                        context.startActivity(browse);
                        break;
                    }
                    default: {
                        // ------------- do nothing
                        break;
                    }
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


    public class MyViewHolder extends RecyclerView.ViewHolder {
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

        public void setBody(String sendby, String message, String OrderID, String To) {
            uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameFrom = Objects.requireNonNull(dataSnapshot.child(sendby).child("name").getValue()).toString();
                    txtName.setText(nameFrom);
                    String URL = Objects.requireNonNull(dataSnapshot.child(sendby).child("ppURL").getValue()).toString();
                    String ToType = Objects.requireNonNull(dataSnapshot.child(To).child("accountType").getValue()).toString();
                    Picasso.get().load(Uri.parse(URL)).into(imgEditPhoto);
                    mDatabase.child(OrderID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                String orderTo = Objects.requireNonNull(dataSnapshot.child("dname").getValue()).toString();
                                String body;
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
                                        body = "قام " + nameFrom + " بتسليمك الاوردر";
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
            txtNotidate.setText(date);
        }

        public void setPostDate(int dS, int dM, int dH, int dD) {
            String finalDate = "";
            if (dS < 60) {
                finalDate = "منذ " + dS + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + dM + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + dH + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " +dD + " ايام";
            }
            txtNotidate.setText(finalDate);
        }

    }
    private void whichProfile () {
        if(uType.equals("Supplier")) {
            context.startActivity(new Intent(context, supplierProfile.class));
        } else {
            context.startActivity(new Intent(context, NewProfile.class));
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