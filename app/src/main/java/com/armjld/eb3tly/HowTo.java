package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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