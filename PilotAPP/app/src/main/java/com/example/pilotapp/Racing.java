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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Racing extends AppCompatActivity implements OnMapReadyCallback{

    private String routName;
    Context con = null;
    private boolean wasClicked=false;
    private MaterialCardView start_stopBtn;
    private TextView start_stop, routeDiscription;
    GoogleMap myGoogleMap;
    private final int ACCESS_LOCATION_REQUEST_CODE=1033;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng[] routePoints;
    Polyline line;
    private static final String FILE_NAME = "/data/data/com.example.pilotapp/app_allRoutes/";
    List<LatLng> filePoints=null;
    LatLng myLocation;
    double distance, finalDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_racing);
        start_stopBtn = findViewById(R.id.start_stopBtn);
        start_stop=findViewById(R.id.start_stop);
        routeDiscription=findViewById(R.id.routeDiscription);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
       routName = intent.getStringExtra("chosenTrace");
        routePoints = new LatLng[2];
        filePoints = new ArrayList<LatLng>();
        start_stopBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (wasClicked)
                {
                    //write down recorded time and go to MainActivity
                    startActivity(new Intent(Racing.this, MainActivity.class));
                    finish();

                }
                else
                {
                    //start recording time and change text to "Stop" and wasClicked on true
                    ReedFromKml();
                    wasClicked=true;
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
                myLocation = new LatLng(location.getLatitude(),location.getLongitude());
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

    private void ReedFromKml(){
        FileInputStream fis =null;
        String nextPoints="";
        try {
            String fileName = FILE_NAME + routName;
            fis = new FileInputStream(new File(fileName));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null){
                sb.append(text).append("#\n");
            }
            nextPoints=sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fis != null){
                try{
                    fis.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        String[] myArray=nextPoints.split("#");
        int k=0;
        for(int i = 0; i<myArray.length;i++){

            if(myArray[i].contains("coordinates")){
                            String temp =myArray[i].replaceFirst("<coordinates>","");
                            temp=temp.replaceFirst("</coordinates>","");
                            String[] cordin = temp.split(",");
                            double lat = Double.parseDouble(cordin[0]);
                            double lng = Double.parseDouble(cordin[1]);
                            LatLng tempLatLng = new LatLng(lat,lng);

                LatLng latTemp = new LatLng(lat, lng);
                filePoints.add(latTemp);
            }
        }
        PolylineOptions pOptions = new PolylineOptions()
                .width(25)
                .color(Color.BLUE)
                .geodesic(true);
        for(int i =0; i<filePoints.size();i++){
            pOptions.add(filePoints.get(i));
        }
        int p=0;
        for(int i =0; i<filePoints.size();i++){
            if(filePoints.get(i).latitude==myLocation.latitude && filePoints.get(i).longitude==myLocation.longitude){
                p=i;
            }
        }
        for(;p<filePoints.size();p++){
            if(p<filePoints.size()-1 ){
                distance = distance + SphericalUtil.computeDistanceBetween(filePoints.get(p), filePoints.get(p + 1));
                finalDistance = distance / 1000;
            }
        }
        line = myGoogleMap.addPolyline(pOptions);
        routeDiscription.setText("Dystans: " + Math.round(finalDistance*100.0)/100.0 +" km");
    }

}