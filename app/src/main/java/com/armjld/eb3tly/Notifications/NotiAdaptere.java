package com.armjld.eb3tly.Notifications;

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
import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.Orders.MapsActivity;
import com.armjld.eb3tly.Settings.Wallet.MyWallet;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
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
                case "noting": {
                    // ------------ Do Nothing
                    break;
                }
                case "profile" : {
                    whichProfile();
                    break;
                }
                case "order": {
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
                                        OneOrder.putExtra("oID", orderData.getId());
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
                case "home" : {
                    context.startActivity(new Intent(context, HomeActivity.class));
                    break;
                }
                case "facebook" : {
                    String fbLink = "https://www.facebook.com/Eb3tlyy/";
                    Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(fbLink));
                    context.startActivity(browse);
                    break;
                }
                case "playstore" : {
                    String psLink = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(psLink));
                    context.startActivity(browse);
                    break;
                }
                case "add" : {
                    if(UserInFormation.getAccountType().equals("Supplier")) {
                        context.startActivity(new Intent(context, AddOrders.class));
                    }
                    break;
                }
                case "edit" : {
                    if(UserInFormation.getAccountType().equals("Supplier")) {
                        Intent editInt = new Intent(context, EditOrders.class);
                        editInt.putExtra("orderid", OrderID);
                        context.startActivity(editInt);
                    }
                    break;
                }
                case "map" : {
                    context.startActivity(new Intent(context, MapsActivity.class));
                    break;
                }

                case "wallet" : {
                    if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                        context.startActivity(new Intent(context, MyWallet.class));
                    }
                    break;
                }

                default: {

                }
            }
        });


    }

    private void whichProfile () {
        HomeActivity.whichFrag = "Profile";
        context.startActivity(new Intent(context, HomeActivity.class));
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
            if(!ppURL.equals("")) {
                Picasso.get().load(Uri.parse(ppURL)).into(imgEditPhoto);

            }
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
        return date != null;
    }
}