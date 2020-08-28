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
import android.os.Vibrator;
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

import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.delets.Delete_Delivery_From_Sup;
import com.armjld.eb3tly.delets.Delete_Reason_Supplier;
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
    private DatabaseReference mDatabase;
    private DatabaseReference rDatabase;
    private DatabaseReference uDatabase;
    private DatabaseReference nDatabase;
    private DatabaseReference reportDatabase;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    private String TAG = "Supplier Adapter";
    String uId = UserInFormation.getId();
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
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
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
        Vibrator vibe = (Vibrator) Objects.requireNonNull(context).getSystemService(Context.VIBRATOR_SERVICE);
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
        holder.setUsername(data.getDName());
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
            assert vibe != null;
            vibe.vibrate(20);
            PopupMenu popup = new PopupMenu(context,v );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            Menu popupMenu = popup.getMenu();
            switch (data.getStatue()) {
                case "accepted" : {
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(true);
                    popupMenu.findItem(R.id.didnt_deliv).setVisible(false);
                    popupMenu.findItem(R.id.already_delv).setVisible(false);
                    break;
                }
                case "recived" : {
                    popupMenu.findItem(R.id.didnt_deliv).setVisible(true);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    popupMenu.findItem(R.id.already_delv).setVisible(true);
                    break;
                }
                case "delivered" : {
                    popupMenu.findItem(R.id.didnt_deliv).setVisible(true);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    popupMenu.findItem(R.id.already_delv).setVisible(false);
                    break;
                }
            }
            popup.setOnMenuItemClickListener(item -> {
                vibe.vibrate(20);
                switch (item.getItemId()) {
                    case R.id.didnt_reciv:
                        Intent deleteAct = new Intent(context, Delete_Delivery_From_Sup.class);
                        deleteAct.putExtra("orderid", orderID);
                        deleteAct.putExtra("acceptID", data.getuAccepted());
                        deleteAct.putExtra("owner", data.getuId());
                        context.startActivity(deleteAct);
                        break;
                    case R.id.didnt_deliv:
                        DialogInterface.OnClickListener dialogClickListener2 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(dilvID).push().getKey();
                                    reportData repo2 = new reportData(uId, dilvID,orderID,datee,"المندوب لم يسلم الاوردر للعميل", id);
                                    assert id != null;
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

                    case R.id.already_delv:
                        DialogInterface.OnClickListener dialogClickListener3 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(dilvID).push().getKey();
                                    reportData repo2 = new reportData(uId, dilvID,orderID,datee,"المندوب سلم الاوردر و لم يضغط علي زر تم التسليم", id);
                                    assert id != null;
                                    reportDatabase.child(dilvID).child(id).setValue(repo2);

                                    // Changing the values in the orders db
                                    mDatabase.child(orderID).child("statue").setValue("delivered");
                                    mDatabase.child(orderID).child("dilverTime").setValue(datee);

                                    // ------------ Send Notification
                                    notiData Noti = new notiData(uId,data.getuAccepted(), orderID,"لا تنسي ان تضغط علي زر تم التسليم عند تسليم الاوردر",datee,"false", "nothing");
                                    nDatabase.child(data.getuAccepted()).push().setValue(Noti);

                                    // Add the Profit of the Dilvery Worker
                                    uDatabase.child(data.getuAccepted()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists() && dataSnapshot.child("profit").exists()) {
                                                String dbprofits = Objects.requireNonNull(dataSnapshot.child("profit").getValue()).toString();
                                                int longProfit = Integer.parseInt(dbprofits);
                                                int finalProfits = (longProfit + Integer.parseInt(data.getGGet()));
                                                uDatabase.child(data.getuAccepted()).child("profit").setValue(String.valueOf(finalProfits));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });

                                    Toast.makeText(context, "شكرا لبلاغك", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                        builder3.setMessage("هل انت متاكد من انك تريد ان المندوب سلم الاوردر ؟").setPositiveButton("نعم", dialogClickListener3).setNegativeButton("لا", dialogClickListener3).show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        // Delete Order for Supplier
        holder.btnDelete.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            Intent deleteAct = new Intent(context, Delete_Reason_Supplier.class);
            deleteAct.putExtra("orderid", orderID);
            deleteAct.putExtra("acceptID", data.getuAccepted());
            context.startActivity(deleteAct);
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);

            DialogInterface.OnClickListener dialogClickListener2 = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.child(orderID).child("statue").setValue("recived");

                        notiData Noti = new notiData(uId,data.getuAccepted() , orderID,"recived",datee,"false", "profile");
                        nDatabase.child(data.getuAccepted()).push().setValue(Noti);

                        Toast.makeText(context, "تم تسليم الاوردر للمندوب", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setMessage("هل قام امندوب بالتسلام الاوردر منك ؟").setPositiveButton("نعم", dialogClickListener2).setNegativeButton("لا", dialogClickListener2).show();
        });

        // ------------------------------------- Comment button ---------------------------------- //
        holder.btnRate.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
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
                vibe.vibrate(20);
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
            assert vibe != null;
            vibe.vibrate(20);
            Intent editInt = new Intent(context, EditOrders.class);
            editInt.putExtra("orderid", data.getId());
            context.startActivity(editInt);
        });

        // ------------------ Show delivery Worker Info -----------------------//
        holder.txtGetStat.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
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

            imgVerfe.setOnClickListener(v1 -> {
                Toast.makeText(context, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
            });

            ddPhone.setOnClickListener(v1 -> {
                vibe.vibrate(20);
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
                    } else {
                        ddRate.setRating(5);
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
            dialog.show();
        });
    }

    @Override
    public int getItemCount() { return filtersData.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public Button btnEdit,btnDelete,btnInfo,btnDelivered,btnRate,btnRecived;
        public TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate,txtUserName, txtOrderFrom, txtOrderTo,txtPostDate;
        public LinearLayout linerDate,linerAll;
        public RatingBar drStar;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans;
        public ImageButton mImageButton;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            btnEdit = myview.findViewById(R.id.btnEdit);
            txtUserName = myview.findViewById(R.id.txtUsername);
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
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtDate = myview.findViewById(R.id.date);
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            mImageButton = myview.findViewById(R.id.imageButton);
        }

        void setUsername(String DName){
            txtUserName.setText(DName);
        }


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
            txtOrderFrom.setText(orderFrom);
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
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String date){
            txtDate.setText(date);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText(ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText(fees + " ج");
        }

        public void setAccepted() {
            btnDelivered.setVisibility(View.GONE);
            btnInfo.setVisibility(View.GONE);
        }

        public void setType(String car, String motor, String metro, String trans) {
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
            if (dS < 60) {
                finalDate = "منذ " + dS + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + dM + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + dH + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " + dD + " ايام";
            }
            txtPostDate.setText(finalDate);
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