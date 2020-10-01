package com.armjld.eb3tly.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Login.LoginManager;
import com.armjld.eb3tly.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import Model.UserInFormation;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class PaymentPanel extends AppCompatActivity {

    EditText txtPhone, txtMoney;
    Button btnAdd;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    TextInputLayout tlPhone, tlMoney;
    ImageView btnSignOut;
    private ProgressDialog mdialog;


    SimpleDateFormat monthSDF = new SimpleDateFormat("yyyy.MM", Locale.ENGLISH);
    String month = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_panel);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اضافة رصيد");
        mdialog = new ProgressDialog(this);


        tlPhone = findViewById(R.id.tlPhone);
        tlMoney = findViewById(R.id.tlMoney);
        txtPhone = findViewById(R.id.txtPhone);
        txtMoney = findViewById(R.id.txtMoney);
        btnAdd = findViewById(R.id.btnAdd);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(v-> {
            LoginManager _lgn = new LoginManager();
            _lgn.clearInfo(this);
        });

        txtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlMoney.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPhone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnAdd.setOnClickListener(v -> {
            String strPhone = txtPhone.getText().toString();
            String strMoney = txtMoney.getText().toString();

            if(strPhone.isEmpty()) {
                tlPhone.setError("ادخل رقم الهاتف");
                txtPhone.requestFocus();
                return;
            }

            if(strPhone.length() < 11) {
                tlPhone.setError("رقم هاتف غير صحيح");
                txtPhone.requestFocus();
                return;
            }

            if(strMoney.isEmpty()) {
                tlMoney.setError("ادخل المبلغ");
                txtMoney.requestFocus();
                return;
            }

            mdialog.setMessage("جاري اضافة الرصد ..");
            mdialog.show();
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("phone").equalTo(strPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot ds: snapshot.getChildren()) {
                            String userId = ds.child("id").getValue().toString();
                            DatabaseReference inputMoney = FirebaseDatabase.getInstance().getReference().child("Pickly").child("fawrypayments");
                            String id = inputMoney.push().getKey();
                            HashMap<String, Object> newPayment = new HashMap<>();
                            newPayment.put("phone", strPhone);
                            newPayment.put("money", strMoney);
                            newPayment.put("date", datee);
                            newPayment.put("id", id);
                            newPayment.put("month", month);
                            newPayment.put("by", UserInFormation.getUserName());

                            assert id != null;
                            inputMoney.child(id).setValue(newPayment);

                            txtPhone.setText("");
                            txtMoney.setText("");

                            notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1", userId , "","تم اضافة " + strMoney + " الي محفظتك.",datee,"false", "wallet","Quicker", "https://firebasestorage.googleapis.com/v0/b/pickly-ed2f4.appspot.com/o/ppUsers%2FVjAuarDirNeLf0pwtHX94srBMBg1.jpeg?alt=media&token=aa26474a-604a-4fd2-976e-aeeabeccd3ec");
                            FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(userId).push().setValue(Noti);

                            Toast.makeText(PaymentPanel.this, "تم اضافة المبلغ في رصيد المستخدم", Toast.LENGTH_SHORT).show();
                            mdialog.dismiss();
                        }
                    } else {
                        tlPhone.setError("رقم الهاتف غير مسجل");
                        Toast.makeText(PaymentPanel.this, "رقم هاتف غير صحيح", Toast.LENGTH_SHORT).show();
                        txtPhone.requestFocus();
                        mdialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });
    }
}