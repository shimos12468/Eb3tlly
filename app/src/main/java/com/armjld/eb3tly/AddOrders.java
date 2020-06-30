package com.armjld.eb3tly;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class AddOrders extends AppCompatActivity {

    private EditText PAddress, PShop, DAddress, DDate, DPhone, DName, GMoney, GGet, txtNotes;
    private CheckBox chkMetro, chkTrans, chkCar, chkMotor;
    private Spinner spPState, spPRegion, spDState, spDRegion;
    private FirebaseAuth mAuth;
    private Button btnsave,btnSaveAdd;
    private String uID;
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase, mDatabase, rDatabase, nDatabase, vDatabase;
    private ImageView btnClose;
    DatePickerDialog dpd;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, profile.class));
    }

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
        spPState = (Spinner) findViewById(R.id.txtPState);
        spPRegion = (Spinner) findViewById(R.id.txtPRegion);
        spDState = (Spinner) findViewById(R.id.txtDState);
        spDRegion = (Spinner) findViewById(R.id.txtDRegion);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);

        btnsave = findViewById(R.id.btnSave);
        btnSaveAdd = findViewById(R.id.btnSaveAdd);

        mdialog = new ProgressDialog(this);


        // Firebasee
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

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

        // ---------------------------------- Date Picker
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

        DDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd = new DatePickerDialog(AddOrders.this, pdate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMinDate(myCalendar.getTimeInMillis() - 100); // disable all the previos dates
                dpd.show();
            }
        });

        // ----------------- Save the Order --------------------//
        btnsave.setOnClickListener(new View.OnClickListener() {
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mdialog.setMessage("جاري اضافة الاوردر");
                                mdialog.show();
                                String id = mDatabase.push().getKey().toString(); // create Order ID
                                String srate = "false";
                                String srateid = "";
                                String drate = "false";
                                String drateid = "";
                                // Send order to Data Base using the DATA MODEL
                                Data data = new Data(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), mPAddress, mPShop, spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                        mDPhone, mDName, mGMoney, mGGet, datee, id, uID, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, "", "", mNote);
                                mDatabase.child(id).setValue(data);
                                mDatabase.child(id).child("lastedit").setValue(datee);
                                mdialog.dismiss();
                                Toast.makeText(AddOrders.this, "تم اضافة اوردرك و في انتظار قبولة من مندوبين الشحن", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddOrders.this, profile.class));
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                mdialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AddOrders.this);
                builder.setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
            }
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
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mdialog.setMessage("جاري اضافة الاوردر");
                                mdialog.show();
                                String id = mDatabase.push().getKey().toString(); // create Order ID
                                String srate = "false";
                                String srateid = "";
                                String drate = "false";
                                String drateid = "";
                                // Send order to Data Base using the DATA MODEL
                                Data data = new Data(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), mPAddress, mPShop, spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                        mDPhone, mDName, mGMoney, mGGet, datee, id, uID, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, "", "", mNote);
                                mDatabase.child(id).setValue(data);
                                mDatabase.child(id).child("lastedit").setValue(datee);
                                clearText();
                                mdialog.dismiss();
                                Toast.makeText(AddOrders.this, "تم اضافة اوردرك و في انتظار قبولة من مندوبين الشحن يمكنك الان اضافه اوردر اخر", Toast.LENGTH_LONG).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                mdialog.dismiss();
                                break;
                        }
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

}