package com.armjld.eb3tly.Orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Adapters.SupplierAdapter;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class acceptedOrdersSupplier extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private static ArrayList<Data> listSup;
    private long countSup;
    private SupplierAdapter supplierAdapter;
    private FirebaseAuth mAuth;
    private String TAG = "Profile";
    private RecyclerView recyclerView;
    String uType = UserInFormation.getAccountType();
    private SwipeRefreshLayout refresh;
    private TextView txtNoOrders;
    String uId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public acceptedOrdersSupplier() { }

    public static acceptedOrdersSupplier newInstance(String param1, String param2) {
        acceptedOrdersSupplier fragment = new acceptedOrdersSupplier();
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
            getSupOrders();
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
        getSupOrders();
        return view;
    }
    public void getSupOrders() {
        clearAdapter();
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("accepted") || Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("recived")) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            listSup.add((int) countSup, orderData);
                            countSup++;
                            Log.i(TAG, "Inside the Query");

                            supplierAdapter = new SupplierAdapter(getContext(), listSup, getContext(), countSup);
                            recyclerView.setAdapter(supplierAdapter);
                            updateNone(listSup.size());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.orderByChild("uId").equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listSup.size();i++){
                    if(listSup.get(i).getId().equals(orderData.getId())) {
                        if(supplierAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            supplierAdapter  = new SupplierAdapter(getContext(), listSup, getContext(), countSup);
                        }
                        supplierAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listSup.size();i++){
                    if(listSup.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(supplierAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            supplierAdapter  = new SupplierAdapter(getContext(), listSup, getContext(), countSup);
                        }
                        supplierAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }



    private void clearAdapter() {
        listSup.clear();
        listSup.trimToSize();
        countSup = 0;
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