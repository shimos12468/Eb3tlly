package com.armjld.eb3tly.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.DatabaseClasses.LocationManeger.LocationManeger;
import com.armjld.eb3tly.DatabaseClasses.LocationManeger.MakeLocationId;
import com.armjld.eb3tly.FilterAdapter;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import java.util.Objects;

public class AddLocation extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener {

    String uId = UserInFormation.getId();


    String Title = "";
    String Address = "";
    public EditText txtTitle, txtAddress;
    Button btnSave;
    ImageView btnBack;
    LatLng currentLocation;
    GoogleApiClient mGoogleApiClient;
    private static GoogleMap mMap;
    TextView txtDelete;
    public static String type = "New";
    String editID;
    AutoCompleteTextView txtDropCity;
    public static String goTo = "";

    String dropVar;
    String strDropGov = "";
    String strDropCity = "";
    ArrayAdapter<String> dropCityAda;

    LocationManeger _Loc = new LocationManeger();

    TextInputLayout tlTitle, tlAddress, tlCity;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        txtTitle = findViewById(R.id.txtTitle);
        txtAddress = findViewById(R.id.txtAddress);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        txtDelete = findViewById(R.id.txtDelete);
        txtDropCity = findViewById(R.id.txtDropCity);

        tlTitle = findViewById(R.id.tlTitle);
        tlAddress = findViewById(R.id.tlAddress);
        tlCity= findViewById(R.id.tlCity);

        txtDelete.setVisibility(View.GONE);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اضافة عنوان جديد");

        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

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

        // ----------- Set Auto Complete for Cities ----------------- //
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        dropCityAda = new FilterAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        txtDropCity.setAdapter(dropCityAda);

        txtDropCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dropVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtDropCity.setOnItemClickListener((parent, view, position, id) -> {
            dropVar = Objects.requireNonNull(dropCityAda.getItem(position)).trim();
            String [] sep = dropVar.split(", ");
            strDropGov = sep[0].trim();
            strDropCity = sep[1].trim();
        });

        if(!type.equals("New")) {
            txtDelete.setVisibility(View.VISIBLE);
            editID = getIntent().getStringExtra("locID");

            if(editID == null) {
                return;
            }
            uDatabase.child(uId).child("locations").child(editID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    txtTitle.setText(Objects.requireNonNull(snapshot.child("title").getValue()).toString());
                    txtAddress.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                    txtDropCity.setText(getGov( Objects.requireNonNull(snapshot.child("state").getValue()).toString(), Objects.requireNonNull(snapshot.child("region").getValue()).toString()));

                    double lati = Double.parseDouble(Objects.requireNonNull(snapshot.child("lattude").getValue()).toString());
                    double longLat = Double.parseDouble(Objects.requireNonNull(snapshot.child("lontude").getValue()).toString());

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
                        //uDatabase.child(uId).child("locations").child(editID).removeValue();
                        _Loc.Remove(editID);
                        startActivity(new Intent(AddLocation.this, LocationForSup.class));
                        finish();
                        dialogInterface.dismiss();
                    }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).setAnimation(R.raw.location).build();
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
                tlTitle.setError("الرجاء ادخال اسم العنوان الخاص بك");
                txtTitle.requestFocus();
                return;
            }

            if(Address.isEmpty()) {
                tlAddress.setError("الرجاء ادخال رقم المبن و اسم الشارع");
                txtAddress.requestFocus();
                return;
            }

            if(dropVar.isEmpty()) {
                tlCity.setError("الرجاء اختيار احد المناطق المتاحة");
                txtDropCity.requestFocus();
                return;
            }
            
            double lat = currentLocation.latitude;
            double _long = currentLocation.longitude;

            MakeLocationId mID = new MakeLocationId(_long, lat);
            String locID = mID.getLocationid();

            DatabaseReference Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("locations").child(locID);
            Bdatabase.child("lattude").setValue(lat);
            Bdatabase.child("lontude").setValue(_long);
            Bdatabase.child("address").setValue(Address);
            Bdatabase.child("region").setValue(strDropGov);
            Bdatabase.child("state").setValue(strDropCity);
            Bdatabase.child("title").setValue(Title);
            Bdatabase.child("id").setValue(locID);

            _Loc.ImportLocation();

            Toast.makeText(this, "تم اضافة العنوان", Toast.LENGTH_LONG).show();

            if(goTo.equals("Add Orders")) {
                finish();
                startActivity(new Intent(this, AddOrders.class));
            } else {
                finish();
                startActivity(new Intent(this, LocationForSup.class));
            }
        });

    }

    private String getGov(String state, String region) {
        dropVar = state + ", " + region;
        strDropGov = state;
        strDropCity = region;
        return state + ", " + region;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            checkGPS();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkGPS();
                }

            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
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
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(AddLocation.this).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).setAnimation(R.raw.location).build();
        mBottomSheetDialog.show();
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
            assert dialog != null;
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
        mMap.clear();
        MarkerOptions marker = new MarkerOptions().position(latLng);
        mMap.addMarker(marker);
        currentLocation = latLng;
    }

}