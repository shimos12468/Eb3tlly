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
import java.util.Objects;

import Model.Data;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

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

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
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

         View view =  inflater.inflate(R.layout.fragment_blank, container, false);
        //txtTotalOrders = view.findViewById(R.id.txtTotalOrders);
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
        recyclerView=view.findViewById(R.id.userRecycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        getDlivaryOrders();
        getOrderCountDel();

        return view;
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
    private void getOrderCountDel() {
        mDatabase.orderByChild("uAccepted").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
    public void getDlivaryOrders() {


        clearAdapter();
        mDatabase.orderByChild("acceptedTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.exists() && Objects.requireNonNull(ds.child("uAccepted").getValue()).toString().equals(uId)) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            listDelv.add((int) countDelv, orderData);
                            countDelv++;


                            deliveryAdapter = new DeliveryAdapter(getContext(), listDelv, getContext(), countDelv);
                            recyclerView.setAdapter(deliveryAdapter);
                            //updateNone(mm.size());
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
}