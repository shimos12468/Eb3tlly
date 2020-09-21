package com.armjld.eb3tly.Utilites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Adapters.MyAdapter;
import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.main.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import Model.Data;

public class   Filters extends AppCompatActivity {

    private Spinner spPState, spPRegion, spDState, spDRegion;
    private EditText txtFilterMoney;
    private static ArrayList<Data> ff;
    private DatabaseReference mDatabase;
    private String TAG = "Filters";
     int filterDuplicte =0;
    private MyAdapter filterAdapter;
    private long countFilter =0;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    String filterDate;
    BlockManeger block = new BlockManeger();

    RecyclerView recyclerView;
    TextView txtNoOrders;
    
    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
        /*Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        spPState = findViewById(R.id.spFilterPState);
        spPRegion = findViewById(R.id.spFilterPRegion);
        spDState = findViewById(R.id.spFilterDState);
        spDRegion = findViewById(R.id.spFilterDRegion);
        txtFilterMoney = findViewById(R.id.txtFilterMoney);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        ff = new ArrayList<>();
        countFilter = 0;
        filterDate = format.format(Calendar.getInstance().getTime());

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        mDatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        TextView fitlerTitle = findViewById(R.id.toolbar_title);
        fitlerTitle.setText("تصفية الاوردرات");

        //-------------------SPINNERS -------------------------//
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(Filters.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPState.setPrompt("اختار المحافظة");
        spPState.setAdapter(adapter2);
        // Get the Government Regions
        spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int itemSelected = spPState.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterCairo, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterGize, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAlex, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMetro, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار محطة المترو");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterQalyobia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة القليوبية");
                    spPRegion.setAdapter(adapter4);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSharqya, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الشرقية");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterDqhlya, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAsyut, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة اسيوط");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAswan, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة اسوان");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMenofya, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة المنوفية");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterIsmalia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAqsor, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الاقصر");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterBehera, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة البحيرة");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterBeniSwef, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة بين سويف");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterPortSaid, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterRedSea, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSouthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterDomyat, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة دمياط");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSohag, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة سوهاج");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSuez, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة السويس");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterGarbya, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الغربية");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterFayoum, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الفييوم");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterQena, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة قنا");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterKafr, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterNorthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spPRegion.setAdapter(adapter4);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMatroh, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة مطروح");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMeia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة المنيا");
                    spPRegion.setAdapter(adapter4);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.filterNewWadi, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spPRegion.setAdapter(adapter4);
                } else {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(Filters.this, R.array.justAll, R.layout.color_spinner_layout);
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
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(Filters.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDState.setPrompt("اختار المحافظة");
        spDState.setAdapter(adapter3);
        // Get the Government Regions
        spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int itemSelected = spDState.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterCairo, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterGize, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAlex, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMetro, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار محطة المترو");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterQalyobia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة القليوبية");
                    spDRegion.setAdapter(adapter5);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSharqya, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الشرقية");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterDqhlya, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الدقهليه");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAsyut, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة اسيوط");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAswan, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة اسوان");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMenofya, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة المنوفية");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterIsmalia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterAqsor, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الاقصر");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterBehera, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة البحيرة");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterBeniSwef, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة بين سويف");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterPortSaid, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة بور سعيد");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterRedSea, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSouthSenia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterDomyat, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة دمياط");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSohag, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة سوهاج");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterSuez, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة السويس");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterGarbya, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الغربية");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterFayoum, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الفييوم");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterQena, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة قنا");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterKafr, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterNorthSenia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spDRegion.setAdapter(adapter5);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMatroh, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة مطروح");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterMeia, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة المنيا");
                    spDRegion.setAdapter(adapter5);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.filterNewWadi, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spDRegion.setAdapter(adapter5);
                } else {
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(Filters.this, R.array.justAll, R.layout.color_spinner_layout);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDRegion.setPrompt("سيتم اضافه مناطق المحافظة في اصدارات جديدة");
                    spDRegion.setAdapter(adapter5);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mDatabase.orderByChild("ddate").startAt(filterDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<ff.size();i++) {
                    if(ff.get(i).getId().equals(orderData.getId())) {
                        if(filterAdapter!=null)
                            filterAdapter.addItem(i, orderData);
                        else{
                            Log.i(TAG,"adapter is null here");
                            filterAdapter = new MyAdapter(Filters.this, ff, getApplicationContext(), countFilter);
                            filterAdapter.addItem(i, orderData);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;

                for(int i = 0;i<ff.size();i++){
                    if(ff.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(filterAdapter!=null)
                            filterAdapter.addItem(i, orderData);
                        else{
                            Log.i(TAG,"adapter is null here");
                            filterAdapter = new MyAdapter(Filters.this, ff, getApplicationContext(), countFilter);
                            filterAdapter.addItem(i, orderData);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        tsferAdapter();
    }

    @Override
    protected void onStart () {
        super.onStart();

        spPRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(filterDuplicte>=1)
                applyFilter(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), txtFilterMoney.getText().toString(), filterDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spDRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                applyFilter(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), txtFilterMoney.getText().toString(), filterDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        txtFilterMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(spPState.getSelectedItem().toString(), spPRegion.getSelectedItem().toString(), spDState.getSelectedItem().toString(), spDRegion.getSelectedItem().toString(), txtFilterMoney.getText().toString(), filterDate);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void applyFilter(String spState,String spRegion, String sdState,String sdRegion, String money, String filterDate) {
        tsferAdapter();
        if(HomeActivity.mm.size() > 0) {
            getFromList();
            return;
        }
        mDatabase.orderByChild("ddate").startAt(filterDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Data filterData = ds.getValue(Data.class);
                        int filterValue=0;
                        assert filterData != null;
                        int dbMoney = Integer.parseInt(filterData.getGMoney());
                        if (TextUtils.isEmpty(money) || money.equals("0")) {
                            filterValue = 5000000;
                        }
                        else {
                            filterValue = Integer.parseInt(money);
                        }


                        // ------------------------ CHECKING AREAS FILTERS --------------------------//
                        if(spRegion.equals("كل المناطق")) {
                            if(sdRegion.equals("كل المناطق")) {
                                if (filterData.getStatue().equals("placed")&&!block.check(filterData.getuId()) && dbMoney <= filterValue && filterData.getTxtPState().equals(spState) && filterData.getTxtDState().equals(sdState) ) {
                                    ff.add((int) countFilter, filterData);
                                    countFilter++;
                                    Log.i(TAG,"first if in all mnat2");
                                    Log.i(TAG ,filterData.getDName());
                                }
                            }
                            else {
                                if (filterData.getStatue().equals("placed")&&!block.check(filterData.getuId()) && dbMoney <= filterValue && filterData.getTxtPState().equals(spState) && filterData.getmDRegion().equals(sdRegion)) {
                                    ff.add((int) countFilter, filterData);
                                    countFilter++;
                                    Log.i(TAG,"second if in all mnat2");
                                }
                            }
                        }

                        else {
                            if(sdState.equals("كل المناطق")) {
                                if (filterData.getStatue().equals("placed")&&!block.check(filterData.getuId())&& dbMoney <= filterValue && filterData.getmPRegion().equals(spRegion) && filterData.getTxtDState().equals(sdState)) {
                                    ff.add((int) countFilter, filterData);
                                    countFilter++;
                                    Log.i(TAG,"first  if in else");
                                }
                            }
                            else {
                                if (filterData.getStatue().equals("placed") && dbMoney <= filterValue &&
                                        filterData.getmPRegion().equals(spRegion) &&
                                        filterData.getmDRegion().equals(sdRegion) &&!block.check(filterData.getuId())) {
                                    ff.add((int) countFilter, filterData);
                                    countFilter++;
                                    Log.i(TAG,"second  if in else");
                                }
                            }
                        }
                    }
                    updateNone(ff.size());
                    filterAdapter = new MyAdapter(Filters.this, ff, getApplicationContext(), ff.size());
                    recyclerView.setAdapter(filterAdapter);
                    filterDuplicte++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void getFromList() {

    }

    private void tsferAdapter() {
        ff.clear();
        ff.trimToSize();
        countFilter = 0;
        recyclerView.setAdapter(null);
    }

    private void updateNone(int listSize) {
        Log.i(TAG, "List size is now : " + listSize);
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }

}

