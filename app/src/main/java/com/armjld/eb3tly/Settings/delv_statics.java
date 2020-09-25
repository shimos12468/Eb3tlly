package com.armjld.eb3tly.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Home.HomeActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.stream.Collectors;

import Model.Data;

@RequiresApi(api = Build.VERSION_CODES.O)
public class delv_statics extends AppCompatActivity {

    SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    SimpleDateFormat dbDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    Calendar myCalendar = Calendar.getInstance();

    Calendar cal = GregorianCalendar.getInstance();

    int AllOrders = 0;
    int AllGMoney = 0;
    int AllgGet = 0;
    int allPrcen = 0;
    int periodGGet = 0;
    int periodGMoney = 0;
    int periodPrcen = 0;
    int numOfOrdersPerDay = 0;
    DatePickerDialog dpd;
    EditText fromDate, toDate;

    TextView txtAllOrders,txtAllGMoney,txtAllGGet,txtAllPrcen,txtPeriodGGet,txtPeriodGMoney,txtPeriodPrecn,txtOrdersPerDay;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delv_statics);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("التقارير");

        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);

        btnBack = findViewById(R.id.btnBack);
        txtAllOrders = findViewById(R.id.txtAllOrders);
        txtAllGMoney= findViewById(R.id.txtAllGMoney);
        txtAllGGet= findViewById(R.id.txtAllGGet);
        txtAllPrcen= findViewById(R.id.txtAllPrcen);
        txtPeriodGGet= findViewById(R.id.txtPeriodGGet);
        txtPeriodGMoney= findViewById(R.id.txtPeriodGMoney);
        txtPeriodPrecn= findViewById(R.id.txtPeriodPrecn);
        txtOrdersPerDay= findViewById(R.id.txtOrdersPerDay);

        cal.add(Calendar.DAY_OF_YEAR, -7);
        String lastWeek = sDF.format(cal.getTime());
        String today = sDF.format(new Date());

        toDate.setText(today);
        fromDate.setText(lastWeek);

        btnBack.setOnClickListener(v-> finish());

        DatePickerDialog.OnDateSetListener _fromDate = new DatePickerDialog.OnDateSetListener() {
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
                fromDate.setText(sDF.format(myCalendar.getTime()));
            }
        };

        fromDate.setOnClickListener(v -> {
            dpd = new DatePickerDialog(this, _fromDate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            dp.setMaxDate(Calendar.getInstance().getTimeInMillis());
            dpd.show();
        });

        DatePickerDialog.OnDateSetListener _toDate = new DatePickerDialog.OnDateSetListener() {
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
                toDate.setText(sDF.format(myCalendar.getTime()));
            }
        };

        toDate.setOnClickListener(v -> {
            dpd = new DatePickerDialog(this, _toDate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            dp.setMaxDate(Calendar.getInstance().getTimeInMillis()); // disable all the previos dates
            dpd.show();
        });

        toDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getStates();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        fromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getStates();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        getStates();
    }

    @SuppressLint("SetTextI18n")
    private void getStates() {

        AllOrders = 0;
        AllGMoney = 0;
        AllgGet = 0;
        allPrcen = 0;
        periodGGet = 0;
        periodGMoney = 0;
        periodPrcen = 0;
        numOfOrdersPerDay = 0;

        AllOrders = HomeActivity.delvList.size();

        for(int i = 0; i < HomeActivity.delvList.size(); i++) {
            Data c = HomeActivity.delvList.get(i);
            String gMoney = c.getGMoney();
            String gGet = c.getGGet();
            AllGMoney = AllGMoney + Integer.parseInt(gMoney);
            AllgGet = AllgGet + Integer.parseInt(gGet);
        }
        allPrcen = (int) (AllgGet * (float) 0.2);

        ArrayList<Data> filterList = (ArrayList<Data>) HomeActivity.delvList.stream().filter(x-> {
            try {
                return conv(x.getAcceptedTime()).compareTo(sDF.parse(fromDate.getText().toString())) >= 0 &&  conv(x.getAcceptedTime()).compareTo(sDF.parse(toDate.getText().toString())) <= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());

        for(int i = 0; i < filterList.size(); i++) {
            Data c = filterList.get(i);
            String gMoney = c.getGMoney();
            String gGet = c.getGGet();
            periodGMoney = periodGMoney + Integer.parseInt(gMoney);
            periodGGet = periodGGet + Integer.parseInt(gGet);
        }
        periodPrcen = (int) (periodGGet * (float) 0.2);

        txtAllOrders.setText("مجموع عدد الاورورات التي قمت بقبولها : " + AllOrders +  " اوردر");
        txtAllGMoney.setText("مجموع المقدمات التي دفعتها :  " + AllGMoney +  " ج");
        txtAllGGet.setText("مجموع مصاريف الشحن : " + AllgGet +  " ج");
        txtAllPrcen.setText("مجموع المبلغ المدفوع للشركة :  " + allPrcen +  " ج");
        txtPeriodGGet.setText("مصاريف الشحن في التفرة المححدة : " + periodGGet +  " ج");
        txtPeriodGMoney.setText("المقدمات المدفوعة في الفترة المحددة :  " + periodGMoney +  " ج");
        txtPeriodPrecn.setText("المبلغ المدفوع للشركة في الفترة المحددة : " + periodPrcen +  " ج");

    }

    private Date conv(String orderDate) throws ParseException {
        Date newDate = dbDate.parse(orderDate);
        SimpleDateFormat later = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        assert newDate != null;
        return sDF.parse(later.format(newDate));
    }
}