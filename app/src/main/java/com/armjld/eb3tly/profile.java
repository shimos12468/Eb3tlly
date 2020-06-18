package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Objects;
import Model.rateData;
import Model.Data;
import Model.notiData;
import Model.userData;

import static com.google.firebase.database.FirebaseDatabase.*;

public class profile extends AppCompatActivity {

    private DatabaseReference uDatabase, mDatabase, rDatabase, nDatabase, vDatabase;
    private RecyclerView userRecycler;
    private FirebaseAuth mAuth;
    private NavigationMenuItemView item;
    private FloatingActionButton btnAdd;
    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList datalist;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<String>();
    private String user_type = "";
    private ImageView btnNavbarProfile, imgSetPP, btnOpenNoti;
    private EditText PAddress, PShop, DAddress, DDate, DPhone, DName, GMoney, GGet, txtNotes;
    private CheckBox chkMetro, chkTrans, chkCar, chkMotor;
    private Spinner spPState, spPRegion, spDState, spDRegion;
    private TextView txtUserDate, tbTitle, uName, txtNoOrders;
    private String TAG = "Profile";
    Toolbar toolbar_home = null;

    private static final int PHONE_CALL_CODE = 100;

    String uType = "";
    FirebaseRecyclerAdapter<Data, myviewholder> adapter;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخزل", Toast.LENGTH_SHORT).show();
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

        //btnSettings = findViewById(R.id.btnSettings);
        btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        btnOpenNoti = findViewById(R.id.btnOpenNoti);
        datalist = new ArrayList<Data>();
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        imgSetPP = findViewById(R.id.imgPPP);


        //Title Bar
        tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("الملف الشخصي");

        txtNoOrders.setVisibility(View.GONE);

        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_timeline, R.id.nav_signout, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();

