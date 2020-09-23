package com.armjld.eb3tly.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.R;

import Model.UserInFormation;

import com.armjld.eb3tly.Settings.Wallet.MyWallet;
import com.armjld.eb3tly.Login.Login_Options;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class SettingFragment extends Fragment {
    
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView txtName,txtType,txtPhone;
    TextView txtUserSettings,txtPassSettings,txtNotiSettings,txtLocationSettings,txtWallet,txtReports,txtSignOut;
    ImageView imgPPP,btnBack;
    DatabaseReference uDatabase;
    FirebaseAuth mAuth;
    String uId;

    public SettingFragment() { }
    
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        uId =  UserInFormation.getId();

        txtName = view.findViewById(R.id.txtName);
        txtType = view.findViewById(R.id.txtType);
        txtPhone = view.findViewById(R.id.txtPhone);
        imgPPP = view.findViewById(R.id.imgPPP);

        txtUserSettings = view.findViewById(R.id.txtUserSettings);
        txtPassSettings = view.findViewById(R.id.txtPassSettings);
        txtNotiSettings = view.findViewById(R.id.txtNotiSettings);
        txtLocationSettings = view.findViewById(R.id.txtLocationSettings);
        txtWallet = view.findViewById(R.id.txtWallet);
        txtReports = view.findViewById(R.id.txtReports);
        txtSignOut = view.findViewById(R.id.txtSignOut);
        btnBack = view.findViewById(R.id.btnBack);

        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("الاعدادات");

        setUserData();

        txtUserSettings.setOnClickListener(v-> startActivity(new Intent(getActivity(), UserInfo.class)));
        txtPassSettings.setOnClickListener(v-> startActivity(new Intent(getActivity(), ChangePassword.class)));

        txtNotiSettings.setOnClickListener(v-> Toast.makeText(getActivity(), "Not Yet", Toast.LENGTH_SHORT).show());

        txtLocationSettings.setOnClickListener(v-> {
            if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(getActivity(), LocationForDelv.class));
            } else {
                startActivity(new Intent(getActivity(), LocationForSup.class));
            }
        });

        txtWallet.setOnClickListener(v-> {
            startActivity(new Intent(getActivity(), MyWallet.class));
        });

        txtReports.setOnClickListener(v-> {
            if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(getActivity(), delv_statics.class));
            }
        });

        txtSignOut.setOnClickListener(v-> {
            signOut();
        });
        
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setUserData() {// ------------ Set User Data ----------- //
        String uType;
        txtName.setText(UserInFormation.getUserName());
        txtPhone.setText("+2" + UserInFormation.getPhone());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgPPP);
        if(UserInFormation.getAccountType().equals("Supplier")) {
            uType = "تاجر";
            txtWallet.setVisibility(View.GONE);
        } else if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            uType = "كابتن";
            txtWallet.setVisibility(View.VISIBLE);
        } else {
            uType = "خدمة عملاء";
            txtWallet.setVisibility(View.GONE);
        }
        txtType.setText(uType);
    }

    private void signOut() {
        uDatabase.child(uId).child("device_token").setValue("");
        requireActivity().finish();
        mAuth.signOut();
        if(Login_Options.mGoogleSignInClient != null) {
            Login_Options.mGoogleSignInClient.signOut();
        }
        startActivity(new Intent(getActivity(), Login_Options.class));
        Toast.makeText(getActivity(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }
}