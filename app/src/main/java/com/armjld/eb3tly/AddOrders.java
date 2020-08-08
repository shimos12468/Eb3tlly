package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;
import Model.notiData;

import static com.armjld.eb3tly.R.drawable.ic_add_money;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class AddOrders extends AppCompatActivity {

    private static final String TAG = "Add Orders";
    private EditText PAddress, PShop, DAddress, DDate, DPhone, DName, GMoney, GGet, txtNotes;
    private CheckBox chkMetro, chkTrans, chkCar, chkMotor;
    private Spinner spPState, spPRegion, spDState, spDRegion;
    private FirebaseAuth mAuth;
    private Button btnsave,btnSaveAdd;
    private String uId;
    String uType = UserInFormation.getAccountType();
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase, mDatabase, rDatabase, nDatabase, vDatabase;
    private ImageView btnClose, imgHelpMoney, imgHelpGet;
    DatePickerDialog dpd;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
        whichProfile();
    }

    private void whichProfile () {
        if(uType.equals("Supplier")) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NewProfile.class));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        // Toolbar
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("اضافة اوردر جديد");

        // Texts
        PAddress = findViewById(R.id.txtPAddress);
        PShop = findViewById(R.id.txtPShop);
        DAddress = findViewById(R.id.txtDAddress);
        DDate = findViewById(R.id.txtDDate);
        DPhone = findViewById(R.id.txtDPhone);
        DName = findViewById(R.id.txtDName);
        GMoney = findViewById(R.id.txtGMoney);
        GGet = findViewById(R.id.txtGGet);
        txtNotes = findViewById(R.id.txtNotes);

        //Check Boxes
        chkMetro = findViewById(R.id.chkMetro);
        chkCar = findViewById(R.id.chkCar);
        chkMotor = findViewById(R.id.chkMotor);
        chkTrans = findViewById(R.id.chkTrans);

        //Spinners
        spPState =  findViewById(R.id.txtPState);
        spPRegion = findViewById(R.id.txtPRegion);
        spDState =  findViewById(R.id.txtDState);
        spDRegion = findViewById(R.id.txtDRegion);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);
        imgHelpMoney = findViewById(R.id.imgHelpMoney);
        imgHelpGet = findViewById(R.id.imgHelpGet);

        btnsave = findViewById(R.id.btnSave);
        btnSaveAdd = findViewById(R.id.btnSaveAdd);

        mdialog = new ProgressDialog(this);


        // Firebasee
        mAuth = FirebaseAuth.getInstance();
        uId = UserInFormation.getId();
        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        // ---------------- Help Buttons----------------------//
        imgHelpGet.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(AddOrders.this).create();
            alertDialog.setTitle("مصاريف الشحن");
            alertDialog.setMessage("حدد مبلغ شحن مناسب كي يوافق المندوبين علي شحن الاوردر الخاص بك.");
            alertDialog.show();
        });

        imgHelpMoney.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(AddOrders.this).create();
            alertDialog.setTitle("مقدم الاوردر");
            alertDialog.setMessage("سعر الاوردر الي هيدفعو العميل بتاعك للمندوب عند التسليم");
            alertDialog.show();
        });

        // Pick up Government Spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPState.setPrompt("اختار المحافظة");
        spPState.setAdapter(adapter2);
        // Get the Government Regions
        spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int itemSelected = spPState.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار محطة المترو");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة القليوبية");
                    spPRegion.setAdapter(adapter4);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الشرقية");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة اسيوط");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة اسوان");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة المنوفية");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاقصر");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة البحيرة");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة بين سويف");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة دمياط");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة سوهاج");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة السويس");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الغربية");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الفييوم");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة قنا");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة مطروح");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة المنيا");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spPRegion.setAdapter(adapter4);
                } else {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddOrders.this, R.array.justAll, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                    spPRegion.setAdapter(adapter4);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Drop Government Spinner
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDState.setPrompt("اختار المحافظة");
        spDState.setAdapter(adapter3);
        // Get the Government Regions
        spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int itemSelected = spDState.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار محطة المترو");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة القليوبية");
                    spDRegion.setAdapter(adapter5);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الشرقية");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة اسيوط");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة اسوان");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة المنوفية");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاقصر");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة البحيرة");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة بين سويف");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة دمياط");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة سوهاج");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة السويس");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الغربية");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الفييوم");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة قنا");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة مطروح");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة المنيا");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spDRegion.setAdapter(adapter5);
                } else {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(AddOrders.this, R.array.justAll, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                    spDRegion.setAdapter(adapter5);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SharedPreferences sharedPreferences = this.getSharedPreferences("Location", MODE_PRIVATE);
        String pState = sharedPreferences.getString("State", null);
        String pRegion = sharedPreferences.getString("Region", null);
        String pStroe = sharedPreferences.getString("Store", null);
        String pAddress = sharedPreferences.getString("Address", null);

        if(pState != null && pRegion != null && pStroe != null && pAddress != null) {
            PAddress.setText(pAddress);
            PShop.setText(pStroe);
            spPState.setSelection(getIndex(spPState, pState));
            spPRegion.setSelection(getIndex(spPRegion, pRegion));
        }

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener pdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String dFormat = "yyyy-MM-dd";
                SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.ENGLISH);
                DDate.setText(sDF.format(myCalendar.getTime()));
            }
        };

        DDate.setOnClickListener(v -> {
            dpd = new DatePickerDialog(AddOrders.this, pdate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            dp.setMinDate(myCalendar.getTimeInMillis() - 100); // disable all the previos dates
            dpd.show();
        });

        GGet.setOnFocusChangeListener((v, event) -> {
            if(!TextUtils.isEmpty(GMoney.getText().toString()) && GGet.isFocused()) {
                int min;
                int max;
                boolean isLess = false;

                int cash = Integer.parseInt(GMoney.getText().toString());
                String from = spPState.getSelectedItem().toString();
                String to = spDState.getSelectedItem().toString();

                if (cash <= 499) {
                    isLess = true;
                    min = 30;
                } else if (cash <= 1000) {
                    min = (int) (cash * 0.06);
                } else if (cash <= 1999) {
                    min = (int) (cash * 0.05);
                } else {
                    min = (int) (cash * 0.03);
                }

                if (!from.equals(to)) {
                    if ((!from.equals("الجيزة") || !to.equals("القاهرة")) && (!from.equals("القاهرة") || !to.equals("الجيزة"))) {
                        if(isLess) {min = min + 15;}
                        Double factor = checkFactor(from,to);
                        min = (int) (min * factor);
                    }
                }
                max = (int) (min * 1.4);

                GGet.setError("مصاريف الشحن المقترحه من : " + (Math.round(min/5.0)*5) + " ج الي : " + (Math.round(max/5.0)*5) + " ج");
            } else if(TextUtils.isEmpty(GMoney.getText().toString())) {
                GMoney.setError("ضع مقدم الاوردر اولا حتي نتمكن من اقتراح سعر الشحن");
            }
        });

        // ----------------- Save the Order --------------------//
        btnsave.setOnClickListener(v -> {
            // Txt Fields Strings
            final String mPAddress = PAddress.getText().toString().trim();
            final String mNote = txtNotes.getText().toString().trim();
            final String mPShop = PShop.getText().toString().trim();
            final String mDAddress = DAddress.getText().toString().trim();
            final String mDDate = DDate.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
            final String mDPhone = DPhone.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
            final String mDName = DName.getText().toString().trim();
            final String mGMoney = GMoney.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
            final String mGGet = GGet.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();

            // Checkboxes Strings
            String isTrans;
            String isMotor;
            String isMetro;
            String isCar;

            //DEFULT ORDER States ON ADD
            final String states = "placed";
            final String uAccepted = "";

            // Check if Empty
            if (TextUtils.isEmpty(mPAddress) && !spPState.getSelectedItem().toString().equals("مترو")) {
                PAddress.setError("الرجاء ادخال البيانات");
                PAddress.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(mDAddress) && !spDState.getSelectedItem().toString().equals("مترو")) {
                DAddress.setError("الرجاء ادخال البيانات");
                DAddress.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(mDDate)) {
                DDate.setError("الرجاء ادخال البيانات");
                Toast.makeText(AddOrders.this, "ادخل تاريخ تسليم الاوردر", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mDPhone.length() != 11) {
                DPhone.setError("الرجاء ادخال البيانات");
                DPhone.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(mDName)) {
                DName.setError("الرجاء ادخال البيانات");
                DName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(mGMoney)) {
                GMoney.setError("الرجاء ادخال البيانات");
                GMoney.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(mGGet)) {
                GGet.setError("الرجاء ادخال البيانات");
                GGet.requestFocus();
                return;
            }
            int intget = Integer.parseInt(mGGet);
            if(intget == 0) {
                GGet.setError("الرجاء وضع سعر للشحن");
                GGet.requestFocus();
                return;
            }
            // Check the way of transportation
            if (chkTrans.isChecked()) {
                isTrans = "مواصلات";
            } else {
                isTrans = "";
            }
            if (chkMetro.isChecked()) {
                isMetro = "مترو";
            } else {
                isMetro = "";
            }
            if (chkCar.isChecked()) {
                isCar = "سياره";
            } else {
                isCar = "";
            }
            if (chkMotor.isChecked()) {
                isMotor = "موتسكل";
            } else {
                isMotor = "";
            }

            final String finalIsMetro = isMetro;
            final String finalIsTrans = isTrans;
            final String finalIsMotor = isMotor;
            final String finalIsCar = isCar;
            DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mdialog.setMessage("جاري اضافة الاوردر");
                        mdialog.show();

                        String id = mDatabase.push().getKey(); // create Order ID
                        String srate = "false";
                        String srateid = "";
                        String drate = "false";
                        String drateid = "";
                        String pState12 = spPState.getSelectedItem().toString();
                        String dState = spDState.getSelectedItem().toString();

                        // Send order to Data Base using the DATA MODEL
                        Data data = new Data(pState12, spPRegion.getSelectedItem().toString(), mPAddress, mPShop, dState, spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                mDPhone, mDName, mGMoney, mGGet, datee, id, uId, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, "", "", mNote);
                        mDatabase.child(id).setValue(data);
                        mDatabase.child(id).child("lastedit").setValue(datee);

                        SharedPreferences sharedPreferences1 = getSharedPreferences("Location", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString("Store", mPShop);
                        editor.putString("State", spPState.getSelectedItem().toString());
                        editor.putString("Region", spPRegion.getSelectedItem().toString());
                        editor.putString("Address", mPAddress);
                        editor.apply();

                        // --------------------- Send notification to all users in State ---------------//
                        uDatabase.orderByChild("userState").equalTo(dState).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                        String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();
                                        if(accType.equals("Delivery Worker")) {
                                            String sendOrderNoti = "true";
                                            if(ds.child("sendOrderNoti").exists()) {
                                                sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNoti").getValue()).toString();
                                            }

                                            if(sendOrderNoti.equals("true")) {
                                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في منطقتك",datee,"false");
                                                nDatabase.child(usId).push().setValue(Noti);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        uDatabase.orderByChild("userState").equalTo(pState12).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                        String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();

                                        if(accType.equals("Delivery Worker")) {
                                            String sendOrderNoti = "true";
                                            if(ds.child("sendOrderNoti").exists()) {
                                                sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNoti").getValue()).toString();
                                            }

                                            if(sendOrderNoti.equals("true")) {
                                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في منطقتك",datee,"false");
                                                nDatabase.child(usId).push().setValue(Noti);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        mdialog.dismiss();
                        Toast.makeText(AddOrders.this, "تم اضافة اوردرك و في انتظار قبولة من مندوبين الشحن", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AddOrders.this, supplierProfile.class));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        mdialog.dismiss();
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(AddOrders.this);
            builder.setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
        });

        // ------------ Save the Order and Add Another -------------//
         btnSaveAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Txt Fields Strings
                final String mPAddress = PAddress.getText().toString().trim();
                final String mNote = txtNotes.getText().toString().trim();
                final String mPShop = PShop.getText().toString().trim();
                final String mDAddress = DAddress.getText().toString().trim();
                final String mDDate = DDate.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDPhone = DPhone.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDName = DName.getText().toString().trim();
                final String mGMoney = GMoney.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mGGet = GGet.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                int intget = Integer.parseInt(mGGet);

                // Checkboxes Strings
                String isTrans = "";
                String isMotor = "";
                String isMetro = "";
                String isCar = "";

                //DEFULT ORDER States ON ADD
                final String states = "placed";
                final String uAccepted = "";

                // Check if Empty
                if (TextUtils.isEmpty(mPAddress) && !spPState.getSelectedItem().toString().equals("مترو")) {
                    PAddress.setError("الرجاء ادخال البيانات");
                    return;
                }
                if (TextUtils.isEmpty(mDAddress) && !spDState.getSelectedItem().toString().equals("مترو")) {
                    DAddress.setError("الرجاء ادخال البيانات");
                    return;
                }
                if (TextUtils.isEmpty(mDDate)) {
                    DDate.setError("الرجاء ادخال البيانات");
                    return;
                }

                if(mDPhone.length() != 11) {
                    DPhone.setError("الرجاء ادخال البيانات");
                    return;
                }

                if (TextUtils.isEmpty(mDName)) {
                    DName.setError("الرجاء ادخال البيانات");
                    return;
                }
                if (TextUtils.isEmpty(mGMoney)) {
                    GMoney.setError("الرجاء ادخال البيانات");
                    return;
                }
                if (TextUtils.isEmpty(mGGet)) {
                    GGet.setError("الرجاء ادخال البيانات");
                    return;
                }
                if(intget == 0) {
                    GGet.setError("الرجاء وضع سعر للشحن");
                    GGet.requestFocus();
                    return;
                }

                // Check the way of transportation
                if (chkTrans.isChecked()) {
                    isTrans = "مواصلات";
                } else {
                    isTrans = "";
                }
                if (chkMetro.isChecked()) {
                    isMetro = "مترو";
                } else {
                    isMetro = "";
                }
                if (chkCar.isChecked()) {
                    isCar = "سياره";
                } else {
                    isCar = "";
                }
                if (chkMotor.isChecked()) {
                    isMotor = "موتسكل";
                } else {
                    isMotor = "";
                }
                final String finalIsMetro = isMetro;
                final String finalIsTrans = isTrans;
                final String finalIsMotor = isMotor;
                final String finalIsCar = isCar;
                DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mdialog.setMessage("جاري اضافة الاوردر");
                            mdialog.show();

                            String id = mDatabase.push().getKey(); // create Order ID
                            String srate = "false";
                            String srateid = "";
                            String drate = "false";
                            String drateid = "";
                            String pState1 = spPState.getSelectedItem().toString();
                            String dState = spDState.getSelectedItem().toString();

                            SharedPreferences sharedPreferences1 = getSharedPreferences("Location", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("Store", mPShop);
                            editor.putString("State", spPState.getSelectedItem().toString());
                            editor.putString("Region", spPRegion.getSelectedItem().toString());
                            editor.putString("Address", mPAddress);
                            editor.apply();

                            // Send order to Data Base using the DATA MODEL
                            Data data = new Data(pState1, spPRegion.getSelectedItem().toString(), mPAddress, mPShop, dState, spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                    mDPhone, mDName, mGMoney, mGGet, datee, id, uId, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, "", "", mNote);
                            assert id != null;
                            mDatabase.child(id).setValue(data);
                            mDatabase.child(id).child("lastedit").setValue(datee);

                            // --------------------- Send notification to all users in State ---------------//
                            uDatabase.orderByChild("userState").equalTo(dState).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();
                                            if(accType.equals("Delivery Worker")) {
                                                String sendOrderNoti = "true";
                                                if(ds.child("sendOrderNoti").exists()) {
                                                    sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNoti").getValue()).toString();
                                                }

                                                if(sendOrderNoti.equals("true")) {
                                                    notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في منطقتك",datee,"false");
                                                    nDatabase.child(usId).push().setValue(Noti);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });

                            uDatabase.orderByChild("userState").equalTo(pState1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
                                            String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();

                                            if(accType.equals("Delivery Worker")) {
                                                String sendOrderNoti = "true";
                                                if(ds.child("sendOrderNoti").exists()) {
                                                    sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNoti").getValue()).toString();
                                                }

                                                if(sendOrderNoti.equals("true")) {
                                                    notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في منطقتك",datee,"false");
                                                    nDatabase.child(usId).push().setValue(Noti);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });

                            clearText();
                            mdialog.dismiss();
                            Toast.makeText(AddOrders.this, "تم اضافة اوردرك و في انتظار قبولة من مندوبين الشحن يمكنك الان اضافه اوردر اخر", Toast.LENGTH_LONG).show();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            mdialog.dismiss();
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AddOrders.this);
                builder.setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
            }

            private void clearText() {
                dpd = null;
                txtNotes.setText("");
                DAddress.setText("");
                DPhone.setText("");
                DName.setText("");
                GMoney.setText("");
                GGet.setText("");
                chkMetro.setChecked(false);
                chkCar.setChecked(false);
                chkMotor.setChecked(false);
                chkTrans.setChecked(false);
                spDState.setSelection(0);
                spDRegion.setSelection(0);
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

    private Double checkFactor(String from, String to) {
        String Region = "";
        double factor;
        factor = (double) 0;

        if(from.equals("اسوان") || from.equals("الاقصر") || to.equals("اسوان") || to.equals("الاقصر")) {
            Region = "الصعيد";
        } else if(from.equals("الأسكندرية") || from.equals("البحيرة") || to.equals("الأسكندرية") || to.equals("البحيرة")) {
            Region = "بحري";
        } else if (from.equals("جنوب سيناء") || from.equals("شمال سيناء") || from.equals("البحر الاحمر") || from.equals("الوادي الجديد") || to.equals("جنوب سيناء") || to.equals("شمال سيناء") || to.equals("البحر الاحمر") || to.equals("الوادي الجديد")) {
            Region = "المنبوذين";
        } else if(from.equals("القليوبية") || from.equals("الشرقية") || from.equals("المنوفية") || to.equals("القليوبية") || to.equals("الشرقية") || to.equals("المنوفية")) {
            Region = "القاهرة الكبري";
        } else if(from.equals("الاسماعلية") || from.equals("السويس") || from.equals("بور سعيد")) {
            Region = "السويس";
        }

        switch (Region) {
            case "الصعيد" : {
                factor = (Double) 1.4;
                break;
            }
            case "بحري" : {
                factor = (Double) 1.2;
                break;
            }
            case "المنبوذين" : {
                factor = (Double) 1.6;
                break;
            }
            case "القاهرة الكبري" : {
                factor = (Double) 1.1;
                break;
            }
            case "السويس" : {
                factor = (Double) 1.3;
                break;
            }
            default: {
                factor = (Double) 1.3;
                break;
            }
        }
    return factor;
    }
}