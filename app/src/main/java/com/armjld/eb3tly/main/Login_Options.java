package com.armjld.eb3tly.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.SignUp.New_SignUp;
import com.armjld.eb3tly.SignUp.Signup;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.admin.Admin;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.armjld.eb3tly.R;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Login_Options extends AppCompatActivity {

    private static final int RC_SIGN_IN = 500;
    Button btnEmail,btnGoogle,btnFacebook;
    boolean doubleBackToExitPressedOnce = false;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private String TAG = "Login Options";
    private ProgressDialog mdialog;
    public static GoogleSignInClient mGoogleSignInClient;
    boolean isLinked = false;


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__options);
        btnEmail = findViewById(R.id.btnEmail);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        mdialog = new ProgressDialog(this);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnEmail.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        // ---------------- implement Google Account ---------------- //
        btnGoogle.setOnClickListener(v -> {
            mGoogleSignInClient.signOut();
            signInGoogle();
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        // --------------- implement Facebook Account -----------------//
        btnFacebook.setOnClickListener(v-> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
            );
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                mdialog.setMessage("جاري تسجيل الدخول ..");
                mdialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                uDatabase.orderByChild("email").equalTo(account.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            //firebaseAuthWithGoogle(account.getIdToken(),credential);
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                linkGoogleAccount(credential, ds.child("email").getValue().toString(), ds.child("mpass").getValue().toString());
                                break;
                            }
                        } else {
                            New_SignUp.provider = "Google";

                            New_SignUp.newEmail = account.getEmail();
                            New_SignUp.newFirstName = account.getGivenName();
                            New_SignUp.newLastName = account.getFamilyName();
                            New_SignUp.defultPP = Objects.requireNonNull(account.getPhotoUrl()).toString();
                            New_SignUp.googleCred = credential;

                            finish();
                            startActivity(new Intent(Login_Options.this, New_SignUp.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });


            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void linkGoogleAccount(AuthCredential credential, String memail, String mpass) {
        Log.i(TAG, memail + " : " + mpass);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                    if(user.getProviderId().equals("google.com")) {
                        isLinked = true;
                    }
                }
                if(!isLinked) {
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, linkGoogle -> {
                        if(linkGoogle.isSuccessful()) {
                            letsGo();
                            mdialog.dismiss();
                        } else {
                            mdialog.dismiss();
                        }
                    });
                    mdialog.dismiss();
                } else {
                    mdialog.dismiss();
                    letsGo();
                }
            } else {
                mdialog.dismiss();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken, AuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                letsGo();
            } else {
                mdialog.dismiss();
            }
        });
    }

    private void letsGo() {
        final String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // ------------------ Set Device Token ----------------- //
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Login_Options.this, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            uDatabase.child(userID).child("device_token").setValue(deviceToken);
        });
        FirebaseDatabase.getInstance().getReference("Pickly").child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && mAuth.getCurrentUser() != null){
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
                        if(snapshot.child("isConfirmed").exists()) {
                            UserInFormation.setisConfirm(Objects.requireNonNull(snapshot.child("isConfirmed").getValue()).toString());
                        }
                        StartUp.dataset = true;

                        if (isActive.equals("true")) { // Check if the account is Disabled
                            // --------------------- check account types and send each type to it's activity --------------//
                            ImportBlockedUsers();
                            switch (uType) {
                                case "Supplier":
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), supplierProfile.class));
                                    break;
                                case "Delivery Worker":
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    break;
                                case "Admin":
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Admin.class));
                                    break;
                            }
                        } else {
                            Toast.makeText(Login_Options.this, "تم تعطيل حسابك بسبب مشاكل مع المستخدمين", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "سجل حسابك مرة اخري", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(Login_Options.this, Signup.class));
                }
                mdialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mdialog.dismiss();
            }
        });
    }

    private void ImportBlockedUsers() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid()).child("Blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    BlockManeger blocedUsers = new BlockManeger();
                    blocedUsers.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        blocedUsers.add(ds.child("id").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }


    private void handleFacebookToken(AccessToken accessToken) {
        Toast.makeText(this, "Handling", Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d("MAAAIn", "HAHAHAHA" );
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

}