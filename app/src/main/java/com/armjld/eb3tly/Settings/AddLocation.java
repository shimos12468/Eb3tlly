package com.armjld.eb3tly.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.DatabaseClasses.LocationManeger.MakeLocationId;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

public class AddLocation extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener {

    String uId = UserInFormation.getId();


    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    LocationRequest mLocationRequest;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;

    String Title = "";
    String Address = "";
    public EditText txtTitle, txtAddress;
    Button btnSave;
    ImageView btnBack;
    LatLng currentLocation;
    GoogleApiClient mGoogleApiClient;
    private View viewMap;
    private static GoogleMap mMap;
    Spinner spnGov, spnCity;
    public static String Gov = "";
    public static String City = "";
    TextView txtDelete;
    public static String type = "New";
    String editID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        txtTitle = findViewById(R.id.txtTitle);
        txtAddress = findViewById(R.id.txtAddress);
        viewMap = findViewById(R.id.viewMap);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        txtDelete = findViewById(R.id.txtDelete);

        spnCity = findViewById(R.id.spnCity);
        spnGov = findViewById(R.id.spnGov);

        intalizeSpiner();

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اضافة عنوان جديد");

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        buildGoogleAPIClient();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        btnBack.setOnClickListener(v-> {
            finish();
            startActivity(new Intent(this, LocationForSup.class));
        });

        if(!type.equals("New")) {
            txtDelete.setVisibility(View.VISIBLE);
            editID = getIntent().getStringExtra("locID");
            uDatabase.child(uId).child("locations").child(editID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    txtTitle.setText(snapshot.child("title").getValue().toString());
                    txtAddress.setText(snapshot.child("address").getValue().toString());
                    getIndex(spnGov, snapshot.child("state").getValue().toString());
                    getIndex(spnCity, snapshot.child("region").getValue().toString());

                    Gov = snapshot.child("state").getValue().toString();
                    City = snapshot.child("region").getValue().toString();

                    double lati = Double.parseDouble(snapshot.child("lattude").getValue().toString());
                    double longLat = Double.parseDouble(snapshot.child("lontude").getValue().toString());

                    LatLng latLng = new LatLng(lati, longLat);
                    currentLocation = latLng;
                    if(mMap != null) {
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        txtDelete.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setTitle("حذف ؟").setMessage("هل انت متأكد من انك تريد حذف هذا العنوان ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {
                        Toast.makeText(getApplicationContext(), "تم حذف العنوان بنجاح", Toast.LENGTH_SHORT).show();
                        uDatabase.child(uId).child("locations").child(editID).removeValue();
                        startActivity(new Intent(AddLocation.this, LocationForSup.class));
                        finish();
                        dialogInterface.dismiss();
                    }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                    }).setAnimation(R.raw.location).build();
            mBottomSheetDialog.show();
        });

        btnSave.setOnClickListener(v-> {
            Title = txtTitle.getText().toString().trim();
            Address = txtAddress.getText().toString().trim();

            if(currentLocation == null) {
                Toast.makeText(this, "الرجاء تحديد مكانك علي الخريطة", Toast.LENGTH_SHORT).show();
                return;
            }

            if(Title.isEmpty()) {
                txtTitle.requestFocus();
                return;
            }

            if(Address.isEmpty()) {
                txtAddress.requestFocus();
                return;
            }

            if(City.isEmpty() || Gov.isEmpty()) {
                Toast.makeText(this, "اختار محافظتك و منطقتك", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double lat = currentLocation.latitude;
            double _long = currentLocation.longitude;

            // -- Title / Government / City // Address // lat // long
            // Now lets send them to user database ref

            MakeLocationId mID = new MakeLocationId(_long, lat);
            String locID = mID.getLocationid();

            DatabaseReference Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("locations").child(locID);
            Bdatabase.child("lattude").setValue(lat);
            Bdatabase.child("lontude").setValue(_long);
            Bdatabase.child("address").setValue(Address);
            Bdatabase.child("region").setValue(City);
            Bdatabase.child("state").setValue(Gov);
            Bdatabase.child("title").setValue(Title);
            Bdatabase.child("id").setValue(locID);

            Toast.makeText(this, "تم اضافة العنوان", Toast.LENGTH_LONG).show();
            finish();
            startActivity(new Intent(this, LocationForSup.class));
        });

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            checkGPS();
            return true;
        }
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        checkGPS();
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public void checkGPS() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) { // Check if GPS is Enabled
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("الرجاء فتح اعدادات اللوكيشن ؟")
                .setCancelable(false)
                .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
        buildGoogleAPIClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 105);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            dialog.show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, ""+ latLng.latitude, Toast.LENGTH_SHORT).show();
        mMap.clear();
        MarkerOptions marker = new MarkerOptions().position(latLng);
        mMap.addMarker(marker);
        currentLocation = latLng;
    }

    private void intalizeSpiner() {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.txtStates, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGov.setPrompt("اختار المحافظة");
        spnGov.setAdapter(adapter2);

        spnGov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Gov = spnGov.getSelectedItem().toString();
                int itemSelected = spnGov.getSelectedItemPosition();
                if (itemSelected == 0) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtCairoRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القاهرة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 1) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtGizaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الجيزة");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 2) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtAlexRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسكندرية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 3) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtMetroRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار محطة المترو");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 4) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtQalyobiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة القليوبية");
                    spnCity.setAdapter(adapter4);
                }else if (itemSelected == 5) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtSharqyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الشرقية");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 6) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtDqhlyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الدقهليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 7) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtAsyutRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسيوط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 8) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtAswanRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة اسوان");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 9) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtMenofyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنوفية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 10) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtIsmaliaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاسماعيليه");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 11) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtAqsorRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الاقصر");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 12) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtBeheraRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحيرة");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 13) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtBaniSwefRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بين سويف");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 14) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtPortSaidRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة بور سعيد");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 15) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtRedSeaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة البحر الاحمر");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 16) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtSouthSeniaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة جنوب سيناء");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 17) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtDomyatRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة دمياط");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 18) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtSohagRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة سوهاج");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 19) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtSuezRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة السويس");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 20) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtGarbyaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الغربية");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 21) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtFayoumRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة الفييوم");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 22) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtQenaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة قنا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 23) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtKafrRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة كفر الشيخ");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 24) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtNorthSenia, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة شمال سيناْء");
                    spnCity.setAdapter(adapter4);
                }  else if (itemSelected == 25) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtMatrohRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة مطروح");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 26) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtMeiaRegion, R.layout.color_spinner_layout);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCity.setPrompt("اختار منطقة محافظة المنيا");
                    spnCity.setAdapter(adapter4);
                } else if (itemSelected == 27) {
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(AddLocation.this, R.array.txtNewWadiRegion, R.layout.color_spinner_layout);
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