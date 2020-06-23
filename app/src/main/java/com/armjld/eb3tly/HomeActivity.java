package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.Data;

@SuppressWarnings("FieldCanBeLocal")
public class HomeActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private ImageView filtrs_btn,btnNavBar;
    private static ArrayList<Data> mm;
    private static ArrayList<Data> ff;
    private long count,countFilter;
    private String FTAG = "Filters ";
    Data a7a = new Data();
    // import firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,rDatabase,uDatabase;
    int indexmm = -1;
    int indexff = -1;
    private Spinner spPState,spPRegion,spDState,spDRegion;
    private Button btnApplyFilters;
    private EditText txtFilterMoney;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txtNoOrders;
    private String TAG = "Home Activity";
    private MyAdapter orderAdapter,filterAdapter;

    //Recycler view
    private RecyclerView recyclerView;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    String datee = sdf.format(new Date());

    // Disable the Back Button
    @Override
    public void onBackPressed() {}

    // On Create Fun
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String datee = sdf.format(new Date());

        //Find View
        count =0;
        countFilter = 0;
        mm = new ArrayList<Data>();
        ff = new ArrayList<Data>();
        toolbar = findViewById(R.id.toolbar_home);
        filtrs_btn = findViewById(R.id.filters_btn);
        btnNavBar = findViewById(R.id.btnNavBar);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("جميع الاوردرات المتاحة");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        // ToolBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Database
        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        mDatabase.keepSynced(true);

        //Recycler
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        // NAV BAR
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_profile, R.id.nav_signout, R.id.nav_share).setDrawerLayout(drawer).build();

        btnNavBar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        final Intent newIntentNB = new Intent(this, HomeActivity.class);
        // Navigation Bar Buttons Function
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_timeline) {
                    newIntentNB.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(newIntentNB);
                }
                if (id == R.id.nav_changepass) {
                    startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                }
                if (id==R.id.nav_profile){
                    startActivity(new Intent(getApplicationContext(), profile.class));
                }
                if(id == R.id.nav_info) {
                    startActivity(new Intent(getApplicationContext(), UserSetting.class));

                }
                if (id == R.id.nav_how) {
                    startActivity(new Intent(getApplicationContext(), HowTo.class));
                }
                if (id==R.id.nav_signout){
                    finish();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    mAuth.signOut();
                }
                if (id==R.id.nav_about){
                    startActivity(new Intent(HomeActivity.this, About.class));
                }
                if(id==R.id.nav_share){
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Play Store Link");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
                }
                if (id == R.id.nav_contact) {
                    startActivity(new Intent(getApplicationContext(), Conatact.class));
                }
                drawer.closeDrawer(Gravity.LEFT);
                return true;
            }
        });

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tbTitle.setText("جميع الاوردرات المتاحة");
                mm.clear();
                mm.trimToSize();
                count = 0;
                recyclerView.setAdapter(null);
                mDatabase.orderByChild("ddate").startAt(datee).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if(ds.exists()) {
                                    Data orderData = ds.getValue(Data.class);
                                    assert orderData != null;
                                    if (orderData.getStatue().equals("placed")) {
                                        mm.add((int) count, orderData);
                                        count++;
                                    }
                                    orderAdapter = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
                                    recyclerView.setAdapter(orderAdapter);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                mSwipeRefreshLayout.setRefreshing(false);
                updateNone((int) count);
            }
        });

        // ------------------------ CHECK FOR REALTIME CHANGES IN ORDERS --------------------------- //
        mDatabase.orderByChild("ddate").startAt(datee).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                    for(int i = 0;i<mm.size();i++){
                        if(mm.get(i).getId().equals(orderData.getId())) {
                            orderAdapter.addItem(i, orderData);
                        }
                    }
                    for(int i = 0;i<ff.size();i++) {
                        if(ff.get(i).getId().equals(orderData.getId())) {
                            filterAdapter.addItem(i, orderData);
                        }
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<mm.size();i++){
                    if(mm.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        orderAdapter.addItem(i, orderData);
                    }
                }

                for(int i = 0;i<ff.size();i++){
                    if(ff.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        filterAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // ---------------------- GET ALL THE ORDERS -------------------//
        mDatabase.orderByChild("ddate").startAt(datee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("placed")) {
                            mm.add((int) count, orderData);
                            count++;
                        }
                        orderAdapter = new MyAdapter(HomeActivity.this, mm, getApplicationContext(), count);
                        recyclerView.setAdapter(orderAdapter);
                        updateNone(mm.size());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Filter Button
        filtrs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater filter = getLayoutInflater();
                View textEntryView = filter.inflate(R.layout.filter, null);

                ImageView btnFClose = textEntryView.findViewById(R.id.btnClose);
                spPState = (Spinner) textEntryView.findViewById(R.id.spFilterPState);
                spPRegion = (Spinner) textEntryView.findViewById(R.id.spFilterPRegion);
                spDState = (Spinner) textEntryView.findViewById(R.id.spFilterDState);
                spDRegion = (Spinner) textEntryView.findViewById(R.id.spFilterDRegion);
                btnApplyFilters = textEntryView.findViewById(R.id.btnApplyFilters);
                txtFilterMoney = textEntryView.findViewById(R.id.txtFilterMoney);

                AlertDialog.Builder myfilterDialog = new AlertDialog.Builder(HomeActivity.this);
                myfilterDialog.setView(textEntryView);
                final AlertDialog filterDialog = myfilterDialog.create();
                filterDialog.show();

                TextView fitlerTitle = textEntryView.findViewById(R.id.toolbar_title);
                fitlerTitle.setText("تصفية الاوردرات");

                //-------------------SPINNERS -------------------------//
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.txtStates, R.layout.color_spinner_layout);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPState.setPrompt("اختار المحافظة");
                spPState.setAdapter(adapter2);
                // Get the Government Regions
                spPState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int itemSelected = spPState.getSelectedItemPosition();
                        if (itemSelected == 0) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterCairo, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة القاهرة");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 1) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterGize, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الجيزة");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 2) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterAlex, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                            spPRegion.setAdapter(adapter4);
                        } else if (itemSelected == 3) {
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterMetro, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setPrompt("اختار محطة المترو");
                            spPRegion.setAdapter(adapter4);
                        } else {
                            spPRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.justAll, R.layout.color_spinner_layout);
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPRegion.setAdapter(adapter4);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // Drop Government Spinner
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.txtStates, R.layout.color_spinner_layout);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDState.setPrompt("اختار المحافظة");
                spDState.setAdapter(adapter3);
                // Get the Government Regions
                spDState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int itemSelected = spDState.getSelectedItemPosition();
                        if (itemSelected == 0) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterCairo, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة القاهرة");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 1) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterGize, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الجيزة");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 2) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterAlex, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار منطقة محافظة الاسكندرية");
                            spDRegion.setAdapter(adapter5);
                        } else if (itemSelected == 3) {
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.filterMetro, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setPrompt("اختار محطة المترو");
                            spDRegion.setAdapter(adapter5);
                        } else {
                            spDRegion.setPrompt("لم يتم تسجيل اي مناطق لتلك المحافظة");
                            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(HomeActivity.this, R.array.justAll, R.layout.color_spinner_layout);
                            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDRegion.setAdapter(adapter5);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });

                btnApplyFilters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tbTitle.setText("تصفية الاوردرات");
                        tsferAdapter();
                        filterAdapter = null;
                        mDatabase.orderByChild("ddate").startAt(datee).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                        final Data filterData = ds.getValue(Data.class);
                                        int filterValue;
                                        int dbMoney = Integer.parseInt(filterData.getGMoney());
                                        String moneyValue = txtFilterMoney.getText().toString();
                                        if (TextUtils.isEmpty(moneyValue)) {
                                            filterValue = 5000000;
                                        } else {
                                            filterValue = Integer.parseInt(moneyValue);
                                        }

                                        // ------------------------ CHECKING AREAS FILTERS --------------------------//
                                        if(spPState.getSelectedItem().toString().equals("كل المناطق")) {
                                            if(spDState.getSelectedItem().toString().equals("كل المناطق")) {
                                                if (filterData.getStatue().equals("placed") && dbMoney <= filterValue ) {
                                                    ff.add((int) countFilter, filterData);
                                                    countFilter++;
                                                }
                                            } else {
                                                if (filterData.getStatue().equals("placed") && dbMoney <= filterValue && filterData.getTxtDState().equals(spDState.getSelectedItem().toString()) ) {
                                                    ff.add((int) countFilter, filterData);
                                                    countFilter++;
                                                }
                                            }
                                        } else {
                                            if(spDState.getSelectedItem().toString().equals("كل المناطق")) {
                                                if (filterData.getStatue().equals("placed") && dbMoney <= filterValue && filterData.getTxtPState().equals(spPState.getSelectedItem().toString())) {
                                                    ff.add((int) countFilter, filterData);
                                                    countFilter++;
                                                }
                                            } else {
                                                if (filterData.getStatue().equals("placed") && dbMoney <= filterValue &&
                                                        filterData.getTxtPState().equals(spPState.getSelectedItem().toString()) &&
                                                        filterData.getTxtDState().equals(spDState.getSelectedItem().toString()) ) {
                                                    ff.add((int) countFilter, filterData);
                                                    countFilter++;
                                                }
                                            }
                                        }
                                    }
                                    updateNone(ff.size());
                                    filterAdapter = new MyAdapter(HomeActivity.this, ff, getApplicationContext(), countFilter);
                                    recyclerView.setAdapter(filterAdapter);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        filterDialog.dismiss();

                    }
                });


                btnFClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                    }
                });
            }
        });

    }

    private void tsferAdapter() {
        mm.clear();
        ff.clear();
        mm.trimToSize();
        ff.trimToSize();
        count = 0;
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}