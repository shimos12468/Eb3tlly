package com.armjld.eb3tly;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.DeleteData;
import Model.notiData;
import Model.reportData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Delete_Delivery_From_Sup extends AppCompatActivity {

    private RadioGroup rdGroup;
    private RadioButton rd1,rd2,rd3,rd4;
    private String Msg = "";
    private EditText txtContact;
    private DatabaseReference dDatabase,mDatabase,nDatabase,uDatabase;
    FirebaseAuth mAuth;
    Button btnSend;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

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
            }

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        // --------------------------- Send Notifications ---------------------//
                        notiData Noti = new notiData(owner, acceptedID, orderID,"deleted",datee,"false");
                        assert acceptedID != null;
                        nDatabase.child(acceptedID).push().setValue(Noti);


                        assert orderID != null;
                        mDatabase.child(orderID).child("uAccepted").setValue("");
                        mDatabase.child(orderID).child("statue").setValue("placed");

                        String id = dDatabase.child(orderID).push().getKey();
                        DeleteData deleteData = new DeleteData(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), orderID, Msg, datee, StartUp.userType, id);
                        assert id != null;
                        dDatabase.child(orderID).child(id).setValue(deleteData);

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