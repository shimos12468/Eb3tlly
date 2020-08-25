package com.armjld.eb3tly.Passaword;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.eb3tly.main.MainActivity;
import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangePassword extends Activity {

    EditText password , con_password,old_pass;
    Button confirm;
    String pass , con_pass,oldd;
    private FirebaseAuth mAuth;
    String oldPass = "";
    private ProgressDialog mdialog;
    String TAG = "Change Password";
    private DatabaseReference uDatabase;
    private String uId = UserInFormation.getId();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        password = findViewById(R.id.txtEditPassword);
        con_password = findViewById(R.id.txtEditPassword2);
        old_pass = findViewById(R.id.txtOldPassword);
        confirm = findViewById(R.id.btnEditInfo);
        mdialog = new ProgressDialog(this);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تغيير الرقم السري");

        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oldPass = dataSnapshot.child("mpass").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass = password.getText().toString();
                con_pass = con_password.getText().toString();
                oldd = old_pass.getText().toString();

                if(TextUtils.isEmpty(con_pass)){
                    con_password.setError("يجب اعاده ادخال كلمه المرور");
                    return;
                }
                if(!pass.equals(con_pass)){
                    con_password.setError("تاكد ان كلمه المرور نفسها");
                    return;
                }
                if (!oldd.equals(oldPass)) {
                    Toast.makeText(ChangePassword.this, "ادخلت كلمه مرور خاطئة", Toast.LENGTH_LONG).show();
                    old_pass.setText("");
                    return;
                }
                mdialog.setMessage("جاري تغيير الرقم السري ...");
                mdialog.show();

                AuthCredential credential2 = EmailAuthProvider.getCredential(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()),oldPass); // Current Login Credentials \\
                mAuth.getCurrentUser().reauthenticate(credential2).addOnCompleteListener(task -> {
                    // ------------------- Code for changing the password -------------//
                    if (!pass.isEmpty()) {
                    mAuth.getCurrentUser().updatePassword(pass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            uDatabase.child(uId).child("mpass").setValue(password.getText().toString().trim());
                            mdialog.dismiss();
                            finish();
                            whichProfile();
                        } else {
                            mdialog.dismiss();
                            Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                        mdialog.dismiss();
                        Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void whichProfile () {
        if(UserInFormation.getAccountType().equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}
