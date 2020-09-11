package com.armjld.eb3tly.Orders;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.main.HomeActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import Model.Data;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Location currentLocation;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final String TAG = "Maps";
    private GoogleMap mMap;
    LocationRequest mLocationRequest;

    String filterDate;
    String uType = UserInFormation.getAccountType();
    String uId = UserInFormation.getId();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    BlockManeger block = new BlockManeger();
    // import firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, uDatabase, Database;
    private ImageView btnHome;
    private FloatingActionButton btnGCL;


    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_orders);
        filterDate = format.format(Calendar.getInstance().getTime());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Database
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        mDatabase.keepSynced(true);
        uDatabase.keepSynced(true);
        btnHome = findViewById(R.id.btnHome);
        btnGCL = findViewById(R.id.btnGCL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("خريطة الاوردرات");

        btnHome.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        });

        btnGCL.setVisibility(View.GONE);
        btnGCL.setOnClickListener(v -> {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) { // Check if GPS is Enabled
                buildAlertMessageNoGps();
            } else {
                fetchLocation();
            }
        });
        fetchLocation();
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

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient();
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
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        btnGCL.setVisibility(View.VISIBLE);
        checkGPS();
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }
        mDatabase.orderByChild("ddate").startAt(filterDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                            Data orderData = ds.getValue(Data.class);
                            assert orderData != null;
                            Date orderDate = null;
                            Date myDate = null;
                            try {
                                orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString().replaceAll("(^\\h*)|(\\h*$)",""));
                                myDate =  format.parse(sdf.format(Calendar.getInstance().getTime()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            assert orderDate != null;
                            assert myDate != null;
                        if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed") && !block.check(orderData.getuId()) && ds.child("long").exists() && ds.child("lat").exists()) {
                            String owner = orderData.getuId();
                            String pAddress = orderData.getmPAddress();
                            double lati = Double.parseDouble(Objects.requireNonNull(ds.child("lat").getValue()).toString());
                            double longLat = Double.parseDouble(Objects.requireNonNull(ds.child("long").getValue()).toString());
                            uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String uName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                    String snipText = "من : " + pAddress + "\n" +  " الي : " + orderData.getDAddress() + "\n" + " مقدم : " + orderData.getGMoney() + "\n" + " مصاريف شحن : " + orderData.getGGet() + "\n" + " اضغط هنا للمزيد من البيانات.";
                                    Marker marker = mMap.addMarker(new MarkerOptions().position(
                                            new LatLng(lati,longLat)).title(uName).snippet(snipText).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.app_icon)));
                                    marker.setTag(orderData.getId());

                                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                        @Override
                                        public View getInfoWindow(Marker arg0) {
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {

                                            LinearLayout info = new LinearLayout(MapsActivity.this);
                                            info.setOrientation(LinearLayout.VERTICAL);

                                            TextView title = new TextView(MapsActivity.this);
                                            title.setTextColor(Color.RED);
                                            title.setGravity(Gravity.CENTER);
                                            title.setTypeface(null, Typeface.BOLD);
                                            title.setText(marker.getTitle());

                                            TextView txt1 = new TextView(MapsActivity.this);
                                            txt1.setGravity(Gravity.CENTER);
                                            txt1.setTextColor(Color.BLACK);
                                            txt1.setElegantTextHeight(true);
                                            txt1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                            txt1.setSingleLine(false);
                                            txt1.setText(marker.getSnippet());

                                            info.addView(title);
                                            info.addView(txt1);

                                            return info;
                                        }
                                    });

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_baseline_my_location_24));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.addMarker(markerOptions);
        }

        mMap.setOnInfoWindowClickListener(arg0 -> {
            if(arg0.getTag() != null) {
                String orderID = Objects.requireNonNull(arg0.getTag()).toString();
                Intent OneOrder = new Intent(MapsActivity.this, com.armjld.eb3tly.Orders.OneOrder.class);
                OneOrder.putExtra("oID", orderID);
                startActivity(OneOrder);
                Log.i(TAG, "Opening the order from the Map!.");
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onClick(View view) { }


    private void checkGPS() {

        if(mGoogleApiClient == null) {
            buildGoogleAPIClient();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            final LocationSettingsStates states = result1.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    fetchLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Toast.makeText(this, "This is Sad.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /*@Override
    public void onInfoWindowClick(Marker marker) {

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        fetchLocation();
                        Toast.makeText(this, "Thank You", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "Fuck You", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    @Override
    public void onInfoWindowClick(Marker marker) {

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
    public void onConnected(@Nullable Bundle bundle) {
        /*mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
        }*/
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

    

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleAPIClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}