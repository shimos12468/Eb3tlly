package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class Admin extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase,mDatabase,rDatabase,vDatabase,nDatabase,reportDatabase;
    private EditText txtChild,txtValue,txtBody;
    private TextView txtAllOrdersCount,txtAllUsersCount,txtAllDevCount,txtAllSupCount,txtAllProfit;
    Button btnResetCounter,btnAddToUsers,btnAddToOrders,btnAccepting,btnAdding,btnAdminSignOut,btnReports,btnAddToComments,btnDeleteUser;
    Button btnMessages,btnSendNotficationDel, btnSendNotficationSup, btnReportss;
    ImageView btnRefresh,imgLogo;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<String>();
    int supCount = 0;
    int devCount = 0;
    int profitCount = 0;
    int usedUsers = 0;
    int isAcitve = 0;
    private ProgressDialog mdialog;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    int notCompleted = 0;
    String TAG = "Admin";

    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String datee = sdf.format(new Date());

    public void onBackPressed() { }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        vDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("values");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");

        btnResetCounter = findViewById(R.id.btnResetCounter);
        btnAddToOrders = findViewById(R.id.btnAddToOrders);
        btnAddToUsers = findViewById(R.id.btnAddToUsers);
        txtChild = findViewById(R.id.txtChild);
        txtValue = findViewById(R.id.txtValue);
        txtAllOrdersCount  = findViewById(R.id.txtAllOrdersCount);
        txtAllUsersCount  = findViewById(R.id.txtAllUsersCount);
        txtAllDevCount  = findViewById(R.id.txtAllDevCount);
        txtAllSupCount  = findViewById(R.id.txtAllSupCount);
        txtAllProfit = findViewById(R.id.txtAllProfit);
        btnAccepting = findViewById(R.id.btnAccepting);
        btnAdding = findViewById(R.id.btnAdding);
        btnAdminSignOut = findViewById(R.id.btnAdminSignOut);
        btnReports = findViewById(R.id.btnReports);
        btnAddToComments = findViewById(R.id.btnAddToComments);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnMessages = findViewById(R.id.btnMessages);
        btnRefresh = findViewById(R.id.btnRefresh);
        imgLogo = findViewById(R.id.imgLogo);
        btnReportss = findViewById(R.id.btnReportss);
        txtBody = findViewById(R.id.txtBody);
        btnSendNotficationDel = findViewById(R.id.btnSendNotficationDel);
        btnSendNotficationSup = findViewById(R.id.btnSendNotficationSup);
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");


        mdialog = new ProgressDialog(Admin.this);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Admin Panel");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        // Navigation Bar Buttons Function
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_timeline) {
                newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(newIntentNB);
            }
            if (id==R.id.nav_profile){
                startActivity(new Intent(getApplicationContext(), NewProfile.class));
            }
            if(id == R.id.nav_info) {
                startActivity(new Intent(getApplicationContext(), UserSetting.class));

            }
            if (id == R.id.nav_changepass) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
            }
            if (id == R.id.nav_how) {
                startActivity(new Intent(getApplicationContext(), HowTo.class));
            }
            if (id == R.id.nav_contact) {
                startActivity(new Intent(getApplicationContext(), Conatact.class));
            }
            if(id==R.id.nav_share){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(Admin.this, About.class));
            }
            if (id==R.id.nav_signout){
                finish();
                startActivity(new Intent(Admin.this, MainActivity.class));
                mAuth.signOut();
            }
            if (id==R.id.nav_exit){
                Admin.this.finish();
                System.exit(0);
            }
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        });

        btnReportss.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, AdminReports.class));
        });
        // ------------------------ Send notfication to all Delivery Workers ----------------------------//
        btnSendNotficationDel.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String theMsg = txtBody.getText().toString().trim();
                        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                        if(ds.exists() && ds.child("id").exists()) {
                                                String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                                if(Objects.requireNonNull(ds.child("accountType").getValue()).toString().equals("Delivery Worker")) {
                                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", userID, "-MAPQWoKEfmHIQG9xv-v", theMsg, datee, "false");
                                                nDatabase.child(userID).push().setValue(Noti);
                                            }
                                        }
                                    }
                                    txtBody.setText("");
                                    Toast.makeText(Admin.this, "تم ارسال الاشعار", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to send the notification to all Delivery Workers ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // ------------------------ Send notfication to all Suppliers ----------------------------//
        btnSendNotficationSup.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String theMsg = txtBody.getText().toString().trim();
                        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                        if(ds.exists() && ds.child("id").exists()) {
                                            String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            if(Objects.requireNonNull(ds.child("accountType").getValue()).toString().equals("Supplier")) {
                                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", userID, "-MAPQWoKEfmHIQG9xv-v", theMsg, datee, "false");
                                                nDatabase.child(userID).push().setValue(Noti);
                                            }
                                        }
                                    }
                                    txtBody.setText("");
                                    Toast.makeText(Admin.this, "تم ارسال الاشعار", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to send the notification to all Suppliers ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        imgLogo.setOnClickListener(v -> Toast.makeText(Admin.this, "I AM CEO, BITCH", Toast.LENGTH_SHORT).show());

        btnRefresh.setOnClickListener(v -> {
            mdialog.setMessage("Refreshing");
            mdialog.show();
            getStatics();
        });

        // -------------------------- Check the Reports ------------------------------------//
        btnReports.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder theReports = new AlertDialog.Builder(Admin.this);
            LayoutInflater inflater = LayoutInflater.from(Admin.this);
            View reportsView = inflater.inflate(R.layout.admin_reports, null);
            theReports.setView(reportsView);
            AlertDialog dialog = theReports.create();
            dialog.show();

            ListView listReports = (ListView) reportsView.findViewById(R.id.listReports);
            final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<>(Admin.this, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
            listReports.setAdapter(arrayAdapterLessons);
            mArraylistSectionLessons.clear();

            listReports.setOnItemClickListener((adapter, v1, position, arg3) -> {
                String value = (String)adapter.getItemAtPosition(position);
                Toast.makeText(Admin.this, value, Toast.LENGTH_SHORT).show();
            });

            rDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot data : ds.getChildren()) {
                            String reportedUser = ds.getKey();
                            String tempComment = Objects.requireNonNull(data.child("comment").getValue()).toString();
                            String isReported = Objects.requireNonNull(data.child("isReported").getValue()).toString();
                            if(!tempComment.equals("") && isReported.equals("true")) {
                                mArraylistSectionLessons.add(reportedUser + "/n " + tempComment);
                            }
                            arrayAdapterLessons.notifyDataSetChanged();
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        });

        // ------------------------- Delete Non Completed ---------------------------//
        btnDeleteUser.setOnClickListener(v -> {
            reportDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot user : snapshot.getChildren()) {
                        String userID = user.getKey();
                        for(DataSnapshot reports : user.getChildren()) {
                            String repoID = reports.getKey();
                            if(!reports.child("id").exists()) {
                                assert userID != null;
                                assert repoID != null;
                                reportDatabase.child(userID).child(repoID).child("id").setValue(repoID);
                                Toast.makeText(Admin.this, "Done Boss!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            /*uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists() && ds.child("id").exists()) {
                            String isCompleted = Objects.requireNonNull(ds.child("completed").getValue()).toString();
                            int deletedCount = 0;
                            if(isCompleted.equals("false")) {
                                ++deletedCount;
                                Log.i(TAG, " Users to Remove : " + ds.getValue());
                                ds.getRef().removeValue();
                            }
                            //Toast.makeText(Admin.this, "Deleted Non Completed : " + deletedCount + " Users", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int fuckedUp = 0;
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(!ds.child("id").exists()) {
                                fuckedUp ++;
                                ds.getRef().removeValue();
                            }
                        }
                    }
                    //Toast.makeText(Admin.this, "Just deleted " + fuckedUp + " Gletches", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int fuckedUp = 0;
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(!ds.child("ddate").exists()) {
                                fuckedUp ++;
                                ds.getRef().removeValue();
                            }
                        }
                    }
                    Toast.makeText(Admin.this, "Just deleted " + fuckedUp + " Gletches", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

            /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int fuckedUp = 0;
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(Objects.requireNonNull(ds.child("ddate").getValue()).toString().contains("(^\\h*)|(\\h*$)") || Objects.requireNonNull(ds.child("gmoney").getValue()).toString().contains("(^\\h*)|(\\h*$)") || Objects.requireNonNull(ds.child("gget").getValue()).toString().contains("(^\\h*)|(\\h*$)") || Objects.requireNonNull(ds.child("dphone").getValue()).toString().contains("(^\\h*)|(\\h*$)")) {
                                fuckedUp ++;
                                ds.getRef().removeValue();
                            }
                        }
                    }
                    //Toast.makeText(Admin.this, "Just deleted " + fuckedUp + " Gletches", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int ToBeDelv = 0;
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if(ds.exists() && ds.child("statue").exists()) {
                                String deliverDate = Objects.requireNonNull(ds.child("ddate").getValue()).toString();
                                Date orderDate = null;
                                Date myDate = null;
                                try {
                                    orderDate= format.parse(deliverDate);
                                    myDate =  format.parse(getYesterdayDate());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                assert orderDate != null;
                                if(orderDate.compareTo(myDate) < 0 && Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("accepted")) {
                                    String orderI = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                    mDatabase.child(orderI).child("statue").setValue("delivered");
                                    ToBeDelv++;
                                }
                            }
                        }
                    }
                    Toast.makeText(Admin.this, "Accepted But not Delv " + ToBeDelv, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/
        });

        // -------------------------- Signing Out of Admin Account -------------------------//
        btnAdminSignOut.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(Admin.this, MainActivity.class));
                        Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to Sign Out ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        btnMessages.setOnClickListener(v ->
                startActivity(new Intent(Admin.this, ReplyByAdmin.class))
        );

        // -------------------------- Reset the cancelled orders counter for delivery workers -------------------------//
        btnResetCounter.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int userCount = (int) dataSnapshot.getChildrenCount();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if(ds.exists() && ds.child("id").exists()) {
                                        String isCompleted = Objects.requireNonNull(ds.child("completed").getValue()).toString();
                                        if(isCompleted.equals("true")) {
                                            String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            uDatabase.child(userID).child("canceled").setValue("0");
                                            Toast.makeText(Admin.this, "Counter Reseted for : " + userCount + " Users", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to reset the Counter ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // ----------------------------------------------- ADD NEW CHILD TO ALL USERS -------------------------//
        btnAddToUsers.setOnClickListener(v -> {

            if(TextUtils.isEmpty(txtChild.getText().toString())){
                txtChild.setError("Can't Be EMPTY !!");
                return;
            }

            if(TextUtils.isEmpty(txtValue.getText().toString())){
                txtValue.setError("Can't Be EMPTY !!");
                return;
            }

            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int userCount = (int) dataSnapshot.getChildrenCount();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if(ds.exists() && ds.child("id").exists()) {
                                        if(Objects.equals(ds.child("completed").getValue(), "true")) {
                                            String userID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            uDatabase.child(userID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                            Toast.makeText(Admin.this, "Add Childs to : " + userCount + " Users", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to add this child to all users ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // -------------------------------------- ADD NEW CHILD TO ALL ORDERS ---------------------------------------//
        btnAddToOrders.setOnClickListener(v -> {
            if(TextUtils.isEmpty(txtChild.getText().toString())){
                txtChild.setError("Can't Be EMPTY !!");
                return;
            }
            if(TextUtils.isEmpty(txtValue.getText().toString())){
                txtValue.setError("Can't Be EMPTY !!");
                return;
            }

            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int userCount = (int) dataSnapshot.getChildrenCount();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String orderID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                    mDatabase.child(orderID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                    Toast.makeText(Admin.this, "Add Childs to : " + userCount + " Orders", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to add this child to all orders ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // -------------------------------------- ADD NEW CHILD TO ALL COMMENTS ---------------------------------------//
        btnAddToComments.setOnClickListener(v -> {
            if(TextUtils.isEmpty(txtChild.getText().toString())){
                txtChild.setError("Can't Be EMPTY !!");
                return;
            }
            if(TextUtils.isEmpty(txtValue.getText().toString())){
                txtValue.setError("Can't Be EMPTY !!");
                return;
            }

            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        rDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot data : dataSnapshot.getChildren()) {
                                    for(DataSnapshot ds : data.getChildren()) {
                                        int ratings = (int) ds.getChildrenCount();
                                        String userRateID = Objects.requireNonNull(data.getKey());
                                        Log.i(TAG, "User Id " + userRateID);
                                        String rateID = Objects.requireNonNull(ds.child("rId").getValue()).toString();
                                        Log.i(TAG, "Comment Id " + rateID);
                                        rDatabase.child(userRateID).child(rateID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                        Toast.makeText(Admin.this, "Added Childs to : " + ratings + " Comments", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to add this child to all comments ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // ----------------------------------- Disable / Enable Adding Orders ---------------------------//
        btnAdding.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString().equals("true")) {
                                    vDatabase.child("adding").setValue("false");
                                } else if(Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString().equals("false")) {
                                    vDatabase.child("adding").setValue("true");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are You Sure ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // -------------------------------------- Disable / Enable Accepting Orders ---------------------------//
        btnAccepting.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString().equals("true")) {
                                    vDatabase.child("accepting").setValue("false");
                                } else if(Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString().equals("false")) {
                                    vDatabase.child("accepting").setValue("true");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are You Sure ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });

        // ----------------------------

    }

    @Override
    protected void onStart () {
        super.onStart();
        // --------------------------------------- Changing Button name Depending on Values ---------------------------//
        vDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String isAccepting = Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString();
                String isAdding = Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString();

                if(isAccepting.equals("true")) {
                    btnAccepting.setText("Disable Accepting");
                } else if(isAccepting.equals("false")){
                    btnAccepting.setText("Enable Accepting");
                }
                if(isAdding.equals("true")) {
                    btnAdding.setText("Disable Adding");
                } else if(isAdding.equals("false")) {
                    btnAdding.setText("Enable Adding");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //-----------------------------
    }

    public void getStatics() {
        // -------------------------------------- Get users Counts --------------------------//
        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int allUsers = (int) dataSnapshot.getChildrenCount();
                    usedUsers = 0;
                    supCount = 0;
                    devCount = 0;
                    notCompleted = 0;
                    profitCount = 0;
                    isAcitve = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists() && ds.child("id").exists() ) {
                            String userType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();
                            int intProfit = (int) Integer.parseInt(Objects.requireNonNull(ds.child("profit").getValue()).toString());
                            profitCount = profitCount + intProfit;
                            if(intProfit > 0) {
                                ++usedUsers;
                            }
                            switch (userType) {
                                case "Supplier" : {
                                    ++supCount;
                                    mDatabase.orderByChild("uId").equalTo(Objects.requireNonNull(ds.child("id").getValue()).toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                ++isAcitve;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }

                                    });
                                    break;
                                }
                                case "Delivery Worker" : {
                                    ++devCount;
                                    break;
                                }
                            }
                        }
                    }
                    int forEach = 0;
                    if(usedUsers != 0) {
                        forEach = profitCount / usedUsers;
                    }
                    txtAllUsersCount.setText("Users Count : " + allUsers);
                    txtAllProfit.setText("Total Profit = " + profitCount + " EGP | " + forEach + " EGP For Each Active Delivery User");
                    txtAllSupCount.setText("Suppliers Count : " + supCount + " | Active Users : " + isAcitve);
                    txtAllDevCount.setText("Delivery Workers Count : " + devCount + " | Active Count : " + usedUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // ----------------------------------------- Get orders Counts --------------------------------//
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int allOrders = 0;
                int ordersWorth = 0;
                int acOrders = 0;
                int plOrders = 0;
                int deOrders = 0;
                int reOrders = 0;
                int DelWorth = 0;
                if(dataSnapshot.exists()) {
                    allOrders = (int) dataSnapshot.getChildrenCount();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists()) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            ordersWorth = ordersWorth + Integer.parseInt(orderData.getGMoney().replaceAll("(^\\h*)|(\\h*$)","").trim());
                            DelWorth = DelWorth + Integer.parseInt(orderData.getGGet().replaceAll("(^\\h*)|(\\h*$)","").trim());
                            switch (orderData.getStatue()) {
                                case "placed":
                                    plOrders++;
                                    break;
                                case "accepted":
                                    acOrders++;
                                    break;
                                case "recived":
                                    reOrders++;
                                    break;
                                case "delivered":
                                    deOrders++;
                                    break;
                            }
                        }
                    }
                }
                txtAllOrdersCount.setText("We Have " + allOrders + " Orders in Our System | Worth : " + ordersWorth + " EGP | Delv Fees " + DelWorth + " EGP | " + plOrders + " Placed | " + acOrders + " Accepted | " + reOrders + " Recived | " + deOrders + " Delivered." );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        Toast.makeText(Admin.this, "Refreshed", Toast.LENGTH_SHORT).show();
        mdialog.dismiss();
    }

    public static String getYesterdayDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }
}
