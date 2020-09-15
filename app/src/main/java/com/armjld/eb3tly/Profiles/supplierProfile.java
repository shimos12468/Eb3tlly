package com.armjld.eb3tly.Profiles;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.armjld.eb3tly.Chat.Chats;
import com.armjld.eb3tly.MyLocation;
import com.armjld.eb3tly.Utilites.About;
import com.armjld.eb3tly.Utilites.Conatact;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.Utilites.HowTo;
import com.armjld.eb3tly.main.Login_Options;
import com.armjld.eb3tly.main.MainActivity;
import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.Passaword.ChangePassword;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Utilites.UserSetting;
import com.armjld.eb3tly.confermations.Account_Confirm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.armjld.eb3tly.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class supplierProfile extends AppCompatActivity {
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP, imgStar,imgVerf, btnChats;
    private TextView txtUserDate,uName,txtNotiCount;
    private String TAG = "Supplier Profile";
    String uType = UserInFormation.getAccountType();
    private ConstraintLayout constSupProfile;
    private String uId = UserInFormation.getId();
    private ViewPager view_pager;
    String user_type;
    private ProgressDialog mdialog;
    private String isConfirmed;

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_profile);

        RatingBar rbProfile = findViewById(R.id.rbProfile);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((supplierProfile)this).getSystemService(Context.VIBRATOR_SERVICE);

        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        btnChats = findViewById(R.id.btnChats);
        constSupProfile= findViewById(R.id.constSupProfile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        mdialog = new ProgressDialog(this);
        assert mUser != null;
        isConfirmed = UserInFormation.getisConfirm();


        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        rbProfile.setRating(UserInFormation.getRating());
        ImageView btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        ConstraintLayout constNoti = findViewById(R.id.constNoti);
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        imgSetPP = findViewById(R.id.imgPPP);
        txtNotiCount = findViewById(R.id.txtNotiCount);
        imgStar = findViewById(R.id.imgStar);
        imgVerf= findViewById(R.id.imgVerf);

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردراتي");

        btnChats.setOnClickListener(v-> {
            startActivity(new Intent(this, Chats.class));
        });

        if(isConfirmed.equals("true")) {
            imgVerf.setVisibility(View.VISIBLE);
        }

        imgVerf.setOnClickListener(v -> {
            Toast.makeText(this, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
        });

        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_timeline, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        constNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            finish();
            startActivity(new Intent(supplierProfile.this, Notifications.class));
        });

        btnNavbarProfile.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        // Navigation Bar Buttons Function
        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_timeline) {
                newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntentNB);
                finish();
            }
            if (id==R.id.nav_profile){
                whichProfile();
            }
            if (id == R.id.nav_info) {
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
            if (id == R.id.nav_share) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
            }
            if (id==R.id.nav_about){
                startActivity(new Intent(supplierProfile.this, About.class));
            }
            if (id == R.id.nav_signout) {
                signOut();
            }
            if (id==R.id.nav_exit){
                finishAffinity();
                System.exit(0);
            }
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        });

        // -------------------------- Get users Notifications Count -------------------//
        nDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int notiCount = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists() && ds.child("isRead").exists()) {
                            if(Objects.equals(ds.child("isRead").getValue(), "false")) {
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

        txtUserDate.setText("اشترك : " + UserInFormation.getUserDate());
        uName.setText(UserInFormation.getUserName());
        if (!isFinishing() && UserInFormation.getUserURL() != null) {
            Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);
        }
        TextView usType = findViewById(R.id.txtUserType);
        usType.setText("تاجر");
        getOrderCountSup();

        btnAdd.setOnClickListener(v -> {
            vibe.vibrate(40);
            mdialog.setMessage("جاري التاكد من اتصال الانترنت ..");
            mdialog.show();
            finish();
            //startActivity(new Intent(this, MyLocation.class));
            vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if(Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString().equals("false")) {
                            Toast.makeText(supplierProfile.this, "عذرا لا يمكنك اضافه اوردرات في الوقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(new Intent(supplierProfile.this, AddOrders.class));
                        }
                        mdialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        });
    }

    private void getOrderCountSup() {
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cOrders = 0;
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        if(!ds.child("statue").getValue().toString().equals("deleted")) {
                            cOrders++;
                        }
                    }
                }

                if(cOrders >= 10) {
                    uName.setTextColor(Color.parseColor("#ffc922"));
                    imgStar.setVisibility(View.VISIBLE);
                } else {
                    uName.setTextColor(Color.BLACK);
                    imgStar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }

    private void signOut() {
        uDatabase.child(uId).child("device_token").setValue("");
        finish();
        mAuth.signOut();
        if(Login_Options.mGoogleSignInClient != null) {
            Login_Options.mGoogleSignInClient.signOut();
        }
        startActivity(new Intent(this, Login_Options.class));
        Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }
}