package com.example.learnmaps;
import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {


    private static MyApplication singletone;

    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public MyApplication getInstance(){
        return singletone;
    }


    public void onCreate(){
        super.onCreate();
        singletone=this;

        myLocations =new ArrayList<>();
    }





}
