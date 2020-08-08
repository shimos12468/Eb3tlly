package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Admin_Delete_Statics extends AppCompatActivity {

    private DatabaseReference dDatabase,mDatabase;
    private TextView sub1,sub2,del1,del2,del3,del4,del5,subdel1,subdel2,subdel3,txtTotalDeleted;
    ImageView btnRefresh,imgLogo;
    private ProgressDialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__delete__statics);

        Vibrator vibe = (Vibrator) Objects.requireNonNull(this).getSystemService(Context.VIBRATOR_SERVICE);
        mdialog = new ProgressDialog(this);

        btnRefresh = findViewById(R.id.btnRefresh);
        imgLogo = findViewById(R.id.imgLogo);
        dDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("delete_reason");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        sub1 = findViewById(R.id.sub1);
        sub2 = findViewById(R.id.sub2);
        del1 = findViewById(R.id.del1);
        del2 = findViewById(R.id.del2);
        del3 = findViewById(R.id.del3);
        del4 = findViewById(R.id.del4);
        del5 = findViewById(R.id.del5);
        subdel1 = findViewById(R.id.subdel1);
        subdel2 = findViewById(R.id.subdel2);
        subdel3 = findViewById(R.id.subdel3);

        txtTotalDeleted = findViewById(R.id.txtTotalDeleted);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اسباب الغاء الاوردرات");

        imgLogo.setOnClickListener(v -> Toast.makeText(this, "I AM CEO, BITCH", Toast.LENGTH_SHORT).show());
        getStats();
        btnRefresh.setOnClickListener(v -> {
            getStats();
        });
    }

    private void getStats() {
        mdialog.setMessage("Getting Statics ..");
        mdialog.show();
        dDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int intsup1 = 0;
                int intsup2 = 0;
                int intsup3 = 0;
                int intdel1 = 0;
                int intdel2 = 0;
                int intdel3 = 0;
                int intdel4 = 0;
                int intdel5 = 0;
                int intdel6 = 0;
                int intsubdel1 = 0;
                int intsubdel2 = 0;
                int intsubdel3 = 0;

                for(DataSnapshot eachUser : snapshot.getChildren()) {
                    for(DataSnapshot eachReason : eachUser.getChildren()) {
                        String cause = eachReason.child("reason").getValue().toString();
                        switch (cause) {
                            case "العميل الغي الاوردر": {
                                intsup1 ++;
                                break;
                            }
                            case "تم التواصل مع مندوب من خارج الابلكيشن" : {
                                intsup2++;
                                break;
                            }
                            case "معاد الاستلام لا يناسبني": {
                                intdel1 ++;
                                break;
                            }
                            case "معاد التسليم لا يناسبني": {
                                intdel2 ++;
                                break;
                            }
                            case "التاجر ادخل خطأ في البيانات": {
                                intdel3 ++;
                                break;
                            }
                            case "التاجر سلم الاوردر لمندوب اخر": {
                                intdel4 ++;
                                break;
                            }
                            case "التاجر لا يرد علي الهاتف": {
                                intdel5 ++;
                                break;
                            }

                            case "المندوب بيفاصل في سعر الشحن": {
                                intsubdel1 ++;
                                break;
                            }
                            case "المندوب عايز يغير معاد التسليم": {
                                intsubdel2 ++;
                                break;
                            }
                            case "تقيمات المندوب غير مناسبه": {
                                intsubdel3 ++;
                                break;
                            }
                        }
                    }
                }

                int totalSup = intsup1 + intsup2 + intsup3;
                sub1.setText("العميل الغي الاوردر " + (intsup1 * 100 / totalSup) + " %");
                sub2.setText("تم التواصل مع مندوب من خارج الابلكيشن " + (intsup2 * 100 / totalSup) + " %");

                int totalDelv = intdel1 + intdel2 + intdel3 + intdel4 + intdel5 + intdel6;
                del1.setText("المعاد الاستلام لا يناسبني " + (intdel1 * 100 / totalDelv) + " %");
                del2.setText("معاد التسليم لا يناسبني " + (intdel2 * 100 / totalDelv) + " %");
                del3.setText("التاجر ادخل خطأ في البيانات " + (intdel3 * 100 / totalDelv) + " %");
                del4.setText("التاجر سلم الاوردر لمندوب اخر " + (intdel4 * 100 / totalDelv) + " %");
                del5.setText("التاجر لا يرد علي الهاتف " + (intdel5 * 100 / totalDelv) + " %");

                int totalSupDel = intsubdel1 + intsubdel2 + intsubdel3;
                if(totalSupDel == 0) {
                    totalSupDel = 1;
                }
                subdel1.setText("المندوب بيفاصل في سعر الشحن " + (intsubdel1 * 100 / totalSupDel) + " %");
                subdel2.setText("المندوب عايز يغير معاد التسليم " + (intsubdel2 * 100 / totalSupDel) + " %");
                subdel3.setText("تقيمات المندوب غير مناسبه " + (intsubdel3 * 100 / totalSupDel) + " %");


                Toast.makeText(Admin_Delete_Statics.this, "Statics are Available NOW", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabase.orderByChild("statue").equalTo("deleted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                txtTotalDeleted.setText("عدد الاوردرات الملغيه " + count + " اوردر");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}