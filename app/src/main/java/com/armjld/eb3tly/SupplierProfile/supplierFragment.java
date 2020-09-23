package com.armjld.eb3tly.SupplierProfile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Utilites.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class supplierFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static DatabaseReference mDatabase;
    private DatabaseReference nDatabase;
    private DatabaseReference vDatabase;
    private FirebaseAuth mAuth;
    private ImageView imgSetPP, imgStar,imgVerf;
    private TextView txtUserDate,uName,txtNotiCount;
    private String TAG = "Supplier Profile";
    private ConstraintLayout constSupProfile;
    private static String uId = UserInFormation.getId();
    private ViewPager viewPager;
    private ProgressDialog mdialog;
    private String isConfirmed;

    public supplierFragment() { }
    
    public static supplierFragment newInstance(String param1, String param2) {
        supplierFragment fragment = new supplierFragment();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supplier, container, false);

        RatingBar rbProfile = view.findViewById(R.id.rbProfile);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((HomeActivity)getActivity()).getSystemService(Context.VIBRATOR_SERVICE);

        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        mDatabase.orderByChild("uId").equalTo(UserInFormation.getId()).keepSynced(true);
        vDatabase = getInstance().getReference().child("Pickly").child("values");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        constSupProfile= view.findViewById(R.id.constSupProfile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        mdialog = new ProgressDialog(getActivity());
        assert mUser != null;
        isConfirmed = UserInFormation.getisConfirm();


        FloatingActionButton btnAdd = view.findViewById(R.id.btnAdd);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        rbProfile.setRating(UserInFormation.getRating());
        ConstraintLayout constNoti = view.findViewById(R.id.constNoti);
        uName = view.findViewById(R.id.txtUsername);
        txtUserDate = view.findViewById(R.id.txtUserDate);
        imgSetPP = view.findViewById(R.id.imgPPP);
        txtNotiCount = view.findViewById(R.id.txtNotiCount);
        imgStar = view.findViewById(R.id.imgStar);
        imgVerf= view.findViewById(R.id.imgVerf);

        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("اوردراتي");

        txtUserDate.setText("اشترك : " + UserInFormation.getUserDate());
        uName.setText(UserInFormation.getUserName());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);
        TextView usType = view.findViewById(R.id.txtUserType);
        usType.setText("تاجر");


        if(isConfirmed.equals("true")) {
            imgVerf.setVisibility(View.VISIBLE);
        }

        imgVerf.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show();
        });

        constNoti.setOnClickListener(v-> {
            startActivity(new Intent(getActivity(), Notifications.class));
        });

        // -------------------------- Get users Notifications Count -------------------//
        nDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int notiCount = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.exists() && ds.child("isRead").exists()) {
                            if(Objects.equals(ds.child("isRead").getValue(), "false")) {
                                notiCount++;
                            }
                        }
                    }
                    if(notiCount > 0) {
                        txtNotiCount.setVisibility(View.VISIBLE);
                        txtNotiCount.setText(""+notiCount);
                    } else {
                        txtNotiCount.setVisibility(View.GONE);
                    }
                } else {
                    txtNotiCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        btnAdd.setOnClickListener(v -> {
            vibe.vibrate(40);
            mdialog.setMessage("جاري التاكد من اتصال الانترنت ..");
            mdialog.show();
            vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if(Objects.requireNonNull(dataSnapshot.child("adding").getValue()).toString().equals("false")) {
                            Toast.makeText(getActivity(), "عذرا لا يمكنك اضافه اوردرات في الوقت الحالي حاول في وقت لاحق", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(new Intent(getActivity(), AddOrders.class));
                        }
                        mdialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        });
        
        return view;
    }
    
}