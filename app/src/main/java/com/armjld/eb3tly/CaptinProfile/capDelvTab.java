package com.armjld.eb3tly.CaptinProfile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class capDelvTab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private static ArrayList<Data> listDelv;
    private long countDelv;
    private DeliveryAdapter deliveryAdapter;
    private FirebaseAuth mAuth;
    private String TAG = "Profile";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private TextView txtNoOrders;
    String uType = UserInFormation.getAccountType();
    String uId;
    private String mParam1;
    private String mParam2;

    public capDelvTab() { }

    public static capDelvTab newInstance(String param1, String param2) {
        capDelvTab fragment = new capDelvTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_blank2, container, false);

        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = UserInFormation.getId();

        recyclerView=view.findViewById(R.id.userRecyclr);
        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);

        // Adapter
        countDelv = 0;
        listDelv = new ArrayList<>();

        // ---- Refresh ----------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            HomeActivity.getDeliveryOrders();
            refresh.setRefreshing(false);
        });

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

    private void getOrders(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ArrayList<Data> filterList = (ArrayList<Data>) HomeActivity.delvList.stream().filter(x -> x.getStatue().equals("delivered")).collect(Collectors.toList());
            deliveryAdapter = new DeliveryAdapter(getContext(), filterList);
            recyclerView.setAdapter(deliveryAdapter);
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