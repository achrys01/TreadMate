package com.example.treadmill20app.models;

import java.util.ArrayList;

// Class of generic Workout object (both HR control and track)
public class WorkoutObject {

    private  String maxV;
    private  String maxHR;
    private ArrayList<String> durList;
    private ArrayList<String> zoneList;
    private ArrayList<String> speedList;
    private ArrayList<String> inclList;

    public WorkoutObject() {
        durList = new ArrayList<>();
        zoneList = new ArrayList<>();
        speedList = new ArrayList<>();
        inclList = new ArrayList<>();
    }

    //Setters
    public void setMaxV(float maxVEntry){
        maxV = String.valueOf(maxVEntry);
    }
    public void setMaxHR(float maxHREntry){
        maxHR = String.valueOf(maxHREntry);
    }
    public void setDurList(float durEntry){
        durList.add(String.valueOf(durEntry));
    }
    public void setZoneList(float zoneEntry){
        zoneList.add(String.valueOf(zoneEntry));
    }
    public void setSpeedList(float speedEntry){
        speedList.add(String.valueOf(speedEntry));
    }
    public void setInclList(float inclEntry){
        inclList.add(String.valueOf(inclEntry));
    }
    //Getters
    public String getMaxV(){
        return maxV;
    }
    public String getMaxHR(){
        return maxHR;
    }
    public ArrayList<String> getDurList(){
        return durList;
    }
    public ArrayList<String> getZoneList(){
        return zoneList;
    }
    public ArrayList<String> getSpeedList(){
        return speedList;
    }
    public ArrayList<String> getInclList(){
        return inclList;
    }
}
