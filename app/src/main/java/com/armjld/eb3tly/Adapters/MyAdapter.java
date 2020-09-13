package com.armjld.eb3tly.Adapters;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Orders.OrderInfo;
import com.armjld.eb3tly.Requests.rquests;
import com.armjld.eb3tly.SignUp.New_SignUp;
import com.armjld.eb3tly.Utilites.StartUp;

import com.armjld.eb3tly.Wallet.requestsandacceptc;
import com.armjld.eb3tly.admin.Admin;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.main.Login_Options;
import com.armjld.eb3tly.main.MainActivity;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.Orders.OrdersBySameUser;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.messeges.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

     static Context context;
    Context context1;
     long count;
     ArrayList<Data>filtersData;
     private FirebaseAuth mAuth = FirebaseAuth.getInstance();
     private DatabaseReference mDatabase;
     private static DatabaseReference rDatabase;
     private DatabaseReference uDatabase;
     private DatabaseReference vDatabase;
     private DatabaseReference nDatabase;
     private DatabaseReference Database;
     private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
     private String TAG = "My Adapter";
     String uType = UserInFormation.getAccountType();
     String uId = UserInFormation.getId();
     private BlockManeger block = new BlockManeger();


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
        this.filtersData =filtersData;
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
        Vibrator vibe = (Vibrator) (context).getSystemService(Context.VIBRATOR_SERVICE);
        //holder.btnBid.setVisibility(View.GONE);
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

        final String PAddress = filtersData.get(position).getmPAddress().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        final String DAddress = filtersData.get(position).getDAddress().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        final String notes = filtersData.get(position).getNotes().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String statues = filtersData.get(position).getStatue().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String removed = filtersData.get(position).getRemoved().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String orderID = filtersData.get(position).getId().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String owner = filtersData.get(position).getuId().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String type = filtersData.get(position).getType();


        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String isConfirm = "false";
                if (snapshot.child("isConfirmed").exists()) {
                    isConfirm = snapshot.child("isConfirmed").getValue().toString();
                }
                holder.setUsername(isConfirm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ordersCount = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (!Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("deleted")) {
                            ordersCount++;
                        }
                    }
                }
                holder.isTop(ordersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.setDate(filtersData.get(position).getDDate().replaceAll("(^\\h*)|(\\h*$)", "").trim(), filtersData.get(position).getpDate().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrdercash(filtersData.get(position).getGMoney().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrderFrom(filtersData.get(position).reStateP().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrderto(filtersData.get(position).reStateD().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setFee(filtersData.get(position).getGGet().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        holder.setType(filtersData.get(position).getIsCar(), filtersData.get(position).getIsMotor(), filtersData.get(position).getIsMetro(), filtersData.get(position).getIsTrans());
        holder.setBid(type);
        holder.setRating(owner);

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


        mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    holder.setBid("true");
                } else {
                    holder.setBid("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        holder.btnBid.setOnClickListener(v1 -> {
            mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        holder.setBid("true");
                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.deleteReq(orderID);

                                    // ------------------ Notificatiom ------------------ //
                                    //notiData Noti = new notiData(uId, owner,orderID,"قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + filtersData.get(position).getDName(),datee,"false","order");
                                    //nDatabase.child(owner).push().setValue(Noti);
                                    holder.btnBid.setText("التقديم علي الشحنه");

                                    Toast.makeText(context, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

                    } else {
                        requestsandacceptc c  = new requestsandacceptc();
                        if(!c.requestNewOrder())
                            return;
                        holder.setBid("false");
                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.addrequest(orderID, datee);

                                    // ------------------ Notificatiom ------------------ //
                                    notiData Noti = new notiData(uId, owner,orderID,"قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + filtersData.get(position).getDName(),datee,"false","order");
                                    nDatabase.child(owner).push().setValue(Noti);
                                    holder.btnBid.setText("الغاء التقديم علي الاوردر");

                                    Toast.makeText(context, "تم التقديم علي الشحنه", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });


        //More Info Button
        holder.btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderInfo.class);
            intent.putExtra("orderID", orderID);
            intent.putExtra("owner", owner);
            context.startActivity(intent);
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



        if(uType.equals("Supplier")) {
            holder.lin1.setVisibility(View.GONE);
            holder.txtWarning.setVisibility(View.GONE);
        } else if(uType.equals("Delivery Worker")) {
            if(removed.equals("true") || !statues.equals("placed")){
                holder.lin1.setVisibility(View.GONE);
                holder.txtWarning.setVisibility(View.VISIBLE);
            } else {
                holder.lin1.setVisibility(View.VISIBLE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.txtWarning.setVisibility(View.GONE);
            }
        } else if(uType.equals("Admin")) {
            holder.lin1.setVisibility(View.GONE);
            holder.txtWarning.setVisibility(View.GONE);
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
                    uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int cancelledCount =  Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("canceled").getValue()).toString());
                            if(cancelledCount >= 3) {
                                Toast.makeText(context, "لقد الغيت 3 اوردرات هذا الاسبوع , لا يمكنك قبول اي اوردرات اخري حتي الاسبوع القادم", Toast.LENGTH_LONG).show();
                            } else {
                                mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                        mDatabase.child(gettingID).child("uAccepted").setValue(uId);
                                                        mDatabase.child(gettingID).child("statue").setValue("accepted");
                                                        mDatabase.child(gettingID).child("acceptedTime").setValue(datee);

                                                        // -------------------------- Send Notifications ---------------------//
                                                        notiData Noti = new notiData(uId, owner, orderID,"accepted",datee,"false", "profile");
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
        public Button btnAccept, btnMore, btnEdit,btnDelete,btnOpen, btnBid;
        public TextView txtWarning,txtgGet, txtgMoney,txtDate, txtOrderFrom,txtOrderTo,txtPostDate, txtDate2;
        public LinearLayout lin1,linerDate,linAdmin;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans,imgStar,imgVerf;
        public RatingBar ddRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
            btnAccept = myview.findViewById(R.id.btnAccept);
            btnMore = myview.findViewById(R.id.btnMore);
            btnOpen = myview.findViewById(R.id.btnOpen);
            lin1 = myview.findViewById(R.id.lin1);
            linAdmin = myview.findViewById(R.id.linAdmin);
            imgStar = myview.findViewById(R.id.imgStar);
            imgVerf = myview.findViewById(R.id.imgVerf);
            txtWarning = myview.findViewById(R.id.txtWarning);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            txtDate2 = myview.findViewById(R.id.date3);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnEdit = myview.findViewById(R.id.btnEdit);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnBid = myview.findViewById(R.id.btnBid);
            ddRate = myview.findViewById(R.id.ddRate);
        }

        public void setUsername(String isConfirm){
            if(isConfirm.equals("true")) {
                imgVerf.setVisibility(View.VISIBLE);
                Log.i("Home", "Visible");
            } else {
                imgVerf.setVisibility(View.GONE);
                Log.i("Home", "Gone");
            }
        }

        public void setBid(String type) {
            if(type.equals("true")) {
                btnBid.setText("الغاء قبول الاوردر");
                btnBid.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bad));
            } else {
                btnBid.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_defult));
                btnBid.setText("قبول الاوردر");
            }
        }

        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String dDate, String pDate){
            txtDate.setText(dDate);
            txtDate2.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText("ثمن الرسالة : " + ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText("مصاريف الشحن : " + fees + " ج");
        }

        @SuppressLint("ResourceAsColor")
        public void isTop(int ordersCount) {
            if(ordersCount >= 10) {
                imgStar.setVisibility(View.VISIBLE);
            } else {
                imgStar.setVisibility(View.GONE);
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

        public void setRating(String owner) {
            rDatabase.child(owner).orderByChild("sId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        ddRate.setRating((int) average);
                    } else {
                        ddRate.setRating(5);
                    }
                    ddRate.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}