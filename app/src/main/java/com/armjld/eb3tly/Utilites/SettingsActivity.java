package com.armjld.eb3tly.Utilites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.armjld.eb3tly.LocationManeger.LocationForDelv;
import com.armjld.eb3tly.LocationManeger.LocationForSup;
import com.armjld.eb3tly.Passaword.ChangePassword;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Utilites.UserSetting;
import com.armjld.eb3tly.Wallet.MyWallet;
import com.armjld.eb3tly.main.Login_Options;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    TextView txtName,txtType,txtPhone;
    TextView txtUserSettings,txtPassSettings,txtNotiSettings,txtLocationSettings,txtWallet,txtReports,txtSignOut;
    ImageView imgPPP,btnBack;
    DatabaseReference uDatabase;
    FirebaseAuth mAuth;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        uId =  UserInFormation.getId();

        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        txtPhone = findViewById(R.id.txtPhone);
        imgPPP = findViewById(R.id.imgPPP);

        txtUserSettings = findViewById(R.id.txtUserSettings);
        txtPassSettings = findViewById(R.id.txtPassSettings);
        txtNotiSettings = findViewById(R.id.txtNotiSettings);
        txtLocationSettings = findViewById(R.id.txtLocationSettings);
        txtWallet = findViewById(R.id.txtWallet);
        txtReports = findViewById(R.id.txtReports);
        txtSignOut = findViewById(R.id.txtSignOut);
        btnBack = findViewById(R.id.btnBack);

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("الاعدادات");

        setUserData();

        txtUserSettings.setOnClickListener(v-> startActivity(new Intent(this, UserSetting.class)));
        txtPassSettings.setOnClickListener(v-> startActivity(new Intent(this, ChangePassword.class)));
        btnBack.setOnClickListener(v->finish());

        txtNotiSettings.setOnClickListener(v-> Toast.makeText(this, "Not Yet", Toast.LENGTH_SHORT).show());

        txtLocationSettings.setOnClickListener(v-> {
            if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(this, LocationForDelv.class));
            } else {
                startActivity(new Intent(this, LocationForSup.class));
            }
        });

        txtWallet.setOnClickListener(v-> {
            startActivity(new Intent(this, MyWallet.class));
        });

        txtReports.setOnClickListener(v-> {
            Toast.makeText(this, "Not Yet", Toast.LENGTH_SHORT).show();
        });

        txtSignOut.setOnClickListener(v-> {
            signOut();
        });

    }

    @SuppressLint("SetTextI18n")
    private void setUserData() {// ------------ Set User Data ----------- //
        String uType;
        txtName.setText(UserInFormation.getUserName());
        txtPhone.setText("+2" + UserInFormation.getPhone());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgPPP);
        if(UserInFormation.getAccountType().equals("Supplier")) {
            uType = "تاجر";
            txtWallet.setVisibility(View.GONE);
        } else if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            uType = "كابتن";
            txtWallet.setVisibility(View.VISIBLE);
        } else {
            uType = "خدمة عملاء";
            txtWallet.setVisibility(View.GONE);
        }
        txtType.setText(uType);
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