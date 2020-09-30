package com.armjld.eb3tly.SupplierProfile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Home.MyAdapter;
import com.armjld.eb3tly.R;

import Model.Data;
import Model.UserInFormation;
import com.armjld.eb3tly.DatabaseClasses.requestsandacceptc;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import Model.notiData;
import Model.requestsData;
import Model.userData;


public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    String orderId;
    int Pos;
    ArrayList<requestsData>requestsData;
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private BlockManeger block = new BlockManeger();
    private static final int PHONE_CALL_CODE = 100;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    public static String TAG = "Requests Adapter";




    public RequestsAdapter(Context context, ArrayList<Model.requestsData> requestsData, String orderId, int Pos) {
        this.context = context;
        this.requestsData = requestsData;
        this.orderId = orderId;
        this.Pos = Pos;
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_requests, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String id = requestsData.get(position).getId();
        String date = requestsData.get(position).getDate();

        holder.setPostDate(date);
        holder.setUserInfo(id);

        holder.btnDecline.setOnClickListener(v-> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderId).child("requests").child(id).child("statue").setValue("declined");
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(id).child("requests").child(orderId).child("statue").setValue("declined");

                        requestsData.remove(position);
                        notifyItemRemoved(position);

                        Toast.makeText(context, "تم الغاء المندوب", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("هل انت متاكد من انك تريد تريد الغاء طلب المندوب ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

        });

        holder.btnAccept.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                requestsandacceptc c = new requestsandacceptc();
                if(!c.acceptdlivaryworker(id)) {
                    Toast.makeText(context, "نعتذر لا يمكن لهذا المندوب قبول اوردرات اخري حاليا", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(orderId).child("uAccepted").setValue(id);
                        mDatabase.child(orderId).child("statue").setValue("accepted");
                        mDatabase.child(orderId).child("acceptedTime").setValue(datee);

                        // ------------------ Notification ------------------ //
                        notiData Noti = new notiData(UserInFormation.getId(), id,orderId,"قام " + UserInFormation.getUserName() + " بقبول طلبك لاستلام الاوردر ",datee,"false","profile",UserInFormation.getUserName(), UserInFormation.getUserURL());
                        nDatabase.child(id).push().setValue(Noti);

                        //------------------ se request as accepted in user db ----------- //
                        uDatabase.child(id).child("requests").child(orderId).child("statue").setValue("accepted");
                        for(int i = 0;i<requestsData.size();i++){
                            if(!requestsData.get(i).getId().equals(id)){
                                mDatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderId).child("requests").child(requestsData.get(i).getId());
                                mDatabase.child("statue").setValue("declined");
                                uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(requestsData.get(i).getId()).child("requests").child(orderId);
                                uDatabase.child("statue").setValue("declined");
                            } else {
                                mDatabase =FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderId).child("requests").child(requestsData.get(i).getId());
                                mDatabase.child("statue").setValue("accepted");
                                uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(requestsData.get(i).getId()).child("requests").child(orderId);
                                uDatabase.child("statue").setValue("accepted");
                            }
                        }

                        requestsData.remove(position);
                        notifyItemRemoved(position);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("هل انت متاكد من انك تريد قبول امندوب ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();


        });
    }

    @Override
    public int getItemCount() {
        return this.requestsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        ImageView imgEditPhoto;
        TextView txtName,txtDate;
        Button btnSendMessage;
        RatingBar rbUser;
        ImageView btnAccept, btnDecline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtName = myview.findViewById(R.id.txtName);
            btnAccept = myview.findViewById(R.id.btnAccept);
            txtDate = myview.findViewById(R.id.txtDate);
            imgEditPhoto = myview.findViewById(R.id.imgEditPhoto);
            btnSendMessage = myview.findViewById(R.id.btnSendMessage);
            rbUser = myview.findViewById(R.id.rbUser);
            btnDecline = myview.findViewById(R.id.btnDecline);
        }

        public void setUserInfo(String id) {
            DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        userData uData = snapshot.getValue(userData.class);
                        assert uData != null;
                        txtName.setText(uData.getname());
                        Picasso.get().load(Uri.parse(uData.getPpURL())).into(imgEditPhoto);
                        getRatings(id);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        public void getRatings(String hisID) {
            rDatabase.child(hisID).orderByChild("dId").equalTo(hisID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        long total = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            long rating = (long) Double.parseDouble(Objects.requireNonNull(ds.child("rate").getValue()).toString());
                            total = total + rating;
                        }
                        double average = (double) total / dataSnapshot.getChildrenCount();
                        if(String.valueOf(average).equals("NaN")) {
                            average = 5;
                        }
                        rbUser.setRating((int) average);
                    } else {
                        rbUser.setRating(5);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }


        public void setPostDate(String startDate) {
            caculateTime _cacu = new caculateTime();
            txtDate.setText(_cacu.setPostDate(startDate));
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ((HomeActivity)context).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}