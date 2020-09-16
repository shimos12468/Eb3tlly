package com.armjld.eb3tly.delets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Adapters.RequestsAdapter;
import com.armjld.eb3tly.Chat.chatListclass;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Requests.RequestsForSup;
import com.armjld.eb3tly.Requests.rquests;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.DeleteData;
import Model.notiData;
import Model.requestsData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Delete_Delivery_From_Sup extends AppCompatActivity {

    private RadioGroup rdGroup;
    private RadioButton rd1,rd2,rd3,rd4;
    private String Msg = "";
    private EditText txtContact;
    private DatabaseReference dDatabase,mDatabase,nDatabase,uDatabase;
    FirebaseAuth mAuth;
    private String uId = UserInFormation.getId();
    Button btnSend;
    private String TAG = "Delete Captin";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onResume() {
        super.onResume();
        if(!StartUp.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__delivery__from__sup);

        dDatabase = getInstance().getReference().child("Pickly").child("delete_reason");
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        uDatabase = getInstance().getReference().child("Pickly").child("users");

        txtContact = findViewById(R.id.txtContact);
        rdGroup = findViewById(R.id.rdGroup);
        rd1 = findViewById(R.id.rd1);
        rd2 = findViewById(R.id.rd2);
        rd3 = findViewById(R.id.rd3);
        rd4 = findViewById(R.id.rd4);

        String orderID = getIntent().getStringExtra("orderid");
        String owner = getIntent().getStringExtra("owner");
        String acceptedID = getIntent().getStringExtra("acceptID");

        mAuth = FirebaseAuth.getInstance();
        btnSend = findViewById(R.id.btnSend);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("سبب الالغاء");

        rdGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rd4) {
                txtContact.setVisibility(View.VISIBLE);
            } else {
                txtContact.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(v -> {
            if(rd1.isChecked()) {
                Msg = "المندوب بيفاصل في سعر الشحن";
            } else if (rd2.isChecked()) {
                Msg = "المندوب عايز يغير معاد التسليم";
            } else if (rd3.isChecked()) {
                Msg = "تقيمات المندوب غير مناسبه";
            } else if (rd4.isChecked()) {
                if(txtContact.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرحاء توضيح سبب الالغاء", Toast.LENGTH_SHORT).show();
                    return;
                }
                Msg = txtContact.getText().toString();
            }

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        // --------------------------- Send Notifications ---------------------//
                        notiData Noti = new notiData(owner, acceptedID, orderID,"deleted",datee,"false", "profile");
                        assert acceptedID != null;
                        nDatabase.child(acceptedID).push().setValue(Noti);


                        assert orderID != null;

                        mDatabase.child(orderID).child("statue").setValue("placed");
                        mDatabase.child(orderID).child("uAccepted").setValue("");
                        mDatabase.child(orderID).child("acceptTime").setValue("");

                        String id = dDatabase.child(orderID).push().getKey();
                        DeleteData deleteData = new DeleteData(uId, orderID, Msg, datee, UserInFormation.getAccountType(), id);
                        assert id != null;
                        dDatabase.child(orderID).child(id).setValue(deleteData);

                        rquests _req = new rquests();
                        _req.deleteReq(acceptedID, orderID);

                        chatListclass chatList = new chatListclass();
                        chatList.supplierchat(acceptedID);

                        startActivity(new Intent(Delete_Delivery_From_Sup.this, supplierProfile.class));
                        Toast.makeText(this, "تم الغاء المندوب و جاري عرض اوردرك علي باقي المندوبين", Toast.LENGTH_LONG).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("هل انت متاكد من انك تريد الغاء المندوب ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

        });

    }
}