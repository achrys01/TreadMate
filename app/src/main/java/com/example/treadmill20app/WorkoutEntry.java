package com.example.treadmill20app;

public class WorkoutEntry {
    private float maxHR;
    private float maxV;
    private float dur;
    private float speed;
    private float incl;
    private float zone;

    public void setMaxHR(float maxHREntry){
        this.maxHR = maxHREntry;
    }
    public void setMaxV(float maxVEntry){
        this.maxV = maxVEntry;
    }
    public void setDur(float durEntry){
        this.dur = durEntry;
    }
    public void setSpeed(float speedEntry){
        this.speed = speedEntry;
    }
    public void setIncl(float inclEntry){
        this.incl = inclEntry;
    }
    public void setZone(float zoneEntry){
        this.zone = zoneEntry;
    }

    public float getMaxHR(){
        return maxHR;
    }
    public float getMaxV(){
        return maxV;
    }
    public float getDur(){
        return dur;
    }
    public float getSpeed(){
        return speed;
    }
    public float getIncl(){
        return incl;
    }
    public float getZone(){
        return zone;
    }
}
