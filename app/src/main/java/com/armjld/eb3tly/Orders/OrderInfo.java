package com.armjld.eb3tly.Orders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Chat.Messages;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.DatabaseClasses.rquests;
import Model.UserInFormation;
import com.armjld.eb3tly.Settings.Wallet.wallet;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.CaptinProfile.Delete_Reaon_Delv;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
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

    public static String cameFrom = "Home Activity";

    TextView date3, date, orderto, OrderFrom,txtPack,txtWeight,ordercash2,fees2,txtPostDate2;
    TextView dsUsername,txtTitle,ddCount,txtNoddComments;
    TextView dsPAddress,dsDAddress,txtCallCustomer;
    ImageView ppStar,imgVerf,supPP,btnOrderMap;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    RatingBar rbUser;
    ImageView btnBlock, btnClose;
    private BlockManeger block = new BlockManeger();
    Button btnBid, btnMore,btnDelete;
    private static final int PHONE_CALL_CODE = 100;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    SimpleDateFormat orderformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    String datee = sdf.format(new Date());
    String orderState = "placed";
    String acceptedTime = "";
    String lastEdit = "";
    String dName = "";
    String ownerName = "";
    String dPhone = "";
    String strPickLat = "";
    String strPickLong = "";

    private void getBack() {
        if(cameFrom.equals("Home Activity")) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (cameFrom.equals("Profile")) {
            whichProfile();
        }
    }

    private void whichProfile () {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        //getBack();
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
        txtCallCustomer = findViewById(R.id.txtCallCustomer);
        txtCallCustomer.setVisibility(View.GONE);
        btnOrderMap = findViewById(R.id.btnOrderMap);

        date3 = findViewById(R.id.date3);
        date = findViewById(R.id.date);
        orderto = findViewById(R.id.orderto);
        OrderFrom = findViewById(R.id.OrderFrom);
        txtPack = findViewById(R.id.txtPack);
        txtWeight = findViewById(R.id.txtWeight);
        ordercash2 = findViewById(R.id.ordercash2);
        fees2 = findViewById(R.id.fees2);

        btnDelete = findViewById(R.id.btnDelete);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("بيانات الاوردر");
        String uId = UserInFormation.getId();

        btnClose.setOnClickListener(v -> {
            finish();
            //getBack();
        });

        if(UserInFormation.getId().equals(owner)) {
            btnBlock.setVisibility(View.GONE);
        }

        btnOrderMap.setVisibility(View.GONE);
        btnOrderMap.setOnClickListener(v -> {
            Intent map = new Intent(this, MapOneOrder.class);
            map.putExtra("pickLat", strPickLat);
            map.putExtra("pickLong", strPickLong);
            startActivity(map);
        });

        btnDelete.setOnClickListener(v -> {
            Intent deleteAct = new Intent(this, Delete_Reaon_Delv.class);
            deleteAct.putExtra("orderid", orderID);
            deleteAct.putExtra("owner", owner);
            deleteAct.putExtra("aTime", acceptedTime);
            deleteAct.putExtra("eTime", lastEdit);
            deleteAct.putExtra("dName", dName);
            startActivity(deleteAct);
        });

        txtCallCustomer.setOnClickListener(v -> {
            if(!dPhone.equals("")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل تريد الاتصال بالعميل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {

                    checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + dPhone));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                }).build();
                mBottomSheetDialog.show();
            } else {
                Toast.makeText(this, "التاجر لم يضع رقم هاتف", Toast.LENGTH_SHORT).show();
            }
        });

        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data orderData = snapshot.getValue(Data.class);
                assert orderData != null;
                orderState = orderData.getStatue();
                acceptedTime = orderData.getAcceptedTime();
                lastEdit = orderData.getLastedit();
                dName = orderData.getDName();
                dPhone = orderData.getDPhone();

                setPostDate(orderData.getDate());

                String PAddress = "عنوان الاستلام : " + orderData.getmPAddress();
                String DAddress = "عنوان التسليم : " + orderData.getDAddress();
                String fees = "مصاريف الشحن : " + orderData.getGGet();
                String money = "سعر الرساله : " + orderData.getGMoney();
                String pDate = orderData.getpDate();
                String dDate = orderData.getDDate();
                String pack = "محتوي الرساله : " + orderData.getPackType();
                String weight = "وزن الرساله : " + orderData.getPackWeight() + " كيلو";
                String from = orderData.reStateP();
                String to = orderData.reStateD();

                if(snapshot.child("lat").exists() && snapshot.child("_long").exists()) {
                    strPickLat = snapshot.child("lat").getValue().toString();
                    strPickLong  = snapshot.child("_long").getValue().toString();
                    btnOrderMap.setVisibility(View.VISIBLE);
                }

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

                fees2.setText(fees + " ج");
                ordercash2.setText(money + " ج");
                date3.setText(pDate);
                date.setText(dDate);
                txtPack.setText(pack);
                txtWeight.setText(weight);
                orderto.setText(to);
                OrderFrom.setText(from);

                txtCallCustomer.setText("رقم هاتف العميل : " + dPhone);
                txtCallCustomer.setPaintFlags(txtCallCustomer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                if(owner.equals(UserInFormation.getId())) {
                    txtCallCustomer.setVisibility(View.VISIBLE);
                }
                if (orderState.equals("placed")) {
                    btnBid.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                } else {
                    btnBid.setVisibility(View.GONE);
                    if(orderState.equals("accepted") && orderData.getuAccepted().equals(UserInFormation.getId())) {
                        btnDelete.setVisibility(View.VISIBLE);
                        txtCallCustomer.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabase.child(orderID).child("requests").child(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setBid("true");
                } else {
                    setBid("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnBid.setOnClickListener(v -> {
            mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل انت متأكد من انك تريد الغاء التقديم علي هذه الشحنه ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                            // ------------------- Send Request -------------------- //
                            rquests _rquests = new rquests();
                            _rquests.deleteReq(uId, orderID);

                            // ------------------ Notificatiom ------------------ //
                            setBid("false");
                            Toast.makeText(OrderInfo.this, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                        }).build();
                        mBottomSheetDialog.show();
                    } else {
                        wallet w = new wallet();
                        if (!w.workerbid()) {
                            Toast.makeText(OrderInfo.this, "لا يمكنك قبول اي اورديد حتي تدفع المبلغ المستحق", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (HomeActivity.requests) {
                            Toast.makeText(OrderInfo.this, "لديك 10 طلبات معلقه, لا يمكنك ارسال المزيدا", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (HomeActivity.orders) {
                            Toast.makeText(OrderInfo.this, "لديك 20 اوردر معلق حتي الان, قم بتوصيلهم اولا", Toast.LENGTH_LONG).show();
                            return;
                        }

                        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                            // ------------------- Send Request -------------------- //
                            rquests _rquests = new rquests();
                            _rquests.addrequest(orderID, datee);

                            // ------------------ Notificatiom ------------------ //
                            String message = "قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + dName;
                            notiData Noti = new notiData(uId, owner, orderID, message, datee, "false", "order",UserInFormation.getUserName(), UserInFormation.getUserURL());
                            nDatabase.child(owner).push().setValue(Noti);
                            setBid("true");

                            Toast.makeText(OrderInfo.this, "تم التقديم علي الشحنه", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                        }).build();
                        mBottomSheetDialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ownerName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                dsUsername.setText(ownerName);

                // Check if account is Verfied
                if (snapshot.child("isConfirmed").exists()) {
                    String isConfirmed = snapshot.child("isConfirmed").getValue().toString();
                    if (isConfirmed.equals("true")) {
                        imgVerf.setVisibility(View.VISIBLE);
                    } else {
                        imgVerf.setVisibility(View.GONE);
                    }
                } else {
                    imgVerf.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Get the Rate Stars
        rDatabase.child(owner).orderByChild("sId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long rating = (long) Double.parseDouble(Objects.requireNonNull(ds.child("rate").getValue()).toString());
                        total = total + rating;
                    }
                    double average = (double) total / dataSnapshot.getChildrenCount();
                    if (String.valueOf(average).equals("NaN")) {
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
        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int oCount = 0;
                int currentOrders = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = orderformat.parse(orderData.getDDate());
                            myDate = orderformat.parse(orderformat.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if (!ds.child("statue").getValue().toString().equals("deleted")) {
                            oCount++;
                            if (orderDate.compareTo(myDate) >= 0 && ds.child("statue").getValue().toString().equals("placed")) {
                                currentOrders++;
                            }
                        }
                    }
                }
                if (oCount == 0) {
                    ddCount.setText("لم يقم بأضافه اي اوردرات");
                } else {
                    ddCount.setText("اضاف " + oCount + " اوردر");
                }

                if (oCount >= 10) {
                    dsUsername.setTextColor(R.color.ic_profile_background);
                    ppStar.setVisibility(View.VISIBLE);
                } else {
                    dsUsername.setTextColor(R.color.colorAccent);
                    ppStar.setVisibility(View.GONE);
                }

                if (currentOrders <= 1) {
                    btnMore.setVisibility(View.GONE);
                } else if (currentOrders > 1) {
                    int finalc = currentOrders - 1;
                    btnMore.setVisibility(View.VISIBLE);
                    btnMore.setText("متاح اوردر " + finalc + " لنفس العميل");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        btnMore.setOnClickListener(v -> {
            Intent otherOrders = new Intent(this, OrdersBySameUser.class);
            otherOrders.putExtra("userid", owner);
            otherOrders.putExtra("name", ownerName);
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
            MaterialDialog materialDialog = new MaterialDialog.Builder(OrderInfo.this).setMessage("هل تريد حظر التاجر نهائيا ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_block, (dialogInterface, which) -> {
                boolean flag = block.addUser(owner);
                if (flag) {
                    Toast.makeText(this, "تم حظر المستخدم", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                } else {
                    Toast.makeText(this, "حدث خطأ في العملية", Toast.LENGTH_SHORT).show();

                }
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            materialDialog.show();
        });

        imgVerf.setOnClickListener(v1 -> {
            Toast.makeText(this, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
        });

    }


    public void setPostDate(String startDate) {
        caculateTime _cacu = new caculateTime();
        txtPostDate2.setText(_cacu.setPostDate(startDate));
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

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
}