        btnNavbarProfile.setOnClickListener(new View.OnClickListener() {
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

        // -------------------------- Get user info for profile
        uDatabase.child(uID).addValueEventListener(new ValueEventListener() {
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
        uDatabase.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uType = snapshot.child("accountType").getValue().toString();
                TextView usType = findViewById(R.id.txtUserType);
                if (uType.equals("Supplier")) {
                    usType.setText("تاجر / موزع");
                    txtNoOrders.setText("لم تقم باضافه اي اوردرات حتي الان");
                    user_type = "sId";
                    final RatingBar rbProfile = findViewById(R.id.rbProfile);
                    rDatabase.child(uID).orderByChild(user_type).equalTo(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long total = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                total = total + rating;
                            }
                            double average = (double) total / dataSnapshot.getChildrenCount();
                            rbProfile.setRating((int) average);
                            Log.i(TAG, String.valueOf(average));
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
                    rDatabase.child(uID).orderByChild(user_type).equalTo(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long total = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                total = total + rating;
                            }
                            double average = (double) total / dataSnapshot.getChildrenCount();
                            rbProfile.setRating((int) average);
                            Log.i(TAG, String.valueOf(average));
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
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uID).addValueEventListener(new ValueEventListener() {
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
                            Toast.makeText(profile.this, "عذرا لا يمكنك اضافه اوردرات في الةقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            AlertDialog.Builder mydialog = new AlertDialog.Builder(profile.this);
                            LayoutInflater inflater = LayoutInflater.from(profile.this);
                            View myview = inflater.inflate(R.layout.cusominputfield, null);
                            mydialog.setView(myview);
                            final AlertDialog dialog = mydialog.create();
                            dialog.show();

                            // Toolbar
                            TextView toolbar_title = myview.findViewById(R.id.toolbar_title);
                            toolbar_title.setText("اضافة اوردر جديد");
                            ImageView btnClose = myview.findViewById(R.id.btnClose);

                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            // Texts
                            PAddress = myview.findViewById(R.id.txtPAddress);
                            PShop = myview.findViewById(R.id.txtPShop);
                            DAddress = myview.findViewById(R.id.txtDAddress);
                            DDate = myview.findViewById(R.id.txtDDate);
                            DPhone = myview.findViewById(R.id.txtDPhone);
                            DName = myview.findViewById(R.id.txtDName);
                            GMoney = myview.findViewById(R.id.txtGMoney);
                            GGet = myview.findViewById(R.id.txtGGet);
                            txtNotes = myview.findViewById(R.id.txtNotes);

                            //Check Boxes
                            chkMetro = myview.findViewById(R.id.chkMetro);
                            chkCar = myview.findViewById(R.id.chkCar);
                            chkMotor = myview.findViewById(R.id.chkMotor);
                            chkTrans = myview.findViewById(R.id.chkTrans);

                            //Spinners
                            spPState = (Spinner) myview.findViewById(R.id.txtPState);
                            spPRegion = (Spinner) myview.findViewById(R.id.txtPRegion);
                            spDState = (Spinner) myview.findViewById(R.id.txtDState);
                            spDRegion = (Spinner) myview.findViewById(R.id.txtDRegion);

                            Button btnsave = myview.findViewById(R.id.btnSave);

                            // Pick up Government Spinner
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(profile.this, R.array.txtStates, R.layout.color_spinner_layout);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPState.setPrompt("اختار المحافظة");
                            spPState.setAdapter(adapter2);
                            // Get the Government Regions
                            spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    int itemSelected = spPState.getSelectedItemPosition();
                                    if (itemSelected == 0) {
                                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                                        spPRegion.setAdapter(adapter4);
                                    } else if (itemSelected == 1) {
                                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                                        spPRegion.setAdapter(adapter4);
                                    } else if (itemSelected == 2) {
                                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                                        spPRegion.setAdapter(adapter4);
                                    } else if (itemSelected == 3) {
                                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spPRegion.setPrompt("اختار محطة المترو");
                                        spPRegion.setAdapter(adapter4);
                                    } else {
                                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.justAll, R.layout.color_spinner_layout);
                                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spPRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                                        spPRegion.setAdapter(adapter4);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                            // Drop Government Spinner
                            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(profile.this, R.array.txtStates, R.layout.color_spinner_layout);
                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDState.setPrompt("اختار المحافظة");
                            spDState.setAdapter(adapter3);
                            // Get the Government Regions
                            spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    int itemSelected = spDState.getSelectedItemPosition();
                                    if (itemSelected == 0) {
                                        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                                        spDRegion.setAdapter(adapter5);
                                    } else if (itemSelected == 1) {
                                        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                                        spDRegion.setAdapter(adapter5);
                                    } else if (itemSelected == 2) {
                                        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                                        spDRegion.setAdapter(adapter5);
                                    } else if (itemSelected == 3) {
                                        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spDRegion.setPrompt("اختار محطة المترو");
                                        spDRegion.setAdapter(adapter5);
                                    } else {
                                        spDRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                                        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.justAll, R.layout.color_spinner_layout);
                                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spDRegion.setAdapter(adapter5);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                            // ---------------------------------- Date Picker
                            final Calendar myCalendar = Calendar.getInstance();
                            final DatePickerDialog.OnDateSetListener pdate = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                    updateLabel();
                                }

                                private void updateLabel() {
                                    String dFormat = "yyyy-MM-dd";
                                    SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.US);
                                    DDate.setText(sDF.format(myCalendar.getTime()));
                                }
                            };

                            DDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatePickerDialog dpd = new DatePickerDialog(profile.this, pdate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                            myCalendar.get(Calendar.DAY_OF_MONTH));
                                    DatePicker dp = dpd.getDatePicker();
                                    dp.setMinDate(myCalendar.getTimeInMillis() - 100); // disable all the previos dates
                                    dpd.show();
                                }
                            });

                            // SAVE BUTTON
                            btnsave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Txt Fields Strings
                                    final String mPAddress = PAddress.getText().toString();
                                    final String mNote = txtNotes.getText().toString();
                                    final String mPShop = PShop.getText().toString();
                                    final String mDAddress = DAddress.getText().toString();
                                    final String mDDate = DDate.getText().toString();
                                    final String mDPhone = DPhone.getText().toString();
                                    final String mDName = DName.getText().toString();
                                    final String mGMoney = GMoney.getText().toString();
                                    final String mGGet = GGet.getText().toString();

                                    // Checkboxes Strings
                                    String isTrans = "";
                                    String isMotor = "";
                                    String isMetro = "";
                                    String isCar = "";

                                    //DEFULT ORDER States ON ADD
                                    final String states = "placed";
                                    final String uAccepted = "";

                                    // Check if Empty
                                    if (TextUtils.isEmpty(mPAddress) && !spPState.getSelectedItem().toString().equals("مترو")) {
                                        PAddress.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mDAddress) && !spDState.getSelectedItem().toString().equals("مترو")) {
                                        DAddress.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mDDate)) {
                                        DDate.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mDPhone)) {
                                        DPhone.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mDName)) {
                                        DName.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mGMoney)) {
                                        GMoney.setError("الرجاء ادخال البيانات");
                                        return;
                                    }
                                    if (TextUtils.isEmpty(mGGet)) {
                                        GGet.setError("الرجاء ادخال البيانات");
                                        return;
                                    }

                                    // Check the way of transportation
                                    if (chkTrans.isChecked()) {
                                        isTrans = "مواصلات";
                                    } else {
                                        isTrans = "";
                                    }
                                    if (chkMetro.isChecked()) {
                                        isMetro = "مترو";
                                    } else {
                                        isMetro = "";
                                    }
                                    if (chkCar.isChecked()) {
                                        isCar = "سياره";
                                    } else {
                                        isCar = "";
                                    }
                                    if (chkMotor.isChecked()) {
                                        isMotor = "موتسكل";
                                    } else {
                                        isMotor = "";
                                    }

                                    final String finalIsMetro = isMetro;
                                    final String finalIsTrans = isTrans;
                                    final String finalIsMotor = isMotor;
                                    final String finalIsCar = isCar;
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface confirmDailog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    String id = mDatabase.push().getKey().toString(); // create Order ID
                                                    String srate = "false";
                                                    String srateid = "";
                                                    String drate = "false";
                                                    String drateid = "";
                                                    // Send order to Data Base using the DATA MODEL
                                                    Data data = new Data(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), mPAddress, mPShop, spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                                            mDPhone, mDName, mGMoney, mGGet, datee, id, uID, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, "", "", mNote);
                                                    mDatabase.child(id).setValue(data);
                                                    mDatabase.child(id).child("lastedit").setValue(datee);
                                                    Toast.makeText(profile.this, "تم اضافة اوردرك و في انتظار قبولة من مندوبين الشحن", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    //No button clicked
                                                    break;
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                                    builder.setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                                    setOrderCount("Supplier", uID);
                                }
                            });
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

        uDatabase.child(uID).addValueEventListener(new ValueEventListener() {
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

        uDatabase.child(uID).addValueEventListener(new ValueEventListener() {
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
                                                    mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            snapshot.getRef().removeValue();
                                                            String idAccepted = snapshot.child("uAccepted").getValue().toString();
                                                            Toast.makeText(getApplicationContext(), "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                                            setOrderCount("Supplier", mUser.getUid());
                                                            // --------------------------- Send Notifications ---------------------//
                                                            notiData Noti = new notiData(mUser.getUid().toString(), idAccepted,orderID,"deleted",datee,"false");
                                                            nDatabase.child(idAccepted).push().setValue(Noti);
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
                                    AlertDialog.Builder editDialog = new AlertDialog.Builder(profile.this);
                                    LayoutInflater inflater = LayoutInflater.from(profile.this);
                                    View editView = inflater.inflate(R.layout.cusominputfield, null);
                                    editDialog.setView(editView);
                                    final AlertDialog dialog = editDialog.create();
                                    dialog.show();

                                    // Toolbar
                                    TextView toolbar_title = editView.findViewById(R.id.toolbar_title);
                                    toolbar_title.setText("تعديل الاوردر");
                                    ImageView btnClose = editView.findViewById(R.id.btnClose);

                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    // Texts
                                    PAddress = editView.findViewById(R.id.txtPAddress);
                                    PAddress.setText(data.getmPAddress().toString());

                                    PShop = editView.findViewById(R.id.txtPShop);
                                    PShop.setText(data.getmPShop().toString());

                                    DAddress = editView.findViewById(R.id.txtDAddress);
                                    DAddress.setText(data.getDAddress().toString());

                                    DDate = editView.findViewById(R.id.txtDDate);
                                    DDate.setText(data.getDDate().toString());

                                    DPhone = editView.findViewById(R.id.txtDPhone);
                                    DPhone.setText(data.getDPhone().toString());

                                    DName = editView.findViewById(R.id.txtDName);
                                    DName.setText(data.getDName().toString());

                                    GMoney = editView.findViewById(R.id.txtGMoney);
                                    GMoney.setText(data.getGMoney().toString());

                                    GGet = editView.findViewById(R.id.txtGGet);
                                    GGet.setText(data.getGGet().toString());

                                    txtNotes = editView.findViewById(R.id.txtNotes);
                                    txtNotes.setText(data.getNotes());

                                    //Check Boxes
                                    chkMetro = editView.findViewById(R.id.chkMetro);
                                    chkCar = editView.findViewById(R.id.chkCar);
                                    chkMotor = editView.findViewById(R.id.chkMotor);
                                    chkTrans = editView.findViewById(R.id.chkTrans);

                                    if(!data.getIsMetro().equals("")) {
                                        chkMetro.setChecked(true);
                                    }
                                    if(!data.getIsCar().equals("")) {
                                        chkCar.setChecked(true);
                                    }
                                    if(!data.getIsMotor().equals("")) {
                                        chkMotor.setChecked(true);
                                    }
                                    if(!data.getIsTrans().equals("")) {
                                        chkTrans.setChecked(true);
                                    }


                                    //Spinners
                                    spPState = (Spinner) editView.findViewById(R.id.txtPState);
                                    spPRegion = (Spinner) editView.findViewById(R.id.txtPRegion);
                                    spDState = (Spinner) editView.findViewById(R.id.txtDState);
                                    spDRegion = (Spinner) editView.findViewById(R.id.txtDRegion);

                                    Button btnsave = editView.findViewById(R.id.btnSave);
                                    btnsave.setText("تعديل الاوردر");

                                    // Pick up Government Spinner
                                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(profile.this, R.array.txtStates, R.layout.color_spinner_layout);
                                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spPState.setPrompt("اختار المحافظة");
                                    spPState.setAdapter(adapter2);
                                    spPState.setSelection(adapter2.getPosition(data.getTxtPState()));
                                    // Get the Government Regions
                                    spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            int itemSelected = spPState.getSelectedItemPosition();
                                            if (itemSelected == 0) {
                                                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                                                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                                                spPRegion.setAdapter(adapter4);
                                                spPRegion.setSelection(adapter4.getPosition(data.getmPRegion()));
                                            } else if (itemSelected == 1) {
                                                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                                                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                                                spPRegion.setAdapter(adapter4);
                                                spPRegion.setSelection(adapter4.getPosition(data.getmPRegion()));
                                            } else if (itemSelected == 2) {
                                                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                                                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                                                spPRegion.setAdapter(adapter4);
                                                spPRegion.setSelection(adapter4.getPosition(data.getmPRegion()));
                                            } else if (itemSelected == 3) {
                                                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                                                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spPRegion.setPrompt("اختار محطة المترو");
                                                spPRegion.setAdapter(adapter4);
                                                spPRegion.setSelection(adapter4.getPosition(data.getmPRegion()));
                                            } else {
                                                ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(profile.this, R.array.justAll, R.layout.color_spinner_layout);
                                                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spPRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                                                spPRegion.setAdapter(adapter4);
                                                spPRegion.setSelection(adapter4.getPosition(data.getmPRegion()));
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });

                                    // Drop Government Spinner
                                    ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(profile.this, R.array.txtStates, R.layout.color_spinner_layout);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spDState.setPrompt("اختار المحافظة");
                                    spDState.setAdapter(adapter3);
                                    spDState.setSelection(adapter3.getPosition(data.getTxtDState()));
                                    // Get the Government Regions
                                    spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            int itemSelected = spDState.getSelectedItemPosition();
                                            if (itemSelected == 0) {
                                                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                                                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                                                spDRegion.setAdapter(adapter5);
                                                spDRegion.setSelection(adapter5.getPosition(data.getmDRegion()));
                                            } else if (itemSelected == 1) {
                                                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                                                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                                                spDRegion.setAdapter(adapter5);
                                                spDRegion.setSelection(adapter5.getPosition(data.getmDRegion()));
                                            } else if (itemSelected == 2) {
                                                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                                                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                                                spDRegion.setAdapter(adapter5);
                                                spDRegion.setSelection(adapter5.getPosition(data.getmDRegion()));
                                            } else if (itemSelected == 3) {
                                                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                                                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spDRegion.setPrompt("اختار محطة المترو");
                                                spDRegion.setAdapter(adapter5);
                                                spDRegion.setSelection(adapter5.getPosition(data.getmDRegion()));
                                            } else {
                                                spDRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                                                ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(profile.this, R.array.justAll, R.layout.color_spinner_layout);
                                                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spDRegion.setAdapter(adapter5);
                                                spDRegion.setSelection(adapter5.getPosition(data.getmDRegion()));

                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });

                                    // ---------------------------------- Date Picker
                                    final Calendar myCalendar = Calendar.getInstance();
                                    final DatePickerDialog.OnDateSetListener pdate = new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                                            myCalendar.set(Calendar.YEAR, year);
                                            myCalendar.set(Calendar.MONTH, monthOfYear);
                                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                            updateLabel();
                                        }

                                        private void updateLabel() {
                                            String dFormat = "yyyy-MM-dd";
                                            SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.US);
                                            DDate.setText(sDF.format(myCalendar.getTime()));
                                        }
                                    };

                                    DDate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DatePickerDialog dpd = new DatePickerDialog(profile.this, pdate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                    myCalendar.get(Calendar.DAY_OF_MONTH));
                                            DatePicker dp = dpd.getDatePicker();
                                            dp.setMinDate(myCalendar.getTimeInMillis() - 100); // disable all the previos dates
                                            dpd.show();
                                        }
                                    });

                                    // SAVE BUTTON
                                    btnsave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Txt Fields Strings
                                            final String mPAddress = PAddress.getText().toString();
                                            final String mPShop = PShop.getText().toString();
                                            final String mDAddress = DAddress.getText().toString();
                                            final String mDDate = DDate.getText().toString();
                                            final String mDPhone = DPhone.getText().toString();
                                            final String mDName = DName.getText().toString();
                                            final String mGMoney = GMoney.getText().toString();
                                            final String mGGet = GGet.getText().toString();
                                            final String mNote = txtNotes.getText().toString();

                                            // Checkboxes Strings
                                            String isTrans = "";
                                            String isMotor = "";
                                            String isMetro = "";
                                            String isCar = "";

                                            //DEFULT ORDER States ON ADD
                                            final String states = data.getStatue();
                                            final String uAccepted = data.getuAccepted();

                                            // Check if Empty
                                            if (TextUtils.isEmpty(mPAddress) && !spPState.getSelectedItem().toString().equals("مترو")) {
                                                PAddress.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mDAddress) && !spDState.getSelectedItem().toString().equals("مترو")) {
                                                DAddress.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mDDate)) {
                                                DDate.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mDPhone)) {
                                                DPhone.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mDName)) {
                                                DName.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mGMoney)) {
                                                GMoney.setError("الرجاء ادخال البيانات");
                                                return;
                                            }
                                            if (TextUtils.isEmpty(mGGet)) {
                                                GGet.setError("الرجاء ادخال البيانات");
                                                return;
                                            }

                                            // Check the way of transportation
                                            if (chkTrans.isChecked()) {
                                                isTrans = "مواصلات";
                                            } else {
                                                isTrans = "";
                                            }
                                            if (chkMetro.isChecked()) {
                                                isMetro = "مترو";
                                            } else {
                                                isMetro = "";
                                            }
                                            if (chkCar.isChecked()) {
                                                isCar = "سياره";
                                            } else {
                                                isCar = "";
                                            }
                                            if (chkMotor.isChecked()) {
                                                isMotor = "موتسكل";
                                            } else {
                                                isMotor = "";
                                            }

                                            final String finalIsMetro = isMetro;
                                            final String finalIsTrans = isTrans;
                                            final String finalIsMotor = isMotor;
                                            final String finalIsCar = isCar;
                                            String id = data.getId();
                                            String srate = data.getSrated();
                                            String srateid = data.getSrateid();
                                            String drate = data.getDrated();
                                            String drateid = data.getDrateid();
                                            String orderDate = data.getDate();
                                            String acceptedTime = data.getAcceptedTime();
                                            // Send order to Data Base using the DATA MODEL
                                            Data data = new Data(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), mPAddress, mPShop, spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                                    mDPhone, mDName, mGMoney, mGGet, orderDate, id, uID, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, acceptedTime, "", mNote);
                                            mDatabase.child(id).setValue(data);
                                            mDatabase.child(id).child("lastedit").setValue(datee);

                                            // --------------------------- Send Notifications ---------------------//
                                            notiData Noti = new notiData(mUser.getUid().toString(), uAccepted,orderID,"edited",datee,"false");
                                            nDatabase.child(uAccepted).push().setValue(Noti);

                                            Toast.makeText(profile.this, "تم تعديل بيانات الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
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
                                    uDatabase.child(dilvID).addValueEventListener(new ValueEventListener() {
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
                                    rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long total = 0;
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                long rating = (long) Double.parseDouble(ds.child("rate").getValue().toString());
                                                total = total + rating;
                                            }
                                            double average = (double) total / dataSnapshot.getChildrenCount();
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
                                    mArraylistSectionLessons.clear(); // To not dublicate comments
                                    rDatabase.child(dilvID).orderByChild("dId").equalTo(dilvID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                int count = (int) dataSnapshot.getChildrenCount();
                                                String tempComment = data.child("comment").getValue().toString();
                                                Log.i(TAG, tempComment);
                                                if(!tempComment.equals("")) {
                                                    mArraylistSectionLessons.add(tempComment);
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


                            // -----------------------   Set ORDER as Delivered (Crashes after updating the data)
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
                                    notiData Noti = new notiData(mUser.getUid().toString(), SID,orderID,"delivered",datee,"false");
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
                                                    // Add the Profit of the Dilvery Worker
                                                    uDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String lastedit = data.getLastedit().toString();
                                                            String acceptedDate = data.getAcceptedTime();
                                                            String owner = data.getuId();

                                                            String now = datee;
                                                            int cancelledCount =  Integer.parseInt(dataSnapshot.child("canceled").getValue().toString());
                                                            int finalCount = (cancelledCount + 1);
                                                            int reminCount = 3 - cancelledCount - 1;

                                                            if(acceptedDate.compareTo(lastedit) == -1) { // if the worker accepted the order after editing it
                                                                uDatabase.child(uID).child("canceled").setValue(String.valueOf(finalCount));
                                                                Log.i(TAG, "Remining tries : " + reminCount);
                                                                Toast.makeText(profile.this, "تم حذف الاوردر بنجاح و تبقي لديك " + reminCount + " فرصه لالغاء الاوردرات هذا الاسبوع", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(profile.this, "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                                            }
                                                            mDatabase.child(DorderID).child("uAccepted").setValue("");
                                                            mDatabase.child(DorderID).child("acceptTime").setValue("");
                                                            mDatabase.child(DorderID).child("statue").setValue("placed");

                                                            // --------------------------- Send Notifications ---------------------//
                                                            notiData Noti = new notiData(mUser.getUid().toString(), owner, orderID,"deleted",datee,"false");
                                                            nDatabase.child(owner).push().setValue(Noti);

                                                            adapter.notifyDataSetChanged();
                                                            setOrderCount("Delivery Worker", mUser.getUid());
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
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
        TextView txtRate,txtGetStat;
        RatingBar drStar;

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
                        txtGetStat.setText("فات معاد تسلم اوردرك و لم يستمله اي مندوب, الرجاء تعديل معاد تسليم الاوردر او الغاءة");
                        txtGetStat.setTextColor(Color.RED);
                    } else {
                        txtGetStat.setEnabled(false);
                        txtGetStat.setVisibility(View.VISIBLE);
                        txtGetStat.setText("لم يتم استلام اوردرك");
                        txtGetStat.setTextColor(Color.RED);
                    }
                    break;
                }
                case "recived" :
                case "accepted": {
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setEnabled(true);
                    DatabaseReference mRef;
                    mRef = getInstance().getReference("Pickly").child("users").child(uAccepted);
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String mName = snapshot.child("name").getValue().toString();
                            if(getStatue.equals("recived")) {
                                txtGetStat.setText("تم استلام اوردرك من : " + mName);
                            } else {
                                txtGetStat.setText("تم قبول اوردرك من : " + mName);
                            }
                            txtGetStat.setTextColor(Color.YELLOW);
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
                    txtGetStat.setTextColor(Color.GREEN);
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
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
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
                                txtGetStat.setVisibility(View.GONE);
                                txtGetStat.setText("تواصل مع التاجر لاستلام الاوردر");
                                txtGetStat.setTextColor(Color.RED);
                                break;
                            }
                            case "recived" : {
                                txtGetStat.setVisibility(View.GONE);
                                btnDelete.setVisibility(View.GONE);
                                btnDelivered.setVisibility(View.VISIBLE);
                                btnInfo.setVisibility(View.VISIBLE);
                                txtGetStat.setText("تم استلام الاوردر من التاجر");
                                txtGetStat.setTextColor(Color.YELLOW);
                                break;
                            }
                            case "delivered" : {
                                btnDelivered.setVisibility(View.GONE);
                                btnDelete.setVisibility(View.GONE);
                                btnInfo.setVisibility(View.GONE);
                                txtGetStat.setText("تم توصيل الاوردر بنجاح");
                                txtGetStat.setTextColor(Color.GREEN);
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
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
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
            ImageView icnCar = myview.findViewById(R.id.icnCar);
            ImageView icnMotor = myview.findViewById(R.id.icnMotor);
            ImageView icnMetro = myview.findViewById(R.id.icnMetro);
            ImageView icnTrans = myview.findViewById(R.id.icnTrans);
            if (car.equals("سياره")) {
                icnCar.setVisibility(View.VISIBLE);
            } else {
                icnCar.setVisibility(View.INVISIBLE);
            }

            if(motor.equals("موتسكل")) {
                icnMotor.setVisibility(View.VISIBLE);
            } else {
                icnMotor.setVisibility(View.INVISIBLE);
            }

            if(metro.equals("مترو")) {
                icnMetro.setVisibility(View.VISIBLE);
            } else {
                icnMetro.setVisibility(View.INVISIBLE);
            }

            if (trans.equals("مواصلات")) {
                icnTrans.setVisibility(View.VISIBLE);
            } else {
                icnTrans.setVisibility(View.INVISIBLE);
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
