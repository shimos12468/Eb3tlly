package com.armjld.eb3tly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends Activity {

    EditText password , con_password,old_pass;
    Button confirm;
    String pass , con_pass,oldd;
    private FirebaseAuth mAuth;
    String oldPass = "";
    private ProgressDialog mdialog;
    String TAG = "Change Password";
    private DatabaseReference uDatabase;

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

        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                AuthCredential credential2 = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(),oldPass); // Current Login Credentials \\
                mAuth.getCurrentUser().reauthenticate(credential2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ------------------- Code for changing the password -------------//
                        if (!pass.isEmpty()) {
                        mAuth.getCurrentUser().updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    uDatabase.child(mAuth.getCurrentUser().getUid()).child("mpass").setValue(password.getText().toString().trim());
                                    Log.i(TAG, "pass Updated : " + password.getText().toString().trim() + " and current user id : " + mAuth.getCurrentUser().getUid());
                                    mdialog.dismiss();
                                    finish();
                                    whichProfile();
                                } else {
                                    mdialog.dismiss();
                                    Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                            mdialog.dismiss();
                            Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void whichProfile () {
        if(StartUp.userType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}
