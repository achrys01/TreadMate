package com.example.treadmill20app;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HrFragment extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    TextInputEditText mFileName;
    private float maxHREntry = 0;
    private float maxVEntry = 0;
    private float durEntry = 0;
    private float zoneEntry = 0;

    private WorkoutEntry entry;
    private ArrayList<WorkoutEntry> entryList;
    private WorkoutObject workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_hr);
        mFileName = findViewById(R.id.workout_name);
        Spinner mMaxHR = findViewById(R.id.max_hr_spinner);
        Spinner mMaxV = findViewById(R.id.max_v_spinner);
        Spinner mDuration = findViewById(R.id.duration_spinner);
        Spinner mZone = findViewById(R.id.zone_spinner);
        Button mEntry = findViewById(R.id.add_step_btn);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        // RecyclerView Adapter
        WorkoutAdapter mAdapter = new WorkoutAdapter(this, entryList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Spinner Listeners
        mDuration.setOnItemSelectedListener(this);
        mMaxHR.setOnItemSelectedListener(this);
        mMaxV.setOnItemSelectedListener(this);
        mZone.setOnItemSelectedListener(this);
        // Spinner options lists
        ArrayList<String> durList = new ArrayList<>();
        ArrayList<String> maxHRList = new ArrayList<>();
        ArrayList<String> maxVList = new ArrayList<>();
        ArrayList<String> zoneList = new ArrayList<>();
        double incrDur = 0.5;
        int durRange = 30;
        for (int i = 0; i < (int) (durRange); i++) {
            durList.add(String.format("%.1f%",i*incrDur+incrDur));
        }

        double incrHR = 5;
        int HRRange = 250;
        for (int i = 150; i < (int) (HRRange); i++) {
            maxHRList.add(String.format("%.1f%",i*incrHR+incrHR));
        }

        double incrV = 0.5;
        int VRange = 25;
        for (int i = 12; i < (int) (VRange); i++) {
            maxVList.add(String.format("%.1f%",i*incrV+incrV));
        }

        double incrZone = 1;
        int zoneRange = 5;
        for (int i = 1; i < (int) (zoneRange); i++) {
            zoneList.add(String.format("%.1f%",i*incrZone+incrZone));
        }

        // Set Spinner Adapters
        ArrayAdapter maxHRAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,maxHRList);
        ArrayAdapter maxVAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,maxVList);
        ArrayAdapter durAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,durList);
        ArrayAdapter zoneAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,zoneList);

        maxHRAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxVAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMaxHR.setAdapter(maxHRAdapter);
        mMaxV.setAdapter(maxVAdapter);
        mDuration.setAdapter(durAdapter);
        mZone.setAdapter(zoneAdapter);

        // Initialize objects
        entry = new WorkoutEntry();
        entryList = new ArrayList<>();

        mEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entry = new WorkoutEntry();
                entry.setMaxHR(maxHREntry);
                entry.setMaxV(maxVEntry);
                entry.setDur(durEntry);
                entry.setZone(zoneEntry);
                entryList.add(entry);

                workout.setMaxHR(durEntry);
                workout.setMaxV(maxVEntry);
                workout.setDurList(durEntry);
                workout.setZoneList(zoneEntry);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.max_hr_spinner){
            maxHREntry = (float) parent.getSelectedItem();

        }
        if(parent.getId() == R.id.max_v_spinner){
            maxVEntry = (float) parent.getSelectedItem();

        }
        if(parent.getId() == R.id.duration_spinner){
            durEntry = (float) parent.getSelectedItem();

        }
        if(parent.getId() == R.id.zone_spinner){
            zoneEntry = (float) parent.getSelectedItem();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
