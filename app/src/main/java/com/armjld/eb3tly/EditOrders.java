package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class EditOrders extends AppCompatActivity {

    private static final String TAG = "Edit Orders";
    private EditText PAddress, PShop, DAddress, DDate, DPhone, DName, GMoney, GGet, txtNotes;
    private CheckBox chkMetro, chkTrans, chkCar, chkMotor;
    private Spinner spPState, spPRegion, spDState, spDRegion;
    private FirebaseAuth mAuth;
    private Button btnsave;
    private String uID;
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase, mDatabase, rDatabase, nDatabase, vDatabase;
    private ImageView btnClose, imgHelpMoney, imgHelpGet;
    String statee, acceptedID;
    String srate, srateid,drate,drateid,orderDate,acceptedTime;
    String uId = UserInFormation.getId();


    DatePickerDialog dpd;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_orders);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderID = getIntent().getStringExtra("orderid");

        // Toolbar
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("تعديل بيانات الاوردر");

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
        imgHelpMoney = findViewById(R.id.imgHelpMoney);
        imgHelpGet = findViewById(R.id.imgHelpGet);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);

        btnsave = findViewById(R.id.btnSave);
        mdialog = new ProgressDialog(this);

        // Firebasee
        mAuth = FirebaseAuth.getInstance();
        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        // ---------------- Help Buttons----------------------//
        imgHelpGet.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(EditOrders.this).create();
            alertDialog.setTitle("مصاريف الشحن");
            alertDialog.setMessage("حدد مبلغ شحن مناسب كي يوافق المندوبين علي شحن الاوردر الخاص بك.");
            alertDialog.show();
        });

        imgHelpMoney.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(EditOrders.this).create();
            alertDialog.setTitle("مقدم الاوردر");
            alertDialog.setMessage("سعر الاوردر الي هيدفعو العميل بتاعك للمندوب عند التسليم");
            alertDialog.show();
        });

        // ------------------- Set the Order Data -------------------- //
        assert orderID != null;
        mDatabase.child(Objects.requireNonNull(orderID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;

                srate = orderData.getSrated();
                srateid = orderData.getSrateid();
                drate = orderData.getDrated();
                drateid = orderData.getDrateid();
                orderDate = orderData.getDate();
                acceptedTime = orderData.getAcceptedTime();
                acceptedID = orderData.getuAccepted();
                statee = orderData.getStatue();
                uID = orderData.getuId();
                PAddress.setText(orderData.getmPAddress().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                PShop.setText(orderData.getmPShop().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                DAddress.setText(orderData.getDAddress().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                DDate.setText(orderData.getDDate().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                DPhone.setText(orderData.getDPhone().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                DName.setText(orderData.getDName().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                GMoney.setText(orderData.getGMoney().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                GGet.setText(orderData.getGGet().toString().replaceAll("(^\\h*)|(\\h*$)","").trim());
                txtNotes.setText(orderData.getNotes());

                if(!orderData.getIsMetro().equals("")) {
                    chkMetro.setChecked(true);
                }
                if(!orderData.getIsCar().equals("")) {
                    chkCar.setChecked(true);
                }
                if(!orderData.getIsMotor().equals("")) {
                    chkMotor.setChecked(true);
                }
                if(!orderData.getIsTrans().equals("")) {
                    chkTrans.setChecked(true);
                }

                // ---------------------------- Pick up Government Spinner ----------------------------------------//
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtStates, R.layout.color_spinner_layout);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPState.setPrompt("اختار المحافظة");
                spPState.setAdapter(adapter2);
                spPState.setSelection(getIndex(spPState, orderData.getTxtPState()));

                // --------------------------- Drop Government Spinner -------------------------------------------//
                spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int itemSelected = spPState.getSelectedItemPosition();
                        if (itemSelected == 0) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 1) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 2) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 3) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار محطة المترو");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 4) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة القليوبية");
                            spPRegion.setAdapter(adapter4);
                        }else if (itemSelected == 5) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الشرقية");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 6) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 7) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة اسيوط");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 8) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة اسوان");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 9) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة المنوفية");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 10) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 11) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الاقصر");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 12) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة البحيرة");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 13) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة بين سويف");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 14) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 15) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 16) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 17) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة دمياط");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 18) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة سوهاج");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 19) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة السويس");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 20) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الغربية");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 21) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الفييوم");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 22) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة قنا");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 23) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 24) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                            spPRegion.setAdapter(adapter4);
                        }  else if (itemSelected == 25) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة مطروح");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 26) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة المنيا");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 27) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                            spPRegion.setAdapter(adapter4);
                        } else {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(EditOrders.this, R.array.justAll, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                            spPRegion.setAdapter(adapter4);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });

                // --------------------------- Drop Government Spinner -------------------------------------------//
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtStates, R.layout.color_spinner_layout);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDState.setPrompt("اختار المحافظة");
                spDState.setAdapter(adapter3);
                spDState.setSelection(getIndex(spDState, orderData.getTxtDState()));

                // ---------------------------- Drop Region Spinner -------------------------------------------//
                spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int itemSelected = spDState.getSelectedItemPosition();
                        if (itemSelected == 0) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 1) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 2) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 3) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار محطة المترو");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 4) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة القليوبية");
                            spDRegion.setAdapter(adapter5);
                        }else if (itemSelected == 5) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الشرقية");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 6) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 7) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة اسيوط");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 8) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة اسوان");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 9) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة المنوفية");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 10) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 11) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الاقصر");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 12) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة البحيرة");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 13) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة بين سويف");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 14) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 15) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 16) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 17) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة دمياط");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 18) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة سوهاج");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 19) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة السويس");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 20) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الغربية");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 21) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الفييوم");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 22) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة قنا");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 23) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 24) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                            spDRegion.setAdapter(adapter5);
                        }  else if (itemSelected == 25) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة مطروح");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 26) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة المنيا");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 27) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                            spDRegion.setAdapter(adapter5);
                        } else {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(EditOrders.this, R.array.justAll, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                            spDRegion.setAdapter(adapter5);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });

                if(spDRegion.getAdapter() != null && spPRegion.getAdapter() != null) {
                    spDRegion.setSelection(getIndex(spDRegion, orderData.getmDRegion()));
                    spPRegion.setSelection(getIndex(spPRegion, orderData.getmPRegion()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



        // ---------------------------------- Date Picker --------------------------------- //
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
                dpd = new DatePickerDialog(EditOrders.this, pdate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
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
                final String mPAddress = PAddress.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mNote = txtNotes.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mPShop = PShop.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDAddress = DAddress.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDDate = DDate.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDPhone = DPhone.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mDName = DName.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mGMoney = GMoney.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();
                final String mGGet = GGet.getText().toString().replaceAll("(^\\h*)|(\\h*$)","").trim();

                // Checkboxes Strings
                String isTrans = "";
                String isMotor = "";
                String isMetro = "";
                String isCar = "";

                //DEFULT ORDER States ON ADD
                final String states = statee;
                final String uAccepted = acceptedID;

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
                    Toast.makeText(EditOrders.this, "ادخل تاريخ تسليم الاوردر", Toast.LENGTH_SHORT).show();
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

                                Data data = new Data(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), mPAddress, mPShop, spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), mDAddress, mDDate,
                                        mDPhone, mDName, mGMoney, mGGet, orderDate, orderID, uID, finalIsTrans, finalIsMetro, finalIsMotor, finalIsCar, states, uAccepted, srate, srateid, drate, drateid, acceptedTime, "", mNote);
                                mDatabase.child(orderID).setValue(data);
                                mDatabase.child(orderID).child("lastedit").setValue(datee);

                                if(!uAccepted.equals("")) {
                                    // --------------------------- Send Notifications ---------------------//
                                    notiData Noti = new notiData(uID, uAccepted,orderID,"edited",datee,"false", "order");
                                    nDatabase.child(uAccepted).push().setValue(Noti);
                                }
                                mdialog.dismiss();
                                if(UserInFormation.getAccountType().equals("Admin")) {
                                    startActivity(new Intent(EditOrders.this, HomeActivity.class));
                                } else {
                                    startActivity(new Intent(EditOrders.this, supplierProfile.class));
                                }
                                Toast.makeText(EditOrders.this, "تم تعديل الاوردر الخاص بك", Toast.LENGTH_LONG).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                mdialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(EditOrders.this);
                builder.setMessage("هل انت متاكد من صحه البيانات و انك تريد تعديل الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
            }
        });
    }

    private int getIndex(Spinner spinner, String value) {
        Log.i(TAG, "Value : " + value);
        for(int i=0;i <spinner.getCount(); i++) {
            Log.i(TAG, "List : " + spinner.getItemAtPosition(i).toString());
            if(spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }
}