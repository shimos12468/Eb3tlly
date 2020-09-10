package com.armjld.eb3tly.Adapters;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.HomeActivity;
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
import Model.requestsData;
import Model.userData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    long count;
    String orderId;
    ArrayList<requestsData>requestsData;
    private DatabaseReference uDatabase,mDatabase,rDatabase,reportDatabase,nDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private BlockManeger block = new BlockManeger();
    private static final int PHONE_CALL_CODE = 100;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();



    public RequestsAdapter(Context context, ArrayList<Model.requestsData> requestsData, long count, String orderId) {
        this.count = count;
        this.context = context;
        this.requestsData = requestsData;
        this.orderId = orderId;
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");
        String TAG = "Requests Adapter";

        Log.i(TAG, " Reached Reply Adapter");
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
        String offer = requestsData.get(position).getOffer();
        String date = requestsData.get(position).getDate();

        String startDate = date;
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
        holder.setUserInfo(id);
        holder.setBody(offer);

        holder.myview.setOnClickListener(v-> {
            AlertDialog.Builder myDialogMore = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogMore = inflater.inflate(R.layout.dialogdevinfo, null);
            myDialogMore.setView(dialogMore);
            final AlertDialog dialog = myDialogMore.create();

            TextView tbTitle = dialogMore.findViewById(R.id.toolbar_title);
            tbTitle.setText("بيانات المندوب");

            ImageView btnClose = dialogMore.findViewById(R.id.btnClose);
            TextView txtTitle = dialogMore.findViewById(R.id.txtTitle);

            btnClose.setOnClickListener(v12 -> dialog.dismiss());

            final TextView ddUsername = dialogMore.findViewById(R.id.ddUsername);
            final TextView ddPhone = dialogMore.findViewById(R.id.ddPhone);
            ddPhone.setPaintFlags(ddPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            final TextView ddCount = dialogMore.findViewById(R.id.ddCount);
            final ImageView ppStar = dialogMore.findViewById(R.id.ppStar);
            final ImageView imgVerfe = dialogMore.findViewById(R.id.imgVerf);
            final RatingBar ddRate = dialogMore.findViewById(R.id.ddRate);
            final ImageView dPP = dialogMore.findViewById(R.id.dPP);
            final TextView txtNodsComments = dialogMore.findViewById(R.id.txtNodsComments);
            final ImageView btnBlock = dialogMore.findViewById(R.id.btnBlock);

            btnBlock.setOnClickListener(v1 -> {
                DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // -------------------------- Start Blocking ---------------------//
                            boolean flag = block.addUser(id);
                            if(flag)
                                Toast.makeText(context, "تم حظر المستخدم", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "حدث خطأ في العملية", Toast.LENGTH_SHORT).show();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("هل انت متاكد من انك تريد خظر هذا المستخدم ؟").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            });

            imgVerfe.setOnClickListener(v1 -> {
                Toast.makeText(context, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
            });

            ddPhone.setOnClickListener(v1 -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + ddPhone.getText().toString()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(callIntent);
            });

            // --------------------- Get the user name && Phone Number -------------------//
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String dPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                    String sPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                    Picasso.get().load(Uri.parse(sPP)).into(dPP);
                    ddUsername.setText(dUser);
                    ddPhone.setText(dPhone);

                    // Check if account is Verfied
                    String isConfirm = "false";
                    if(snapshot.child("isConfirmed").exists()) {
                        isConfirm = snapshot.child("isConfirmed").getValue().toString();
                    }
                    if(isConfirm.equals("true")) {
                        imgVerfe.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            // -------------------- Get the Rate Stars ------------------//
            rDatabase.child(id).orderByChild("dId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        long total = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.exists()) {
                                long rating = (long) Double.parseDouble(Objects.requireNonNull(ds.child("rate").getValue()).toString());
                                total = total + rating;
                            }
                        }
                        double average = (double) total / dataSnapshot.getChildrenCount();
                        if(String.valueOf(average).equals("NaN")) {
                            average = 5;
                        }
                        ddRate.setRating((int) average);
                    } else {
                        ddRate.setRating(5);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            // -------------------------- Get total delivered orders
            mDatabase.orderByChild("uAccepted").equalTo(id).addListenerForSingleValueEvent (new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int oCount = 0;
                    if (dataSnapshot.exists()) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        oCount = count;
                        String strCount = String.valueOf(count);
                        ddCount.setText( "وصل " + strCount + " اوردر");
                    } else {
                        count = 0;
                        ddCount.setText("لم يقم بتوصيل اي اوردر");
                    }

                    if(oCount >= 10) {
                        ddUsername.setTextColor(Color.parseColor("#ffc922"));
                        ppStar.setVisibility(View.VISIBLE);
                    } else {
                        ddUsername.setTextColor(Color.WHITE);
                        ppStar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });



            // ------------------------------ Get that user Comments --------------------------- //
            ListView listComment = dialogMore.findViewById(R.id.dsComment);
            final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<>(context, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
            listComment.setAdapter(arrayAdapterLessons);
            mArraylistSectionLessons.clear();
            txtNodsComments.setVisibility(View.VISIBLE);// To not dublicate comments
            rDatabase.child(id).orderByChild("dId").equalTo(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int comments = 0;
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot cData : dataSnapshot.getChildren()) {
                            if(cData.exists()) {
                                String tempComment = Objects.requireNonNull(cData.child("comment").getValue()).toString();
                                if(!tempComment.equals("")) {
                                    mArraylistSectionLessons.add(tempComment);
                                    comments ++;
                                }
                                arrayAdapterLessons.notifyDataSetChanged();
                            }
                        }
                    }
                    if(comments > 0) {
                        txtNodsComments.setVisibility(View.GONE);
                        listComment.setVisibility(View.VISIBLE);
                        txtTitle.setVisibility(View.VISIBLE);
                    } else {
                        txtNodsComments.setVisibility(View.VISIBLE);
                        listComment.setVisibility(View.GONE);
                        txtTitle.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            dialog.show();
        });

        holder.btnAccept.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(orderId).child("uAccepted").setValue(id);
                        mDatabase.child(orderId).child("statue").setValue("accepted");
                        mDatabase.child(orderId).child("acceptedTime").setValue(datee);

                        // ------------------ Notificatiom ------------------ //
                        notiData Noti = new notiData(UserInFormation.getId(), id,orderId,"قام " + UserInFormation.getUserName() + " بقبول طلبك لاستلام الاوردر ",datee,"false","order");
                        nDatabase.child(id).push().setValue(Noti);

                        // ----------------- Set the new Shipping Price --------//
                        mDatabase.child(orderId).child("gget").setValue(offer);

                        //------------------ se request as accepted in user db ----------- //
                        uDatabase.child(id).child("requests").child(orderId).child("statue").setValue("accepted");



                        Toast.makeText(context, "تم قبول المندوب", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, supplierProfile.class));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("هل انت متاكد من انك تريد قبول هذا العرض ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();


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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        ImageView imgEditPhoto;
        TextView txtName, txtBody,txtDate;
        Button btnAccept;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtName = myview.findViewById(R.id.txtName);
            txtBody = myview.findViewById(R.id.txtBody);
            btnAccept = myview.findViewById(R.id.btnAccept);
            txtDate = myview.findViewById(R.id.txtDate);
            imgEditPhoto = myview.findViewById(R.id.imgEditPhoto);

        }

        @SuppressLint("SetTextI18n")
        public void setBody(String offer) {
            txtBody.setText(offer + " ج");
        }

        public void setUserInfo(String id) {
            DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userData uData = snapshot.getValue(userData.class);
                    assert uData != null;
                    txtName.setText(uData.getname());
                    Picasso.get().load(Uri.parse(uData.getPpURL())).into(imgEditPhoto);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
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
            txtDate.setText(finalDate);
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ((supplierProfile)context).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}