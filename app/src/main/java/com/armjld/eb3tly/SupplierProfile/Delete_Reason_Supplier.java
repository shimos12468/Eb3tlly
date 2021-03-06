package com.armjld.eb3tly.SupplierProfile;

import androidx.appcompat.app.AppCompatActivity;

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
import com.armjld.eb3tly.Login.LoginManager;
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

public class Delete_Reason_Supplier extends AppCompatActivity {

    private RadioButton rd1,rd2,rd3;
    private String Msg = "";
    private EditText txtContact;
    private DatabaseReference dDatabase,mDatabase,nDatabase;
    FirebaseAuth mAuth;
    Button btnSend;

    String uId = UserInFormation.getId();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

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
        setContentView(R.layout.activity_delete__reason__supplier);

        rd1 = findViewById(R.id.rd1);
        rd2 = findViewById(R.id.rd2);
        rd3 = findViewById(R.id.rd3);

        dDatabase = getInstance().getReference().child("Pickly").child("delete_reason");
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");


        txtContact = findViewById(R.id.txtContact);
        RadioGroup rdGroup = findViewById(R.id.rdGroup);
        String orderID = getIntent().getStringExtra("orderid");
        String acceptID = getIntent().getStringExtra("acceptID");
        mAuth = FirebaseAuth.getInstance();
        btnSend = findViewById(R.id.btnSend);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("سبب الالغاء");

        rdGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rd3) {
                txtContact.setVisibility(View.VISIBLE);
            } else {
                txtContact.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(v -> {
            if(rd1.isChecked()) {
                Msg = "العميل الغي الاوردر";
            } else if (rd2.isChecked()) {
                Msg = "تم التواصل مع مندوب من خارج الابلكيشن";
            } else if(rd3.isChecked()) {
                if(txtContact.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرحاء توضيح سبب الالغاء", Toast.LENGTH_SHORT).show();
                    return;
                }
                Msg = txtContact.getText().toString();
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {

                assert orderID != null;
                String id = dDatabase.child(orderID).push().getKey();
                DeleteData deleteData = new DeleteData(uId, orderID, Msg, datee, UserInFormation.getAccountType(), id);
                assert id != null;
                dDatabase.child(orderID).child(id).setValue(deleteData);

                rquests requests = new rquests();
                requests.deletedOrder(orderID);

                mDatabase.child(orderID).child("statue").setValue("deleted");


                assert acceptID != null;
                if(!acceptID.equals("")) {
                    String message = "قام " + UserInFormation.getUserName() + " بالغاء الاوردر";
                    notiData Noti = new notiData(uId, acceptID, orderID,message,datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                    nDatabase.child(acceptID).push().setValue(Noti);
                    chatListclass chatList = new chatListclass();
                    chatList.supplierchat(acceptID);
                }

                Toast.makeText(this, "شكرا لك, تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                finish();

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });
    }
}