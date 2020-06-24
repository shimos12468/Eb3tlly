package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import Model.notiData;
import Model.userData;

import static com.armjld.eb3tly.R.layout.activity_signup;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Signup extends AppCompatActivity {

    private SOMEUSERDATAPROVIDER impdata;
    private EditText user,email,pass,con_password , phoneNum,editTextCode;
    private Button btnreg;
    private TextView logintxt ,timer,txtViewPhone ,txtretype,txtSended;
    private ImageView imgSetPP;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase,nDatabase;
    private ConstraintLayout linerVerf, linersignUp;
    private RadioGroup rdAccountType;
    private RadioButton rdDlivery,rdSupplier;
    private String accountType;
    private Bitmap bitmap, ssnBitmap;
    private String ssnURL = "none";
    Button btnConfirmCode;
    private String defultPP = "https://firebasestorage.googleapis.com/v0/b/pickly-ed2f4.appspot.com/o/ppUsers%2Fdefult.jpg?alt=media&token=a1b6b5cc-6f03-41fa-acf2-0c14e601935f";
    private String TAG = "Sign Up Activity";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    int SSN_IMAGE = 10002;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    private CountDownTimer Timer;
    private long timeleft = 60000;
    private Boolean timerRunning = false;


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, Terms.class));
    }

    public void UpdateTimer(){
        int minutes = (int)timeleft/60000;
        int seconds = (int)timeleft%60000/1000;
        String time;
        time = " "+minutes+":";
        if(seconds<10)time+="0";
        time+=seconds;
        timer.setText("يمكنك الضغط هنا لاعادة ارسال الرمز بعد " + time);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap (source, 150);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = null;
                try {
                    uri = Uri.parse(getFilePath(Signup.this, photoUri));
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if(uri != null) {
                    bitmap = rotateImage(bitmap , uri , photoUri);
                }
                Log.i(TAG,"uri : " + uri.toString());
                imgSetPP.setImageBitmap(bitmap);
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private Bitmap rotateImage(Bitmap bitmap , Uri uri , Uri photoUri){
        ExifInterface exifInterface =null;
        try {
            if(uri==null){
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        }
       catch (IOException e){
            e.printStackTrace();
       }

        assert exifInterface != null;
        int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
        Log.i(TAG, "Orign: " + String.valueOf(orintation));
        if(orintation == 6 || orintation == 3 || orintation == 8) {
            Matrix matrix = new Matrix();
            if (orintation == 6) {
                matrix.postRotate(90);
            }
            else if (orintation == 3) {
                matrix.postRotate(180);
            }
            else if (orintation == 8) {
                matrix.postRotate(270);
            }
            Bitmap rotatedmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            return rotatedmap;
        } else {
            return bitmap;
        }
    }
    private void handleUpload (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uID + ".jpeg");
        final String did = uID;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownUrl(did, reference);
                Log.i("Sign UP", " onSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Upload Error: ", "Fail:", e.getCause());
            }
        });
        Log.i("Sign UP", " Handel Upload");
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("Sign UP", " add Profile URL");
                uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
                mdialog.dismiss();
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signup);

        Log.i(TAG, "Reached Signup");
        mAuth = FirebaseAuth.getInstance();

        TextView tbTitle = findViewById(R.id.toolbar_title);
        linersignUp = findViewById(R.id.linearsignUp);
        linerVerf = findViewById(R.id.linerVerf);
        logintxt = findViewById(R.id.signup_text);
        linerVerf.setVisibility(View.GONE);
        linersignUp.setVisibility(View.VISIBLE);
        tbTitle.setText("تسجيل حساب جديد");

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        mdialog = new ProgressDialog(this);
        user = findViewById(R.id.txtEditName);
        email = findViewById(R.id.txtEditEmail);
        pass = findViewById(R.id.txtEditPassword);
        con_password = findViewById(R.id.txtEditPassword2);
        btnreg = findViewById(R.id.btnEditInfo);
        txtretype = findViewById(R.id.btnReType);
        imgSetPP = findViewById(R.id.imgEditPhoto);
        phoneNum = findViewById(R.id.phoneNumber);
        timer = findViewById(R.id.timer);
        txtViewPhone = findViewById(R.id.txtViewPhone);
        editTextCode = findViewById(R.id.txtVerfCode);
        btnConfirmCode = findViewById(R.id.btnConfirmCode);
        txtSended = findViewById(R.id.txtSended);
        Picasso.get().load(Uri.parse(defultPP)).into(imgSetPP);


        //Check For Account Type
        rdAccountType = (RadioGroup) findViewById(R.id.rdAccountType);
        rdDlivery = (RadioButton) findViewById(R.id.rdDlivery);
        rdSupplier = (RadioButton) findViewById(R.id.rdSupplier);
        accountType = "Delivery Worker";
        rdAccountType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.rdSupplier) {
                    accountType = "Supplier";
                } else {
                    accountType = "Delivery Worker";
                }
                return;
            }
        });

        //Set PP
        imgSetPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
                if (ContextCompat.checkSelfPermission(Signup.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if(intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, TAKE_IMAGE_CODE);
                    }
                }
            }
        });
        // Register Fun
        btnreg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        final String muser = user.getText().toString().trim();
        final String memail = email.getText().toString().trim();
        final String mpass = pass.getText().toString().trim();
        final String con_pass = con_password.getText().toString().trim();
        final String phone = phoneNum.getText().toString().trim();
        // Check For empty fields
        if(TextUtils.isEmpty(muser)){
       user.setError("يجب ادخال اسم المستخدم");
       return;
        }
        if(TextUtils.isEmpty(memail)){
            email.setError("يجب ادخال البريد ألالكتروني");
            return;
        }
        if(TextUtils.isEmpty(mpass)){
            pass.setError("يجب ادخال كلمه المرور");
            return;
        }
        //Toast.makeText(Signup.this, SNN.length(), Toast.LENGTH_SHORT).show();
        if(!mpass.equals(con_pass)){
            con_password.setError("تاكد ان كلمه المرور نفسها");
            return;
        }
        if(phone.length() != 11|| phone.charAt(0)!='0'|| phone.charAt(1)!='1'){
            phoneNum.setError("ادخل رقم هاتف صحيح");
            phoneNum.requestFocus();
            return;
        }

        mdialog.setMessage("جاري التأكد من البيانات ..");
        mdialog.show();

        impdata = new SOMEUSERDATAPROVIDER(memail ,mpass ,muser ,phone);

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.getValue() != null) {
                        mdialog.dismiss();
                        Toast.makeText(Signup.this, "رقم الهاتف بالفعل مسجل", Toast.LENGTH_SHORT).show();
                    } else {
                        startTimer();
                        mdialog.dismiss();
                        linersignUp.setVisibility(View.GONE);
                        txtSended.setText("تم ارسال الكود الي رقم : " + phone);
                        linerVerf.setVisibility(View.VISIBLE);
                        txtViewPhone.setText("ضع الرمز المرسل اليك");
                        sendVerificationCode(phone);

                        timer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!timerRunning){
                                    sendVerificationCode(phone);
                                    startTimer();
                                    Toast.makeText(Signup.this, "تم ارسال الرمز مجددا", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(Signup.this, "ارجاء الانتظار قليلا", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    startTimer();
                    mdialog.dismiss();
                    txtSended.setText("تم ارسال الكود الي رقم : " + phone);
                    linersignUp.setVisibility(View.GONE);
                    linerVerf.setVisibility(View.VISIBLE);
                    txtViewPhone.setText("ضع الرمز المرسل اليك");
                    sendVerificationCode(phone);
                    timer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!timerRunning){
                                sendVerificationCode(phone);
                                startTimer();
                                Toast.makeText(Signup.this, "تم ارسال الرمز مجددا", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Signup.this,"ارجاء الانتظار قليلا  ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

    }});

        btnConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCode.getText().toString().trim();
                if (code.length() != 6) {
                    editTextCode.setError("ادخل كود صحيح");
                    editTextCode.requestFocus();
                    return;
                }
                mdialog.setMessage("جاري التأكد من الكود ..");
                mdialog.show();
                verifyVerificationCode(code);
            }
        });

        txtretype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linersignUp.setVisibility(View.VISIBLE);
                linerVerf.setVisibility(View.GONE);
                stopTimer();
            }
        });

    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(Signup.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Signup.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Signup.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Signup.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
        Log.i(TAG, "Send Verfication Code fun to : +2" + mobile);
    }
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
            Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Failed to verfiy");
        }

        @Override
        public void onCodeSent(String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(Signup.this, "تم ارسال الكود", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onCodeSent : " + s);
            mVerificationId = s;
        }
    };

        private void verifyVerificationCode(String code) {
            Log.i(TAG, "verf : " + mVerificationId + " code : " + code);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.i(TAG, "Signed Up via phone");
        mAuth.signInWithCredential(credential).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String id = mAuth.getCurrentUser().getUid().toString();
                    final  String memail = impdata.getMail();
                    final  String mpass  = impdata.getPassword();
                    final String muser = impdata.getUser();
                    final  String phone = impdata.getPhone();
                    AuthCredential credential = EmailAuthProvider.getCredential(memail, mpass);
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                FirebaseUser user = task.getResult().getUser();
                                datee = DateFormat.getDateInstance().format(new Date());
                                userData data= new userData(muser, phone, memail, datee, id, accountType, defultPP, mpass, "0");
                                uDatabase.child(id).setValue(data);
                                uDatabase.child(id).child("completed").setValue("true");
                                uDatabase.child(id).child("profit").setValue("0");
                                uDatabase.child(id).child("active").setValue("true");
                                Toast.makeText(getApplicationContext(),"تم التسجيل الحساب بنجاح" , Toast.LENGTH_LONG).show();

                                if(bitmap != null) {
                                    handleUpload(bitmap);
                                } else {
                                    uDatabase.child(id).child("ppURL").setValue(defultPP);
                                    mdialog.dismiss();
                                }

                                // ------------- Welcome message in Notfications----------------------//

                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", mAuth.getCurrentUser().getUid().toString(), "-MAPQWoKEfmHIQG9xv-v", "welcome", datee, "false");
                                nDatabase.child(mAuth.getCurrentUser().getUid()).push().setValue(Noti);

                                if (accountType.equals("Supplier")) {
                                    startActivity(new Intent(getApplicationContext(), introSup.class));
                                } else if (accountType.equals("Delivery Worker")) {
                                    startActivity(new Intent(getApplicationContext(), intro2.class));
                                }
                            } else {
                                String ERROR = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "فشل في تسجيل البيانات حاول مجددا", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String message = "Somthing is wrong, we will fix it soon...";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                    mdialog.dismiss();
                    Toast.makeText(Signup.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                Log.i("SignUp", "Returned a Resized Photo");
                return result;
            } else {
                int targetWidth = maxLength;
                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                Log.i("SignUp", "Returned a Resized Photo");
                return result;
            }
        }
        catch (Exception e)
        {
            Log.i("SignUp", "Returned the source Photo");
            return source;
        }
    }
}





