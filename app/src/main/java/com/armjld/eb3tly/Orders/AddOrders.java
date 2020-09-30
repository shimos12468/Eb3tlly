package com.armjld.eb3tly.Orders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.eb3tly.FilterAdapter;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.LocationSpinnerAdapter;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Home.StartUp;

import Model.LocationDataType;
import Model.UserInFormation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import Model.Data;
import Model.notiData;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class AddOrders extends AppCompatActivity {

    private static final String TAG = "Add Orders";
    private EditText DAddress, DDate, DPhone, DName, GMoney, GGet,txtPickDate, txtWeight, txtType;
    private TextInputLayout tlDAddress, tlDDate, tlDPhone, tlDName, tlGMoney, tlGGet,tlPickDate, tlWeight, tlType, tlDropCity;

    private AutoCompleteTextView txtDropCity;
    private Spinner spnMyLocations;

    private Button btnsave, btnSaveAdd;
    String uId = UserInFormation.getId();
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase, mDatabase, nDatabase;
    private ImageView btnBack, imgHelpMoney, imgHelpGet;

    DatePickerDialog dpd;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    String dropVar = "";
    String strDropGov = "";
    String strDropCity = "";
    ArrayAdapter<String> dropCityAda;

    String pickGov = "";
    String pickCity = "";
    String pickAdd = "";
    String lat = "";
    String _long = "";


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    private void whichProfile() {
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders);

        // Firebasee
        mDatabase = getInstance().getReference("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        // ----------------- Check for Data Loss ------------------- //
        if(uId == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }

        // Toolbar
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("اضافة اوردر جديد");

        // Texts
        DAddress = findViewById(R.id.txtDAddress);
        DDate = findViewById(R.id.txtDDate);
        DPhone = findViewById(R.id.txtDPhone);
        DName = findViewById(R.id.txtDName);
        GMoney = findViewById(R.id.txtGMoney);
        GGet = findViewById(R.id.txtGGet);

        txtPickDate = findViewById(R.id.txtPickDate);
        txtWeight = findViewById(R.id.txtWeight);
        txtType = findViewById(R.id.txtType);

        spnMyLocations = findViewById(R.id.spnMyLocations);
        txtDropCity = findViewById(R.id.txtDropCity);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());


        imgHelpMoney = findViewById(R.id.imgHelpMoney);
        imgHelpGet = findViewById(R.id.imgHelpGet);

        btnsave = findViewById(R.id.btnSave);
        btnSaveAdd = findViewById(R.id.btnSaveAdd);
        mdialog = new ProgressDialog(this);

        tlDAddress = findViewById(R.id.tlDAddress);
        tlDDate = findViewById(R.id.tlDDate);
        tlDPhone = findViewById(R.id.tlDPhone);
        tlDName = findViewById(R.id.tlDName);
        tlGMoney = findViewById(R.id.tlGMoney);
        tlGGet = findViewById(R.id.tlGGet);
        tlPickDate = findViewById(R.id.tlPickDate);
        tlWeight = findViewById(R.id.tlWeight);
        tlType = findViewById(R.id.tlType);
        tlDropCity = findViewById(R.id.tlDropCity);



        // ----------- Set Auto Complete for Cities ----------------- //
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        dropCityAda = new FilterAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        txtDropCity.setAdapter(dropCityAda);

        txtDropCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dropVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtDropCity.setOnItemClickListener((parent, view, position, id) -> {
            dropVar = Objects.requireNonNull(dropCityAda.getItem(position)).trim();
            String [] sep = dropVar.split(", ");
            strDropGov = sep[0].trim();
            strDropCity = sep[1].trim();
        });


        // ----------- Get Locations to Spinner --------------------- //
        uDatabase.child(uId).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<LocationDataType> listLoc = new ArrayList<>();
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        LocationDataType locData = ds.getValue(LocationDataType.class);
                        listLoc.add(locData);
                        LocationSpinnerAdapter adapter = new LocationSpinnerAdapter(listLoc, AddOrders.this);
                        spnMyLocations.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // ------------ Get the location data from the Spinners ----------------- //
        spnMyLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LocationDataType choosen = (LocationDataType) spnMyLocations.getItemAtPosition(i);
                lat = String.valueOf(choosen.getLattude());
                _long = String.valueOf(choosen.getLontude());
                pickAdd = choosen.getAddress();
                pickCity = choosen.getState();
                pickGov = choosen.getRegion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


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


        // ---------------- set Drop Date --------------------- //
        final DatePickerDialog.OnDateSetListener ddate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR, year);
                newDate.set(Calendar.MONTH, monthOfYear);
                newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(newDate);
            }

            private void updateLabel(Calendar newDate) {
                String dFormat = "yyyy-MM-dd";
                SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.ENGLISH);
                DDate.setText(sDF.format(newDate.getTime()));
            }
        };

        DDate.setOnClickListener(v -> {
            dpd = new DatePickerDialog(AddOrders.this, ddate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            long now = Calendar.getInstance().getTimeInMillis() - 100;
            dp.setMinDate(now);
            dp.setMaxDate((now + (1000*60*60*24*7)));// disable all the previos dates
            dpd.show();
        });

        // ------------------ Set Pick Date --------------------- //

        final DatePickerDialog.OnDateSetListener pdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR, year);
                newDate.set(Calendar.MONTH, monthOfYear);
                newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(newDate);
            }

            private void updateLabel(Calendar newDate) {
                String dFormat = "yyyy-MM-dd";
                SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.ENGLISH);
                txtPickDate.setText(sDF.format(newDate.getTime()));
            }
        };

        txtPickDate.setOnClickListener(v -> {
            dpd = new DatePickerDialog(AddOrders.this, pdate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            long now = Calendar.getInstance().getTimeInMillis() - 100;
            dp.setMinDate(now);
            dp.setMaxDate((now + (1000*60*60*24*2)));
            dpd.show();
        });

        // ----------------- Save the Order --------------------//
        btnsave.setOnClickListener(v -> {
            if(!check()) {
                return;
            }
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(AddOrders.this).setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                addOrder();
                clearText();
                finish();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();

        });

        // ------------ Save the Order and Add Another -------------//
         btnSaveAdd.setOnClickListener(v -> {
             if(!check()) {
                 return;
             }
             BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(AddOrders.this).setMessage("هل انت متاكد من صحه البيانات و انك تريد اضافة الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                 addOrder();
                 clearText();
                 dialogInterface.dismiss();
             }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                 dialogInterface.dismiss();
             }).build();
             mBottomSheetDialog.show();
         });
    }

    private void clearText() {
        dpd = null;
        DAddress.setText("");
        DPhone.setText("");
        DName.setText("");
        GMoney.setText("");
        GGet.setText("");
        txtDropCity.setText("");
        strDropCity = "";
        strDropGov = "";
        dropVar = "";
        txtPickDate.setText("");
        DDate.setText("");
        txtWeight.setText("");
        txtType.setText("");
    }

    private boolean check () {
         String mDAddress = DAddress.getText().toString().trim();
         String mDDate = DDate.getText().toString().trim();
         String mDPhone = DPhone.getText().toString().trim();
         String mDName = DName.getText().toString().trim();
         String mGMoney = GMoney.getText().toString().trim();
         String mGGet = GGet.getText().toString().trim();

         String weight = txtWeight.getText().toString();
         String type = txtType.getText().toString();
         String pickDate = txtPickDate.getText().toString();

        int intget;

        if(_long.isEmpty() || lat.isEmpty() || pickGov.isEmpty() || pickCity.isEmpty() || pickAdd.isEmpty()) {
            Toast.makeText(this, "Pick Address is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(pickDate)) {
            tlPickDate.setError("الرجاء ادخال تاريخ الاستلام");
            return false;
        }

        if(!isValidFormat("yyyy-MM-dd", pickDate)) {
            tlPickDate.setError("الرجاء ادخال تاريخ الاستلام");
            return false;
        }

        if (TextUtils.isEmpty(mDName)) {
            tlDName.setError("الرجاء ادخال اسم السمتلم");
            DName.requestFocus();
            return false;
        }

        if(mDPhone.length() != 11) {
            tlDPhone.setError("الرجاء ادخال رقم هاتف صحيح");
            DPhone.requestFocus();
            return false;
        }

        if (!isNumb(mDPhone)) {
            tlDPhone.setError("الرجاء ادخال رقم هاتف صحيح");
            DPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mDDate)) {
            tlDDate.setError("الرجاء ادخال تاريخ التسليم");
            return false;
        }

        if(!isValidFormat("yyyy-MM-dd", mDDate)) {
            tlDDate.setError("الرجاء ادخال تاريخ التسليم");
            return false;
        }

        if(dropVar.isEmpty() || strDropCity.isEmpty() || strDropGov.isEmpty()) {
            tlDropCity.setError("الرجاء ادخال اسم المدينة");
            txtDropCity.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mDAddress)) {
            tlDAddress.setError("الرجاء ادخال عنوان التسلم بالتفصيل");
            DAddress.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(weight)) {
            tlWeight.setError("الرجاء ادخال وزن الشحنة بالكيلو");
            txtWeight.requestFocus();
            return false;
        }

        if (!isNumb(weight)) {
            tlWeight.setError("الرجاء ادخال وزن الشحنة بالكيلو");
            txtWeight.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(type)) {
            tlType.setError("الرجاء توضيح محتوي الشحنة");
            txtType.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mGMoney)) {
            tlGMoney.setError("الرجاء تحديد سعر الشحنة");
            GMoney.requestFocus();
            return false;
        }

        if (!isNumb(mGMoney)) {
            tlGMoney.setError("الرجاء تحديد سعر الشحنة");
            GMoney.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mGGet)) {
            tlGGet.setError("الرجاء تحديد سعر الشحن");
            GGet.requestFocus();
            return false;
        }

        if (!isNumb(mGGet)) {
            tlGGet.setError("الرجاء تحديد سعر الشحن");
            GGet.requestFocus();
            return false;
        }

        intget = Integer.parseInt(mGGet);

        if(intget == 0) {
            tlGGet.setError("الرجاء وضع سعر للشحن");
            GGet.requestFocus();
            return false;
        }
        return true;
    }

    private void addOrder() {
        mdialog.setMessage("جاري اضافة الاوردر");
        mdialog.show();

         String mDAddress = DAddress.getText().toString().trim();
         String mDDate = DDate.getText().toString().trim();
         String mDPhone = DPhone.getText().toString().trim();
         String mDName = DName.getText().toString().trim();
         String mGMoney = GMoney.getText().toString().trim();
         String mGGet = GGet.getText().toString().trim();
         String weight = txtWeight.getText().toString().trim();
         String strType = txtType.getText().toString().trim();
         String strPickDate = txtPickDate.getText().toString().trim();

         //DEFULT ORDER States ON ADD
         String states = "placed";
         String uAccepted = "";

         String id = mDatabase.push().getKey(); // create Order ID
         String srate = "false";
         String srateid = "";
         String drate = "false";
         String drateid = "";

         String owner = UserInFormation.getUserName();
         // Send order to Data Base using the DATA MODEL
         Data data = new Data(pickGov, pickCity, pickAdd, "", strDropGov, strDropCity, mDAddress, mDDate, mDPhone, mDName, mGMoney, mGGet, datee, id, uId, "", "", "", "", states, uAccepted, srate, srateid, drate, drateid, "", "", "","Bid",owner, strPickDate, weight, strType, lat, _long);
         assert id != null;

         mDatabase.child(id).setValue(data);
         HomeActivity.supList.add(data);

         mDatabase.child(id).child("lastedit").setValue(datee);
         sendNotiState(pickGov, pickCity, id);

         mdialog.dismiss();
         Toast.makeText(AddOrders.this, "تم اضافة اوردرك بنجاح", Toast.LENGTH_LONG).show();
    }


    private Double checkFactor(String from, String to) {
        String Region = "";
        double factor;

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
                factor = 1.4;
                break;
            }
            case "بحري" : {
                factor = 1.2;
                break;
            }
            case "المنبوذين" : {
                factor = 1.6;
                break;
            }
            case "القاهرة الكبري" : {
                factor = 1.1;
                break;
            }
            case "السويس" : {
                factor = 1.3;
                break;
            }
            default: {
                factor = 1.3;
                break;
            }
        }
    return factor;
    }

    private void sendNotiState(String pickGov, String picCity, String id) {
        uDatabase.orderByChild("userState").equalTo(pickGov).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                        String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();

                        if(accType.equals("Delivery Worker")) {
                            String sendOrderNoti = "false";
                            if(ds.child("sendOrderNoti").exists()) {
                                sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNoti").getValue()).toString();
                            }

                            if(sendOrderNoti.equals("true")) {
                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في محافظنك",datee,"false", "order",UserInFormation.getUserName(), UserInFormation.getUserURL());
                                nDatabase.child(usId).push().setValue(Noti);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        uDatabase.orderByChild("userCity").equalTo(picCity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String usId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                        String accType = Objects.requireNonNull(ds.child("accountType").getValue()).toString();
                        if(accType.equals("Delivery Worker")) {
                            String sendOrderNoti = "false";
                            if(ds.child("sendOrderNotiCity").exists()) {
                                sendOrderNoti = Objects.requireNonNull(ds.child("sendOrderNotiCity").getValue()).toString();
                            }

                            if(sendOrderNoti.equals("true")) {
                                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", usId,id,"يوجد اوردر جديد في مدينتك",datee,"false","order",UserInFormation.getUserName(), UserInFormation.getUserURL());
                                nDatabase.child(usId).push().setValue(Noti);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            assert date != null;
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public boolean isNumb (String value) {
        return !Pattern.matches("[a-zA-Z]+", value);
    }
}