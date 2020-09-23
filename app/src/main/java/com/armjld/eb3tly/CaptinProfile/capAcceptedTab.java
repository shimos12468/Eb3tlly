package com.armjld.eb3tly.CaptinProfile;

import android.app.Activity;
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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.stream.Collectors;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class capAcceptedTab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String TAG = "CapAcceptedTab";
    private static DeliveryAdapter deliveryAdapter;
    private FirebaseAuth mAuth;
    private static RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private static TextView txtNoOrders;
    public static ArrayList<Data> filterList;
    String uType = UserInFormation.getAccountType();
    String uId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static Context mContext;

    public capAcceptedTab() { }

    public static capAcceptedTab newInstance(String param1, String param2) {
        capAcceptedTab fragment = new capAcceptedTab();
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

        View view =  inflater.inflate(R.layout.fragment_blank, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = UserInFormation.getId();

        recyclerView = view.findViewById(R.id.userRecycle);
        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);

        // ---- Refresh ----------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            HomeActivity.getDeliveryOrders();
            refresh.setRefreshing(false);
        });

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        getOrders();
        recyclerView.setAdapter(deliveryAdapter);
        updateNone(filterList.size());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getOrders();
    }

    public static void getOrders(){
        Log.i(TAG, "Setting orders in ArrayList");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filterList = (ArrayList<Data>) HomeActivity.delvList.stream().filter(x -> x.getStatue().equals("accepted") || x.getStatue().equals("recived")).collect(Collectors.toList());
            deliveryAdapter = new DeliveryAdapter(mContext, filterList);
            if(recyclerView != null) {
                recyclerView.setAdapter(deliveryAdapter);
                updateNone(filterList.size());
            }
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

    private static void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }
}