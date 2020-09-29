package com.armjld.eb3tly.Settings;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Home.StartUp;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Login.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import Model.UserInFormation;

public class UserInfo extends AppCompatActivity {

    EditText Email;
    Button confirm;
    private ImageView UserImage;
    String email;
    int TAKE_IMAGE_CODE = 10001;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private Bitmap bitmap;
    private ProgressDialog mdialog;
    private String ppURL = "";
    String oldPass = "";
    private static String TAG = "User Settings";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    String uId = UserInFormation.getId();
    String isConfirmed = UserInFormation.getisConfirm();

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
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
        Email = findViewById(R.id.txtEditEmail);
        mdialog = new ProgressDialog(this);
        confirm = findViewById(R.id.btnEditInfo);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

        for(com.google.firebase.auth.UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if(user.getProviderId().equals("google.com")) {
                Email.setEnabled(false);
                Email.setClickable(false);
                Email.setKeyListener(null);
            } else {
                Email.setEnabled(true);
                Email.setClickable(true);
            }
        }

        /*if(isConfirmed.equals("false")) {
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
        }*/

        oldPass = UserInFormation.getPass();
        Email.setText(UserInFormation.getEmail());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(UserImage);

        UserImage.setOnClickListener(view -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(UserInfo.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        confirm.setOnClickListener(view -> {
            email = Email.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                Email.setError("يجب ادخال البريد ألالكتروني");
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if(!Email.getText().toString().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())) {
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()), oldPass); // Current Login Credentials \\
                assert user != null;
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    mAuth.getCurrentUser().updateEmail(Email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                uDatabase.child(uId).child("email").setValue(Email.getText().toString().trim());
                                UserInFormation.setEmail(Email.getText().toString().trim());
                            }
                        }
                    });
                });
            }

            if(bitmap != null) {
                handleUpload(bitmap);
                mdialog.setMessage("جاري تحديث الصور الشخصية ...");
                mdialog.show();
            } else {
                Log.i(TAG, "no Photo to update.");
                Toast.makeText(UserInfo.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 150);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(UserInfo.this, photoUri));
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
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
            Toast.makeText(UserInfo.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(UserInfo.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(UserInfo.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(UserInfo.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserInfo.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
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
}
