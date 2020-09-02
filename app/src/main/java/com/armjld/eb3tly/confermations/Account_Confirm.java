package com.armjld.eb3tly.confermations;

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
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Account_Confirm extends AppCompatActivity {

    private String uId = UserInFormation.getId();
    private String isConfirmed = UserInFormation.getisConfirm();
    private String uEmail = UserInFormation.getEmail();
    private String uPass = UserInFormation.getPass();
    private String uPhone = UserInFormation.getPhone();

    private String TAG = "Account Confirm";

    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase, confirmDatabase;
    private EditText txtCode;
    private TextView txtPhone,txtDone;
    private Button btnNext,btnAddSSN,btnFinish,btnBack;
    private ScrollView scrPhone,scrSsn,scrFinish;
    private boolean mVerificationInProgress = false;
    private Bitmap bitmap;
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private String uType = UserInFormation.getAccountType();


    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog mdialog;

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__confirm);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        confirmDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("confirms");

        txtCode = findViewById(R.id.txtCode);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnAddSSN = findViewById(R.id.btnAddSSN);
        btnFinish = findViewById(R.id.btnFinish);
        txtPhone = findViewById(R.id.txtPhone);
        txtDone = findViewById(R.id.txtDone);

        scrSsn = findViewById(R.id.scrSsn);
        scrPhone = findViewById(R.id.scrPhone);
        scrFinish = findViewById(R.id.scrFinish);
        mdialog = new ProgressDialog(this);
        txtPhone.setText("+2" + uPhone);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تفعيل الحساب");

        btnNext.setEnabled(false);
        btnNext.setClickable(false);
        btnNext.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_bad));


        if(!checkPhone()) {
            scrPhone.setVisibility(View.GONE);
            scrFinish.setVisibility(View.GONE);
            scrSsn.setVisibility(View.VISIBLE);
        } else {
            scrPhone.setVisibility(View.VISIBLE);
            scrFinish.setVisibility(View.GONE);
            scrSsn.setVisibility(View.GONE);

            mCallBack();
            if(uPhone != null) {
                mdialog.setMessage("جاري ارسال رمز التاكيد الي رقمك ..");
                mdialog.show();
                sendCode(uPhone);
            } else {
                startActivity(new Intent(this, StartUp.class));
            }
        }

        btnNext.setOnClickListener(v -> {
            String code = txtCode.getText().toString();
            if (TextUtils.isEmpty(code)) {
                txtCode.setError("الرجاء ادخال الكود");
                return;
            }

            if(code.length() != 6) {
                txtCode.setError("ادخل كود صحيح");
                return;
            }
            mdialog.setMessage("جاري التأكد من الكود ..");
            mdialog.show();
            verifyPhoneNumberWithCode(mVerificationId, code);
        });

        btnAddSSN.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(Account_Confirm.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        btnFinish.setOnClickListener(v -> {
            if(bitmap != null) {
                mdialog.setMessage("جاري تحميل صورة البطاقة");
                mdialog.show();
                handleUpload(bitmap);
            } else {
                Toast.makeText(this, "يجب اضافة صورة البطاقة الشخصية", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
            whichProfile();
        });

    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }


    // --------------------------------- Phone Number Functions -------------------------- //
    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                btnNext.setEnabled(true);
                btnNext.setClickable(true);
                btnNext.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_defult));
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;
                btnNext.setEnabled(true);
                btnNext.setClickable(true);
                btnNext.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_defult));
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Account_Confirm.this, "رقم هاتف غير صحيح", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(Account_Confirm.this, "تم ارسال الرمز", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
                mVerificationId = verificationId;
                btnNext.setEnabled(true);
                btnNext.setClickable(true);
                btnNext.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_defult));
                mResendToken = token;
            }
        };
    }

    private void sendCode(String uPhone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + uPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        mdialog.setMessage("جاري التاكد من الكود ..");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void reauthenticate() {
        FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(uEmail, uPass);
        assert user != null;
        user.reauthenticate(credential).addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        reauthenticate();
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential).addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
            if (task.isSuccessful()) {
                mdialog.dismiss();
                Toast.makeText(this, "تم تفعيل رقم هاتفك", Toast.LENGTH_SHORT).show();
                scrPhone.setVisibility(View.GONE);
                scrSsn.setVisibility(View.VISIBLE);
                scrFinish.setVisibility(View.GONE);
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                }

                mdialog.dismiss();
            }
        });
    }


    // -------------------------- SSN Code Functions -------------------------------- //
    private Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;
            } else {
                int targetWidth = maxLength;
                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;
            }
        }
        catch (Exception e)
        {
            Log.i("SignUp", "Returned the source Photo");
            return source;
        }
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(Account_Confirm.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Account_Confirm.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Account_Confirm.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Account_Confirm.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(Account_Confirm.this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;
            Log.i(TAG,"uri : " + uri.toString());
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
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
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

        if(exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
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
        } else {
            return bitmap;
        }

    }

    private void handleUpload (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ssn").child(uId + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            getDownUrl(reference);
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
        Log.i("Updating", " Handel Upload");
    }

    private void getDownUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {

            confirmDatabase.child(uId).child("ssnURL").setValue(uri.toString());
            confirmDatabase.child(uId).child("isConfirmed").setValue("pending");
            confirmDatabase.child(uId).child("id").setValue(uId);
            confirmDatabase.child(uId).child("date").setValue(datee);
            uDatabase.child(uId).child("isConfirmed").setValue("pending");

            UserInFormation.setisConfirm("pending");

            Toast.makeText(Account_Confirm.this, "تم حفظ صورة البطاقة", Toast.LENGTH_SHORT).show();
            mdialog.dismiss();

            scrSsn.setVisibility(View.GONE);
            scrPhone.setVisibility(View.GONE);
            scrFinish.setVisibility(View.VISIBLE);
        });
    }

    private boolean checkPhone () {
        boolean check = true;
        for (UserInfo user: Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData()) {
            if(user.getProviderId().equals("phone")) {
                check = false;
            }
        }
        return check;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(bitmap == null) {
            btnFinish.setEnabled(false);
            btnFinish.setClickable(false);
            btnFinish.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_bad));
            btnAddSSN.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_defult));
            txtDone.setVisibility(View.GONE);
            btnAddSSN.setText("اضف صورة البطاقة الشخصية");
        } else {
            btnFinish.setEnabled(true);
            btnFinish.setClickable(true);
            btnFinish.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_defult));
            btnAddSSN.setBackground(ContextCompat.getDrawable(Account_Confirm.this, R.drawable.btn_bad));
            txtDone.setVisibility(View.VISIBLE);
            btnAddSSN.setText("تغيير الصورة");
        }
    }

}