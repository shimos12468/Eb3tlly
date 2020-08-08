package com.armjld.eb3tly;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        assert orderID != null;
                        String id = dDatabase.child(orderID).push().getKey();
                        DeleteData deleteData = new DeleteData(uId, orderID, Msg, datee, UserInFormation.getAccountType(), id);
                        dDatabase.child(orderID).child(id).setValue(deleteData);

                        // --------------- Add the cencelled order to the counter ----------------------- //
                        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Date lastedit = null;
                                Date acceptedDate = null;
                                try {
                                    lastedit = sdf.parse(editTime);
                                    acceptedDate = sdf.parse(acceptTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // ------------------------------- Adding the order to the worker cancelled orders counter --------------- //
                                int cancelledCount =  Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("canceled").getValue()).toString());
                                int finalCount = (cancelledCount + 1);
                                int reminCount = 3 - cancelledCount - 1;

                                assert acceptedDate != null;
                                if(acceptedDate.compareTo(lastedit) > 0 && !rd6.isChecked() && !rd5.isChecked()) { // if the worker accepted the order before it has been edited
                                    uDatabase.child(uId).child("canceled").setValue(String.valueOf(finalCount));
                                    Toast.makeText(Delete_Reaon_Delv.this, "تم حذف الاوردر بنجاح و تبقي لديك " + reminCount + " فرصه لالغاء الاوردرات هذا الاسبوع", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Delete_Reaon_Delv.this, "تم حذف الاوردر بنجاح", Toast.LENGTH_SHORT).show();
                                }

                                // --------------- Setting the order as placed again ---------------- //
                                mDatabase.child(orderID).child("statue").setValue("placed");
                                mDatabase.child(orderID).child("uAccepted").setValue("");
                                mDatabase.child(orderID).child("acceptTime").setValue("");

                                // --------------------------- Send Notifications ---------------------//
                                notiData Noti = new notiData(uId, owner, orderID,"deleted",datee,"false");
                                nDatabase.child(owner).push().setValue(Noti);
                                startActivity(new Intent(Delete_Reaon_Delv.this, NewProfile.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Delete_Reaon_Delv.this);
            builder.setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

        });
    }
}