package com.armjld.eb3tly.Orders;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Requests.rquests;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Wallet.requestsandacceptc;
import com.armjld.eb3tly.main.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

public class OrderInfo extends AppCompatActivity {

    String orderID;
    DatabaseReference mDatabase, uDatabase,rDatabase,nDatabase;
    String owner;

    TextView date3, date, orderto, OrderFrom,txtPack,txtWeight,ordercash2,fees2,txtPostDate2;
    TextView dsUsername,txtTitle,ddCount,txtNoddComments;
    TextView dsPAddress,dsDAddress,dsOrderNotes;
    ImageView ppStar,imgVerf,supPP;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    RatingBar rbUser;
    ImageView btnBlock, btnClose;
    private BlockManeger block = new BlockManeger();
    Button btnBid, btnMore;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    SimpleDateFormat orderformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    String datee = sdf.format(new Date());
    String DName = "";

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogsupinfo);

        orderID = getIntent().getStringExtra("orderID");
        owner = getIntent().getStringExtra("owner");
        
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");

        dsUsername = findViewById(R.id.ddUsername);
        dsPAddress = findViewById(R.id.ddPhone);
        dsDAddress = findViewById(R.id.dsDAddress);
        dsOrderNotes = findViewById(R.id.dsOrderNotes);
        txtTitle = findViewById(R.id.txtTitle);
        supPP = findViewById(R.id.supPP);
        ppStar = findViewById(R.id.ppStar);
        imgVerf = findViewById(R.id.imgVerf);
        rbUser = findViewById(R.id.ddRate);
        ddCount = findViewById(R.id.ddCount);
        txtNoddComments = findViewById(R.id.txtNoddComments);
        btnClose = findViewById(R.id.btnClose);
        btnBlock = findViewById(R.id.btnBlock);
        txtPostDate2 = findViewById(R.id.txtPostDate2);
        btnBid = findViewById(R.id.btnBid);
        btnMore = findViewById(R.id.btnMore);

        date3  = findViewById(R.id.date3);
        date  = findViewById(R.id.date);
        orderto = findViewById(R.id.orderto);
        OrderFrom = findViewById(R.id.OrderFrom);
        txtPack = findViewById(R.id.txtPack);
        txtWeight = findViewById(R.id.txtWeight);
        ordercash2 = findViewById(R.id.ordercash2);
        fees2 = findViewById(R.id.fees2);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("بيانات الاوردر");
        String uId = UserInFormation.getId();

        btnClose.setOnClickListener(v-> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data orderData = snapshot.getValue(Data.class);
                assert orderData != null;
                DName = orderData.getDName();

                String startDate = orderData.getDate();
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

                setPostDate((int)diffSeconds, (int)diffMinutes, (int)diffHours, (int) diffDays);

                String PAddress = "عنوان الاستلام : "+orderData.getmPAddress();
                String DAddress = "عنوان التسليم : " + orderData.getDAddress();
                String notes = "الملاحظات : " + orderData.getNotes();
                String fees = "مصاريف الشحن : " + orderData.getGGet();
                String money = "سعر الرساله : " +orderData.getGMoney();
                String pDate = orderData.getpDate();
                String dDate =  orderData.getDDate();
                String pack = "الرساله : " +orderData.getPackType();
                String weight = "وزن الرسالة : " +orderData.getPackWeight();
                String from = orderData.reStateP();
                String to = orderData.reStateD();

                if (PAddress.trim().equals("")) {
                    dsPAddress.setVisibility(View.GONE);
                } else {
                    dsPAddress.setVisibility(View.VISIBLE);
                    dsPAddress.setText(PAddress);
                }
                if (DAddress.trim().equals("")) {
                    dsDAddress.setVisibility(View.GONE);
                } else {
                    dsDAddress.setText(DAddress);
                    dsDAddress.setVisibility(View.VISIBLE);
                }
                if(notes.trim().equals("")) {
                    dsOrderNotes.setVisibility(View.GONE);
                } else {
                    dsOrderNotes.setText(notes);
                    dsOrderNotes.setVisibility(View.VISIBLE);
                }
                fees2.setText(fees + " ج");
                ordercash2.setText(money + " ج");
                date3.setText(pDate);
                date.setText(dDate);
                txtPack.setText(pack);
                txtWeight.setText(weight);
                orderto.setText(to);
                OrderFrom.setText(from);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabase.child(orderID).child("requests").child(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    setBid("true");
                } else {
                    setBid("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnBid.setOnClickListener(v-> {
            mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        setBid("true");
                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.deleteReq(orderID);

                                    // ------------------ Notificatiom ------------------ //
                                    //notiData Noti = new notiData(uId, owner,orderID,"قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + filtersData.get(position).getDName(),datee,"false","order");
                                    //nDatabase.child(owner).push().setValue(Noti);
                                    btnBid.setText("التقديم علي الشحنه");

                                    Toast.makeText(OrderInfo.this, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderInfo.this);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

                    } else {
                        // -------------- New Request
                        requestsandacceptc c  = new requestsandacceptc();
                        if(!c.requestNewOrder())
                            return;
                        setBid("false");
                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.addrequest(orderID, datee);

                                    // ------------------ Notificatiom ------------------ //
                                    notiData Noti = new notiData(uId, owner,orderID,"قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + DName ,datee,"false","order");
                                    nDatabase.child(owner).push().setValue(Noti);
                                    btnBid.setText("الغاء التقديم علي الاوردر");

                                    Toast.makeText(OrderInfo.this, "تم التقديم علي الشحنه", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderInfo.this);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });
        
        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dsUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                dsUsername.setText(dsUser);

                // Check if account is Verfied
                if(snapshot.child("isConfirmed").exists()) {
                    String isConfirmed = snapshot.child("isConfirmed").getValue().toString();
                    if(isConfirmed.equals("true")) {
                        imgVerf.setVisibility(View.VISIBLE);
                    } else {
                        imgVerf.setVisibility(View.GONE);
                    }
                } else {
                    imgVerf.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //Get the Rate Stars
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
                    rbUser.setRating((int) average);
                } else {
                    rbUser.setRating(5);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Get posted orders count
        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent (new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int oCount = 0;
                int currentOrders = 0;
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = orderformat.parse(orderData.getDDate());
                            myDate =  orderformat.parse(orderformat.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if(!ds.child("statue").getValue().toString().equals("deleted")) {
                            oCount ++;
                            if (orderDate.compareTo(myDate) >= 0 && ds.child("statue").getValue().toString().equals("placed")) {
                                currentOrders ++;
                            }
                        }
                    }
                }
                if(oCount == 0) { ddCount.setText("لم يقم بأضافه اي اوردرات");
                } else { ddCount.setText( "اضاف "+ oCount + " اوردر");
                }

                if(oCount >= 10) {
                    dsUsername.setTextColor(R.color.ic_profile_background);
                    ppStar.setVisibility(View.VISIBLE);
                } else {
                    dsUsername.setTextColor(R.color.colorAccent);
                    ppStar.setVisibility(View.GONE);
                }

                if(currentOrders == 1) {
                    btnMore.setVisibility(View.GONE);
                } else {
                    int finalc = currentOrders - 1;
                    btnMore.setVisibility(View.VISIBLE);
                    btnMore.setText("متاح اوردر " + finalc + " لنفس العميل");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        btnMore.setOnClickListener(v-> {
            Intent otherOrders = new Intent(this, OrdersBySameUser.class);
            otherOrders.putExtra("userid", owner);
            otherOrders.putExtra("name", DName);
            startActivity(otherOrders);
        });



        /*
        // Get that user Comments
        ListView listComment = findViewById(R.id.dsComment);
        final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<>(this, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
        listComment.setAdapter(arrayAdapterLessons);
        mArraylistSectionLessons.clear(); // To not dublicate comments
        rDatabase.child(owner).orderByChild("sId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
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
        }); */


        btnBlock.setOnClickListener(v1 -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        boolean flag=block.addUser(owner);
                        if(flag) {
                            Toast.makeText(this, "تم حظر المستخدم", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            Toast.makeText(this, "حدث خطأ في العملية", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("هل انت متاكد من انك تريد حظر هذا المستخدم ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
        });

        imgVerf.setOnClickListener(v1 -> {
            Toast.makeText(this, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
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
        txtPostDate2.setText(finalDate);
    }

    public void setBid(String type) {
        if(type.equals("true")) {
            btnBid.setText("الغاء قبول الاوردر");
            btnBid.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bad));
        } else {
            btnBid.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_defult));
            btnBid.setText("قبول الاوردر");
        }
    }
    
}