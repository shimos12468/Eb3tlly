package com.armjld.eb3tly.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Passaword.Forget_Password;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.SignUp.New_SignUp;

import Model.UserInFormation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MainActivity extends AppCompatActivity {

    private TextView signup,txtForgetPass;
    @SuppressLint("StaticFieldLeak")
    public static EditText email,pass;
    private Button btnlogin;
    public UserInFormation userInfo = new UserInFormation();
    //FireBase
    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private ImageView btnBack;
    private TextInputLayout tlEmail, tlPass;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Login_Options.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Connect to Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        btnBack = findViewById(R.id.btnBack);

        mdialog = new ProgressDialog(this);
        signup = findViewById(R.id.signup_text);
        email = findViewById(R.id.txtEditName);
        pass = findViewById(R.id.txtEditPassword);
        btnlogin = findViewById(R.id.btnEditInfo);
        txtForgetPass = findViewById(R.id.txtForgetPass);
        tlEmail = findViewById(R.id.tlEmail);
        tlPass = findViewById(R.id.tlPass);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تسجيل الدخول");

        btnBack.setOnClickListener(v-> {
            startActivity(new Intent(this, Login_Options.class));
        });

        signup.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, New_SignUp.class));
            New_SignUp.provider = "Email";
        });

        final Intent forgetIntent = new Intent(this, Forget_Password.class);
        txtForgetPass.setOnClickListener(v -> {
            finish();
            forgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(forgetIntent);
        });

        String uMail = getIntent().getStringExtra("umail");
        String uPass = getIntent().getStringExtra("upass");

        if(uMail != null && uPass != null) {
            email.setText(uMail);
            pass.setText(uPass);
            login(uMail,uPass);
        }

        btnlogin.setOnClickListener(v -> {
            String memail = email.getText().toString().trim();
            String mpass = pass.getText().toString().trim();
            login(memail, mpass);
        });

        textWatchers();

    }

    private void textWatchers() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlEmail.setErrorEnabled(false);
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlPass.setErrorEnabled(false);
            }
        });
    }

    private void login(String memail, String mpass) {
        if (TextUtils.isEmpty(memail)) {
            tlEmail.setError("يجب ادخال اسم المستخدم");
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mpass)) {
            tlPass.setError("يجب ادخال كلمه المرور");
            pass.requestFocus();
            return;
        }
        mdialog.setMessage("جاري تسجيل الدخول..");
        mdialog.show();

        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LoginManager _lgnMn = new LoginManager();
                _lgnMn.setMyInfo(MainActivity.this);
            } else {
                Toast.makeText(getApplicationContext(), "تأكد من بيانات الحساب", Toast.LENGTH_LONG).show();
                mdialog.dismiss();
            }
        });
    }
}
