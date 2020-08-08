package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class BlankFragment2 extends Fragment {

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

    public BlankFragment2() { }

    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
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
        uId = mUser.getUid();

        recyclerView=view.findViewById(R.id.userRecyclr);
        refresh = view.findViewById(R.id.refresh);
        txtNoOrders = view.findViewById(R.id.txtNoOrders);

        // Adapter
        countDelv = 0;
        listDelv = new ArrayList<>();

        // ---- Refresh ----------- //
        refresh.setOnRefreshListener(() -> {
            getDlivaryOrders();
            refresh.setRefreshing(false);
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        getDlivaryOrders();
        return view;
    }

    private void clearAdapter() {
        listDelv.clear();
        listDelv.trimToSize();
        countDelv = 0;
        recyclerView.setAdapter(null);
    }

    public void getDlivaryOrders() {
        clearAdapter();
        mDatabase.orderByChild("acceptedTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.exists() && ds.child("uAccepted").exists()) {
                            if(Objects.requireNonNull(ds.child("uAccepted").getValue()).toString().equals(uId)) {
                                if(Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("delivered")) {
                                    Data orderData = ds.getValue(Data.class);
                                    assert orderData != null;
                                    listDelv.add((int) countDelv, orderData);
                                    countDelv++;
                                    deliveryAdapter = new DeliveryAdapter(getContext(), listDelv, getContext(), countDelv);
                                    recyclerView.setAdapter(deliveryAdapter);
                                    updateNone(listDelv.size());
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.orderByChild("uAccepted").equalTo(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listDelv.size();i++){
                    if(listDelv.get(i).getId().equals(orderData.getId())) {
                        if(deliveryAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            deliveryAdapter  = new DeliveryAdapter(getContext(), listDelv,getContext(), countDelv);
                        }
                        deliveryAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data orderData = dataSnapshot.getValue(Data.class);
                assert orderData != null;
                for(int i = 0;i<listDelv.size();i++){
                    if(listDelv.get(i).getId().equals(orderData.getId())) {
                        orderData.setRemoved("true");
                        if(deliveryAdapter == null) {
                            Log.i(TAG,"adapter is null here");
                            deliveryAdapter  = new DeliveryAdapter(getContext(), listDelv, getContext(), countDelv);
                        }
                        deliveryAdapter.addItem(i, orderData);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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