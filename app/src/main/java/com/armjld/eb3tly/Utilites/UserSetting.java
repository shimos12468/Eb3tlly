package com.armjld.eb3tly.Utilites;

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
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Profiles.NewProfile;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.confermations.Account_Confirm;
import com.armjld.eb3tly.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;

public class UserSetting extends AppCompatActivity {

    EditText name,Email;
    Button confirm,btnConfirmAccount;
    private ImageView UserImage;
    String email,Name;
    int TAKE_IMAGE_CODE = 10001;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private Bitmap bitmap;
    private ProgressDialog mdialog;
    private String ppURL = "";
    String oldPass = "";
    private CheckBox chkStateNoti;
    private Spinner spState;
    private static String TAG = "User Settings";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    String uId = UserInFormation.getId();
    String isConfirmed = UserInFormation.getisConfirm();
    private ConstraintLayout constUserSettings;

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            StartUp.setUserData(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 150);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(UserSetting.this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;
            Log.i(TAG,"uri : " + uri.toString());
            UserImage.setImageBitmap(bitmap);
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
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uId + ".jpeg");
        final String did = uId;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            getDownUrl(did, reference);
            Log.i("Updating ", " onSuccess");
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
        Log.i("Updating", " Handel Upload");
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.i("Sign UP", " add Profile URL");
            uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
            Toast.makeText(UserSetting.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
            finish();
            whichProfile();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(UserSetting.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(UserSetting.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(UserSetting.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserSetting.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تغيير البيانات الشخصية");

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        UserImage = findViewById(R.id.imgEditPhoto);
        name = findViewById(R.id.txtEditName);
        Email = findViewById(R.id.txtEditEmail);
        mdialog = new ProgressDialog(this);
        confirm = findViewById(R.id.btnEditInfo);
        spState = findViewById(R.id.spState);
        chkStateNoti = findViewById(R.id.chkStateNoti);
        constUserSettings = findViewById(R.id.constUserSettings);
        btnConfirmAccount = findViewById(R.id.btnConfirmAccount);
        btnConfirmAccount.setVisibility(View.GONE);

        for(UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if(user.getProviderId().equals("google.com")) {
                Email.setEnabled(false);
                Email.setKeyListener(null);
            } else {
                Email.setEnabled(true);
                //Email.setKeyListener(KeyListener);
            }
        }

        if(isConfirmed.equals("false")) {
            Snackbar snackbar = Snackbar.make(constUserSettings, "لم تقم بتأكيد حسابك بعد", LENGTH_INDEFINITE).setAction("تأكيد الحساب", view -> {
                finish();
                startActivity(new Intent(this, Account_Confirm.class));
            });
            snackbar.getView().setBackgroundColor(Color.RED);
            snackbar.show();
        } else if (isConfirmed.equals("pending")) {
            Snackbar snackbar = Snackbar.make(constUserSettings, "جاري التحقق من بيانات حسابك", LENGTH_INDEFINITE).setTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(Color.YELLOW);
            snackbar.show();
        }

        btnConfirmAccount.setOnClickListener(v ->{
            finish();
            startActivity(new Intent(this, Account_Confirm.class));
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UserSetting.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setPrompt("اختار المحافظة");
        spState.setAdapter(adapter);

        if(UserInFormation.getAccountType().equals("Supplier")) {
            chkStateNoti.setVisibility(View.GONE);
        }

        // ---------------------- Get Current Data -------------------------- //
        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = Objects.requireNonNull(dataSnapshot.child("ppURL").getValue()).toString();
                String sendOrderNoti = "true";
                if(dataSnapshot.child("sendOrderNoti").exists()) {
                    sendOrderNoti = Objects.requireNonNull(dataSnapshot.child("sendOrderNoti").getValue()).toString();
                }
                oldPass = Objects.requireNonNull(dataSnapshot.child("mpass").getValue()).toString();
                name.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                Email.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                Picasso.get().load(Uri.parse(url)).into(UserImage);

                if(dataSnapshot.child("userState").exists()) {
                    spState.setSelection(getIndex(spState, Objects.requireNonNull(dataSnapshot.child("userState").getValue()).toString()));
                }

                if(sendOrderNoti.equals("false")) {
                    chkStateNoti.setChecked(false);
                } else if (sendOrderNoti.equals("true")){
                    chkStateNoti.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        UserImage.setOnClickListener(view -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(UserSetting.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        confirm.setOnClickListener(view -> {
            email = Email.getText().toString().trim();
            Name = name.getText().toString().trim();
            Log.i(TAG, "Old Pass : " + oldPass);

            if(TextUtils.isEmpty(Name)){
                name.setError("يجب ادخال اسم المستخدم");
                return;
            }
            if(TextUtils.isEmpty(email)){
                Email.setError("يجب ادخال البريد ألالكتروني");
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();

            // ------------------ Update the Name -----------------//
            uDatabase.child(uId).child("name").setValue(name.getText().toString().trim());
            uDatabase.child(uId).child("userState").setValue(spState.getSelectedItem().toString());

            UserInFormation.setUserName(name.getText().toString());


            // -------------- Get auth credentials from the user for re-authentication
            if(!Email.getText().toString().equals(mAuth.getCurrentUser().getEmail())) {
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()), oldPass); // Current Login Credentials \\
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //----------------Code for Changing Email Address----------\\
                        mAuth.getCurrentUser().updateEmail(Email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    uDatabase.child(uId).child("email").setValue(Email.getText().toString().trim());
                                }
                            }
                        });
                    }
                });
            } else {
                Log.i(TAG, "The Email is the same no need to re auth");
            }

            if(bitmap != null) {
                handleUpload(bitmap);
                mdialog.setMessage("جاري تحديث الصور الشخصية ...");
                mdialog.show();
                Log.i(TAG, "Photo Updated and current user is : " + FirebaseAuth.getInstance().getCurrentUser());
            } else {
                Log.i(TAG, "no Photo to update.");
                Toast.makeText(UserSetting.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
                finish();
                whichProfile();
            }

            if(chkStateNoti.isChecked()) {
                uDatabase.child(uId).child("sendOrderNoti").setValue("true");
            } else {
                uDatabase.child(uId).child("sendOrderNoti").setValue("false");
            }

        });
    }

    private int getIndex(Spinner spinner, String value) {
        for(int i=0;i <spinner.getCount(); i++) {
            if(spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
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
                Log.i(TAG, "Returned a Resized Photo");
                return result;
            } else {
                int targetWidth = maxLength;
                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                Log.i(TAG, "Returned a Resized Photo");
                return result;
            }
        }
        catch (Exception e)
        {
            Log.i("SignUp", "Returned the source Photo");
            return source;
        }
    }

    private void whichProfile () {
        if(UserInFormation.getAccountType().equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }
}
