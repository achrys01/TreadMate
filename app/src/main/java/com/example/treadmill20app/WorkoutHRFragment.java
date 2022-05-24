package com.example.treadmill20app;
/*
Activity to create a new workout
*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treadmill20app.adapters.WorkoutAdapter;
import com.example.treadmill20app.models.WorkoutEntry;
import com.example.treadmill20app.models.WorkoutObject;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class WorkoutHRFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    public WorkoutHRFragment() {
        // Required empty constructor
    }

    TextInputEditText mFileName;
    private float maxHREntry = 0;
    private float maxVEntry = 0;
    private float durEntry = 0;
    private float zoneEntry = 0;
    private WorkoutObject workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_hr, container, false);

        mFileName = view.findViewById(R.id.workout_name);
        Spinner mMaxHR = view.findViewById(R.id.max_hr_spinner);
        Spinner mMaxV = view.findViewById(R.id.max_v_spinner);
        Spinner mDuration = view.findViewById(R.id.duration_spinner);
        Spinner mZone = view.findViewById(R.id.zone_spinner);
        Button mEntry = view.findViewById(R.id.add_step_btn);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);

        // Spinner options lists
        ArrayList<Float> durList = new ArrayList<>();
        ArrayList<Float> maxHRList = new ArrayList<>();
        ArrayList<Float> maxVList = new ArrayList<>();
        ArrayList<Float> zoneList = new ArrayList<>();

        float incrDur = 0.5F;
        int durMin = 0;
        int durMax = 30;
        for (int i = 0; i <= (durMax-durMin)/incrDur; i++) {
            durList.add(durMin+i*incrDur);
        }

        float incrHR = 5;
        int HrMin = 150;
        int HrMax = 250;
        for (int i = 0; i <= (HrMax-HrMin)/incrHR; i++) {
            maxHRList.add(HrMin+i*incrHR);
        }

        float incrV = 0.5F;
        int Vmin = 12;
        int Vmax = 25;
        for (int i = 0; i <= (Vmax-Vmin)/incrV; i++) {
            maxVList.add(Vmin+i*incrV);
        }

        float incrZone = 1;
        int ZoneMin = 1;
        int ZoneMax = 4;
        for (int i = 0; i <= (ZoneMax-ZoneMin)/incrZone; i++) {
            zoneList.add(ZoneMin+i*incrZone);
        }

        // Set Spinner Adapters
        ArrayAdapter maxHRAdapter =
                new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,maxHRList);
        ArrayAdapter maxVAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,maxVList);
        ArrayAdapter durAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,durList);
        ArrayAdapter zoneAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,zoneList);

        maxHRAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxVAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMaxHR.setAdapter(maxHRAdapter);
        mMaxV.setAdapter(maxVAdapter);
        mDuration.setAdapter(durAdapter);
        mZone.setAdapter(zoneAdapter);

        // Spinner Listeners
        mMaxHR.setOnItemSelectedListener(this);
        mMaxV.setOnItemSelectedListener(this);
        mDuration.setOnItemSelectedListener(this);
        mZone.setOnItemSelectedListener(this);

        // RecyclerView Adapter
        WorkoutAdapter mAdapter = new WorkoutAdapter(view.getContext(), workout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mEntry.setOnClickListener(v -> {
            workout.setMaxHR(maxHREntry);
            workout.setMaxV(maxVEntry);
            workout.setDurList(durEntry);
            workout.setZoneList(zoneEntry);
            int workoutSize = workout.getDurList().size();
            mRecyclerView.getAdapter().notifyItemInserted(workoutSize+1);
            // Scroll to the bottom.
            mRecyclerView.smoothScrollToPosition(workoutSize);
        });
        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.max_hr_spinner) maxHREntry = (float) parent.getSelectedItem();
        if(parent.getId() == R.id.max_v_spinner) maxVEntry = (float) parent.getSelectedItem();
        if(parent.getId() == R.id.duration_spinner) durEntry = (float) parent.getSelectedItem();
        if(parent.getId() == R.id.zone_spinner) zoneEntry = (float) parent.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
