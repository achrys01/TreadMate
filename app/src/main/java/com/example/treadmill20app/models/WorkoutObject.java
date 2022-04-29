package com.example.treadmill20app.models;

import java.util.ArrayList;

// Class of generic Workout object (both HR control and track)
public class WorkoutObject {

    private  String maxV;
    private  String maxHR;
    private ArrayList<String> durList;
    private ArrayList<String> speedList;
    private ArrayList<String> inclList;
    private ArrayList<String> zoneList;

    //Setters
    public WorkoutObject(String maxHR, String maxV, ArrayList<String> dur, ArrayList<String> speed, ArrayList<String> incl, ArrayList<String> zone) {

        this.maxHR = maxHR;
        this.maxV=  maxV;
        this.durList = dur;
        this.speedList = speed;
        this.inclList = incl;
        this.zoneList = zone;
    }
    public void setMaxHR(float maxHREntry){
        this.maxHR = String.valueOf(maxHREntry);
    }
    public void setMaxV(float maxVEntry){
        this.maxV = String.valueOf(maxVEntry);
    }
    public void setDurList(float durEntry){
        this.durList.add(String.valueOf(durEntry));
    }
    public void setSpeedList(float speedEntry){
        this.speedList.add(String.valueOf(speedEntry));
    }
    public void setInclList(float inclEntry){
        this.inclList.add(String.valueOf(inclEntry));
    }
    public void setZoneList(float zoneEntry){
        this.zoneList.add(String.valueOf(zoneEntry));
    }
    //Getters
    public ArrayList<String> getDurList(){
        return durList;
    }
    public ArrayList<String> getSpeedList(){
        return speedList;
    }
    public ArrayList<String> getInclList(){
        return inclList;
    }
    public ArrayList<String> getZoneList(){
        return zoneList;
    }
    public String getMaxV(){
        return maxV;
    }
    public String getMaxHR(){
        return maxHR;
    }
}
