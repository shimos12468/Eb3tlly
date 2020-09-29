package com.armjld.eb3tly.SignUp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;

import Model.UserInFormation;
import com.armjld.eb3tly.Login.Login_Options;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import Model.userData;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class New_SignUp extends AppCompatActivity {

    private ProgressDialog mdialog;
    private ViewFlipper viewFlipper;
    FloatingActionButton btnNext,btnPrev;
    EditText txtFirstName, txtLastName, txtEmail, txtPass1, txtPass2, txtPhone;
    EditText txtCode;
    ImageView btnBack,btnDelivery, btnSupplier;
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    String acDate = sdf2.format(new Date());
    RadioButton rdMotor, rdTruck, rdCar, rdTrans;
    String phoneNumb;

    public String mVerificationId = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase, nDatabase;
    private String TAG = "SignUp";
    private TextView txtCCode;
    public static String defultPP = "https://firebasestorage.googleapis.com/v0/b/pickly-ed2f4.appspot.com/o/ppUsers%2Fdefult.jpg?alt=media&token=a1b6b5cc-6f03-41fa-acf2-0c14e601935f";
    private ImageView imgSetPP;
    String newType = "Delivery Worker";
    CountryCodePicker ccp;
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    String cCode = "+20";
    public static String provider = "Email";

    public static String newFirstName = "";
    public static String newLastName = "";
    public static String newEmail = "";
    public static String newPass ="";

    String isCar = "false";
    String isTruck = "false";
    String isMotor = "false";
    String isTrans = "false";
    String City = "15 مايو";
    String Gov = "القاهرة";

    Button btnCar,btnTruck,btnMotor,btnTrans;
    Spinner spnGov, spnCity;

    public static AuthCredential googleCred;
    public static AuthCredential faceCred;


    @Override
    public void onBackPressed() {
        showPrev();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__sign_up);
        
        viewFlipper = findViewById(R.id.viewFlipper);
        mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("حساب جديد");

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnBack = findViewById(R.id.btnBack);
        txtCCode = findViewById(R.id.txtCCode);

        btnDelivery = findViewById(R.id.btnDelivery);
        btnSupplier = findViewById(R.id.btnSupplier);

        btnCar = findViewById(R.id.btnCar);
        btnTruck = findViewById(R.id.btnTruck);
        btnTrans = findViewById(R.id.btnTrans);
        btnMotor = findViewById(R.id.btnMotor);
        spnCity = findViewById(R.id.spnCity);
        spnGov = findViewById(R.id.spnGov);
        rdCar  =findViewById(R.id.rdCar);
        rdMotor = findViewById(R.id.rdMotor);
        rdTrans = findViewById(R.id.rdTrans);
        rdTruck = findViewById(R.id.rdTruck);


        txtFirstName= findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass1 = findViewById(R.id.txtPass1);
        txtPass2 = findViewById(R.id.txtPass2);
        imgSetPP = findViewById(R.id.imgEditPhoto);
        txtPhone = findViewById(R.id.txtPhone);
        ccp = findViewById(R.id.ccp);

        txtCode = findViewById(R.id.txtCode);


        btnNext.setVisibility(View.GONE);
        btnPrev.setVisibility(View.GONE);
        viewFlipper.setDisplayedChild(0);

        Log.i(TAG, "PPURL : " + defultPP);
        Picasso.get().load(Uri.parse(defultPP)).into(imgSetPP);
        txtEmail.setText(newEmail);
        txtLastName.setText(newLastName);
        txtFirstName.setText(newFirstName);

        if(provider.equals("Google") || provider.equals("facebook")) {
            txtEmail.setEnabled(false);
            txtEmail.setFocusable(false);
            txtEmail.setKeyListener(null);
        }

        btnBack.setOnClickListener(v-> showPrev());

        btnCar.setOnClickListener(v-> {
            isCar = "true";
            isTruck = "false";
            isMotor = "false";
            isTrans = "false";

            btnCar.setBackgroundResource(R.drawable.btn_defult);
            btnTruck.setBackgroundResource(R.drawable.btn_bad);
            btnTrans.setBackgroundResource(R.drawable.btn_bad);
            btnMotor.setBackgroundResource(R.drawable.btn_bad);
            rdCar.setChecked(true);
            rdTruck.setChecked(false);
            rdTrans.setChecked(false);
            rdMotor.setChecked(false);
        });

        btnMotor.setOnClickListener(v-> {
            isCar = "false";
            isTruck = "false";
            isMotor = "true";
            isTrans = "false";

            btnMotor.setBackgroundResource(R.drawable.btn_defult);
            btnTruck.setBackgroundResource(R.drawable.btn_bad);
            btnTrans.setBackgroundResource(R.drawable.btn_bad);
            btnCar.setBackgroundResource(R.drawable.btn_bad);

            rdCar.setChecked(false);
            rdTruck.setChecked(false);
            rdTrans.setChecked(false);
            rdMotor.setChecked(true);
        });

        btnTrans.setOnClickListener(v-> {
            isCar = "false";
            isTruck = "false";
            isMotor = "false";
            isTrans = "true";

            btnTrans.setBackgroundResource(R.drawable.btn_defult);
            btnTruck.setBackgroundResource(R.drawable.btn_bad);
            btnMotor.setBackgroundResource(R.drawable.btn_bad);
            btnCar.setBackgroundResource(R.drawable.btn_bad);

            rdCar.setChecked(false);
            rdTruck.setChecked(false);
            rdTrans.setChecked(true);
            rdMotor.setChecked(false);
        });

        btnTruck.setOnClickListener(v-> {
            isCar = "false";
            isTruck = "true";
            isMotor = "false";
            isTrans = "false";

            btnTruck.setBackgroundResource(R.drawable.btn_defult);
            btnTrans.setBackgroundResource(R.drawable.btn_bad);
            btnMotor.setBackgroundResource(R.drawable.btn_bad);
            btnCar.setBackgroundResource(R.drawable.btn_bad);

            rdCar.setChecked(false);
            rdTruck.setChecked(true);
            rdTrans.setChecked(false);
            rdMotor.setChecked(false);
        });

        ccp.setOnCountryChangeListener(selectedCountry -> {
            cCode = ccp.getSelectedCountryCodeWithPlus();
            txtCCode.setText(ccp.getSelectedCountryCodeWithPlus());
        });

        btnNext.setOnClickListener(v-> showNext());

        btnPrev.setOnClickListener(v-> showPrev());

        //Set PP
        imgSetPP.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(New_SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        btnDelivery.setOnClickListener(v-> {
            //btnDelivery.setSelected(true);
            newType = "Delivery Worker";
            viewFlipper.setDisplayedChild(1);
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.VISIBLE);
            clearTexts();
        });

        btnSupplier.setOnClickListener(v-> {
            //btnSupplier.setSelected(true);
            newType = "Supplier";
            viewFlipper.setDisplayedChild(2);
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.VISIBLE);
            clearTexts();
        });

        // Pick up Government Spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGov.setPrompt("اختار المحافظة");
        spnGov.setAdapter(adapter2);
        // Get the Government Regions
        spnGov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Gov = spnGov.getSelectedItem().toString();
                int itemSelected = spnGov.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القاهرة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الجيزة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار محطة المترو");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القليوبية");
                    spnCity.setAdapter(adapter4);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الشرقية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الدقهليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسيوط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسوان");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنوفية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاقصر");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحيرة");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بين سويف");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بور سعيد");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة دمياط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة سوهاج");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة السويس");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الغربية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الفييوم");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة قنا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة مطروح");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنيا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spnCity.setAdapter(adapter4);
                } else {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(New_SignUp.this, R.array.justAll, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                    spnCity.setAdapter(adapter4);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City = spnCity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void showPrev() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0 : {
                startActivity(new Intent(this, Login_Options.class));
                break;
            }
            case 1 : {
                viewFlipper.showPrevious();
                btnPrev.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                break;
            }
            case 2 : {
                if(newType.equals("Supplier")) {
                    btnPrev.setVisibility(View.GONE);
                    btnNext.setVisibility(View.GONE);
                    viewFlipper.setDisplayedChild(0);
                } else if(newType.equals("Delivery Worker")) {
                    viewFlipper.setDisplayedChild(1);
                }
                break;
            }
            default: {
                viewFlipper.showPrevious();
                break;
            }
        }
    }
    private void showNext() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0 : {
                viewFlipper.showNext();
                break;
            }
            case 1 : {
                if(isCar.equals("false") && isMotor.equals("false") && isTrans.equals("false") && isTruck.equals("false")) {
                    Toast.makeText(this, "الرجاء اختيار وسيلة نقل واحدة", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewFlipper.showNext();
                break;
            }
            case 2 : {
                String phone = txtPhone.getText().toString();
                if(txtFirstName.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء ادخال الاسم الاول", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(txtLastName.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء ادخال الاسم الاخير", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(txtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(this, "ارجاء ادخال البريد الالكتروني", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(txtPhone.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء ادخال رقم الهاتف", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(txtPass1.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء ادخال كلمه السر", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(txtPass1.getText().toString().length() < 6) {
                    Toast.makeText(this, "الرجاء ادخال كلمه سر من 6 ارقام علي الاقل", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!txtPass1.getText().toString().equals(txtPass2.getText().toString())) {
                    Toast.makeText(this, "تاكد من تطابق كلمة السر", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phone.charAt(0) == '1' && phone.length() != 10) {
                    Toast.makeText(this, "رقم الهاتف غير صحيح", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Returned First");
                    return;
                }

                if(phone.charAt(0) == '0' && phone.length() != 11) {
                    Toast.makeText(this, "رقم الهاتف غير صحيح", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Returned Second");
                    return;
                }

                newEmail = txtEmail.getText().toString();
                newPass = txtPass1.getText().toString();
                newFirstName = txtFirstName.getText().toString();
                newLastName = txtLastName.getText().toString();

                mdialog.setMessage("جاري التاكد من رقم الهاتف ..");
                mdialog.show();

                if(phone.length() == 10) {
                    phoneNumb = phone;
                } else {
                    phoneNumb = phone.substring(1,11);
                }

                uDatabase.orderByChild("phone").equalTo("0"+phoneNumb).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Log.i(TAG ,"Phone Number is Already Exist For uID : " + snapshot.getValue().toString());
                            mdialog.dismiss();
                            Toast.makeText(New_SignUp.this, "رقم الهاتف مسجل مسبقا", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG ,"Phone Number isn't Exist, Let's Continue");
                            mdialog.setMessage("جاري ارسال الكود ..");
                            mCallBack();
                            sendCode(phoneNumb);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(New_SignUp.this, "حدث خطأ في التاكد من البيانات", Toast.LENGTH_SHORT).show();
                        mdialog.dismiss();
                    }});
                break;
            }

            case 3 : {
                if(txtCode.getText().toString().length() != 6) {
                    Toast.makeText(this, "الكود الذي ادخلته خطأ", Toast.LENGTH_SHORT).show();
                    return;
                }

                mdialog.setMessage("جاري التأكد من الرمز ..");
                mdialog.show();
                verifyPhoneNumberWithCode(mVerificationId, txtCode.getText().toString().trim());
                Log.i(TAG, "Gonne Send Verfiy : " + mVerificationId);
                break;
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendCode(String uPhone) {
        Log.i(TAG, "Sending Code to Phone Numb " + cCode + uPhone);

        if(mCallbacks == null) {
            mCallBack();
            Log.i(TAG, "mCallbacks was null");
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                cCode + uPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        Log.i(TAG, "verifyPhoneNumberWithCode : " + verificationId);
        mdialog.setMessage("جاري التاكد من الكود ..");
        if(verificationId.equals("")) {
            Toast.makeText(this, "We Are Sorry", Toast.LENGTH_SHORT).show();
            mdialog.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        if(provider.equals("Google")) {
            linkGoogle(credential);
        } else if(provider.equals("facebook")) {
            linkFace(credential);
        } else {
            signUp(credential);
        }
    }

    private void setUserData() {
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String memail = txtEmail.getText().toString().trim().toLowerCase();
        String mpass = txtPass1.getText().toString().trim();
        String muser = newFirstName + " " + newLastName;
        String mPhone = "0"+phoneNumb;

        userData data= new userData(muser, mPhone, memail, acDate, id, newType, defultPP, mpass, "0");
        uDatabase.child(id).setValue(data);
        uDatabase.child(id).child("completed").setValue("true");
        uDatabase.child(id).child("profit").setValue("0");
        uDatabase.child(id).child("active").setValue("true");
        uDatabase.child(id).child("isConfirmed").setValue("false");

        if(newType.equals("Delivery Worker")) {
            uDatabase.child(id).child("isCar").setValue(isCar);
            uDatabase.child(id).child("isTrans").setValue(isTrans);
            uDatabase.child(id).child("isMotor").setValue(isMotor);
            uDatabase.child(id).child("isTruck").setValue(isTruck);
            uDatabase.child(id).child("userState").setValue(Gov);
            uDatabase.child(id).child("userCity").setValue(City);
        }

        // ------------------ Set Device Token ----------------- //
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(New_SignUp.this, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            uDatabase.child(id).child("device_token").setValue(deviceToken);
        });

        if(bitmap != null) {
            handleUpload(bitmap);
        } else {
            uDatabase.child(id).child("ppURL").setValue(defultPP);
            mdialog.dismiss();
        }

        LoginManager _lgnMn = new LoginManager();
        _lgnMn.setMyInfo(New_SignUp.this);

        Toast.makeText(getApplicationContext(),"تم انشاء حسابك بنجاح" , Toast.LENGTH_LONG).show();
        mdialog.dismiss();
    }

    private void linkGoogle(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(googleCred).addOnCompleteListener(New_SignUp.this, googleSign -> {
            if(googleSign.isSuccessful() && mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(New_SignUp.this, taskPhone -> {
                    if(taskPhone.isSuccessful()) {
                        AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(emailCred).addOnCompleteListener(New_SignUp.this, taskEmail -> {
                            if(taskEmail.isSuccessful()) {
                                setUserData();
                            } else {
                                mdialog.dismiss();
                                Toast.makeText(this, "حدث خطأ ما حاول لاحقا", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void linkFace(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(faceCred).addOnCompleteListener(New_SignUp.this, faceSign -> {
            if(faceSign.isSuccessful() && mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(New_SignUp.this, taskPhone -> {
                    if(taskPhone.isSuccessful()) {
                        AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                       mAuth.getCurrentUser().linkWithCredential(emailCred).addOnCompleteListener(New_SignUp.this, taskEmail -> {
                            if(taskEmail.isSuccessful()) {
                                setUserData();
                            } else {
                                mdialog.dismiss();
                                Toast.makeText(this, "حدث خطأ ما حاول لاحقا", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void signUp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(New_SignUp.this, taskPhone -> {
            if(taskPhone.isSuccessful()) {
                AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(emailCred).addOnCompleteListener(New_SignUp.this, taskEmail -> {
                   if(taskEmail.isSuccessful()) {
                       setUserData();
                   } else {
                       Toast.makeText(this, "حدث خطأ ما حاول لاحقا", Toast.LENGTH_SHORT).show();
                   }
                });
            } else {
                if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --------------------------------- Phone Number Functions -------------------------- //
    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                txtCode.setText(credential.getSmsCode());
                mdialog.setMessage("جاري التأكد من الرمز ..");
                mdialog.show();
                if(provider.equals("Google")) {
                    linkGoogle(credential);
                } else if(provider.equals("facebook")) {
                    linkFace(credential);
                } else {
                    signUp(credential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mdialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(New_SignUp.this, "رقم هاتف غير صحيح", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "لقد حاولت كثيراو حاول بعد قليلي", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toast.makeText(New_SignUp.this, "تم ارسال الرمز", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
                mVerificationId = verificationId;
                Log.i(TAG, "Code has been sent to : " + phoneNumb + " and Verf id has been set : " + mVerificationId);
                viewFlipper.setDisplayedChild(3);
            }
        };
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap (source, 150);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(New_SignUp.this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;
            Log.i(TAG,"uri : " + uri.toString());
            imgSetPP.setImageBitmap(bitmap);
        }
    }

    @SuppressLint({"NewApi", "Recycle"})
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
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
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
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
                } else if (orintation == 3) {
                    matrix.postRotate(180);
                } else if (orintation == 8) {
                    matrix.postRotate(270);
                }
                Bitmap rotatedmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                return rotatedmap;
            }
            else {
                return bitmap;
            }
        }
        else {
            return bitmap;
        }
    }
    private void handleUpload (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        String uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uID + ".jpeg");
        final String did = uID;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            getDownUrl(did, reference);
            Log.i("Sign UP", " onSuccess");
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
        Log.i("Sign UP", " Handel Upload");
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.i("Sign UP", " add Profile URL");
            uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(New_SignUp.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(New_SignUp.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(New_SignUp.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(New_SignUp.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm= (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void clearTexts() {
        if(provider.equals("Email")) {
            txtFirstName.setText("");
            txtEmail.setText("");
            txtLastName.setText("");
        }
        txtPhone.setText("");
        txtPass1.setText("");
        txtPass2.setText("");
    }
}