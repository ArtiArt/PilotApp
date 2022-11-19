package com.example.pilotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

public class RecordingRoute extends AppCompatActivity implements OnMapReadyCallback{

    private boolean wasClicked=false;
    private MaterialCardView start_stopRecording;
    private TextView start_stop;
    GoogleMap myGoogleMap;
    final int ACCESS_LOCATION_REQUEST_CODE=1033;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recording_route);

        start_stopRecording = findViewById(R.id.start_stopRecordingBtn);
        start_stop=findViewById(R.id.start_stop);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        start_stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wasClicked)
                {
                    //write down recorded route and go to MainActivity
                    /*startActivity(new Intent(RecordingRoute.this, MainActivity.class));
                    finish();*/
                    startActivity(new Intent(RecordingRoute.this, Racing.class));
                    finish();
                }
                else
                {
                    //start recording and change text to "Zakończ" and wasClicked on true
                    wasClicked=true;
                    start_stop.setText("Zakończ");
                }
            }
        });

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myGoogleMap=googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.
                PERMISSION_GRANTED)
        {
            enableUserLocation();
            zoomToUserLocation();
        }
        else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //asking for permission
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            }
        }
    }
    private void enableUserLocation(){
        myGoogleMap.setMyLocationEnabled(true);
    }
    private  void  zoomToUserLocation()
    {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            }
            else
            {

            }
        }
    }
}