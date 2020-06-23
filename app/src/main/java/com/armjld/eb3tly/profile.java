package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.widget.Toast;
import android.widget.Toolbar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import Model.rateData;
import Model.Data;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.*;

public class profile extends AppCompatActivity {

    private DatabaseReference uDatabase, mDatabase, rDatabase, nDatabase, vDatabase;
    private ConstraintLayout constNoti;
    private RecyclerView userRecycler;
    private FirebaseAuth mAuth;
    private NavigationMenuItemView item;
    private FloatingActionButton btnAdd;
    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList datalist;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<String>();
    private String user_type = "";
    private ImageView btnNavbarProfile, imgSetPP, btnOpenNoti;
    private TextView txtUserDate, tbTitle, uName, txtNoOrders,txtNotiCount;
    private String TAG = "Profile";

    private static final int PHONE_CALL_CODE = 100;

    String uType = "";
    FirebaseRecyclerAdapter<Data, myviewholder> adapter;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    String notiDate = DateFormat.getDateInstance().format(new Date());

    // Disable the Back Button
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        // ---------------------- Database Access
        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uId = mUser.getUid();
        final String uID = uId;

        // -------------------- intalize
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.GONE);

        //btnSettings = findViewById(R.id.btnSettings);
        btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        constNoti = findViewById(R.id.constNoti);
        btnOpenNoti = findViewById(R.id.btnOpenNoti);
        datalist = new ArrayList<Data>();
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        imgSetPP = findViewById(R.id.imgPPP);
        txtNotiCount = findViewById(R.id.txtNotiCount);


        //Title Bar
        tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردراتي");

        txtNoOrders.setVisibility(View.GONE);
        txtNotiCount.setVisibility(View.GONE);

        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_timeline, R.id.nav_signout, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();

        constNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(profile.this, Notifications.class));
            }
        });

        btnNavbarProfile.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        btnOpenNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(profile.this, Notifications.class));
            }
        });

        // Navigation Bar Buttons Function
        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_timeline) {
                    newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(newIntentNB);
                    finish();
                }
                if (id == R.id.nav_signout) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        finish();
                        startActivity(new Intent(profile.this, MainActivity.class));
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
                    }
                }
                if (id==R.id.nav_profile){
                    startActivity(new Intent(getApplicationContext(), profile.class));
                }
                if (id == R.id.nav_info) {
                    startActivity(new Intent(getApplicationContext(), UserSetting.class));
                }
                if (id == R.id.nav_contact) {
                    startActivity(new Intent(getApplicationContext(), Conatact.class));
                }
                if (id == R.id.nav_how) {
                    startActivity(new Intent(getApplicationContext(), HowTo.class));
                }
                if (id == R.id.nav_changepass) {
                    startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                }
                if (id == R.id.nav_share) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Play Store Link");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
                }
                if (id==R.id.nav_about){
                    startActivity(new Intent(profile.this, About.class));
                }
                drawer.closeDrawer(Gravity.LEFT);
                return true;
            }
        });

        // -------------------------- Get users Notifications Count -------------------//
        nDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int notiCount = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists()) {
                            if(ds.child("isRead").getValue().equals("false")) {
                                notiCount++;
                            }
                        }
                    }
                    if(notiCount > 0) {
                        txtNotiCount.setVisibility(View.VISIBLE);
                        txtNotiCount.setText(""+notiCount);
                    } else {
                        txtNotiCount.setVisibility(View.GONE);
                    }
                } else {
                    txtNotiCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // -------------------------- Get user info for profile
        uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ppURL = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                uName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                txtUserDate.setText("اشترك : " + Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                if (!isFinishing()) {
                    Log.i(TAG, "Photo " + ppURL);
                    Picasso.get().load(Uri.parse(ppURL)).into(imgSetPP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //Get this user account type
        uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uType = snapshot.child("accountType").getValue().toString();
                TextView usType = findViewById(R.id.txtUserType);
                if (uType.equals("Supplier")) {
                    usType.setText("تاجر");
                    txtNoOrders.setText("لم تقم باضافه اي اوردرات حتي الان");
                    user_type = "sId";
                    final RatingBar rbProfile = findViewById(R.id.rbProfile);
                    rDatabase.child(uID).orderByChild(user_type).equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long total = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                total = total + rating;
                            }
                            double average = (double) total / dataSnapshot.getChildrenCount();
                            Log.i(TAG, "Average Before : " + String.valueOf(average));
                            if(String.valueOf(average).equals("NaN")) {
                               average = 5;
                               Log.i(TAG, "Average Midel : " + String.valueOf(average));
                            }
                            Log.i(TAG, "Average Final : " + String.valueOf(average));
                            rbProfile.setRating((int) average);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    Log.i(TAG, user_type);
                } else if (uType.equals("Delivery Worker")) {
                    usType.setText("مندوب شحن");
                    txtNoOrders.setText("لم تقم بقبول اي اوردرات حتي الان");
                    user_type = "dId";
                    final RatingBar rbProfile = findViewById(R.id.rbProfile);
                    rDatabase.child(uID).orderByChild(user_type).equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long total = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                total = total + rating;
                            }
                            double average = (double) total / dataSnapshot.getChildrenCount();
                            if(String.valueOf(average).equals("NaN")) {
                                average = 5;
                            }
                            rbProfile.setRating((int) average);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.i(TAG, user_type);
                }
                setOrderCount(uType, uID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // ------------------ Show or Hide Buttons depending on the User Type
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                if (uType.equals("Supplier")) {
                    btnAdd.setVisibility(View.VISIBLE);
                    Menu nav_menu = navigationView.getMenu();
                    nav_menu.findItem(R.id.nav_timeline).setVisible(false);
                    btnNavbarProfile.setVisibility(View.VISIBLE);
                } else {
                    btnAdd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // ADD Order Button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("adding").getValue().toString().equals("false")) {
                            Toast.makeText(profile.this, "عذرا لا يمكنك اضافه اوردرات في الوقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            finish();
                            startActivity(new Intent(profile.this, AddOrders.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference("Pickly").child("users");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        final String uID = mUser.getUid();

        userRecycler = findViewById(R.id.userRecycler);
        userRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        userRecycler.setLayoutManager(layoutManager);

        uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String uType = snapshot.child("accountType").getValue().toString();
                if (uType.equals("Supplier")) {
                    mDatabase.orderByChild("uId").equalTo(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                txtNoOrders.setVisibility(View.GONE);
                            } else {
                                txtNoOrders.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                } else {
                    mDatabase.orderByChild("uAccepted").equalTo(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                txtNoOrders.setVisibility(View.GONE);
                            } else {
                                txtNoOrders.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String uType = snapshot.child("accountType").getValue().toString();
                // Get orders posted by the supplier
                if (uType.equals("Supplier")) {
                    adapter = new FirebaseRecyclerAdapter<Data, myviewholder>(
                            Data.class,
                            R.layout.supplieritems,
                            myviewholder.class,
                            mDatabase.orderByChild("uId").equalTo(uID)) {

                        @Override
                        protected void populateViewHolder(myviewholder myviewholder, final Data data, int i) {
                            // Get Post Date
                            String startDate = data.getDate();
                            String stopDate = datee;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = format.parse(startDate);
                                d2 = format.parse(stopDate);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            long diff = d2.getTime() - d1.getTime();
                            long diffSeconds = diff / 1000;
                            long diffMinutes = diff / (60 * 1000);
                            long diffHours = diff / (60 * 60 * 1000);
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            int idiffSeconds = (int) diffSeconds;
                            int idiffMinutes = (int) diffMinutes;
                            int idiffHours = (int) diffHours;
                            int idiffDays = (int) diffDays;

                            myviewholder.setDate(data.getDDate());
                            myviewholder.setUsername(mUser.getUid(), data.getuId(), data.getDName(), uType);
                            myviewholder.setOrdercash(data.getGMoney());
                            myviewholder.setOrderFrom(data.reStateP());
                            myviewholder.setOrderto(data.reStateD());
                            myviewholder.setFee(data.getGGet().toString());
                            myviewholder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
                            myviewholder.setAccepted();
                            myviewholder.setStatue(data.getStatue(), data.getuAccepted(), data.getDDate());
                            myviewholder.setDilveredButton(data.getStatue());
                            myviewholder.setRateButton(data.getDrated(), data.getStatue());
                            myviewholder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());

                            final String dilvID = data.getuAccepted();
                            final String sID = data.getuId();
                            //final String rateUID = data.getuId();

                            // Delete Order for Supplier
                            final String orderID = data.getId();
                                myviewholder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                                                                                String orderI = Objects.requireNonNull(sn.child("orderid").getValue().toString());
                                                                                if(orderI.equals(orderID)) {
                                                                                    sn.getRef().removeValue();
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
                                                            Toast.makeText(getApplicationContext(), "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                                            setOrderCount("Supplier", mUser.getUid());
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE: break;
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                                    builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                                }
                            });

                            // ---------------- Set order to Recived
                            myviewholder.btnRecived.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDatabase.child(orderID).child("statue").setValue("recived");
                                    Toast.makeText(getApplicationContext(), "تم تسليم الاوردر للمندوب", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //Comment button
                            myviewholder.btnRate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder myRate = new AlertDialog.Builder(profile.this);
                                    LayoutInflater inflater = LayoutInflater.from(profile.this);
                                    final View dialogRate = inflater.inflate(R.layout.dialograte, null);
                                    myRate.setView(dialogRate);
                                    final AlertDialog dialog = myRate.create();
                                    dialog.show();

                                    TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
                                    tbTitle.setText("تقييم المندوب");

                                    ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    Button btnSaveRate = dialogRate.findViewById(R.id.btnSaveRate);
                                    final EditText txtRate = dialogRate.findViewById(R.id.drComment);
                                    final RatingBar drStar = dialogRate.findViewById(R.id.drStar);
                                    final TextView txtReport = dialogRate.findViewById(R.id.txtReport);

                                    // -------------- Make suer that the minmum rate is 1 star --------------------//
                                    drStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                        @Override
                                        public void onRatingChanged(RatingBar drStar, float rating, boolean fromUser) {
                                            if(rating<1.0f) {
                                                drStar.setRating(1.0f);
                                            } else if (rating == 1.0f) {
                                                txtReport.setVisibility(View.VISIBLE);
                                            } else {
                                                txtReport.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    btnSaveRate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final String rRate = txtRate.getText().toString().trim();
                                            final String rId = rDatabase.push().getKey().toString();
                                            final int intRating = (int) drStar.getRating();
                                            rateData data = new rateData(rId, orderID, sID, dilvID, intRating, rRate, datee);
                                            rDatabase.child(dilvID).child(rId).setValue(data);
                                            mDatabase.child(orderID).child("drated").setValue("true");
                                            mDatabase.child(orderID).child("drateid").setValue(rId);
                                            if(intRating == 1) {
                                                rDatabase.child(dilvID).child(rId).child("isReported").setValue("true");
                                            } else {
                                                rDatabase.child(dilvID).child(rId).child("isReported").setValue("false");
                                            }
                                            Toast.makeText(profile.this, "شكرا لتقيمك", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            // ----------------- Edit Order for Supplier ------------------------//
                            myviewholder.btnEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent editInt = new Intent(profile.this, EditOrders.class);
                                    editInt.putExtra("orderid", data.getId().toString());
                                    startActivity(editInt);
                                }
                            });

                            // ------------------ Show delivery Worker Info -----------------------//
                            myviewholder.txtGetStat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder myDialogMore = new AlertDialog.Builder(profile.this);
                                    LayoutInflater inflater = LayoutInflater.from(profile.this);
                                    View dialogMore = inflater.inflate(R.layout.dialogdevinfo, null);
                                    myDialogMore.setView(dialogMore);
                                    final AlertDialog dialog = myDialogMore.create();
                                    dialog.show();

                                    TextView tbTitle = dialogMore.findViewById(R.id.toolbar_title);
                                    tbTitle.setText("بيانات المندوب");

                                    ImageView btnClose = dialogMore.findViewById(R.id.btnClose);

                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    final TextView ddUsername = dialogMore.findViewById(R.id.ddUsername);
                                    final TextView ddPhone = dialogMore.findViewById(R.id.ddPhone);
                                    ddPhone.setPaintFlags(ddPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                                    final TextView ddCount = dialogMore.findViewById(R.id.ddCount);
                                    final RatingBar ddRate = dialogMore.findViewById(R.id.ddRate);
                                    final ImageView dPP = dialogMore.findViewById(R.id.dPP);
                                    final TextView txtNodsComments = dialogMore.findViewById(R.id.txtNodsComments);

                                    ddPhone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + ddPhone.getText().toString()));
                                            if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            startActivity(callIntent);
                                        }
                                    });

                                    // --------------------- Get the user name && Phone Number -------------------//
                                    uDatabase.child(dilvID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            String dUser = snapshot.child("name").getValue().toString();
                                            String dPhone = snapshot.child("phone").getValue().toString();
                                            String sPP = snapshot.child("ppURL").getValue().toString();

                                            if (!isFinishing()) {
                                                Log.i(TAG, "Photo " + sPP);
                                                Picasso.get().load(Uri.parse(sPP)).into(dPP);
                                            }
                                            ddUsername.setText(dUser);
                                            ddPhone.setText(dPhone);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });

                                    // -------------------- Get the Rate Stars ------------------//
                                    rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long total = 0;
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                                total = total + rating;
                                            }
                                            double average = (double) total / dataSnapshot.getChildrenCount();
                                            if(String.valueOf(average).equals("NaN")) {
                                                average = 5;
                                            }
                                            ddRate.setRating((int) average);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    // -------------------------- Get total delivered orders
                                    mDatabase.orderByChild("uAccepted").equalTo(data.getuAccepted().toString()).addListenerForSingleValueEvent (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
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

                                    // ------------------------------ Get that user Comments
                                    ListView listComment = (ListView) dialogMore.findViewById(R.id.dsComment);
                                    final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<String>(profile.this, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
                                    listComment.setAdapter(arrayAdapterLessons);
                                    mArraylistSectionLessons.clear();
                                    txtNodsComments.setVisibility(View.VISIBLE);// To not dublicate comments
                                    rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                int count = (int) dataSnapshot.getChildrenCount();
                                                String tempComment = data.child("comment").getValue().toString();
                                                Log.i(TAG, tempComment);
                                                if(!tempComment.equals("")) {
                                                    mArraylistSectionLessons.add(tempComment);
                                                    txtNodsComments.setVisibility(View.GONE);
                                                }
                                                arrayAdapterLessons.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });
                            datalist.add(data);
                        }
                    };
                    if (datalist.isEmpty()) {
                        userRecycler.setAdapter(adapter);
                    }
                } else { // ------------------------------ Get orders accepted by the Dilvery worker
                    adapter = new FirebaseRecyclerAdapter<Data, myviewholder>(
                            Data.class,
                            R.layout.supplieritems,
                            myviewholder.class,
                            mDatabase.orderByChild("uAccepted").equalTo(uID)) {

                        @Override
                        protected void populateViewHolder(final myviewholder myviewholder, final Data data, int i) {

                            // Get Post Date
                            String startDate = data.getDate();
                            String stopDate = datee;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = format.parse(startDate);
                                d2 = format.parse(stopDate);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            long diff = d2.getTime() - d1.getTime();
                            long diffSeconds = diff / 1000;
                            long diffMinutes = diff / (60 * 1000);
                            long diffHours = diff / (60 * 60 * 1000);
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            int idiffSeconds = (int) diffSeconds;
                            int idiffMinutes = (int) diffMinutes;
                            int idiffHours = (int) diffHours;
                            int idiffDays = (int) diffDays;

                            myviewholder.setDate(data.getDDate());
                            myviewholder.setUsername(mUser.getUid(), data.getuId(), data.getDName(), uType);
                            myviewholder.setOrdercash(data.getGMoney());
                            myviewholder.setOrderFrom(data.reStateP());
                            myviewholder.setOrderto(data.reStateD());
                            myviewholder.setFee(data.getGGet());
                            myviewholder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
                            myviewholder.setAccepted();
                            myviewholder.setDilveredButton(data.getStatue());
                            myviewholder.setRateButton(data.getSrated(), data.getStatue());
                            myviewholder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());

                            final String sId = data.getuId().toString();

                            final String iPShop = data.getmPShop();
                            final String iPAddress = data.getmPAddress();
                            final String iDAddress = data.getDAddress();
                            final String iDPhone = data.getDPhone();
                            final String iDName = data.getDName();
                            final String iUID = data.getuId();

                            //Order info
                            myviewholder.btnInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder myInfo = new AlertDialog.Builder(profile.this);
                                    LayoutInflater inflater = LayoutInflater.from(profile.this);
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
                                    txtDPhone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + iDPhone));
                                            if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            startActivity(callIntent);
                                        }
                                    });

                                    // -----------------------  call the supplier
                                    txtPPhone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            String ppPhone = (String) txtPPhone.getText();
                                            callIntent.setData(Uri.parse("tel:" +ppPhone));
                                            if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            startActivity(callIntent);
                                        }
                                    });

                                    btniClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    txtDName.setText("اسم العميل : " + iDName);
                                    uDatabase.child(iUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String uPhone = snapshot.child("phone").getValue().toString();
                                            txtPPhone.setText(uPhone);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });

                            myviewholder.linerDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this,"معاد تسليم الاوردر يوم : " + myviewholder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            myviewholder.txtgGet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "مصاريف شحن الاوردر : "+ myviewholder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            myviewholder.txtgMoney.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "مقدم الاوردر : "+ myviewholder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });


                            // -----------------------   Set ORDER as Delivered
                            final String orderID = data.getId();
                            myviewholder.btnDelivered.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String DID = data.getuAccepted();
                                    String STATUE = data.getStatue();
                                    String SID = data.getuId();

                                    // Changing the values in the orders db
                                    mDatabase.child(orderID).child("statue").setValue("delivered");
                                    mDatabase.child(orderID).child("dilverTime").setValue(datee);
                                    adapter.notifyDataSetChanged();

                                    // Add the Profit of the Dilvery Worker
                                    uDatabase.child(DID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String dbprofits = dataSnapshot.child("profit").getValue().toString();
                                            int longProfit = Integer.parseInt(dbprofits);
                                            int finalProfits = (longProfit + Integer.parseInt(data.getGGet()));
                                            uDatabase.child(DID).child("profit").setValue(String.valueOf(finalProfits));
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    // --------------------------- Send Notifications ---------------------//
                                    notiData Noti = new notiData(mUser.getUid().toString(), SID,orderID,"delivered",notiDate,"false");
                                    nDatabase.child(SID).push().setValue(Noti);
                                    Toast.makeText(getApplicationContext(), "تم توصيل الاوردر", Toast.LENGTH_SHORT).show();
                                }
                            });


                            // -----------------------  Comment button
                            myviewholder.btnRate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder myRate = new AlertDialog.Builder(profile.this);
                                    LayoutInflater inflater = LayoutInflater.from(profile.this);
                                    final View dialogRate = inflater.inflate(R.layout.dialograte, null);
                                    myRate.setView(dialogRate);
                                    final AlertDialog dialog = myRate.create();
                                    dialog.show();

                                    TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
                                    tbTitle.setText("تقييم التاجر");

                                    ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    Button btnSaveRate = dialogRate.findViewById(R.id.btnSaveRate);
                                    final EditText txtRate = dialogRate.findViewById(R.id.drComment);
                                    final RatingBar drStar = dialogRate.findViewById(R.id.drStar);
                                    final TextView txtReport = dialogRate.findViewById(R.id.txtReport);

                                    // -------------- Make suer that the minmum rate is 1 star --------------------//
                                    drStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                        @Override
                                        public void onRatingChanged(RatingBar drStar, float rating, boolean fromUser) {
                                            if(rating<1.0f) {
                                                drStar.setRating(1.0f);
                                            } else if (rating == 1.0f) {
                                                txtReport.setVisibility(View.VISIBLE);
                                            } else {
                                                txtReport.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    btnSaveRate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final String rRate=txtRate.getText().toString().trim();
                                            final String rId=rDatabase.push().getKey().toString();
                                            final int intRating = (int) drStar.getRating();
                                            rateData data=new rateData(rId, orderID, sId ,uID, intRating,rRate , datee);
                                            rDatabase.child(sId).child(rId).setValue(data);
                                            mDatabase.child(orderID).child("srated").setValue("true");
                                            mDatabase.child(orderID).child("srateid").setValue(rId);
                                            if(intRating == 1) {
                                                rDatabase.child(sId).child(rId).child("isReported").setValue("true");
                                            } else {
                                                rDatabase.child(sId).child(rId).child("isReported").setValue("false");
                                            }
                                            Toast.makeText(profile.this, "Order Rated", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                            // ----------------------- Transportations Toasts ------------------- //
                            myviewholder.icnCar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "يمكن توصيل الاوردر بالسيارة", Toast.LENGTH_SHORT).show();
                                }
                            });
                            myviewholder.icnMetro.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "يمكن توصيل الاوردر بالمترو", Toast.LENGTH_SHORT).show();
                                }
                            });
                            myviewholder.icnMotor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "يمكن توصيل الاوردر بالموتسكل", Toast.LENGTH_SHORT).show();
                                }
                            });
                            myviewholder.icnTrans.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(profile.this, "يمكن توصيل الاوردر بالمواصلات", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // -----------------------  Delete order for Delivery
                            final String DorderID = data.getId();
                            myviewholder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    // --------------- Add the cencelled order to the counter ----------------------- //
                                                    uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Date lastedit = null;
                                                            Date acceptedDate = null;
                                                            try {
                                                                lastedit = format.parse(data.getLastedit().toString());
                                                                acceptedDate = format.parse(data.getAcceptedTime());
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }

                                                            // ------------------------------- Adding the order to the worker cancelled orders counter --------------- //
                                                            int cancelledCount =  Integer.parseInt(dataSnapshot.child("canceled").getValue().toString());
                                                            Log.i(TAG, "You Already Canceled : " + cancelledCount);
                                                            int finalCount = (cancelledCount + 1);
                                                            int reminCount = 3 - cancelledCount - 1;

                                                            assert acceptedDate != null;
                                                            Log.i(TAG, "acc date : " + acceptedDate + " last edited" + lastedit);
                                                            if(acceptedDate.compareTo(lastedit) > 0) { // if the worker accepted the order before it has been edited
                                                                uDatabase.child(uID).child("canceled").setValue(String.valueOf(finalCount));
                                                                Log.i(TAG, "Remining tries : " + reminCount);
                                                                Toast.makeText(profile.this, "تم حذف الاوردر بنجاح و تبقي لديك " + reminCount + " فرصه لالغاء الاوردرات هذا الاسبوع", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(profile.this, "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                                            }

                                                            // --------------- Setting the order as placed again ---------------- //
                                                            mDatabase.child(DorderID).child("statue").setValue("placed");
                                                            mDatabase.child(DorderID).child("uAccepted").setValue("");
                                                            mDatabase.child(DorderID).child("acceptTime").setValue("");

                                                            // --------------------------- Send Notifications ---------------------//
                                                            String owner = data.getuId();
                                                            notiData Noti = new notiData(mUser.getUid().toString(), owner, orderID,"deleted",notiDate,"false");
                                                            nDatabase.child(owner).push().setValue(Noti);

                                                            adapter.notifyDataSetChanged();
                                                            setOrderCount("Delivery Worker", mUser.getUid());
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) { }
                                                    });
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                                    builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                                }
                            });
                            datalist.add(data);
                        }
                    };
                    if (datalist.isEmpty()) {
                        userRecycler.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        userRecycler.setAdapter(adapter);
    }

    // GET ORDERS COUNT FOR MY PROFILE
    public void setOrderCount (String uType, String uID) {
        if (uType.equals("Supplier")){
            // Get Orders Count for Supplier
            final TextView txtTotalOrders = findViewById(R.id.txtTotalOrders);
            Query query = mDatabase.orderByChild("uId").equalTo(uID);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        String strCount = String.valueOf(count);
                        txtTotalOrders.setText( "اضاف "+ strCount + " اوردر");
                    } else {
                        txtTotalOrders.setText("لم يقم بأضافه اي اوردرات");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);
        } else if (uType.equals("Delivery Worker")) {
            //GET order count for Delivery
            final TextView txtTotalOrders = findViewById(R.id.txtTotalOrders);
            Query query = mDatabase.orderByChild("uAccepted").equalTo(uID);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        String strCount = String.valueOf(count);
                        txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                    } else {
                        txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    public static class myviewholder extends RecyclerView.ViewHolder {
        View myview;
        Button btnEdit,btnDelete,btnInfo,btnDelivered,btnRate,btnRecived;
        TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate;
        LinearLayout linerDate;
        RatingBar drStar;
        ImageView icnCar,icnMotor,icnMetro,icnTrans;

        public myviewholder(@NonNull View itemView) {
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

            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
        }

        void setUsername(String currentUser, final String orderOwner, final String DName, String uType){
            final TextView mtitle = myview.findViewById(R.id.txtUsername);
                    if (uType.equals("Supplier")) {
                        mtitle.setText(DName);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(orderOwner).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mtitle.setText(dataSnapshot.child("name").getValue().toString());
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
            String valid_until = ddate;
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            Date yesterday = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
            Date strDate = null;
            try { strDate = sdf2.parse(valid_until); } catch (ParseException e) { e.printStackTrace(); }
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
                    DatabaseReference mRef;
                    mRef = getInstance().getReference("Pickly").child("users").child(uAccepted.toString());
                        mRef.addValueEventListener(new ValueEventListener() {
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
                            public void onCancelled(DatabaseError databaseError) { }
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
                    switch (statue) {
                        case "delivered" : {
                            btnRate.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        public void setDilveredButton(final String state) {
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uType = snapshot.child("accountType").getValue().toString();
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
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        public void setOrderto(String orderto){
            TextView mtitle=myview.findViewById(R.id.orderto);
            mtitle.setText(orderto);
        }

        public void setDate (String date){
            TextView mdate= myview.findViewById(R.id.date);
            mdate.setText(date);
        }
        public void setOrdercash(String ordercash){
            TextView mtitle=myview.findViewById(R.id.ordercash);
            mtitle.setText(ordercash + " ج");
        }
        public void setFee(String fees) {
            TextView mtitle=myview.findViewById(R.id.fees);
            mtitle.setText(fees + " ج");
        }
        public void setAccepted() {
            final Button btnEdit = myview.findViewById(R.id.btnEdit);
            final Button btnDilvered = myview.findViewById(R.id.btnDelivered);
            final Button btnInfo = myview.findViewById(R.id.btnInfo);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uType = snapshot.child("accountType").getValue().toString();
                    if (uType.equals("Supplier")) {
                        //btnEdit.setVisibility(View.VISIBLE);
                        btnDilvered.setVisibility(View.GONE);
                        btnInfo.setVisibility(View.GONE);
                    } else {
                        //btnEdit.setVisibility(View.GONE);
                        //btnDilvered.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
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
                finalDate = "منذ " + String.valueOf(dS) + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + String.valueOf(dM) + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + String.valueOf(dH) + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " + String.valueOf(dD) + " ايام";
            }
            mtitle.setText(finalDate);
        }
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(profile.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(profile.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(profile.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(profile.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
