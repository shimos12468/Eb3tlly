package com.armjld.eb3tly;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;
import Model.notiData;
import Model.rateData;
import Model.reportData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context , context1;
    long count;
    ArrayList<Data>filtersData;
    private DatabaseReference mDatabase,rDatabase,uDatabase,nDatabase,reportDatabase;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    private String TAG = "Supplier Adapter";
    String uType = StartUp.userType;
    String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private static final int PHONE_CALL_CODE = 100;
    
    public void addItem(int position , Data data){
        int size = filtersData.size();
        if(size > position && size != 0) {
            filtersData.set(position,data);
            notifyItemChanged(position);
            Log.i(TAG, "Filter Data Statue : " + data.getStatue());
        }
    }
    
    public SupplierAdapter(Context context, ArrayList<Data> filtersData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.filtersData = filtersData;
        this.context1 = context1;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");

    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.supplieritems,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        Log.i(TAG, "Inside the Supplier Adapter");
        Data data = filtersData.get(position);
        String startDate = data.getDate();
        String stopDate = datee;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(startDate);
            d2 = format.parse(stopDate);
        } catch (ParseException ex) {
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

        holder.setDate(filtersData.get(position).getDDate());
        holder.setUsername(data.getuId(), data.getDName(), uType);
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        holder.setAccepted();
        holder.setStatue(data.getStatue(), data.getuAccepted(), data.getDDate());
        holder.setDilveredButton(data.getStatue());
        holder.setRateButton(data.getDrated(), data.getStatue());
        holder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());
        holder.checkDeleted(data.getRemoved());

        final String dilvID = data.getuAccepted();
        final String sID = data.getuId();
        final String orderID = data.getId();
        //final String rateUID = data.getuId();

        if(data.getStatue().equals("placed")) {
            holder.mImageButton.setVisibility(View.GONE);
        }

        holder.mImageButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context,v );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            Menu popupMenu = popup.getMenu();
            switch (data.getStatue()) {
                case "accepted" : {
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(true);
                    popupMenu.findItem(R.id.didnt_deliv).setVisible(false);
                    break;
                }
                case "recived" :
                case "delivered" : {
                    popupMenu.findItem(R.id.didnt_deliv).setVisible(true);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    break;
                }
            }
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.didnt_reciv:
                        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(dilvID).push().getKey();
                                    reportData repo = new reportData(uId, dilvID,orderID,datee,"المندوب لم يستلم الاوردر , اريد عرضه علي باقي المندوبين", id);
                                    reportDatabase.child(dilvID).child(id).setValue(repo);

                                    mDatabase.child(orderID).child("uAccepted").setValue("");
                                    mDatabase.child(orderID).child("statue").setValue("placed");

                                    Toast.makeText(context, "تم الابلاغ عن المندوب", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("هل انت متاكد من انك تريد تقديم البلاغ ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                        break;
                    case R.id.didnt_deliv:
                        DialogInterface.OnClickListener dialogClickListener2 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(dilvID).push().getKey();
                                    reportData repo2 = new reportData(uId, dilvID,orderID,datee,"المندوب لم يسلم الاوردر للعميل", id);
                                    reportDatabase.child(dilvID).child(id).setValue(repo2);
                                    Toast.makeText(context, "تم الابلاغ عن المندوب", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setMessage("هل انت متاكد من انك تريد تقديم البلاغ ؟").setPositiveButton("نعم", dialogClickListener2).setNegativeButton("لا", dialogClickListener2).show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        // Delete Order for Supplier
        holder.btnDelete.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // ------------ Delete the Orders Notfications ------------------- //
                        nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if(ds.exists()) {
                                            for(DataSnapshot sn : ds.getChildren()) {
                                                if(sn.child("orderid").exists()) {
                                                    String orderI = Objects.requireNonNull(Objects.requireNonNull(sn.child("orderid").getValue()).toString());
                                                    if(orderI.equals(orderID)) {
                                                        sn.getRef().removeValue();

                                                        filtersData.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, filtersData.size());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });

                        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();
                                Toast.makeText(context, "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            mDatabase.child(orderID).child("statue").setValue("recived");
            Toast.makeText(context, "تم تسليم الاوردر للمندوب", Toast.LENGTH_SHORT).show();
        });

        //Comment button
        holder.btnRate.setOnClickListener(v -> {
            AlertDialog.Builder myRate = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogRate = inflater.inflate(R.layout.dialograte, null);
            myRate.setView(dialogRate);
            final AlertDialog dialog = myRate.create();
            dialog.show();

            TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
            tbTitle.setText("تقييم المندوب");

            ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v14 -> dialog.dismiss());

            Button btnSaveRate = dialogRate.findViewById(R.id.btnSaveRate);
            final EditText txtRate = dialogRate.findViewById(R.id.drComment);
            final RatingBar drStar = dialogRate.findViewById(R.id.drStar);
            final TextView txtReport = dialogRate.findViewById(R.id.txtReport);

            // -------------- Make suer that the minmum rate is 1 star --------------------//
            drStar.setOnRatingBarChangeListener((drStar1, rating, fromUser) -> {
                if(rating<1.0f) {
                    drStar1.setRating(1.0f);
                } else if (rating == 1.0f) {
                    txtReport.setVisibility(View.VISIBLE);
                } else {
                    txtReport.setVisibility(View.GONE);
                }
            });

            btnSaveRate.setOnClickListener(v13 -> {
                final String rRate = txtRate.getText().toString().trim();
                final String rId = rDatabase.push().getKey();
                assert rId != null;
                final int intRating = (int) drStar.getRating();

                rateData data1 = new rateData(rId, orderID, sID, dilvID, intRating, rRate, datee);
                rDatabase.child(dilvID).child(rId).setValue(data1);

                mDatabase.child(orderID).child("drated").setValue("true");
                mDatabase.child(orderID).child("drateid").setValue(rId);

                if(intRating == 1) {
                    rDatabase.child(dilvID).child(rId).child("isReported").setValue("true");
                } else {
                    rDatabase.child(dilvID).child(rId).child("isReported").setValue("false");
                }
                Toast.makeText(context, "شكرا لتقيمك", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        // ----------------- Edit Order for Supplier ------------------------//
        holder.btnEdit.setOnClickListener(v -> {
            Intent editInt = new Intent(context, EditOrders.class);
            editInt.putExtra("orderid", data.getId());
            context.startActivity(editInt);
        });

        // ------------------ Show delivery Worker Info -----------------------//
        holder.txtGetStat.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder myDialogMore = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogMore = inflater.inflate(R.layout.dialogdevinfo, null);
            myDialogMore.setView(dialogMore);
            final AlertDialog dialog = myDialogMore.create();
            dialog.show();

            TextView tbTitle = dialogMore.findViewById(R.id.toolbar_title);
            tbTitle.setText("بيانات المندوب");

            ImageView btnClose = dialogMore.findViewById(R.id.btnClose);
            TextView txtTitle = dialogMore.findViewById(R.id.txtTitle);

            btnClose.setOnClickListener(v12 -> dialog.dismiss());

            final TextView ddUsername = dialogMore.findViewById(R.id.ddUsername);
            final TextView ddPhone = dialogMore.findViewById(R.id.ddPhone);
            ddPhone.setPaintFlags(ddPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            final TextView ddCount = dialogMore.findViewById(R.id.ddCount);
            final RatingBar ddRate = dialogMore.findViewById(R.id.ddRate);
            final ImageView dPP = dialogMore.findViewById(R.id.dPP);
            final TextView txtNodsComments = dialogMore.findViewById(R.id.txtNodsComments);

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
            uDatabase.child(dilvID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String dUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        String dPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                        String sPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                        Log.i(TAG, "Photo " + sPP);
                        Picasso.get().load(Uri.parse(sPP)).into(dPP);
                        ddUsername.setText(dUser);
                        ddPhone.setText(dPhone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            // -------------------- Get the Rate Stars ------------------//
            rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            // -------------------------- Get total delivered orders
            mDatabase.orderByChild("uAccepted").equalTo(data.getuAccepted()).addListenerForSingleValueEvent (new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        String strCount = String.valueOf(count);
                        ddCount.setText( "وصل " + strCount + " اوردر");
                    } else {
                        ddCount.setText("لم يقم بتوصيل اي اوردر");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            // ------------------------------ Get that user Comments --------------------------- //
            ListView listComment = (ListView) dialogMore.findViewById(R.id.dsComment);
            final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<>(context, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
            listComment.setAdapter(arrayAdapterLessons);
            mArraylistSectionLessons.clear();
            txtNodsComments.setVisibility(View.VISIBLE);// To not dublicate comments
            rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int comments = 0;
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot cData : dataSnapshot.getChildren()) {
                            if(cData.exists()) {
                                String tempComment = Objects.requireNonNull(cData.child("comment").getValue()).toString();
                                Log.i(TAG, tempComment);
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
        });
    }

    @Override
    public int getItemCount() {
        return (int) count;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ((profile)context).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        Button btnEdit,btnDelete,btnInfo,btnDelivered,btnRate,btnRecived;
        TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate;
        LinearLayout linerDate,linerAll;
        RatingBar drStar;
        ImageView icnCar,icnMotor,icnMetro,icnTrans;
        ImageButton mImageButton;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            btnEdit = myview.findViewById(R.id.btnEdit);
            btnRecived = myview.findViewById(R.id.btnRecived);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnRate = myview.findViewById(R.id.btnRate);
            txtRate = myview.findViewById(R.id.drComment);
            drStar = myview.findViewById(R.id.drStar);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);

            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            mImageButton = (ImageButton) myview.findViewById(R.id.imageButton);
        }

        void setUsername(String orderOwner, String DName, String uType){
            final TextView mtitle = myview.findViewById(R.id.txtUsername);
            if (uType.equals("Supplier")) {
                mtitle.setText(DName);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(orderOwner).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            mtitle.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        //Get Order Satues in Profile
        @SuppressLint("ResourceAsColor")
        public void setStatue(final String getStatue, final String uAccepted, String ddate){
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date yesterday = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
            Date strDate = null;
            try { strDate = sdf2.parse(ddate); } catch (ParseException e) { e.printStackTrace(); }
            switch (getStatue) {
                case "placed": {
                    if (yesterday.compareTo(strDate) > 0) {
                        txtGetStat.setEnabled(false);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("فات معاد تسلم اوردرك و لم يقبله اي مندوب, الرجاء تعديل معاد تسليم الاوردر او الغاءة");
                        txtGetStat.setBackgroundColor(Color.RED);
                    } else {
                        txtGetStat.setEnabled(false);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("لم يتم قبول اوردرك بعد");
                        txtGetStat.setBackgroundColor(Color.RED);
                    }
                    break;
                }
                case "recived" :
                case "accepted": {
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setEnabled(true);
                    uDatabase.child(uAccepted).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                            if(getStatue.equals("recived")) {
                                txtGetStat.setText("تم استلام اوردرك من : " + mName + " اضغط هنا للمزيد من التفاصيل و للتواصل مع المندوب");
                            } else {
                                txtGetStat.setText("تم قبول اوردرك من : " + mName + " اضغط هنا للمزيد من التفاصيل و للتواصل مع المندوب");
                            }
                            txtGetStat.setBackgroundColor(Color.parseColor("#ffc922"));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    break;
                }
                case "delivered": {
                    txtGetStat.setEnabled(false);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("تم توصيل اوردرك");
                    txtGetStat.setBackgroundColor(Color.parseColor("#4CAF50"));
                    break;
                }
            }
        }

        public void setOrderFrom(String orderFrom){
            TextView mtitle = myview.findViewById(R.id.OrderFrom);
            mtitle.setText(orderFrom);
        }

        public void setRateButton(String rated, String statue) {
            switch (rated){
                case "true" : {
                    btnRate.setVisibility(View.GONE);
                    break;
                }
                case "false" : {
                    btnRate.setVisibility(View.GONE);
                    if ("delivered".equals(statue)) {
                        btnRate.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        }

        public void setDilveredButton(final String state) {
            if (uType.equals("Supplier")) {
                btnDelivered.setVisibility(View.GONE);
                btnInfo.setVisibility(View.GONE);
                btnRate.setText("تقييم المندوب");
                switch (state) {
                    case "placed" : {
                        btnEdit.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnRecived.setVisibility(View.GONE);
                        break;
                    }
                    case "accepted": {
                        btnRecived.setVisibility(View.VISIBLE);
                        btnEdit.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "recived":
                    case "delivered" : {
                        btnRecived.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        break;
                    }
                }
            } else {
                btnEdit.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnRate.setText("تقييم التاجر");
                switch (state) {
                    case "accepted" : {
                        btnDelete.setVisibility(View.VISIBLE);
                        btnDelivered.setVisibility(View.GONE);
                        btnInfo.setVisibility(View.VISIBLE);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("تواصل مع التاجر لاستلام الاوردر");
                        txtGetStat.setBackgroundColor(Color.RED);
                        break;
                    }
                    case "recived" : {
                        txtGetStat.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.GONE);
                        btnDelivered.setVisibility(View.VISIBLE);
                        btnInfo.setVisibility(View.VISIBLE);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("تم استلام الاوردر من التاجر");
                        txtGetStat.setBackgroundColor(Color.parseColor("#ffc922"));
                        break;
                    }
                    case "delivered" : {
                        btnDelivered.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnInfo.setVisibility(View.GONE);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("تم توصيل الاوردر بنجاح");
                        txtGetStat.setBackgroundColor(Color.parseColor("#4CAF50"));
                        break;
                    }
                }
            }
        }

        public void setOrderto(String orderto){
            TextView mtitle=myview.findViewById(R.id.orderto);
            mtitle.setText(orderto);
        }

        public void setDate (String date){
            TextView mdate= myview.findViewById(R.id.date);
            mdate.setText(date);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            TextView mtitle=myview.findViewById(R.id.ordercash);
            mtitle.setText(ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            TextView mtitle=myview.findViewById(R.id.fees);
            mtitle.setText(fees + " ج");
        }

        public void setAccepted() {
            Button btnDilvered = myview.findViewById(R.id.btnDelivered);
            Button btnInfo = myview.findViewById(R.id.btnInfo);
            btnDilvered.setVisibility(View.GONE);
            btnInfo.setVisibility(View.GONE);
        }

        public void setType(String car, String motor, String metro, String trans) {
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            if (car.equals("سياره")) {
                icnCar.setVisibility(View.VISIBLE);
            } else {
                icnCar.setVisibility(View.GONE);
            }

            if(motor.equals("موتسكل")) {
                icnMotor.setVisibility(View.VISIBLE);
            } else {
                icnMotor.setVisibility(View.GONE);
            }

            if(metro.equals("مترو")) {
                icnMetro.setVisibility(View.VISIBLE);
            } else {
                icnMetro.setVisibility(View.GONE);
            }

            if (trans.equals("مواصلات")) {
                icnTrans.setVisibility(View.VISIBLE);
            } else {
                icnTrans.setVisibility(View.GONE);
            }
        }
        public void setPostDate(int dS, int dM, int dH, int dD) {
            String finalDate = "";
            TextView mtitle = myview.findViewById(R.id.txtPostDate);
            if (dS < 60) {
                finalDate = "منذ " + dS + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + dM + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + dH + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " + dD + " ايام";
            }
            mtitle.setText(finalDate);
        }

        public void checkDeleted(String removed) {
            if(removed.equals("true")) {
                linerAll.setVisibility(View.GONE);
            } else {
                linerAll.setVisibility(View.VISIBLE);
            }
        }
    }
}
