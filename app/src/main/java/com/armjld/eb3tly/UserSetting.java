package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class UserSetting extends AppCompatActivity {

    EditText name;
    EditText Email;
    Button confirm;
    private ImageView UserImage;
    String  email,Name;
    int TAKE_IMAGE_CODE = 10001;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private Bitmap bitmap;
    private ProgressDialog mdialog;
    private String ppURL = "";
    String oldPass = "";
    String TAG = "User Settings";

    public void onBackPressed () {
        Intent newIntentNB = new Intent(this, profile.class);
        newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntentNB);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(UserSetting.this, photoUri));
                Log.i(TAG,"uri : " + uri.toString());
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri ,photoUri);
            }
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

    private Bitmap rotateImage(Bitmap bitmap , Uri uri , Uri photouri){
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

        if(orintation == 6 || orintation == 3 || orintation == 8) {
            Matrix matrix = new Matrix();
            if (orintation == 6) {
                matrix.postRotate(90);
            } else if (orintation == 3) {
                matrix.postRotate(180);
            } else if (orintation == 8) {
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
                Log.i("Updating ", " onSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Upload Error: ", "Fail:", e.getCause());
            }
        });
        Log.i("Updating", " Handel Upload");
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("Sign UP", " add Profile URL");
                uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
                mdialog.dismiss();
                Toast.makeText(UserSetting.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), profile.class));
                ppURL = uri.toString();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تغيير بيانات الحساب");

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        UserImage = findViewById(R.id.imgEditPhoto);
        name = findViewById(R.id.txtEditName);
        Email = findViewById(R.id.txtEditEmail);
        mdialog = new ProgressDialog(this);
        confirm = findViewById(R.id.btnEditInfo);


        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child("ppURL").getValue().toString();
                oldPass = dataSnapshot.child("mpass").getValue().toString();
                name.setText(dataSnapshot.child("name").getValue().toString());
                Email.setText(dataSnapshot.child("email").getValue().toString());
                Picasso.get().load(Uri.parse(url)).into(UserImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Id = mAuth.getCurrentUser().getUid().toString();
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


                final String id = mAuth.getCurrentUser().getUid();
                FirebaseUser user = mAuth.getCurrentUser();

                // ------------------ Update the Name -----------------//
                uDatabase.child(id).child("name").setValue(name.getText().toString().trim());

                // -------------- Get auth credentials from the user for re-authentication
                if(!Email.getText().equals(mAuth.getCurrentUser().getEmail())) {
                    AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), oldPass); // Current Login Credentials \\
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "Auth Success");
                            Log.i(TAG, "Prev Email " + FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                            //----------------Code for Changing Email Address----------\\
                            mAuth.getCurrentUser().updateEmail(Email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("email").setValue(Email.getText().toString().trim());
                                        Log.i(TAG, "New Email " + FirebaseAuth.getInstance().getCurrentUser().getEmail().toString() + " ID : " + mAuth.getCurrentUser().getUid());
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
                    Log.i(TAG, "Photo Updated and current user is : " + mAuth.getInstance().getCurrentUser());
                } else {
                    Log.i(TAG, "no Photo to update.");
                    Toast.makeText(UserSetting.this, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), profile.class));
                }
            }
        });
    }
}
