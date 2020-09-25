package com.armjld.eb3tly.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.CaptinProfile.DeliveryAdapter;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.Orders.MapsActivity;
import com.armjld.eb3tly.R;

import Model.UserInFormation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.stream.Collectors;

import Model.Data;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    private static LinearLayout footer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static TextView txtNoOrders;
    public static String TAG = "Home Fragment";

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private static RecyclerView recyclerView;
    private static MyAdapter orderAdapter;
    public static ArrayList<Data> filterList;
    public static Context mContext;
    static TextView txtOrderCount;

    public HomeFragment() { }
    
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        //Find View
        TextView filtrs_btn = view.findViewById(R.id.filters_btn);
        ImageView btnMaps = view.findViewById(R.id.btnMaps);
        txtOrderCount = view.findViewById(R.id.txtOrderCount);

        txtNoOrders = view.findViewById(R.id.txtNoOrders);
        txtNoOrders.setVisibility(View.GONE);

        footer = view.findViewById(R.id.footer);
        footer.setVisibility(View.GONE);

        FloatingActionButton btnAdd = view.findViewById(R.id.btnAdd);

        if(UserInFormation.getAccountType().equals("Supplier")) {
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(v -> {
            if(UserInFormation.getAccountType().equals("Supplier")) {
                startActivity(new Intent(getActivity(), AddOrders.class));
            }
        });

        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("جميع الاوردرات المتاحة");

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);

        //Recycler
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);



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
            mSwipeRefreshLayout.setRefreshing(true);
            HomeActivity.getOrdersByLatest();
            mSwipeRefreshLayout.setRefreshing(false);
        });


        getOrders();
        recyclerView.setAdapter(orderAdapter);
        updateNone(filterList.size());

        return view;
    }

    public static void getOrders(){
        Log.i(TAG, "Setting orders in Home Fragment");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filterList = HomeActivity.mm;
            orderAdapter = new MyAdapter(mContext, filterList);
            if(recyclerView != null) {
                recyclerView.setAdapter(orderAdapter);
                updateNone(filterList.size());
                checkForAdvice();
            }
        }
    }

    private static void checkForAdvice() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();

                int lastVisible = layoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached;

                endHasBeenReached = lastVisible >= totalItemCount;

                if (endHasBeenReached) {
                    footer.setVisibility(View.VISIBLE);
                } else {
                    footer.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private static void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
            txtOrderCount.setVisibility(View.VISIBLE);
            txtOrderCount.setText("يوجد " + listSize + " اوردر");
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
            txtOrderCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}