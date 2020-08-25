package com.armjld.eb3tly.delets;

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

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.Profiles.supplierProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        assert orderID != null;
                        String id = dDatabase.child(orderID).push().getKey();
                        DeleteData deleteData = new DeleteData(uId, orderID, Msg, datee, UserInFormation.getAccountType(), id);
                        assert id != null;
                        dDatabase.child(orderID).child(id).setValue(deleteData);

                        mDatabase.child(orderID).child("statue").setValue("deleted");
                        Toast.makeText(this, "شكرا لك, تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Delete_Reason_Supplier.this, supplierProfile.class));

                        if(acceptID != "") {
                            notiData Noti = new notiData(uId, acceptID, orderID,"deleted",datee,"false", "profile");
                            nDatabase.child(acceptID).push().setValue(Noti);
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
        });
    }
}