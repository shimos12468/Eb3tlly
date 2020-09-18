package com.armjld.eb3tly.LocationManeger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Intros.IntroFirstRun;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.SignUp.New_SignUp;
import com.armjld.eb3tly.Utilites.StartUp;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.Login_Options;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LocationForDelv extends AppCompatActivity {


    Button btnSave;
    DatabaseReference uDatabase;
    FirebaseAuth mAuth;
    String uId;
    Spinner spnGov, spnCity;
    String Gov;
    String City;
    ImageView btnBack;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_for_delv);

        spnCity = findViewById(R.id.spnCity);
        spnGov = findViewById(R.id.spnGov);
        btnSave = findViewById(R.id.btnSave);
        uId = UserInFormation.getId();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mAuth = FirebaseAuth.getInstance();
        btnBack = findViewById(R.id.btnBack);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("العناوين");

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGov.setPrompt("اختار المحافظة");
        spnGov.setAdapter(adapter2);

        intalizeSpiner();

        btnSave.setOnClickListener(v-> {
            uDatabase.child(uId).child("userState").setValue(Gov);
            uDatabase.child(uId).child("userCity").setValue(City);
            Toast.makeText(this, "تم تغيير العنوان بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v-> finish());

        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("userState").exists()) {
                    Gov = snapshot.child("userState").getValue().toString();
                    spnGov.setSelection(getIndex(spnGov,Gov));
                }

                if(snapshot.child("userCity").exists()) {
                    City =  snapshot.child("userCity").getValue().toString();
                    spnCity.setSelection(getIndex(spnCity,City));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        

    }

    private void intalizeSpiner() {
        spnGov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Gov = spnGov.getSelectedItem().toString();
                int itemSelected = spnGov.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القاهرة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الجيزة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار محطة المترو");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القليوبية");
                    spnCity.setAdapter(adapter4);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الشرقية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الدقهليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسيوط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسوان");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنوفية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاقصر");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحيرة");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بين سويف");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بور سعيد");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة دمياط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة سوهاج");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة السويس");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الغربية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الفييوم");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة قنا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة مطروح");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنيا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(LocationForDelv.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الوادي الجديد");
                    spnCity.setAdapter(adapter4);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City = spnCity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }



    private int getIndex(Spinner spinner, String value) {
        for(int i=0;i <spinner.getCount(); i++) {
            Log.i("LocationForDelv", spinner.getItemAtPosition(i).toString());
            if(spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }
}