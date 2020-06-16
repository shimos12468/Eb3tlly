package com.armjld.eb3tly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    String TAG = "Change Password";
    private DatabaseReference uDatabase;

    public void onBackPressed () {
        finish();
        Intent newIntentNB = new Intent(this, profile.class);
        newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntentNB);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        password = findViewById(R.id.txtEditPassword);
        con_password = findViewById(R.id.txtEditPassword2);
        old_pass = findViewById(R.id.txtOldPassword);
        confirm = findViewById(R.id.btnEditInfo);

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
                pass = password.getText().toString().trim();
                con_pass = con_password.getText().toString().trim();
                oldd = old_pass.getText().toString().trim();

                if(TextUtils.isEmpty(con_pass)){
                    con_password.setError("يجب اعاده ادخال كلمه المرور");
                    return;
                }
                if(!pass.equals(con_pass)){
                    con_password.setError("تاكد ان كلمه المرور نفسها");
                    return;
                }
                if (!oldd.equals(oldPass)) {
                    Toast.makeText(ChangePassword.this, "ادخلت كلمه مرور خاطئة", Toast.LENGTH_SHORT).show();
                    old_pass.setText("");
                    return;
                }

                AuthCredential credential2 = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(),oldPass); // Current Login Credentials \\
                mAuth.getCurrentUser().reauthenticate(credential2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ------------------- Code for changing the password -------------//
                            mAuth.getCurrentUser().updatePassword(password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("mpass").setValue(password.getText().toString().trim());
                                        Log.i(TAG, "pass Updated : " + password.getText().toString().trim() + " and current user id : " + mAuth.getCurrentUser().getUid());
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), profile.class));
                                    } else {
                                        Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                });
            }
        });
    }
}
