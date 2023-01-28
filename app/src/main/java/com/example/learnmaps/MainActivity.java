package com.example.learnmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_FINE_LOCATION =99 ;
    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address,tv_wayPointsCounts;
    Button btn_newWayPoint,btn_showWayPointList,btn_showMap;
    Switch sw_locationsupdates,sw_gps;
    Location currentLocation;
    List<Location> savedLocations;
    //Google's API for Location Service
    FusedLocationProviderClient fusedLocationProviderClient;
    //Location file config
    LocationRequest locationRequest;
    LocationCallback locationCallBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ui Variables to values
        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_altitude=findViewById(R.id.tv_altitude);
        tv_accuracy=findViewById(R.id.tv_accuracy);
        tv_speed=findViewById(R.id.tv_speed);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_updates=findViewById(R.id.tv_updates);
        tv_address=findViewById(R.id.tv_address);
        sw_gps=findViewById(R.id.sw_gps);
        sw_locationsupdates=findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint=findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList=findViewById(R.id.btn_showWayPointList);
        tv_wayPointsCounts=findViewById(R.id.tv_countOfCrumbs);
        btn_showMap=findViewById(R.id.btn_showmap);



        //Set properties of location request
        locationRequest= new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        //event that is triggered whenever the update location interval met
        locationCallBack=new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location
                updateUIValues(locationResult.getLastLocation());
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_gps.isChecked()){
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationsupdates.isChecked()){
                    tv_updates.setText("Location is being tracked");

                    startLocationTracking();
                }
                else{
                    tv_updates.setText("Location is not being tracked");
                    stopLocationTracking();
                }
            }
        });
        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location




                //add the new location to the global list
                MyApplication myApplication=(MyApplication)getApplicationContext();
                savedLocations=myApplication.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });
        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,ShowWaypointsList.class);
                startActivity(i);
            }
        });
        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Maps.class);
                startActivity(i);
            }
        });
        updateGps();
    } //end on create method

    private void stopLocationTracking() {

        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("Not tracking");
        tv_lon.setText("Not tracking");
        tv_altitude.setText("Not tracking");
        tv_accuracy.setText("Not tracking");
        tv_speed.setText("Not tracking");
        tv_sensor.setText("Not tracking");
        tv_address.setText("Not tracking");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);


    }

    private void startLocationTracking() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null);
        updateGps();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateGps();
                }
                else {
                    Toast.makeText(this,"The Permissions are needed",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGps(){
        //get permission from user to use gps
        //get the location from fused client
        //update the UI
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //user gave permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got permission and location and now put em in UI
                    updateUIValues(location);
                    currentLocation= location;
                }
            });
        }
        else{
            //refused the permission
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
            }
        }


    }

    private void updateUIValues(Location location) {
        //update all of text views with location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tv_altitude.setText("Not Available");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not Available");
        }
        Geocoder geocoder=new Geocoder(MainActivity.this);
        try {
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            tv_address.setText("Unable to get address");
        }
        MyApplication myApplication=(MyApplication)getApplicationContext();
        savedLocations=myApplication.getMyLocations();

        //show number of saved waypoints
        tv_wayPointsCounts.setText(Integer.toString(savedLocations.size()));




    }

}