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
    public void setMaxV(String maxVEntry){
        maxV = maxVEntry;
    }
    public void setMaxHR(String maxHREntry){
        maxHR = maxHREntry;
    }
    public void setDurList(String durEntry){
        durList.add(durEntry);
    }
    public void setZoneList(String zoneEntry){
        zoneList.add(zoneEntry);
    }
    public void setSpeedList(String speedEntry){
        speedList.add(speedEntry);
    }
    public void setInclList(String inclEntry){
        inclList.add(inclEntry);
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
