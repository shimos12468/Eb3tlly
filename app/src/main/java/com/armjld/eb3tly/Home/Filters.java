package com.armjld.eb3tly.Home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.FilterAdapter;
import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import Model.Data;

public class Filters extends AppCompatActivity {

    private EditText txtFilterMoney;
    ArrayList<Data> filterList = new ArrayList<>();
    private String TAG = "Filters";
    RecyclerView recyclerView;
    TextView txtNoOrders;
    int filterValue;

    ImageView btnBack;

    AutoCompleteTextView autoComp;
    AutoCompleteTextView autoCompDrop;
    String pickVar;
    String strPickGov = "";
    String strPickCity = "";

    String dropVar;
    String strDropGov = "";
    String strDropCity = "";

    ArrayAdapter<String> cityAda;
    ArrayAdapter<String> dropCityAda;



    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        txtFilterMoney = findViewById(R.id.txtFilterMoney);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        autoComp = findViewById(R.id.autoComp);
        autoCompDrop = findViewById(R.id.autoCompDrop);
        btnBack = findViewById(R.id.btnBack);

        String[] cities = getResources().getStringArray(R.array.arrayCities);
        cityAda = new FilterAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        autoComp.setAdapter(cityAda);

        dropCityAda = new FilterAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        autoCompDrop.setAdapter(dropCityAda);

        btnBack.setOnClickListener(v-> finish());

        autoCompDrop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dropVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoComp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { pickVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        TextView fitlerTitle = findViewById(R.id.toolbar_title);
        fitlerTitle.setText("انشاء خط سر");

        tsferAdapter();
    }

    @Override
    protected void onStart () {
        super.onStart();

        autoComp.setOnItemClickListener((parent, view, position, id) -> {
            pickVar = Objects.requireNonNull(cityAda.getItem(position)).trim();
            String [] sep = pickVar.split(", ");
            strPickGov = sep[0].trim();
            strPickCity = sep[1].trim();
            Log.i(TAG, "Gov : " + strPickGov + " City : " + strPickCity);
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        autoCompDrop.setOnItemClickListener((parent, view, position, id) -> {
            dropVar = Objects.requireNonNull(dropCityAda.getItem(position)).trim();
            String [] sep = dropVar.split(", ");
            strDropGov = sep[0].trim();
            strDropCity = sep[1].trim();
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        txtFilterMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void getFromList(String spState,String spRegion, String sdState,String sdRegion, String money) {
        Log.i(TAG, "Getting Orders From : " + spState + " : " + spRegion + "    To    " + sdState + " : " + sdRegion + " For : " + money);
        filterList.clear();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (TextUtils.isEmpty(money) || money.equals("0")) {
                filterValue = 5000000;
            } else {
                filterValue = Integer.parseInt(money);
            }

            // ------------------------ CHECKING AREAS FILTERS --------------------------//
            if(spRegion.equals("كل المناطق")) {
                if(sdRegion.equals("كل المناطق")) {
                    filterList = (ArrayList<Data>) HomeActivity.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getTxtPState().equals(spState) && x.getTxtDState().equals(sdState)).collect(Collectors.toList());
                } else {
                    filterList = (ArrayList<Data>) HomeActivity.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getTxtPState().equals(spState) && x.getmDRegion().equals(sdRegion)).collect(Collectors.toList());
                }
            } else {
                if(sdState.equals("كل المناطق")) {
                    filterList = (ArrayList<Data>) HomeActivity.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getmPRegion().equals(spRegion) && x.getTxtDState().equals(sdState)).collect(Collectors.toList());
                } else {
                    filterList = (ArrayList<Data>) HomeActivity.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getmPRegion().equals(spRegion) && x.getmDRegion().equals(sdRegion)).collect(Collectors.toList());
                }
            }
            updateNone(filterList.size());
            MyAdapter filterAdapter = new MyAdapter(Filters.this, filterList);
            recyclerView.setAdapter(filterAdapter);
        }
    }

    private void tsferAdapter() {
        filterList.clear();
        filterList.trimToSize();
        recyclerView.setAdapter(null);
    }

    private void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }

}

