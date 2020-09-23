package com.armjld.eb3tly.SupplierProfile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class acceptedTab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private static ArrayList<Data> listSup;
    private long countSup;
    private SupplierAdapter supplierAdapter;
    private FirebaseAuth mAuth;
    private String TAG = "Profile";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private TextView txtNoOrders;
    String uId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_accepted_orders_supplier, container, false);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
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

        // Adapter
        countSup = 0;
        listSup = new ArrayList<>();

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

    private void getOrders() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ArrayList<Data> filterList = (ArrayList<Data>) HomeActivity.supList.stream().filter(x -> x.getStatue().equals("accepted") || x.getStatue().equals("recived")).collect(Collectors.toList());
            supplierAdapter = new SupplierAdapter(getContext(), filterList);
            recyclerView.setAdapter(supplierAdapter);
            updateNone(filterList.size());
        }
    }

    private void updateNone(int listSize) {
        if(listSize > 0) {
            txtNoOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }
}