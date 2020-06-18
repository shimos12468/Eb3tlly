package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import Model.userData;

import static com.armjld.eb3tly.R.layout.activity_signup;

public class Signup extends AppCompatActivity {

    private EditText user,email,pass,con_password;
    private Button btnreg;
    private TextView logintxt;
    private ImageView imgSetPP;

    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase;

    private RadioGroup rdAccountType;
    private RadioButton rdDlivery,rdSupplier;
    private String accountType;
    private Bitmap bitmap, ssnBitmap;
    private String ssnURL = "none";
    private String ppURL = "https://firebasestorage.googleapis.com/v0/b/pickly-ed2f4.appspot.com/o/ppUsers%2Fdefult.jpg?alt=media&token=a1b6b5cc-6f03-41fa-acf2-0c14e601935f";
    private String TAG = "Sign Up Activity";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    int SSN_IMAGE = 10002;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    public Signup() { }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            if(requestCode == TAKE_IMAGE_CODE) {
                try {
                    bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
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
                handleUpload(bitmap);
            } else if (requestCode == SSN_IMAGE) {
                try {
                    ssnBitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "تم اختيار صورة البطاقة بنجاح", Toast.LENGTH_SHORT).show();
                handleUpload2(ssnBitmap);
            }
            
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
        int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
        Log.i(TAG, "Orign: " + String.valueOf(orintation));

        if(orintation==6||orintation==3||orintation==8) {
            Matrix matrix = new Matrix();

            if (orintation == 6) {
                matrix.postRotate(90);
            } else if (orintation == 3) {
                matrix.postRotate(180);
            } else if (orintation == 8) {
                matrix.postRotate(270);
            }
            Bitmap rotatedmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return rotatedmap;
        }
        return bitmap;
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
                ppURL = uri.toString();
            }
        });
    }

    private void handleUpload2 (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ssnUsers").child(uID + ".jpeg");
        final String did = uID;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownUrl2(did, reference);
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

    private void getDownUrl2(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("Sign UP", " add Profile URL");
                uDatabase.child(uIDd).child("ssnURL").setValue(uri.toString());
                ssnURL = uri.toString();
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signup);
        mAuth=FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return;
        }

        mdialog = new ProgressDialog(this);
        user = findViewById(R.id.txtEditName);
        email = findViewById(R.id.txtEditEmail);
        pass = findViewById(R.id.txtEditPassword);
        con_password = findViewById(R.id.txtEditPassword2);
        btnreg = findViewById(R.id.btnEditInfo);
        logintxt = findViewById(R.id.signup_text);
        imgSetPP = findViewById(R.id.imgEditPhoto);
        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getURL = dataSnapshot.child("ppURL").getValue().toString();
                Picasso.get().load(Uri.parse(getURL)).into(imgSetPP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });
        // Register Fun
        btnreg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        final String muser = user.getText().toString().trim();
        final String memail = email.getText().toString().trim();
        final String mphone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        final String egyPhone = mphone.substring(mphone.length() - 11);
        final String mpass = pass.getText().toString().trim();
        final String con_pass = con_password.getText().toString().trim();

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
        }
        //Toast.makeText(Signup.this, SNN.length(), Toast.LENGTH_SHORT).show();
        if(!mpass.equals(con_pass)){
            con_password.setError("تاكد ان كلمه المرور نفسها");
            return;
        }


        mdialog.setMessage("جاري انشاء حسابك..");
        mdialog.show();
        final String id = mAuth.getCurrentUser().getUid().toString();

        if(mAuth.getCurrentUser() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(memail, mpass);
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()&&mAuth.getCurrentUser() != null) {
                        Log.d(TAG, "linkWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        datee = DateFormat.getDateInstance().format(new Date());
                        userData data= new userData(muser, egyPhone, memail, datee, id, accountType, ppURL, mpass, "0");
                        uDatabase.child(id).setValue(data);
                        uDatabase.child(id).child("completed").setValue("true");
                        uDatabase.child(id).child("profit").setValue("0");
                        uDatabase.child(id).child("active").setValue("true");
                        Toast.makeText(getApplicationContext(),"تم التسجيل الحساب بنجاح" , Toast.LENGTH_LONG).show();
                        if (accountType.equals("Supplier")) {
                            startActivity(new Intent(getApplicationContext(), introSup.class));
                        } else if (accountType.equals("Delivery Worker")) {
                            startActivity(new Intent(getApplicationContext(), intro2.class));
                        }
                    } else {
                        String ERROR = task.getException().getMessage();
                        Log.w(TAG, "linkWithCredential:failure : " + ERROR + " Code : " + task.getException().getCause());
                        Toast.makeText(getApplicationContext(), "فشل في تسجيل البيانات حاول مجددا", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "YOU ARE LOGGED OUT", Toast.LENGTH_SHORT).show();
        }
        mdialog.dismiss();
    }});
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
                Toast.makeText(Signup.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Signup.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





