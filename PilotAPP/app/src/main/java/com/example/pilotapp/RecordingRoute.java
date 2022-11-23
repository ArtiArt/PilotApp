package com.example.pilotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RecordingRoute extends AppCompatActivity implements OnMapReadyCallback{

    private String routName;
    Context con = null;

    private boolean wasClicked=false;
    private MaterialCardView start_stopRecording;
    private TextView start_stop;
    GoogleMap myGoogleMap;
    private final int ACCESS_LOCATION_REQUEST_CODE = 1033;

//    Time to saving current localisation to kml file (1000 = 1s)
    private final int SAVING_TIME = 1000;

    FusedLocationProviderClient fusedLocationProviderClient;

    CreateFileKML createFile;
    private boolean threadWorking;

    KMLFileWriterThread kmlFileWriterThread = new KMLFileWriterThread();


    LatLng[] routePoints;
    Polyline line;


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

        Intent intent = getIntent();
        routName = intent.getStringExtra(SetRouteName.Extra_name);
        con = getApplicationContext();

        routePoints = new LatLng[2];

        setCreateFile(new CreateFileKML(con, routName));
        getCreateFile().writeStartElementToFile();

        Thread writingThread = new Thread(kmlFileWriterThread);

        start_stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wasClicked)
                {
                    setThreadWorking(false);
                    getCreateFile().writeEndElementsToFile();
                    Toast.makeText(RecordingRoute.this, "Trasa dodana", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RecordingRoute.this, MainActivity.class));
                    finish();
                }
                else
                {
                    //start recording and change text to "Zakończ" and wasClicked on true
                    setThreadWorking(true);
                    writingThread.start();
                    wasClicked = true;
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

    @SuppressLint("MissingPermission")
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

                PolylineOptions pOptions = new PolylineOptions()
                        .width(25)
                        .color(Color.BLUE)
                        .geodesic(true);


                if(isThreadWorking()) {
                    routePoints[0] = latLng;

                    if(routePoints[1] != null) {
                        for (int z = 0; z < routePoints.length; z++) {
                            LatLng point = routePoints[z];
                            pOptions.add(point);
                        }
                    }

                    line = myGoogleMap.addPolyline(pOptions);
                    routePoints[1] = latLng;

                    createFile.writeElementsToFile(location.getLatitude(), location.getLongitude());
//                    Toast.makeText(RecordingRoute.this, "Lat: " + location.getLatitude() + ", lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }

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
//                zoomToUserLocation();
            }
            else
            {

            }
        }
    }

    private boolean isThreadWorking() {
        return threadWorking;
    }

    private void setThreadWorking(boolean threadWorking) {
        this.threadWorking = threadWorking;
    }

    public CreateFileKML getCreateFile() {
        return createFile;
    }

    public void setCreateFile(CreateFileKML cf) {
        createFile = cf;
    }

    //    Thread class to start writing current localisation
    private class KMLFileWriterThread implements Runnable {
        @Override
        public void run() {
            while (isThreadWorking()) {
                zoomToUserLocation();

                Log.i("Watek", "Jestem w watek");
                try {
                    Thread.sleep(SAVING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
        getCreateFile().deleteFile();
        Toast.makeText(RecordingRoute.this, "Trasa usunięta", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RecordingRoute.this, SetRouteName.class));
        finish();
    }
}