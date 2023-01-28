package com.example.learnmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowWaypointsList extends AppCompatActivity {

    ListView lv_savedLocations;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_waypoints_list);
        MyApplication myApplication=(MyApplication)getApplicationContext();
        List<Location> savedLocations=myApplication.getMyLocations();
        lv_savedLocations=findViewById(R.id.lv_waypoints);
        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savedLocations));
    }
}