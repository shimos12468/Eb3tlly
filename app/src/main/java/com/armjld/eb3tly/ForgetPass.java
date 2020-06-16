package com.armjld.eb3tly;

import android.app.Activity;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class ForgetPass extends Activity {
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private EditText editTextMobile, editTextCode, newPass1, newPass2;
    private Button btnConfirmCode, btnConfirmPhone, btnSetPass;
    private TextView txtViewPhone, btnReType;
    private ConstraintLayout linerVerf, linerPhone,linerPass;
    private String TAG = "Forget Pass";
    private DatabaseReference uDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget1);

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        editTextCode = findViewById(R.id.txtVerfCode);
        editTextMobile = findViewById(R.id.txtPhoneNumb);
        btnConfirmCode = findViewById(R.id.btnConfirmCode);
        btnConfirmPhone = findViewById(R.id.btnConfirmPhone);
        btnSetPass = findViewById(R.id.btnSetPass);
        newPass1 = findViewById(R.id.newPass1);
        newPass2 = findViewById(R.id.newPass2);
        txtViewPhone = findViewById(R.id.txtViewPhone);
        btnReType = findViewById(R.id.btnReType);

        linerVerf = findViewById(R.id.linerVerf);
        linerPhone = findViewById(R.id.linerPhone);
        linerPass = findViewById(R.id.linerPass);

        linerVerf.setVisibility(View.GONE);
        linerPass.setVisibility(View.GONE);

        txtViewPhone.setText("ادخل رقم الهاتف");

        btnConfirmPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = editTextMobile.getText().toString().trim();
                if(mobile.length() != 11){
                    editTextMobile.setError("ادخل رقم هاتف صحيح");
                    editTextMobile.requestFocus();
                    return;
                } else {
                    final String getMobile = mobile;
                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("users")
                            .orderByChild("phone").equalTo(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                if (snapshot.getValue() != null) {
                                    txtViewPhone.setText("ضع الرمز المرسل اليك");
                                    linerPhone.setVisibility(View.GONE);
                                    linerPass.setVisibility(View.GONE);
                                    linerVerf.setVisibility(View.VISIBLE);
                                    Toast.makeText(ForgetPass.this, "تم ارسال الكود", Toast.LENGTH_SHORT).show();
                                    sendVerificationCode(getMobile);
                                } else {
                                    Toast.makeText(ForgetPass.this, "رقم الهاتف غير مسجل", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ForgetPass.this, "رقم الهاتف غير مسجل", Toast.LENGTH_SHORT).show();
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }});
                }
            }});

        btnConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.length() != 6) {
                    editTextCode.setError("ادخل كود صحيح");
                    editTextCode.requestFocus();
                    return;
                } else {
                    verifyVerificationCode(code);
                }
            }
        });

        btnReType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCode.setText("");
                linerVerf.setVisibility(View.GONE);
                linerPass.setVisibility(View.GONE);
                linerPhone.setVisibility(View.VISIBLE);
                txtViewPhone.setText("ادخل رقم الهاتف");
            }
        });
    }

    //the method is sending verification code
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
        Log.i(TAG, "Send Verfication Code fun to : +2" + mobile);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.i(TAG, "Message Detected " + code);
            if (code != null) {
                editTextCode.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ForgetPass.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Failed to verfiy");
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.i(TAG, "onCodeSent : " + s);
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //-----------------------------------------------
        Log.i(TAG, "Verfied");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.i(TAG, "Signed Up via phone");
        mAuth.signInWithCredential(credential).addOnCompleteListener(ForgetPass.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    txtViewPhone.setText("ادخل الرقم السري الجديد");
                    linerPass.setVisibility(View.VISIBLE);
                    linerPhone.setVisibility(View.GONE);
                    linerVerf.setVisibility(View.GONE);

                    btnSetPass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(newPass1.getText().toString())){
                                newPass1.setError("يجب ادخال كلمه المرور");
                                return;
                            }
                            if(!newPass1.getText().toString().equals(newPass2.getText().toString())){
                                newPass2.setError("تاكد ان كلمه المرور نفسها");
                                return;
                            }

                            mAuth.getCurrentUser().updatePassword(newPass1.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("mpass").setValue(newPass1.getText().toString().trim());
                                        Log.i(TAG, "Pass Updated : " + newPass1.getText().toString().trim() + " and current user id : " + mAuth.getCurrentUser().getUid());
                                        mAuth.signOut();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(ForgetPass.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    String message = "Something is wrong, we will fix it soon...";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                    Toast.makeText(ForgetPass.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
