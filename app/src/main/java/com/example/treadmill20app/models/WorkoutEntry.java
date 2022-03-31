package com.example.treadmill20app.models;

public class WorkoutEntry {
    private float dur;
    private float speed;
    private float incl;

    public void setDur(float durEntry){dur = durEntry;}
    public void setSpeed(float speedEntry){speed = speedEntry;}
    public void setIncl(float inclEntry){incl = inclEntry;}

    public float getDur(){
        return dur;
    }
    public float getSpeed(){
        return speed;
    }
    public float getIncl(){
        return incl;
    }
}