package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dlivared_orders_supplier#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dlivared_orders_supplier extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference uDatabase,mDatabase,rDatabase,nDatabase, vDatabase;
    private static ArrayList<Data> listSup;
    private static ArrayList<Data> listDelv;
    private long countSup;
    private long countDelv;
    private SupplierAdapter supplierAdapter;
    private DeliveryAdapter deliveryAdapter;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP;
    private TextView txtUserDate;
    private TextView uName;
    private TextView txtNotiCount,txtTotalOrders;
    private String TAG = "Profile";
    private RecyclerView recyclerView;
    String uType = StartUp.userType;
    String uId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dlivared_orders_supplier() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dlivared_orders_supplier.
     */
    // TODO: Rename and change types and number of parameters
    public static dlivared_orders_supplier newInstance(String param1, String param2) {
        dlivared_orders_supplier fragment = new dlivared_orders_supplier();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dlivared_orders_supplier, container, false);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        rDatabase = getInstance().getReference().child("Pickly").child("comments");
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uId = mUser.getUid();

        //FloatingActionButton btnAdd = view.findViewById(R.id.btnAdd);
        //btnAdd.setVisibility(View.GONE);

        //ImageView btnNavbarProfile = view.findViewById(R.id.btnNavbarProfile);
        //ConstraintLayout constNoti = view.findViewById(R.id.constNoti);
        //ImageView btnOpenNoti = view.findViewById(R.id.btnOpenNoti);
        //uName = view.findViewById(R.id.txtUsername);
        //txtUserDate = view.findViewById(R.id.txtUserDate);
        //TextView txtNoOrders = view.findViewById(R.id.txtNoOrders);
        //imgSetPP = view.findViewById(R.id.imgPPP);
        //txtNotiCount = view.findViewById(R.id.txtNotiCount);


        //Title Bar
        //TextView tbTitle = view.findViewById(R.id.toolbar_title);
        //tbTitle.setText("اوردراتي");
        //txtNoOrders.setVisibility(View.GONE);
        // txtNotiCount.setVisibility(View.GONE);

        // Adapter
        countDelv = 0;
        countSup = 0;
        listDelv = new ArrayList<>();
        listSup = new ArrayList<>();

        //Recycler
        recyclerView=view.findViewById(R.id.userRecyclr);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        getSupOrders();
        //getOrderCountSup();

        return view;
    }
    public void getSupOrders() {
        clearAdapter();
        Log.i(TAG, "Getting Supplier Orders");
        // ---------------------- GET ALL THE ORDERS -------------------//
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        listSup.add((int) countSup, orderData);
                        countSup++;
                        Log.i(TAG, "Inside the Query");

                        supplierAdapter = new SupplierAdapter(getContext(), listSup, getContext(), countSup);
                        recyclerView.setAdapter(supplierAdapter);                     }
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
        listDelv.clear();
        listDelv.trimToSize();
        listSup.trimToSize();
        countSup = 0;
        countDelv = 0;
        recyclerView.setAdapter(null);
    }


    /*private void getOrderCountSup() {
        mDatabase.orderByChild("uId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    String strCount = String.valueOf(count);
                    txtTotalOrders.setText( "وصل " + strCount + " اوردر");
                } else {
                    txtTotalOrders.setText("لم يقم بتوصيل اي اوردر");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}