package com.example.treadmill20app.utils;

import android.util.JsonReader;

import com.example.treadmill20app.models.WorkoutObject;

import java.io.IOException;
import java.util.ArrayList;

// json parser class
public class WorkoutUtils {

    /*------ METHODS FOR PARSING WORKOUT LIST ------*/
    public static void parseWorkouts(JsonReader reader, ArrayList<WorkoutObject> dataSet) throws IOException {
        // read json array, loop through workouts on dataSet and read each of them
        reader.beginArray();
        while (reader.hasNext()) {
            dataSet.add(readWorkout(reader));
        }
        reader.endArray();
    }

    public static WorkoutObject readWorkout(JsonReader reader) throws IOException {
        // initialize workout parameters
        String maxHR = null;
        String maxV = null;
        ArrayList<String> dur = null;
        ArrayList<String> speed = null;
        ArrayList<String> incl = null;
        ArrayList<String> zone = null;
        // read workout info
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "maxHR":
                    maxHR = reader.nextString();
                    break;
                case "maxV":
                    maxV = reader.nextString();
                    break;
                case "dur":
                    dur = readArrayList(reader);
                    break;
                case "speed":
                    speed = readArrayList(reader);
                    break;
                case "incl":
                    incl = readArrayList(reader);
                    break;
                case "zone":
                    zone = readArrayList(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        WorkoutObject workout = new WorkoutObject(maxHR, maxV, dur, speed, incl, zone);
        return workout;
    }

    public static ArrayList<String> readArrayList(JsonReader reader) throws IOException {
        ArrayList<String> stringArrayList = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            stringArrayList.add(reader.nextString());
        }
        reader.endArray();
        return stringArrayList;
    }
}
