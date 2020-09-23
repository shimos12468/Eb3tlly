package com.armjld.eb3tly.CaptinProfile;

import android.annotation.SuppressLint;
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

import com.armjld.eb3tly.Utilites.FragmentsAdapters.SectionsPagerAdapter;
import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class captinFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static DatabaseReference mDatabase;
    private DatabaseReference nDatabase;
    private ImageView imgSetPP,imgStar, imgVerf;
    private TextView txtUserDate,usType;
    private TextView uName;
    private TextView txtNotiCount;
    private String TAG = "Delivery Profile";
    RatingBar rbProfile;
    String uType = UserInFormation.getAccountType();
    static String uId;
    String isConfirmed = UserInFormation.getisConfirm();
    SectionsPagerAdapter sectionsPagerAdapter;

    public captinFragment() { }

    public static captinFragment newInstance(String param1, String param2) {
        captinFragment fragment = new captinFragment();
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
        View view = inflater.inflate(R.layout.fragment_captin, container, false);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((HomeActivity)getActivity()).getSystemService(Context.VIBRATOR_SERVICE);

        //txtTotalOrders = view.findViewById(R.id.txtTotalOrders);
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).keepSynced(true);
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        ConstraintLayout constNoti = view.findViewById(R.id.constNoti);
        uName = view.findViewById(R.id.txtUsername);
        txtUserDate = view.findViewById(R.id.txtUserDate);
        imgStar = view.findViewById(R.id.imgStar);
        imgSetPP = view.findViewById(R.id.imgPPP);
        txtNotiCount = view.findViewById(R.id.txtNotiCount);
        rbProfile = view.findViewById(R.id.rbProfile);
        imgVerf = view.findViewById(R.id.imgVerf);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        txtNotiCount.setVisibility(View.GONE);
        usType = view.findViewById(R.id.txtUserType);


        uId = UserInFormation.getId();
        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        NavigationView navigationView = view.findViewById(R.id.nav_view);

        txtUserDate.setText("اشترك : " + UserInFormation.getUserDate());
        tbTitle.setText("اوردراتي");
        usType.setText("كابتن");
        rbProfile.setRating(UserInFormation.getRating());
        uName.setText(UserInFormation.getUserName());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);

        viewPager.setAdapter(new SectionsPagerAdapter(getActivity(), getChildFragmentManager()));
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if(isConfirmed.equals("true")) {
            imgVerf.setVisibility(View.VISIBLE);
        }

        imgVerf.setOnClickListener(v -> Toast.makeText(getActivity(), "هذا الحساب مفعل برقم الهاتف و البطاقة الشخصية", Toast.LENGTH_SHORT).show());

        constNoti.setOnClickListener(v -> {
            vibe.vibrate(40);
            startActivityForResult(new Intent(getActivity(), Notifications.class), 1);
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

        return view;
    }
}