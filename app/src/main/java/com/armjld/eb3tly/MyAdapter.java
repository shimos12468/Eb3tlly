package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import Model.Data;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

     Context context , context1;
     long count;
     ArrayList<Data>filtersData;
     private FirebaseAuth mAuth = FirebaseAuth.getInstance();
     private DatabaseReference mDatabase,rDatabase,uDatabase,vDatabase,nDatabase;
     private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
     private String TAG = "My Adapter";
     String uType = StartUp.userType;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
     String datee = sdf.format(new Date());
    int howMany = 0;

    public void addItem(int position , Data data){
        int size = filtersData.size();
        if(size > position && size != 0) {
            filtersData.set(position,data);
            notifyItemChanged(position);
            Log.i(TAG, "Filter Data Statue : " + data.getStatue());
        }
    }

    public MyAdapter(Context context, ArrayList<Data> filtersData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.filtersData = filtersData;
        this.context1 = context1;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        vDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.item_data,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Vibrator vibe = (Vibrator)(context).getSystemService(Context.VIBRATOR_SERVICE);
        // Get Post Date
        holder.lin1.setVisibility(View.GONE);
        holder.txtWarning.setVisibility(View.GONE);
        String startDate = filtersData.get(position).getDate();
        String stopDate = datee;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

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

        final String PAddress = filtersData.get(position).getmPAddress().replaceAll("(^\\h*)|(\\h*$)","").trim();
        final String DAddress = filtersData.get(position).getDAddress().replaceAll("(^\\h*)|(\\h*$)","").trim();
        final String rateUID = filtersData.get(position).getuId().replaceAll("(^\\h*)|(\\h*$)","").trim();
        final String notes = filtersData.get(position).getNotes().replaceAll("(^\\h*)|(\\h*$)","").trim();
        String statues = filtersData.get(position).getStatue().replaceAll("(^\\h*)|(\\h*$)","").trim();
        String removed = filtersData.get(position).getRemoved().replaceAll("(^\\h*)|(\\h*$)","").trim();
        String orderID = filtersData.get(position).getId().replaceAll("(^\\h*)|(\\h*$)","").trim();
        String owner = filtersData.get(position).getuId().replaceAll("(^\\h*)|(\\h*$)","").trim();

        uDatabase.child(owner).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = Objects.requireNonNull(snapshot.getValue()).toString();
                holder.setUsername(uName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ordersCount = 0;
                if (snapshot.exists()) {
                    ordersCount = (int) snapshot.getChildrenCount();
                }
                holder.isTop(ordersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.setDate(filtersData.get(position).getDDate().replaceAll("(^\\h*)|(\\h*$)","").trim());

        holder.setOrdercash(filtersData.get(position).getGMoney().replaceAll("(^\\h*)|(\\h*$)","").trim());
        holder.setOrderFrom(filtersData.get(position).reStateP().replaceAll("(^\\h*)|(\\h*$)","").trim());
        holder.setOrderto(filtersData.get(position).reStateD().replaceAll("(^\\h*)|(\\h*$)","").trim());
        holder.setFee(filtersData.get(position).getGGet().replaceAll("(^\\h*)|(\\h*$)","").trim());
        holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        holder.setType(filtersData.get(position).getIsCar(), filtersData.get(position).getIsMotor(), filtersData.get(position).getIsMetro(), filtersData.get(position).getIsTrans());

        //Hide this order Button
        holder.btnHide.setOnClickListener(v -> {
            Toast.makeText(context, "Still working on this", Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });

        holder.linerDate.setOnClickListener(v -> {
            Toast.makeText(context,"معاد تسليم الاوردر يوم : " + holder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });

        holder.txtgGet.setOnClickListener(v -> {
            Toast.makeText(context, "مصاريف شحن الاوردر : "+ holder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });
        holder.txtgMoney.setOnClickListener(v -> {
            Toast.makeText(context, "مقدم الاوردر : "+ holder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });

        //More Info Button
        holder.btnMore.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            AlertDialog.Builder myDialogMore = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogMore = inflater.inflate(R.layout.dialogsupinfo, null);
            myDialogMore.setView(dialogMore);
            final AlertDialog dialog = myDialogMore.create();
            dialog.show();

            TextView tbTitle = dialogMore.findViewById(R.id.toolbar_title);
            tbTitle.setText("بيانات الاوردر");

            ImageView btnClose = dialogMore.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v1 -> {
                vibe.vibrate(20);
                dialog.dismiss();
            });

            final TextView dsUsername = dialogMore.findViewById(R.id.ddUsername);
            TextView dsPAddress = dialogMore.findViewById(R.id.ddPhone);
            TextView dsDAddress = dialogMore.findViewById(R.id.dsDAddress);
            TextView dsOrderNotes = dialogMore.findViewById(R.id.dsOrderNotes);
            TextView txtTitle = dialogMore.findViewById(R.id.txtTitle);
            final ImageView supPP = dialogMore.findViewById(R.id.supPP);
            final ImageView ppStar = dialogMore.findViewById(R.id.ppStar);
            final RatingBar rbUser = dialogMore.findViewById(R.id.ddRate);
            final TextView ddCount = dialogMore.findViewById(R.id.ddCount);
            final TextView txtNoddComments = dialogMore.findViewById(R.id.txtNoddComments);


            // Get posted orders count
            mDatabase.orderByChild("uId").equalTo(filtersData.get(position).getuId()).addListenerForSingleValueEvent (new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int oCount = 0;
                    if (dataSnapshot.exists()) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        oCount = count;
                        String strCount = String.valueOf(count);
                        ddCount.setText( "اضاف "+ strCount + " اوردر");
                    } else {
                        ddCount.setText("لم يقم بأضافه اي اوردرات");
                    }

                    if(oCount >= 10) {
                        dsUsername.setTextColor(Color.parseColor("#ffc922"));
                        ppStar.setVisibility(View.VISIBLE);
                    } else {
                        dsUsername.setTextColor(Color.WHITE);
                        ppStar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //Get the user name & Pic
            uDatabase.child(rateUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dsUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();

                    Log.i(TAG, "Photo URL : " + dsPP);
                    Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                    dsUsername.setText(dsUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            if (PAddress.trim().equals("")) {
                dsPAddress.setVisibility(View.GONE);
            } else {
                dsPAddress.setVisibility(View.VISIBLE);
                dsPAddress.setText("عنوان الاستلام : " + PAddress);
            }
            if (DAddress.trim().equals("")) {
                dsDAddress.setVisibility(View.GONE);
            } else {
                dsDAddress.setText("عنوان التسليم : " + DAddress);
                dsDAddress.setVisibility(View.VISIBLE);
            }
            if(notes.trim().equals("")) {
                dsOrderNotes.setVisibility(View.GONE);
            } else {
                dsOrderNotes.setText(notes);
                dsOrderNotes.setVisibility(View.VISIBLE);
            }

            //Get the Rate Stars
            rDatabase.child(rateUID).orderByChild("sId").equalTo(rateUID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Get that user Comments
            ListView listComment = dialogMore.findViewById(R.id.dsComment);
            final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<>(context, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
            listComment.setAdapter(arrayAdapterLessons);
            mArraylistSectionLessons.clear(); // To not dublicate comments
            rDatabase.child(rateUID).orderByChild("sId").equalTo(rateUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int comments = 0;
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot cData : dataSnapshot.getChildren()) {
                            if(cData.exists()) {
                                String tempComment = Objects.requireNonNull(cData.child("comment").getValue()).toString();
                                if(!tempComment.equals("")) {
                                    mArraylistSectionLessons.add(tempComment);
                                    comments++;
                                }
                                arrayAdapterLessons.notifyDataSetChanged();
                            }
                        }
                    }

                    if(comments > 0) {
                        txtNoddComments.setVisibility(View.GONE);
                        listComment.setVisibility(View.VISIBLE);
                        txtTitle.setVisibility(View.VISIBLE);
                    } else {
                        txtNoddComments.setVisibility(View.VISIBLE);
                        listComment.setVisibility(View.GONE);
                        txtTitle.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });

        holder.icnCar.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالسيارة", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.icnMetro.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالمترو", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.icnMotor.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالموتسكل", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });
        holder.icnTrans.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالمواصلات", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.linerDate.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context,"معاد تسليم الاوردر يوم : " + holder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });
        holder.txtgGet.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "مصاريف شحن الاوردر : "+ holder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.txtgMoney.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "مقدم الاوردر : "+ holder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        if(!uType.equals("Delivery Worker")) {
            holder.lin1.setVisibility(View.GONE);
            holder.txtWarning.setVisibility(View.GONE);
        }

        if(uType.equals("Delivery Worker")) {
            if(removed.equals("true") || !statues.equals("placed")){
                Log.i(TAG, "You're inside the if of GGMONEY" + filtersData.get(position).getGMoney().replaceAll("(^\\h*)|(\\h*$)","").trim());
                holder.lin1.setVisibility(View.GONE);
                holder.txtWarning.setVisibility(View.VISIBLE);
            } else {
                holder.lin1.setVisibility(View.VISIBLE);
                holder.txtWarning.setVisibility(View.GONE);
            }
        }

        if(uType.equals("Admin")) {
            holder.linAdmin.setVisibility(View.VISIBLE);
        }
        holder.btnEdit.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            Intent editInt = new Intent(context, EditOrders.class);
            editInt.putExtra("orderid", orderID);
            context.startActivity(editInt);
        });

        holder.btnDelete.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            mDatabase.child(orderID).child("statue").setValue("deleted");
            Toast.makeText(context, "Order is Deleted", Toast.LENGTH_SHORT).show();
        });

        holder.btnOpen.setOnClickListener(v -> {
            uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uPass = Objects.requireNonNull(snapshot.child("mpass").getValue()).toString();
                    String uMail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    mAuth.signOut();
                    Intent editInt = new Intent(context, MainActivity.class);
                    editInt.putExtra("umail", uMail);
                    editInt.putExtra("upass", uPass);
                    context.startActivity(editInt);
                    Log.i(TAG , "User Info : " + uMail + " : " + uPass);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });

        //Accept Order Button
        holder.btnAccept.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            String gettingID = filtersData.get(position).getId();
            vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString().equals("false")) {
                        Toast.makeText(context, "لا يمكن قبول اي اوردرات الان حاول بعد قليل", Toast.LENGTH_LONG).show();
                        return;
                    }
                    uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int cancelledCount =  Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("canceled").getValue()).toString());
                            if(cancelledCount >= 3) {
                                Toast.makeText(context, "لقد الغيت 3 اوردرات هذا الاسبوع , لا يمكنك قبول اي اوردرات اخري حتي الاسبوع القادم", Toast.LENGTH_LONG).show();
                            } else {
                                mDatabase.orderByChild("uAccepted").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int zcount  = 0;
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            if(ds.child("statue").getValue().toString().equals("accepted")) {
                                                zcount++;
                                            }
                                        }

                                        if(zcount <= 7) {
                                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        mDatabase.child(gettingID).child("uAccepted").setValue(mAuth.getCurrentUser().getUid());
                                                        mDatabase.child(gettingID).child("statue").setValue("accepted");
                                                        mDatabase.child(gettingID).child("acceptedTime").setValue(datee);

                                                        // -------------------------- Send Notifications ---------------------//
                                                        notiData Noti = new notiData( mAuth.getUid(), owner, orderID,"accepted",datee,"false");
                                                        nDatabase.child(owner).push().setValue(Noti);

                                                        Toast.makeText(context, "تم قبول الاوردر تواصل مع التاجر من بيانات الاوردر", Toast.LENGTH_LONG).show();

                                                        howMany = 0;
                                                        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if(snapshot.exists()) {
                                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                                        if(ds.exists() && ds.child("ddate").exists()) {
                                                                            Data orderData = ds.getValue(Data.class);
                                                                            assert orderData != null;
                                                                            Date orderDate = null;
                                                                            Date myDate = null;
                                                                            try {
                                                                                orderDate = format2.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                                                                                myDate =  format2.parse(sdf2.format(Calendar.getInstance().getTime()));
                                                                            } catch (ParseException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            assert orderDate != null;
                                                                            assert myDate != null;
                                                                            if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")) {
                                                                                howMany++;
                                                                            }
                                                                        }
                                                                    }

                                                                    if(howMany == 0) {
                                                                        context.startActivity(new Intent(context, NewProfile.class));
                                                                    } else {
                                                                        Intent otherOrders = new Intent(context, OrdersBySameUser.class);
                                                                        otherOrders.putExtra("userid", owner);
                                                                        context.startActivity(otherOrders);
                                                                    }
                                                                }
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            }
                                                        });

                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        break;
                                                }
                                            };
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage("هل انت متاكد من انك تريد استلام الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                                        } else {
                                            Toast.makeText(context, "لا يمكنك قبول اكثر من سبع اوردرات في نفس الوقت", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
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
        public Button btnAccept, btnHide, btnMore, btnEdit,btnDelete,btnOpen;
        public TextView txtWarning,txtgGet, txtgMoney,txtDate, txtUsername, txtOrderFrom,txtOrderTo,txtPostDate;
        public LinearLayout lin1,linerDate,linAdmin;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans,imgStar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
            btnAccept = myview.findViewById(R.id.btnAccept);
            btnHide = myview.findViewById(R.id.btnHide);
            btnMore = myview.findViewById(R.id.btnMore);
            btnOpen = myview.findViewById(R.id.btnOpen);
            lin1 = myview.findViewById(R.id.lin1);
            linAdmin = myview.findViewById(R.id.linAdmin);
            imgStar = myview.findViewById(R.id.imgStar);
            txtWarning = myview.findViewById(R.id.txtWarning);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnEdit = myview.findViewById(R.id.btnEdit);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
        }

        public void setUsername(String name){
            txtUsername.setText(name);
        }

        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
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

        public void isTop(int ordersCount) {
            if(ordersCount >= 10) {
                imgStar.setVisibility(View.VISIBLE);
                txtUsername.setTextColor(Color.parseColor("#ffc922"));
            } else {
                imgStar.setVisibility(View.GONE);
                txtUsername.setTextColor(Color.parseColor("#FF0099CC"));
            }
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
                finalDate = "منذ " +dD + " ايام";
            }
            txtPostDate.setText(finalDate);
        }
    }
}