package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import com.armjld.eb3tly.ui.main.Account_Confirm;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NewProfile extends AppCompatActivity {

    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP,imgStar, imgVerf;
    private TextView txtUserDate;
    private TextView uName;
    private TextView txtNotiCount,txtTotalOrders;
    private ConstraintLayout constNewProfile;
    private String TAG = "Delivery Profile";
    String uType = UserInFormation.getAccountType();
    String uId;
    String user_type;
    String isConfirmed = UserInFormation.getisConfirm();


    public NewProfile() { }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((NewProfile)this).getSystemService(Context.VIBRATOR_SERVICE);

        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        constNewProfile = findViewById(R.id.constNewProfile);
        imgVerf = findViewById(R.id.imgVerf);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = UserInFormation.getId();

        if(isConfirmed.equals("false")) {
            Snackbar snackbar = Snackbar.make(constNewProfile, "لم تقم بتأكيد حسابك بعد", LENGTH_INDEFINITE).setAction("تأكيد الحساب", view -> {
                finish();
                startActivity(new Intent(this, Account_Confirm.class));
            });
            snackbar.getView().setBackgroundColor(Color.RED);
            snackbar.show();
        } else if (isConfirmed.equals("pending")) {
            Snackbar snackbar = Snackbar.make(constNewProfile, "جاري التحقق من بيانات حسابك", LENGTH_INDEFINITE).setTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(Color.YELLOW);
            snackbar.show();
        } else if(isConfirmed.equals("true")) {
            imgVerf.setVisibility(View.VISIBLE);
        }

        imgVerf.setOnClickListener(v -> {
            Toast.makeText(this, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        ImageView btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        ConstraintLayout constNoti = findViewById(R.id.constNoti);
        ImageView btnOpenNoti = findViewById(R.id.btnOpenNoti);
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        imgStar = findViewById(R.id.imgStar);
        imgSetPP = findViewById(R.id.imgPPP);
        txtNotiCount = findViewById(R.id.txtNotiCount);

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردراتي");
        txtNotiCount.setVisibility(View.GONE);

        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_timeline, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        constNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            finish();
            startActivity(new Intent(NewProfile.this, Notifications.class));
        });

        btnNavbarProfile.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        btnOpenNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            finish();
            startActivity(new Intent(NewProfile.this, Notifications.class));
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
                startActivity(new Intent(NewProfile.this, About.class));
            }
            if (id == R.id.nav_signout) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mAuth.signOut();
                }
                finish();
                startActivity(new Intent(NewProfile.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
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
        if (!isFinishing() && UserInFormation.getUserURL()!= null) {
            Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);
        }
        TextView usType = findViewById(R.id.txtUserType);
        user_type = "dId";
        usType.setText("مندوب شحن");
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_how).setVisible(false);
        getRatings();
        getOrderCountDel();

    }

    public void getRatings() {
        RatingBar rbProfile = findViewById(R.id.rbProfile);
        rDatabase.child(uId).orderByChild(user_type).equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    long total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long rating = (long) Double.parseDouble(Objects.requireNonNull(ds.child("rate").getValue()).toString());
                        total = total + rating;
                    }
                    double average = (double) total / dataSnapshot.getChildrenCount();
                    Log.i(TAG, "Average Before : " +average);
                    if(String.valueOf(average).equals("NaN")) {
                        average = 5;
                        Log.i(TAG, "Average Midel : " + average);
                    }
                    Log.i(TAG, "Average Final : " + average);
                    rbProfile.setRating((int) average);
                } else {
                    rbProfile.setRating((int) 5);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getOrderCountDel() {
        mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cOrders = 0;
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    cOrders = count;
                    String strCount = String.valueOf(count);
                    txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                } else {
                    cOrders = 0;
                    txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                }

                if(cOrders >= 10) {
                    uName.setTextColor(Color.parseColor("#ffc922"));
                    imgStar.setVisibility(View.VISIBLE);
                } else {
                    uName.setTextColor(Color.WHITE);
                    imgStar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}