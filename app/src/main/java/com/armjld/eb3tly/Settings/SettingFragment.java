package com.armjld.eb3tly.Settings;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Home.HomeFragment;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;

import Model.UserInFormation;

import com.armjld.eb3tly.Settings.Wallet.MyWallet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;


public class SettingFragment extends Fragment {

    TextView txtName,txtType,txtPhone;
    TextView txtPassSettings,txtLocationSettings,txtWallet,txtReports,txtSignOut,txtContact,txtAbout,txtShare;
    ImageView imgPPP,btnBack;
    DatabaseReference uDatabase;
    FirebaseAuth mAuth;
    String uId;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchNotiGov,switchNotiCity;
    static String TAG = "Settings";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    private Bitmap bitmap;
    private ProgressDialog mdialog;



    public SettingFragment() { }
    
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        uId =  UserInFormation.getId();
        mdialog = new ProgressDialog(getActivity());

        txtName = view.findViewById(R.id.txtName);
        txtType = view.findViewById(R.id.txtType);
        txtPhone = view.findViewById(R.id.txtPhone);
        imgPPP = view.findViewById(R.id.imgPPP);

        txtPassSettings = view.findViewById(R.id.txtPassSettings);
        txtLocationSettings = view.findViewById(R.id.txtLocationSettings);
        txtWallet = view.findViewById(R.id.txtWallet);
        txtReports = view.findViewById(R.id.txtReports);
        txtSignOut = view.findViewById(R.id.txtSignOut);
        btnBack = view.findViewById(R.id.btnBack);
        txtContact = view.findViewById(R.id.txtContact);
        txtAbout = view.findViewById(R.id.txtAbout);
        txtShare  = view.findViewById(R.id.txtShare);
        switchNotiGov = view.findViewById(R.id.switchNotiGov);
        switchNotiCity = view.findViewById(R.id.switchNotiCity);

        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("الاعدادات");

        setUserData();
        btnBack.setOnClickListener(v-> {
            HomeActivity.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), HomeActivity.whichFrag).addToBackStack("Home").commit();
            HomeActivity.bottomNavigationView.setSelectedItemId(R.id.home);
        });

        txtPassSettings.setOnClickListener(v-> startActivity(new Intent(getActivity(), ChangePassword.class)));
        txtContact.setOnClickListener(v->startActivity(new Intent(getActivity(), Conatact.class)));
        txtAbout.setOnClickListener(v->startActivity(new Intent(getActivity(), About.class)));

        txtShare.setOnClickListener(v->{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
        });

        switchNotiGov.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                uDatabase.child(uId).child("sendOrderNoti").setValue("true");
                UserInFormation.setSendGovNoti("true");
            } else {
                uDatabase.child(uId).child("sendOrderNoti").setValue("false");
                UserInFormation.setSendGovNoti("false");
            }
        });

        switchNotiCity.setEnabled(false);

        switchNotiCity.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                uDatabase.child(uId).child("sendOrderNotiCity").setValue("true");
                UserInFormation.setSendCityNoti("true");
            } else {
                uDatabase.child(uId).child("sendOrderNotiCity").setValue("false");
                UserInFormation.setSendCityNoti("false");
            }
        });

        txtLocationSettings.setOnClickListener(v-> {
            if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(getActivity(), LocationForDelv.class));
            } else {
                startActivity(new Intent(getActivity(), LocationForSup.class));
            }
        });

        txtWallet.setOnClickListener(v-> {
            startActivity(new Intent(getActivity(), MyWallet.class));
        });

        txtReports.setOnClickListener(v-> {
            if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(getActivity(), delv_statics.class));
            } else if(UserInFormation.getAccountType().equals("Supplier")) {
                startActivity(new Intent(getActivity(), sup_statics.class));
            }
        });

        txtSignOut.setOnClickListener(v-> {
            signOut();
        });

        imgPPP.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });
        
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setUserData() {// ------------ Set User Data ----------- //
        String uType;
        txtName.setText(UserInFormation.getUserName());
        txtPhone.setText("+2" + UserInFormation.getPhone());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgPPP);
        if(UserInFormation.getAccountType().equals("Supplier")) {
            uType = "تاجر";
            txtWallet.setVisibility(View.GONE);
            switchNotiCity.setVisibility(View.GONE);
            switchNotiGov.setVisibility(View.GONE);
        } else if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            uType = "كابتن";
            txtWallet.setVisibility(View.VISIBLE);
            switchNotiCity.setVisibility(View.VISIBLE);
            switchNotiGov.setVisibility(View.VISIBLE);
        } else {
            uType = "خدمة عملاء";
            txtWallet.setVisibility(View.GONE);
            switchNotiCity.setVisibility(View.GONE);
            switchNotiGov.setVisibility(View.GONE);
        }
        txtType.setText(uType);

        if(UserInFormation.getSendGovNoti().equals("true")) {
            switchNotiGov.setChecked(true);
        } else {
            switchNotiGov.setChecked(false);
        }

        if(UserInFormation.getSendCityNoti().equals("true")) {
            switchNotiCity.setChecked(true);
        } else {
            switchNotiCity.setChecked(false);
        }

    }

    private void signOut() {
        LoginManager _lgnMng = new LoginManager();
        _lgnMng.clearInfo(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 150);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(getActivity(), photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;

            mdialog.setMessage("تحديث الصورة الشخصية ..");
            mdialog.show();
            imgPPP.setImageBitmap(bitmap);
            handleUpload(bitmap);
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
            Toast.makeText(getActivity(), "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
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