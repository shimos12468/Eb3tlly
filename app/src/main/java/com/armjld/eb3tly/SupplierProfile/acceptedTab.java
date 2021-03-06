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
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.stream.Collectors;
import Model.Data;

public class acceptedTab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String TAG = "SupplierAccepted";
    private static SupplierAdapter supplierAdapter;
    private static RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private static TextView txtNoOrders;
    String uId;
    public static Context mContext;
    public static ArrayList<Data> filterList = new ArrayList<>();

    public acceptedTab() { }

    public static acceptedTab newInstance(String param1, String param2) {
        acceptedTab fragment = new acceptedTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_accepted_orders_supplier, container, false);

        recyclerView = view.findViewById(R.id.userRecyclr);
        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);

        // ---- Refresh ----------- //
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
        recyclerView.setAdapter(supplierAdapter);
        updateNone(filterList.size());

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public static void getOrders(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filterList = (ArrayList<Data>) HomeActivity.supList.stream().filter(x -> x.getStatue().equals("accepted") || x.getStatue().equals("recived") || x.getStatue().equals("recived2") || x.getStatue().equals("denied")).collect(Collectors.toList());
            supplierAdapter = new SupplierAdapter(mContext, filterList);
            if(recyclerView != null) {
                recyclerView.setAdapter(supplierAdapter);
                updateNone(filterList.size());
            }
        }
    }

    private static void updateNone(int listSize) {
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