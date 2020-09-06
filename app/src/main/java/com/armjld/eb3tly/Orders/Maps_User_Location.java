package com.armjld.eb3tly.Orders;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.armjld.eb3tly.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class Maps_User_Location extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double _long;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int  LOCATION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps__user__location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Maps_User_Location.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_CODE);
            Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                currentLocation = location;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(Maps_User_Location.this);

                LatLng lot = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                lat = lot.latitude;
                _long = lot.longitude;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(lot);
                markerOptions.title(lot.latitude + " , " + lot.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lot, 10));
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            lat = latLng.latitude;
            _long = latLng.longitude;
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + " , " + latLng.longitude);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.addMarker(markerOptions);
        });
    }
}