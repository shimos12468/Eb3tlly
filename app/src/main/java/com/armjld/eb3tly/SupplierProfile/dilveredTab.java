package com.armjld.eb3tly.SupplierProfile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Home.HomeActivity;
import java.util.ArrayList;
import java.util.stream.Collectors;
import Model.Data;


public class dilveredTab extends Fragment {

    private static Context mContext;
    private static SupplierAdapter supplierAdapter;
    private static String TAG = "SupplierDilvered";
    private static RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private static TextView txtNoOrders;

    public dilveredTab() { }

    public static dilveredTab newInstance(String param1, String param2) {
        dilveredTab fragment = new dilveredTab();
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
        View view =  inflater.inflate(R.layout.fragment_dlivared_orders_supplier, container, false);

        recyclerView = view.findViewById(R.id.userRecyclr);
        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);

        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            HomeActivity.getSupOrders();
            refresh.setRefreshing(false);
        });


        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        getOrders();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getOrders();
    }

    public static void getOrders() {
        Log.i(TAG, "Getting Local Delivered Orders for Supplier");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ArrayList<Data> filterList = (ArrayList<Data>) HomeActivity.supList.stream().filter(x -> x.getStatue().equals("delivered") || x.getStatue().equals("deniedback")).collect(Collectors.toList());
            supplierAdapter = new SupplierAdapter(mContext, filterList);
            if(recyclerView != null) {
                recyclerView.setAdapter(supplierAdapter);
                updateNone(filterList.size());
            }
        }
    }


    public static void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
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