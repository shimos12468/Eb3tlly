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

import com.armjld.eb3tly.Home.HomeFragment;
import com.armjld.eb3tly.Utilites.dilvPageAdapter;
import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class captinFragment extends Fragment {

    private static DatabaseReference mDatabase;
    private DatabaseReference nDatabase;
    private ImageView imgSetPP, imgVerf,btnBack;
    private TextView txtUserDate,usType;
    private TextView uName;
    private TextView txtNotiCount;
    private String TAG = "Delivery Profile";
    RatingBar rbProfile;
    static String uId;
    String isConfirmed = UserInFormation.getisConfirm();
    private Context mContext;

    public captinFragment() { }

    public static captinFragment newInstance(String param1, String param2) {
        captinFragment fragment = new captinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captin, container, false);

        Vibrator vibe = (Vibrator) Objects.requireNonNull((HomeActivity)getActivity()).getSystemService(Context.VIBRATOR_SERVICE);

        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).keepSynced(true);
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        btnBack = view.findViewById(R.id.btnBack);
        ConstraintLayout constNoti = view.findViewById(R.id.constNoti);
        uName = view.findViewById(R.id.txtUsername);
        txtUserDate = view.findViewById(R.id.txtUserDate);
        imgSetPP = view.findViewById(R.id.imgPPP);
        txtNotiCount = view.findViewById(R.id.txtNotiCount);
        rbProfile = view.findViewById(R.id.rbProfile);
        imgVerf = view.findViewById(R.id.imgVerf);
        txtNotiCount.setVisibility(View.GONE);
        usType = view.findViewById(R.id.txtUserType);

        btnBack.setOnClickListener(v-> {
            HomeActivity.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), HomeActivity.whichFrag).addToBackStack("Home").commit();
            HomeActivity.bottomNavigationView.setSelectedItemId(R.id.home);
        });


        uId = UserInFormation.getId();
        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        txtUserDate.setText("اشترك : " + UserInFormation.getUserDate());
        tbTitle.setText("اوردراتي");
        usType.setText("كابتن");

        rbProfile.setRating(UserInFormation.getRating());
        uName.setText(UserInFormation.getUserName());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgSetPP);

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new dilvPageAdapter(getActivity(), getChildFragmentManager()));
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