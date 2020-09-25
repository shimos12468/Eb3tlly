package com.armjld.eb3tly.CaptinProfile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Chat.chatListclass;
import com.armjld.eb3tly.LoginManager;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.DatabaseClasses.rquests;
import com.armjld.eb3tly.Home.StartUp;
import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Model.DeleteData;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Delete_Reaon_Delv extends AppCompatActivity {

    private RadioGroup rdGroup;
    private RadioButton rd1,rd2,rd3,rd4,rd5,rd6;
    private String Msg = "";
    private EditText txtContact;
    private DatabaseReference dDatabase,mDatabase,nDatabase,uDatabase;
    FirebaseAuth mAuth;
    Button btnSend;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__reaon__delv);

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
        rd5 = findViewById(R.id.rd5);
        rd6 = findViewById(R.id.rd6);

        String orderID = getIntent().getStringExtra("orderid");
        String owner = getIntent().getStringExtra("owner");
        String acceptTime =  getIntent().getStringExtra("aTime");
        String editTime =  getIntent().getStringExtra("eTime");
        String clientName = getIntent().getStringExtra("dName");

        mAuth = FirebaseAuth.getInstance();
        String uId = UserInFormation.getId();
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
                    Msg = "معاد الاستلام لا يناسبني";
            } else if (rd2.isChecked()) {
                    Msg = "معاد التسليم لا يناسبني";
            } else if (rd3.isChecked()) {
                    Msg = "التاجر ادخل خطأ في البيانات";
            } else if (rd4.isChecked()) {
                if(txtContact.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرحاء توضيح سبب الالغاء", Toast.LENGTH_SHORT).show();
                    return;
                }
                Msg = txtContact.getText().toString();
            } else if(rd5.isChecked()) {
                Msg = "التاجر سلم الاوردر لمندوب اخر";
            } else if(rd6.isChecked()) {
                Msg = "التاجر لا يرد علي الهاتف";
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {

                assert orderID != null;
                String id = dDatabase.child(orderID).push().getKey();
                DeleteData deleteData = new DeleteData(uId, orderID, Msg, datee, UserInFormation.getAccountType(), id);
                assert id != null;
                dDatabase.child(orderID).child(id).setValue(deleteData);

                // --------------- Setting the order as placed again ---------------- //
                mDatabase.child(orderID).child("statue").setValue("placed");
                mDatabase.child(orderID).child("uAccepted").setValue("");
                mDatabase.child(orderID).child("acceptTime").setValue("");

                rquests _req = new rquests();
                _req.deleteReq(uId,orderID);

                chatListclass chatList = new chatListclass();
                chatList.dlevarychat(owner);

                // --------------------------- Send Notifications ---------------------//
                String message = "قام " + UserInFormation.getUserName() + " بالغاء اوردر " + clientName;
                notiData Noti = new notiData(uId, owner, orderID,message,datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                assert owner != null;
                nDatabase.child(owner).push().setValue(Noti);
                Toast.makeText(this, "تم الغاء الاوردر", Toast.LENGTH_SHORT).show();
                finish();

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });
    }
}