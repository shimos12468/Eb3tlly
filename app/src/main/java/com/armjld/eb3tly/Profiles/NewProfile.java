package com.armjld.eb3tly.Profiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import com.armjld.eb3tly.Chat.Chats;
import com.armjld.eb3tly.Utilites.SettingsActivity;
import com.armjld.eb3tly.Utilites.About;
import com.armjld.eb3tly.Utilites.Conatact;
import com.armjld.eb3tly.Fragments.SectionsPagerAdapter;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.Utilites.HowTo;
import com.armjld.eb3tly.main.Login_Options;
import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.Passaword.ChangePassword;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Utilites.UserSetting;
import com.google.android.material.tabs.TabLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class NewProfile extends AppCompatActivity {

    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP,imgStar, imgVerf,btnChats,btnNavbarProfile,btnOpenNoti,btnSettings;
    private TextView txtUserDate,usType;
    private TextView uName;
    private TextView txtNotiCount;
    private ConstraintLayout constNewProfile;
    private String TAG = "Delivery Profile";
    RatingBar rbProfile;
    String uType = UserInFormation.getAccountType();
    String uId;
    String isConfirmed = UserInFormation.getisConfirm();


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((NewProfile)this).getSystemService(Context.VIBRATOR_SERVICE);

        //txtTotalOrders = findViewById(R.id.txtTotalOrders);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).keepSynced(true);
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        mAuth = FirebaseAuth.getInstance();

        ConstraintLayout constNoti = findViewById(R.id.constNoti);
        btnNavbarProfile = findViewById(R.id.btnNavbarProfile);
        btnOpenNoti = findViewById(R.id.btnOpenNoti);
        uName = findViewById(R.id.txtUsername);
        txtUserDate = findViewById(R.id.txtUserDate);
        imgStar = findViewById(R.id.imgStar);
        imgSetPP = findViewById(R.id.imgPPP);
        txtNotiCount = findViewById(R.id.txtNotiCount);
        rbProfile = findViewById(R.id.rbProfile);
        btnChats = findViewById(R.id.btnChats);
        constNewProfile = findViewById(R.id.constNewProfile);
        imgVerf = findViewById(R.id.imgVerf);
        ViewPager viewPager = findViewById(R.id.view_pager);
        txtNotiCount.setVisibility(View.GONE);
        usType = findViewById(R.id.txtUserType);
        btnSettings = findViewById(R.id.btnSettings);


        uId = UserInFormation.getId();
        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        NavigationView navigationView = findViewById(R.id.nav_view);

        txtUserDate.setText("اشترك : " + UserInFormation.getUserDate());
        tbTitle.setText("اوردراتي");
        usType.setText("كابتن");
        rbProfile.setRating(UserInFormation.getRating());
        uName.setText(UserInFormation.getUserName());
        if (!isFinishing() && UserInFormation.getUserURL()!= null) {
            Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        btnChats.setOnClickListener(v-> startActivityForResult(new Intent(this, Chats.class), 1));

        if(isConfirmed.equals("true")) {
            imgVerf.setVisibility(View.VISIBLE);
        }

        imgVerf.setOnClickListener(v -> Toast.makeText(this, "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show());

        constNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            startActivityForResult(new Intent(this, Notifications.class), 1);
        });



        btnOpenNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            startActivityForResult(new Intent(this, Notifications.class), 1);
        });

        btnSettings.setOnClickListener(v-> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        // NAV BAR
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        btnNavbarProfile.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_how).setVisible(false);

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
                signOut();
            }
            if (id==R.id.nav_exit){
                startActivity(new Intent(this, SettingsActivity.class));
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
    }

    /*private void getOrderCountDel() {
        mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cOrders = 0;
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    cOrders = count;
                    String strCount = String.valueOf(count);
                    //txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                } else {
                    cOrders = 0;
                    //txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                }

                if(cOrders >= 10) {
                    uName.setTextColor(Color.parseColor(String.valueOf(R.color.ic_profile_background)));
                    imgStar.setVisibility(View.VISIBLE);
                } else {
                    uName.setTextColor(R.color.colorAccent);
                    imgStar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

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

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
}