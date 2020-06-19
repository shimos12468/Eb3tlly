package com.armjld.eb3tly;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Signup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class code22 extends AppCompatActivity {

    private String mVerificationId;
    private FirebaseAuth mAuth;
    private EditText editTextMobile, editTextCode;
    private Button btnConfirmCode, btnConfirmPhone;
    private TextView txtViewPhone, btnReType,timer;
    private ConstraintLayout linerVerf, linerPhone;
    private String TAG = "Phone Auth";
    private CountDownTimer Timer;
    private long timeleft = 60000;
    private Boolean timerRunning = false;


    public void UpdateTimer(){
    int minutes = (int)timeleft/60000;
    int seconds = (int)timeleft%60000/1000;
    String time;
    time = " "+minutes+":";
    if(seconds<10)time+="0";
    time+=seconds;
    timer.setText("يمكنك الضغط هنا لاعادة ارسال الرمز ؟ بعد " + time);
    }

    public void startTimer(){
    Timer = new CountDownTimer(timeleft , 1000) {
        @Override
        public void onTick(long l) {
            timeleft = l;
            timerRunning = true;
            UpdateTimer();
            if(timeleft<1000){
                stopTimer();
            }

        }

        @Override
        public void onFinish() {

        }
        }.start();

    }

    public void stopTimer(){
        Timer.cancel();
        timerRunning = false;
        timeleft = 60000;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Toast.makeText(code22.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        Log.i(TAG, "Verfied");
        Log.i(TAG, "verf : " + mVerificationId + " code : " + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.i(TAG, "Signed Up via phone");
        mAuth.signInWithCredential(credential).addOnCompleteListener(code22.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference uDatabase = getInstance().getReference().child("Pickly").child("users");
                    uDatabase.child(mAuth.getCurrentUser().getUid()).child("completed").setValue("false");
                    uDatabase.child(mAuth.getCurrentUser().getUid()).child("id").setValue(mAuth.getCurrentUser().getUid());
                    uDatabase.child(mAuth.getCurrentUser().getUid()).child("ppURL").setValue("https://firebasestorage.googleapis.com/v0/b/pickly-ed2f4.appspot.com/o/ppUsers%2Fdefult.jpg?alt=media&token=a1b6b5cc-6f03-41fa-acf2-0c14e601935f");
                    Toast.makeText(code22.this, "تم تاكيد رقم الهاتف الرجاء استكمال البيانات", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(code22.this, Signup.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String message = "Somthing is wrong, we will fix it soon...";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                    Toast.makeText(code22.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}