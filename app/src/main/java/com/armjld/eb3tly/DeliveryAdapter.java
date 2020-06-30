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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.DateFormat;
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

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context , context1;
    long count;
    ArrayList<Data>filtersData;
    private DatabaseReference mDatabase,rDatabase,uDatabase,nDatabase,reportDatabase;
    private String TAG = "Supplier Adapter";
    String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    String notiDate = DateFormat.getDateInstance().format(new Date());
    private static final int PHONE_CALL_CODE = 100;

    public void addItem(int position , Data data){
        int size = filtersData.size();
        if(size > position && size != 0) {
            filtersData.set(position,data);
            notifyItemChanged(position);
            Log.i(TAG, "Filter Data Statue : " + data.getStatue());
        }
    }

    public DeliveryAdapter(Context context, ArrayList<Data> filtersData, Context context1, long count) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        Data data = filtersData.get(position);
        // Get Post Date
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

        holder.setDate(data.getDDate());
        holder.setUsername(data.getuId());
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        holder.setDilveredButton(data.getStatue());
        holder.setRateButton(data.getSrated(), data.getStatue());
        holder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());
        holder.checkDeleted(data.getRemoved());

        final String sId = data.getuId();
        final String iPShop = data.getmPShop();
        final String iPAddress = data.getmPAddress();
        final String iDAddress = data.getDAddress();
        final String iDPhone = data.getDPhone();
        final String iDName = data.getDName();

        if(data.getStatue().equals("placed")) {
            holder.mImageButton.setVisibility(View.GONE);
        }

        //Order info
        holder.btnInfo.setOnClickListener(v -> {
            AlertDialog.Builder myInfo = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View infoView = inflater.inflate(R.layout.orderinfo, null);
            myInfo.setView(infoView);
            final AlertDialog dialog = myInfo.create();
            dialog.show();

            TextView tbTitle = infoView.findViewById(R.id.toolbar_title);
            tbTitle.setText("بيانات الاوردر");

            // Intializa Objects
            TextView PShop = infoView.findViewById(R.id.itxtPShop);
            TextView txtPAddress = infoView.findViewById(R.id.itxtPAddress);
            TextView txtDAddress = infoView.findViewById(R.id.itxtDAddress);
            final TextView txtPPhone = infoView.findViewById(R.id.itxtPPhone);
            TextView txtDPhone = infoView.findViewById(R.id.itxtDPhone);
            TextView txtDName = infoView.findViewById(R.id.itxtDName);
            ImageView btniClose = infoView.findViewById(R.id.btniClose);

            // Set Data
            PShop.setText(iPShop);
            txtPAddress.setText("عنوان الاستلام : " + iPAddress);
            txtDAddress.setText("عنوان التسليم : " + iDAddress);
            txtDPhone.setText(iDPhone);
            txtDPhone.setPaintFlags(txtDPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            txtPPhone.setPaintFlags(txtPPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            // call the Customer
            txtDPhone.setOnClickListener(v14 -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + iDPhone));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(callIntent);
            });

            // -----------------------  call the supplier
            txtPPhone.setOnClickListener(v13 -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String ppPhone = (String) txtPPhone.getText();
                callIntent.setData(Uri.parse("tel:" +ppPhone));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(callIntent);
            });

            btniClose.setOnClickListener(v12 -> dialog.dismiss());

            txtDName.setText("اسم العميل : " + iDName);
            uDatabase.child(sId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String uPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                        txtPPhone.setText(uPhone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        });

        holder.linerDate.setOnClickListener(v -> Toast.makeText(context,"معاد تسليم الاوردر يوم : " + holder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show());
        holder.txtgGet.setOnClickListener(v -> Toast.makeText(context, "مصاريف شحن الاوردر : "+ holder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show());
        holder.txtgMoney.setOnClickListener(v -> Toast.makeText(context, "مقدم الاوردر : "+ holder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show());


        // -----------------------   Set ORDER as Delivered
        final String orderID = data.getId();
        holder.btnDelivered.setOnClickListener(v -> {
            final String DID = data.getuAccepted();
            String SID = data.getuId();

            // Changing the values in the orders db
            mDatabase.child(orderID).child("statue").setValue("delivered");
            mDatabase.child(orderID).child("dilverTime").setValue(datee);

            // Add the Profit of the Dilvery Worker
            uDatabase.child(DID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists() && dataSnapshot.child("profit").exists()) {
                        String dbprofits = Objects.requireNonNull(dataSnapshot.child("profit").getValue()).toString();
                        int longProfit = Integer.parseInt(dbprofits);
                        int finalProfits = (longProfit + Integer.parseInt(data.getGGet()));
                        uDatabase.child(DID).child("profit").setValue(String.valueOf(finalProfits));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            // --------------------------- Send Notifications ---------------------//
            notiData Noti = new notiData(uId, SID,orderID,"delivered",notiDate,"false");
            nDatabase.child(SID).push().setValue(Noti);
            Toast.makeText(context, "تم توصيل الاوردر", Toast.LENGTH_SHORT).show();
        });


        // -----------------------  Comment button
        holder.btnRate.setOnClickListener(v -> {
            AlertDialog.Builder myRate = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogRate = inflater.inflate(R.layout.dialograte, null);
            myRate.setView(dialogRate);
            final AlertDialog dialog = myRate.create();
            dialog.show();

            TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
            tbTitle.setText("تقييم التاجر");

            ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v1 -> dialog.dismiss());

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

            btnSaveRate.setOnClickListener(v15 -> {
                final String rRate = txtRate.getText().toString().trim();
                final String rId = rDatabase.push().getKey();
                final int intRating = (int) drStar.getRating();
                assert rId != null;

                rateData data1 = new rateData(rId, orderID, sId ,uId, intRating,rRate , datee);
                rDatabase.child(sId).child(rId).setValue(data1);

                mDatabase.child(orderID).child("srated").setValue("true");
                mDatabase.child(orderID).child("srateid").setValue(rId);
                if(intRating == 1) {
                    rDatabase.child(sId).child(rId).child("isReported").setValue("true");
                } else {
                    rDatabase.child(sId).child(rId).child("isReported").setValue("false");
                }
                Toast.makeText(context, "Order Rated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        // ----------------------- Transportations Toasts ------------------- //
        holder.icnCar.setOnClickListener(v -> Toast.makeText(context, "يمكن توصيل الاوردر بالسيارة", Toast.LENGTH_SHORT).show());
        holder.icnMetro.setOnClickListener(v -> Toast.makeText(context, "يمكن توصيل الاوردر بالمترو", Toast.LENGTH_SHORT).show());
        holder.icnMotor.setOnClickListener(v -> Toast.makeText(context, "يمكن توصيل الاوردر بالموتسكل", Toast.LENGTH_SHORT).show());
        holder.icnTrans.setOnClickListener(v -> Toast.makeText(context, "يمكن توصيل الاوردر بالمواصلات", Toast.LENGTH_SHORT).show());

        // --------- Report for Delvery
        holder.mImageButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context,v );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu_delv, popup.getMenu());
            Menu popupMenu = popup.getMenu();

            switch (data.getStatue().toString()) {
                case "accepted" : {
                    popupMenu.findItem(R.id.deleted).setVisible(true);
                    popupMenu.findItem(R.id.doesntanswer).setVisible(true);
                    popupMenu.findItem(R.id.idelv).setVisible(true);
                    popupMenu.findItem(R.id.falsemoney).setVisible(false);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    break;
                }
                case "recived" : {
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(true);
                    popupMenu.findItem(R.id.deleted).setVisible(false);
                    popupMenu.findItem(R.id.falsemoney).setVisible(false);
                    popupMenu.findItem(R.id.doesntanswer).setVisible(false);
                    popupMenu.findItem(R.id.idelv).setVisible(false);
                    break;
                }
                case "delivered" : {
                    popupMenu.findItem(R.id.falsemoney).setVisible(true);
                    popupMenu.findItem(R.id.deleted).setVisible(false);
                    popupMenu.findItem(R.id.doesntanswer).setVisible(false);
                    popupMenu.findItem(R.id.idelv).setVisible(false);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    break;
                }
            }

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.deleted:
                        reportData repo3 = new reportData(uId, data.getuId(),orderID,datee,"التاجر لغي الاوردر او شخص اخر استمله");

                        mDatabase.child(orderID).child("statue").setValue("placed");
                        mDatabase.child(orderID).child("uAccepted").setValue("");

                        reportDatabase.child(data.getuId()).push().setValue(repo3);
                        Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.falsemoney:
                        reportData repo4 = new reportData(uId, data.getuId(),orderID,datee,"التاجر اخل بالاتفاق");
                        reportDatabase.child(data.getuId()).push().setValue(repo4);
                        Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.doesntanswer:
                        reportData repo5 = new reportData(uId, data.getuId(),orderID,datee,"التاجر لا يريد تسليم الاوردر و اريد الغاء الاوردر");
                        reportDatabase.child(data.getuId()).push().setValue(repo5);


                        Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.idelv:
                        reportData repo6 = new reportData(uId, data.getuId(),orderID,datee,"وصلت الاوردر و زر تم التوصيل غير موجود");
                        reportDatabase.child(data.getuId()).push().setValue(repo6);
                        Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.didnt_reciv:
                        reportData repo7 = new reportData(uId, data.getuId(),orderID,datee,"لم استلم الاوردر بعد");
                        reportDatabase.child(data.getuId()).push().setValue(repo7);
                        Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        // -----------------------  Delete order for Delivery
        final String DorderID = data.getId();
        holder.btnDelete.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // --------------- Add the cencelled order to the counter ----------------------- //
                        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Date lastedit = null;
                                Date acceptedDate = null;
                                try {
                                    lastedit = format.parse(data.getLastedit());
                                    acceptedDate = format.parse(data.getAcceptedTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // ------------------------------- Adding the order to the worker cancelled orders counter --------------- //
                                int cancelledCount =  Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("canceled").getValue()).toString());
                                Log.i(TAG, "You Already Canceled : " + cancelledCount);
                                int finalCount = (cancelledCount + 1);
                                int reminCount = 3 - cancelledCount - 1;

                                assert acceptedDate != null;
                                Log.i(TAG, "acc date : " + acceptedDate + " last edited" + lastedit);
                                if(acceptedDate.compareTo(lastedit) > 0) { // if the worker accepted the order before it has been edited
                                    uDatabase.child(uId).child("canceled").setValue(String.valueOf(finalCount));
                                    Log.i(TAG, "Remining tries : " + reminCount);
                                    Toast.makeText(context, "تم حذف الاوردر بنجاح و تبقي لديك " + reminCount + " فرصه لالغاء الاوردرات هذا الاسبوع", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                }

                                // --------------- Setting the order as placed again ---------------- //
                                mDatabase.child(DorderID).child("statue").setValue("placed");
                                mDatabase.child(DorderID).child("uAccepted").setValue("");
                                mDatabase.child(DorderID).child("acceptTime").setValue("");

                                // --------------------------- Send Notifications ---------------------//
                                String owner = data.getuId();
                                notiData Noti = new notiData(uId, owner, orderID,"deleted",notiDate,"false");
                                nDatabase.child(owner).push().setValue(Noti);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
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
        LinearLayout linerDate, linerAll;
        RatingBar drStar;
        ImageView icnCar,icnMotor,icnMetro,icnTrans;
        ImageButton mImageButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
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

        void setUsername(final String orderOwner){
            final TextView mtitle = myview.findViewById(R.id.txtUsername);
           uDatabase.child(orderOwner).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        mtitle.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }


        public void setOrderFrom(String orderFrom){
            TextView mtitle=myview.findViewById(R.id.OrderFrom);
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

        public void setDilveredButton(String state) {
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