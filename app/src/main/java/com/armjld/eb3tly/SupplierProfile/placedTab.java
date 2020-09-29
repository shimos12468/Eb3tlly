package com.armjld.eb3tly.SupplierProfile;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.stream.Collectors;
import Model.Data;


public class placedTab extends Fragment {
    public static String TAG = "SupplierPlaced";
    public static Context mContext;
    public static SupplierAdapter supplierAdapter;
    private FirebaseAuth mAuth;
    private static RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    public static TextView txtNoOrders;
    public static ArrayList<Data> filterList = new ArrayList<>();
    String uId;


    public placedTab() { }

    public static placedTab newInstance(String param1, String param2) {
        placedTab fragment = new placedTab();
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
        View view =  inflater.inflate(R.layout.fragment_placed_oreders_supplier, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = UserInFormation.getId();

        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);
        recyclerView = view.findViewById(R.id.userRecyclr);

        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            HomeActivity.getSupOrders();
            refresh.setRefreshing(false);
        });

        // -------------- Recycler
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

    public static void getOrders(){
        filterList.clear();
        filterList.trimToSize();
        Log.i(TAG, "Getting Local Placed Orders for Supplier");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filterList = (ArrayList<Data>) HomeActivity.supList.stream().filter(x -> x.getStatue().equals("placed")).collect(Collectors.toList());
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
