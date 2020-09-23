package com.armjld.eb3tly.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Orders.MapsActivity;
import com.armjld.eb3tly.R;

import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.Data;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView btnMaps;
    private ImageView filtrs_btn;
    private LinearLayout footer;
    public static ArrayList<Data> mm = new ArrayList<Data>();
    public static long count = 0;
    BlockManeger block = new BlockManeger();
    // import firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, uDatabase ,Database;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txtNoOrders;
    public static boolean requests = false;
    public static boolean orders = false;
    private String TAG = "Home Activity";
    private MyAdapter orderAdapter;
    String filterDate;
    String uType = UserInFormation.getAccountType();
    String uId = UserInFormation.getId();
    public static boolean sortDate = false;
    private Thread t = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //Recycler view
    private RecyclerView recyclerView;

    // Disable the Back Button
    boolean doubleBackToExitPressedOnce = false;
    

    public HomeFragment() { }
    
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        filterDate = format.format(Calendar.getInstance().getTime());
        //Find View
        filtrs_btn = view.findViewById(R.id.filters_btn);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);
        btnMaps = view.findViewById(R.id.btnMaps);
        footer = view.findViewById(R.id.footer);
        txtNoOrders.setVisibility(View.GONE);
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("جميع الاوردرات المتاحة");

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);


        //Database
        mAuth= FirebaseAuth.getInstance();
        uDatabase  = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");

        //Recycler
        recyclerView=view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        footer.setVisibility(View.GONE);
        btnMaps.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), MapsActivity.class), 1);
        });

        // Filter Button
        filtrs_btn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), Filters.class);
            startActivityForResult(i, 1);
        });

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mm.clear();
            mm.trimToSize();
            count = 0;
            recyclerView.setAdapter(null);
            getOrdersByLatest();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        // ---------------------- GET ALL THE ORDERS -------------------//
        if(mm.size() > 0) {
            getLocalOrders();
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
            getOrdersByLatest();
        }

        return view;
    }

    private void checkForAdvice() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();

                int lastVisible = layoutManager.findLastVisibleItemPosition();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                boolean endHasBeenReached;
                if(!sortDate) {
                    endHasBeenReached = firstVisible >= totalItemCount;
                } else {
                    endHasBeenReached = lastVisible + 2 >= totalItemCount;
                }

                if (totalItemCount > 0 && endHasBeenReached) {
                    footer.setVisibility(View.VISIBLE);
                } else {
                    footer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getLocalOrders () {
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        orderAdapter = new MyAdapter(getActivity(), mm, getApplicationContext(), count);
        recyclerView.setAdapter(orderAdapter);
        updateNone(mm.size());
        checkForAdvice();
        mSwipeRefreshLayout.setRefreshing(false);

    }

    private void getOrdersByLatest() {
        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                            myDate =  format.parse(sdf.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed") && !block.check(orderData.getuId())) {
                            mm.add((int) count, orderData);
                            count++;
                        }

                        // --- To sort by Date o
                        Collections.sort(mm, (o1, o2) -> {
                            String one = o1.getDate();
                            String two = o2.getDate();
                            return one.compareTo(two);
                        });

                        orderAdapter = new MyAdapter(getActivity(), mm, getApplicationContext(), count);
                        recyclerView.setAdapter(orderAdapter);
                        updateNone(mm.size());
                        checkForAdvice();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }


}