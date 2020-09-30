package com.armjld.eb3tly.SupplierProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Chat.chatListclass;
import com.armjld.eb3tly.DatabaseClasses.requestsandacceptc;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.Orders.OrderInfo;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.DatabaseClasses.Ratings;
import Model.UserInFormation;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.Chat.Messages;
import com.google.android.play.core.review.ReviewInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
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
import Model.requestsData;
import Model.userData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder>{

    Context context;
    private ArrayList<Data>filtersData;

    private DatabaseReference mDatabase;
    private DatabaseReference rDatabase;
    private DatabaseReference uDatabase;
    private DatabaseReference nDatabase;

    int requestNumber = 0;
    private DatabaseReference reportDatabase;
    private String TAG = "Supplier Adapter";
    String uId = UserInFormation.getId();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    public static caculateTime _cacu = new caculateTime();

    
    public SupplierAdapter(Context context, ArrayList<Data> filtersData) {
        this.context = context;
        this.filtersData = filtersData;

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        Vibrator vibe = (Vibrator) Objects.requireNonNull(context).getSystemService(Context.VIBRATOR_SERVICE);
        Log.i(TAG, "Inside the Supplier Adapter");
        Data data = filtersData.get(position);
        String startDate = data.getDate();
        requestNumber = 0;

        holder.setDate(filtersData.get(position).getDDate(), filtersData.get(position).getpDate());
        holder.setUsername(data.getDName());
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(startDate);
        holder.setAccepted();
        holder.setStatue(data.getStatue(), data.getDDate(), data.getId(), position);
        holder.setDilveredButton(data.getStatue());
        holder.setRateButton(data.getDrated(), data.getStatue());
        holder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());
        holder.checkDeleted(data.getRemoved());

        final String dilvID = data.getuAccepted();
        final String sID = data.getuId();
        final String orderID = data.getId();

        mDatabase.child(orderID).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestNumber = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.mImageButton.setVisibility(View.GONE);
        holder.mImageButton.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            PopupMenu popup = new PopupMenu(context, v);
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
                                    notiData Noti = new notiData(uId,data.getuAccepted(), orderID,"لا تنسي ان تضغط علي زر تم التسليم عند تسليم الاوردر",datee,"false", "nothing", UserInFormation.getUserName(), UserInFormation.getUserURL());
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

        holder.btnChat.setOnClickListener(v-> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(uId, data.getuAccepted(), context);
            Messages.cameFrom = "Profile";
        });

        holder.btnInfo.setOnClickListener(v-> {
            Intent intent = new Intent(context, OrderInfo.class);
            intent.putExtra("orderID", orderID);
            intent.putExtra("owner", UserInFormation.getId());
            ((Activity) context).startActivityForResult(intent, 1);
            OrderInfo.cameFrom = "Profile";
        });

        holder.btnOrderBack.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل استلمت المرتجع من الكابتن ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                mDatabase.child(orderID).child("statue").setValue("deniedback");
                mDatabase.child(orderID).child("deniedbackTime").setValue(datee);

                String message = "قام " + UserInFormation.getUserName() + " باستلام المرتجع منك";
                notiData Noti = new notiData(uId,data.getuAccepted() , orderID,message,datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                nDatabase.child(data.getuAccepted()).push().setValue(Noti);

                filtersData.get(position).setStatue("deniedback");
                holder.setDilveredButton("deniedback");

                chatListclass _ch = new chatListclass();
                _ch.supplierchat(data.getuAccepted());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل قام الندوب باستلام الشحنة منك ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                filtersData.get(position).setStatue("recived");
                holder.setDilveredButton("recived");

                mDatabase.child(orderID).child("statue").setValue("recived");
                mDatabase.child(orderID).child("recivedTime").setValue(datee);

                String message = "قام " + UserInFormation.getUserName() + " بتسليمك الاوردر";
                notiData Noti = new notiData(uId,data.getuAccepted() , orderID,message,datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                nDatabase.child(data.getuAccepted()).push().setValue(Noti);

                Toast.makeText(context, "في انتظار تأكيد المندوب باستلامة الشحنة", Toast.LENGTH_LONG).show();

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();

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

                Ratings _rate = new Ratings();
                _rate.setRating(dilvID, intRating);

                holder.setRateButton("true", filtersData.get(position).getStatue());

                mDatabase.child(orderID).child("drated").setValue("true");
                mDatabase.child(orderID).child("drateid").setValue(rId);

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
            if (filtersData.get(position).getStatue().equals("placed") && requestNumber > 0){
                if(holder.requestRecycler.isShown()) {
                    holder.icnArrowDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_black));
                    holder.requestRecycler.setVisibility(View.GONE);
                    holder.txtGetStat.setVisibility(View.VISIBLE);
                } else {
                    holder.icnArrowDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_black));
                    holder.requestRecycler.setVisibility(View.VISIBLE);
                    holder.txtGetStat.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.icnArrowDown.setOnClickListener(v-> {
            if(holder.requestRecycler.isShown()) {
                holder.icnArrowDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_black));
                holder.requestRecycler.setVisibility(View.GONE);
                holder.txtGetStat.setVisibility(View.VISIBLE);
            } else {
                holder.icnArrowDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_black));
                holder.requestRecycler.setVisibility(View.VISIBLE);
                holder.txtGetStat.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() { return filtersData.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public Button btnEdit,btnDelete,btnChat,btnRate,btnRecived,btnOrderBack;
        public TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate,txtUserName, txtOrderFrom, txtOrderTo,txtPostDate,pickDate;
        public LinearLayout linerDate,linerAll;
        public RatingBar drStar;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans,icnArrowDown, btnInfo;
        public ImageButton mImageButton;
        RecyclerView requestRecycler;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            btnChat = myview.findViewById(R.id.btnChat);
            btnEdit = myview.findViewById(R.id.btnEdit);
            txtUserName = myview.findViewById(R.id.txtUsername);
            btnRecived = myview.findViewById(R.id.btnRecived);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnRate = myview.findViewById(R.id.btnRate);
            txtRate = myview.findViewById(R.id.drComment);
            drStar = myview.findViewById(R.id.drStar);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
            pickDate = myview.findViewById(R.id.pickDate);

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
            requestRecycler = myview.findViewById(R.id.requestRecycler);
            btnOrderBack = myview.findViewById(R.id.btnOrderBack);
            btnInfo = myview.findViewById(R.id.btnInfo);
            icnArrowDown = myview.findViewById(R.id.icnArrowDown);
        }

        void setUsername(String DName){
            txtUserName.setText(DName);
        }


        @SuppressLint("ResourceAsColor")
        public void setStatue(String getStatue, String ddate, String orderID, int position){
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
                        // --------------- Get the Requests
                        txtGetStat.setEnabled(false);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("لا يوجد لديك اي تقديمات");
                        txtGetStat.setBackgroundColor(Color.RED);
                        icnArrowDown.setVisibility(View.GONE);

                        mDatabase.child(orderID).child("requests").orderByChild("statue").equalTo("N/A").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int countt = (int) snapshot.getChildrenCount();
                                if(countt > 0) {
                                    String statText = "لديك " + countt + " طلب للاوردر اضغط هنا للمزيد";
                                    txtGetStat.setEnabled(true);
                                    txtGetStat.setVisibility(View.VISIBLE);
                                    txtGetStat.setText(statText);
                                    txtGetStat.setBackgroundColor(Color.parseColor("#ffc922"));
                                    icnArrowDown.setVisibility(View.VISIBLE);

                                    requestRecycler.setHasFixedSize(true);
                                    LinearLayoutManager layoutManager= new LinearLayoutManager(context);
                                    layoutManager.setReverseLayout(true);
                                    layoutManager.setStackFromEnd(true);
                                    requestRecycler.setLayoutManager(layoutManager);


                                    ArrayList<requestsData> mm = new ArrayList<>();
                                    mDatabase.child(orderID).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                for(DataSnapshot ds : snapshot.getChildren()) {
                                                    requestsData rData = ds.getValue(requestsData.class);
                                                    if(ds.child("statue").exists()) {
                                                        assert rData != null;
                                                        if(rData.getStatue().equals("N/A")) { // Get only the not decliend requests
                                                            mm.add(rData);
                                                            RequestsAdapter req = new RequestsAdapter(context, mm, orderID, position);
                                                            requestRecycler.setAdapter(req);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                    break;
                }
                case "recived" : {
                    txtGetStat.setVisibility(View.GONE);
                }
                case "accepted": {
                    txtGetStat.setVisibility(View.GONE);
                    txtGetStat.setEnabled(false);
                    break;
                }
                case "delivered": {
                    txtGetStat.setEnabled(false);
                    txtGetStat.setVisibility(View.GONE);
                    txtGetStat.setText("تم توصيل اوردرك");
                    txtGetStat.setBackgroundColor(Color.parseColor("#4CAF50"));
                    break;
                }

                case "denied" : {
                    txtGetStat.setEnabled(false);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("لم يقبل العميل استلام الاوردر");
                    txtGetStat.setBackgroundColor(Color.parseColor("#ff0000"));
                    break;
                }

                case "deniedback" : {
                    txtGetStat.setEnabled(false);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("مرتجع");
                    txtGetStat.setBackgroundColor(Color.parseColor("#ff0000"));
                    break;
                }

                default: {
                    txtGetStat.setEnabled(false);
                    txtGetStat.setVisibility(View.GONE);
                    txtGetStat.setText("");
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
                    if (statue.equals("delivered") || statue.equals("deniedback")) {
                        btnRate.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        }

        public void setDilveredButton(final String state) {
                btnRate.setText("تقييم المندوب");
                switch (state) {
                    case "placed" : {
                        btnEdit.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnRecived.setVisibility(View.GONE);
                        btnChat.setVisibility(View.GONE);
                        btnOrderBack.setVisibility(View.GONE);
                        break;
                    }
                    case "accepted": {
                        btnRecived.setVisibility(View.VISIBLE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnChat.setVisibility(View.VISIBLE);
                        btnOrderBack.setVisibility(View.GONE);

                        break;
                    }
                    case "recived": {
                        btnChat.setVisibility(View.VISIBLE);
                        btnRecived.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnOrderBack.setVisibility(View.GONE);

                    }
                    case "delivered" : {
                        btnChat.setVisibility(View.GONE);
                        btnRecived.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnOrderBack.setVisibility(View.GONE);
                        break;
                    }

                    case "denied": {
                        btnChat.setVisibility(View.GONE);
                        btnRecived.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnOrderBack.setVisibility(View.VISIBLE);
                        break;
                    }

                    case "deniedback": {
                        btnChat.setVisibility(View.GONE);
                        btnRecived.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnOrderBack.setVisibility(View.GONE);
                        break;
                    }
                }
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String date, String pDate){
            txtDate.setText(date);
            pickDate.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText("ثمن الشحنة : " + ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText("مصاريف الشحن : " + fees + " ج");
        }

        public void setAccepted() {
            btnChat.setVisibility(View.GONE);
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

        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
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
