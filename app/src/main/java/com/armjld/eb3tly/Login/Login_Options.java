package com.armjld.eb3tly.Login;

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
import com.armjld.eb3tly.SignUp.New_SignUp;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.armjld.eb3tly.R;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    boolean fbLink = false;
    String fbEmail;

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
        //FacebookSdk.sdkInitialize(Login_Options.this);
        //AppEventsLogger.activateApp(Login_Options.this);

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



        // --------------- implement Facebook Login -----------------//
        btnFacebook.setOnClickListener(v-> {
            if(AccessToken.getCurrentAccessToken() != null) {
                disconnectFromFacebook();
                return;
            }
            connectToFacebook();
        });

    }

    protected void connectToFacebook() {
        ArrayList<String> fbList = new ArrayList<>();
        fbList.add("email");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (json, response) -> {
                    // Application code
                    if (response.getError() != null) {
                        Toast.makeText(Login_Options.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        String jsonresult = String.valueOf(json);
                        System.out.println("JSON Result" + jsonresult);
                        String fbUserId = json.optString("id");
                        String fbUserFirstName = json.optString("first_name");
                        String fbUserLastName = json.optString("last_name");
                        String fbUserProfilePics = "https://graph.facebook.com/" + fbUserId + "/picture?type=small";
                        fbEmail = json.optString("email");
                        handleFacebookToken(loginResult.getAccessToken(), fbEmail,fbUserFirstName,fbUserLastName,fbUserProfilePics);
                        Log.i(TAG, "First Name : " + fbUserFirstName + " : " + fbUserLastName);
                    }
                    Log.v("FaceBook Response :", response.toString());
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login_Options.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login_Options.this, "Error" + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken, String fbEmail, String fbUserFirstName, String fbUserLastName, String fbUserProfilePics) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        uDatabase.orderByChild("email").equalTo(fbEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        linkFacebook(credential, ds.child("email").getValue().toString(), ds.child("mpass").getValue().toString());
                        break;
                    }
                } else {
                    fbNewAccount(fbEmail,fbUserFirstName,fbUserLastName,fbUserProfilePics, credential);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void linkFacebook(AuthCredential credential, String memail, String mpass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(UserInfo user: Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData()) {
                    if(user.getProviderId().equals("facebook.com")) {
                        fbLink = true;
                    }
                }
                LoginExistFacebook(credential);
            } else {
                Toast.makeText(this, "فشل في الاتصال", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
            }
        });
    }

    private void LoginExistFacebook(AuthCredential credential) {
        if(!fbLink) {
            Log.i(TAG, "Linking with Facebook Credential");
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, linkFace -> {
                if(linkFace.isSuccessful()) {
                    Log.i(TAG, "Linked to Facebook Succesfully, Logging in ..");
                    Toast.makeText(this, "تم ربط حسابك بالفيس بوك", Toast.LENGTH_SHORT).show();
                    letsGo();
                    mdialog.dismiss();
                } else {
                    Log.i(TAG, "Linked to Facebook Failed");
                    Toast.makeText(this, "فشل في الاتصال بالفيس بوك", Toast.LENGTH_SHORT).show();
                    mdialog.dismiss();
                }
            });
            mdialog.dismiss();
        } else {
            Log.i(TAG, "Account is Linked to Facebook, Logging ..");
            letsGo();
            mdialog.dismiss();
        }
    }

    private void fbNewAccount(String fbEmail, String fbFirstName, String fbLastName, String fbPP, AuthCredential credential) {
        New_SignUp.provider = "facebook";
        New_SignUp.newEmail = fbEmail;
        New_SignUp.newFirstName = fbFirstName;
        New_SignUp.newLastName = fbLastName;
        New_SignUp.defultPP = fbPP;
        New_SignUp.faceCred = credential;
        finish();
        startActivity(new Intent(Login_Options.this, New_SignUp.class));
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
                mdialog.dismiss();
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

    private void letsGo() {
        com.armjld.eb3tly.Login.LoginManager _lgnMn = new com.armjld.eb3tly.Login.LoginManager();
        _lgnMn.setMyInfo(Login_Options.this);
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

    public static void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> LoginManager.getInstance().logOut()).executeAsync();
    }

}