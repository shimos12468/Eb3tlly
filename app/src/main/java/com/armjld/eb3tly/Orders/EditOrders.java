package com.armjld.eb3tly.Orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.FilterAdapter;
import com.armjld.eb3tly.Home.StartUp;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.LocationSpinnerAdapter;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;

import Model.LocationDataType;
import Model.UserInFormation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class EditOrders extends AppCompatActivity {

    private static final String TAG = "Edit Orders";
    private EditText DAddress, DDate, DPhone, DName, GMoney, GGet,txtPickDate, txtWeight, txtType;
    private TextInputLayout tlDAddress, tlDDate, tlDPhone, tlDName, tlGMoney, tlGGet,tlPickDate, tlWeight, tlType, tlDropCity;

    private AutoCompleteTextView txtDropCity;
    private Spinner spnMyLocations;
    private ProgressDialog mdialog;
    private DatabaseReference mDatabase;
    String orderID;

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
    String ownerID, ownerName, strStatue, strUAccepted;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_orders);

        // Firebasee
        mDatabase = getInstance().getReference("Pickly").child("orders");
        DatabaseReference uDatabase = getInstance().getReference().child("Pickly").child("users");

        orderID = getIntent().getStringExtra("orderID");
        ownerID = getIntent().getStringExtra("ownerID");

        // Toolbar
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("تعديل الاوردر");

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

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());


        ImageView imgHelpMoney = findViewById(R.id.imgHelpMoney);
        ImageView imgHelpGet = findViewById(R.id.imgHelpGet);

        Button btnsave = findViewById(R.id.btnSave);
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
        dropCityAda = new FilterAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        txtDropCity.setAdapter(dropCityAda);

        txtDropCity.setOnItemClickListener((parent, view, position, id) -> {
            dropVar = Objects.requireNonNull(dropCityAda.getItem(position)).trim();
            String [] sep = dropVar.split(", ");
            strDropGov = sep[0].trim();
            strDropCity = sep[1].trim();
        });


        // ----------- Get Locations to Spinner --------------------- //
        uDatabase.child(ownerID).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<LocationDataType> listLoc = new ArrayList<>();
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        LocationDataType locData = ds.getValue(LocationDataType.class);
                        listLoc.add(locData);
                        LocationSpinnerAdapter adapter = new LocationSpinnerAdapter(listLoc, EditOrders.this);
                        spnMyLocations.setAdapter(adapter);

                        assert locData != null;
                        if(locData.getAddress().equals(pickAdd)) {
                            LocationDataType locData2 = ds.getValue(LocationDataType.class);
                            spnMyLocations.setSelection(listLoc.indexOf(locData2));
                        }
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
            dpd = new DatePickerDialog(EditOrders.this, ddate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
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
            dpd = new DatePickerDialog(EditOrders.this, pdate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            long now = Calendar.getInstance().getTimeInMillis() - 100;
            dp.setMinDate(now);
            dp.setMaxDate((now + (1000*60*60*24*2)));
            dpd.show();
        });

        setData();

        btnsave.setOnClickListener(v -> {
            if(!check()) {
                return;
            }
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(EditOrders.this).setMessage("هل انت متأكد من البيانات المعدله ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                editOrder();
                finish();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

    }

    private void editOrder () {
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

        String srate = "false";
        String srateid = "";
        String drate = "false";
        String drateid = "";

        // Send order to Data Base using the DATA MODEL
        Data data = new Data(pickGov, pickCity, pickAdd, "", strDropGov, strDropCity, mDAddress, mDDate, mDPhone, mDName, mGMoney, mGGet, datee,orderID , ownerID, "", "", "", "", strStatue, strUAccepted, srate, srateid, drate, drateid, "", "", "","Bid",ownerName, strPickDate, weight, strType, lat, _long);

        mDatabase.child(orderID).setValue(data);
        mDatabase.child(orderID).child("lastedit").setValue(datee);

        mdialog.dismiss();

        finish();
        HomeActivity.whichFrag = "Home";
        startActivity(new Intent(this, HomeActivity.class));

        Toast.makeText(EditOrders.this, "تم تعديل بيانات الشحنة", Toast.LENGTH_LONG).show();
    }

    private void setData() {
        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data orderData = snapshot.getValue(Data.class);
                assert orderData != null;

                DAddress.setText(orderData.getDAddress());
                DDate.setText(orderData.getDDate());
                DPhone.setText(orderData.getDPhone());
                DName.setText(orderData.getDName());
                GMoney.setText(orderData.getGMoney());
                GGet.setText(orderData.getGGet());
                txtPickDate.setText(orderData.getpDate());
                txtWeight.setText(orderData.getPackWeight());
                txtType.setText(orderData.getPackType());

                // -------------- Set Drop Location ---------------- //
                txtDropCity.setText(getGov(orderData.getTxtDState(), orderData.getmDRegion()));

                // ----------- Set Pick Location --------------- //
                /*pickGov = orderData.getmPRegion();
                pickCity = orderData.getTxtPState();
                pickAdd = orderData.getmPAddress();
                lat = orderData.getLat();
                _long = orderData.get_long();*/

                // ----------- Set Strings -------------- //
                ownerName = orderData.getOwner();
                strStatue = orderData.getStatue();
                strUAccepted = orderData.getuAccepted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
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

        String [] City = getResources().getStringArray(R.array.arrayCities);
        ArrayList listCity = new ArrayList(Arrays.asList(City));
        if(!listCity.contains(txtDropCity.getText().toString())) {
            tlDropCity.setError("الرجاء اختيار مدينة من المدن المتاحة");
            txtDropCity.requestFocus();
            return false;
        }

        dropVar = txtDropCity.getText().toString().trim();
        String [] sep = dropVar.split(", ");
        strDropGov = sep[0].trim();
        strDropCity = sep[1].trim();

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

    private String getGov(String state, String region) {
        dropVar = state + ", " + region;
        strDropGov = state;
        strDropGov = region;
        return state + ", " + region;
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