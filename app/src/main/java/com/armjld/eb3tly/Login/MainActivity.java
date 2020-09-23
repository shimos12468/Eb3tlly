package com.armjld.eb3tly.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Passaword.Forget_Password;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.DatabaseClasses.Ratings;
import com.armjld.eb3tly.SignUp.New_SignUp;
import com.armjld.eb3tly.Home.StartUp;
import Model.UserInFormation;
import com.armjld.eb3tly.admin.Admin;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Objects;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MainActivity extends AppCompatActivity {

    private TextView signup,txtForgetPass;
    @SuppressLint("StaticFieldLeak")
    public static EditText email,pass;
    private Button btnlogin;
    public UserInFormation userInfo = new UserInFormation();
    //FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase,Database;
    private ProgressDialog mdialog;
    private ImageView btnBack;

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
        uDatabase = getInstance().getReference().child("Pickly").child("users");

        btnBack = findViewById(R.id.btnBack);

        mdialog = new ProgressDialog(this);
        signup = findViewById(R.id.signup_text);
        email = findViewById(R.id.txtEditName);
        pass = findViewById(R.id.txtEditPassword);
        btnlogin = findViewById(R.id.btnEditInfo);
        txtForgetPass = findViewById(R.id.txtForgetPass);

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
    }

    private void ImportBlockedUsers() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        Database = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid());
        Database.child("Blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        blocedUsers.add(ds.child("id").getValue().toString());
                        //Toast.makeText(context, ds.child("id").getValue().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }

    private void login(String memail, String mpass) {
        if (TextUtils.isEmpty(memail)) {
            email.setError("يجب ادخال اسم المستخدم");
            return;
        }
        if (TextUtils.isEmpty(mpass)) {
            pass.setError("يجب ادخال كلمه المرور");
            return;
        }
        mdialog.setMessage("جاري تسجيل الدخول..");
        mdialog.show();

        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                // ------------------ Set Device Token ----------------- //
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
                    String deviceToken = instanceIdResult.getToken();
                    uDatabase.child(userID).child("device_token").setValue(deviceToken);
                });
                    FirebaseDatabase.getInstance().getReference("Pickly").child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && mAuth.getCurrentUser() != null){
                                uDatabase.child(userID).child("mpass").setValue(mpass);
                                String isCompleted = Objects.requireNonNull(snapshot.child("completed").getValue()).toString();

                                if (isCompleted.equals("true")) {
                                    String uType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();
                                    String isActive = Objects.requireNonNull(snapshot.child("active").getValue()).toString();
                                    UserInFormation.setAccountType(uType);
                                    UserInFormation.setUserName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                                    UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                                    UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString());
                                    UserInFormation.setId(mAuth.getCurrentUser().getUid());
                                    UserInFormation.setEmail(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                                    UserInFormation.setPass(Objects.requireNonNull(snapshot.child("mpass").getValue()).toString());
                                    UserInFormation.setPhone(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                                    UserInFormation.setisConfirm("false");

                                    Ratings _ratings = new Ratings();
                                    _ratings.setMyRating();

                                    if(snapshot.child("isConfirmed").exists()) {
                                        UserInFormation.setisConfirm(Objects.requireNonNull(snapshot.child("isConfirmed").getValue()).toString());
                                    }
                                    if(snapshot.child("accountType").getValue().toString().equals("Delivery Worker") && snapshot.child("currentDate").exists()) {
                                        UserInFormation.setCurrentdate(snapshot.child("currentDate").getValue().toString());
                                    }
                                    StartUp.dataset = true;

                                    if (isActive.equals("true")) { // Check if the account is Disabled
                                        // --------------------- check account types and send each type to it's activity --------------//
                                        ImportBlockedUsers();
                                        switch (uType) {
                                            case "Supplier":
                                            case "Delivery Worker":
                                                mdialog.dismiss();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                break;
                                            case "Admin":
                                                mdialog.dismiss();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), Admin.class));
                                                break;
                                        }
                                    } else {
                                        mdialog.dismiss();
                                        Toast.makeText(MainActivity.this, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                } else {
                                    mdialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Please clear the app data and signon again", Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                Toast.makeText(getApplicationContext(), "سجل حسابك مرة اخري", Toast.LENGTH_LONG).show();
                                mdialog.dismiss();
                                finish();
                                startActivity(new Intent(MainActivity.this, New_SignUp.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
            } else {
                Toast.makeText(getApplicationContext(), "تأكد من بيانات الحساب", Toast.LENGTH_LONG).show();
                mdialog.dismiss();
            }
        });
    }
}
