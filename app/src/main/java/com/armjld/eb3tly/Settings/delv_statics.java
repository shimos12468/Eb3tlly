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

    int periodGGet = 0;
    int periodGMoney = 0;
    int accepted = 0;
    int recived = 0;
    int dilv = 0;

    DatePickerDialog dpd;
    EditText fromDate, toDate;

    TextView txtPeriodGGet,txtPeriodGMoney, txtAccepted, txtRecived, txtDliv;
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
        txtPeriodGGet= findViewById(R.id.txtPeriodGGet);
        txtPeriodGMoney= findViewById(R.id.txtPeriodGMoney);

        txtAccepted= findViewById(R.id.txtAccepted);
        txtRecived= findViewById(R.id.txtRecived);
        txtDliv= findViewById(R.id.txtDliv);

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
        periodGGet = 0;
        periodGMoney = 0;

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
            switch (c.getStatue()) {
                case "accepted" : {
                    accepted ++;
                    break;
                }

                case "recived2" :
                case "recived" : {
                    recived ++;
                    break;
                }

                case "deniedback":
                case "denied" :
                case "delivered" : {
                    dilv++;
                    break;
                }
            }
            periodGMoney = periodGMoney + Integer.parseInt(gMoney);
            periodGGet = periodGGet + Integer.parseInt(gGet);
        }

        txtAccepted.setText("تم قبول : " + accepted + " شحنة");
        txtRecived.setText("تم استلام : " + recived + " شحنة");
        txtDliv.setText("تم تسليم : " + dilv + " شحنة");
        txtPeriodGGet.setText("مصاريف الشحن في التفرة المححدة : " + periodGGet +  " ج");
        txtPeriodGMoney.setText("المقدمات المدفوعة في الفترة المحددة :  " + periodGMoney +  " ج");
    }

    private Date conv(String orderDate) throws ParseException {
        Date newDate = dbDate.parse(orderDate);
        SimpleDateFormat later = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        assert newDate != null;
        return sDF.parse(later.format(newDate));
    }
}