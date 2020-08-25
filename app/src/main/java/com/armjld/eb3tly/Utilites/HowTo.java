package com.armjld.eb3tly.Utilites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HowTo extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(HowTo.this, supplierProfile.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        Button btnGoToProfile = findViewById(R.id.btnGoToProfile);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("طريقة استعمال البرنامج");

        btnGoToProfile.setOnClickListener(v -> {
            finish();
            whichProfile();
        });
    }

    private void whichProfile() {
        if(UserInFormation.getAccountType().equals("Supplier")) {
            startActivity(new Intent(HowTo.this, supplierProfile.class));
        } else {
            startActivity(new Intent(HowTo.this, NewProfile.class));
        }
    }
}