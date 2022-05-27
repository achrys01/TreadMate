package com.example.treadmill20app;
/*
Activity to create a new workout
*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treadmill20app.models.WorkoutObject;
import com.google.android.material.textfield.TextInputEditText;

// Activity connected to tab_track for manually creating a track
public class WorkoutTrackFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    public WorkoutTrackFragment() {
        // Required empty constructor
    }

    TextInputEditText mFileName;
    private float durEntry = 0;
    private float speedEntry = 0;
    private float inclEntry = 0;
    private WorkoutObject workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_track, container, false);

        mFileName = view.findViewById(R.id.workout_name);
        Spinner mDuration = view.findViewById(R.id.duration_spinner);
        Spinner mSpeed = view.findViewById(R.id.speed_spinner);
        Spinner mIncl = view.findViewById(R.id.incl_spinner);
        Button mEntry = view.findViewById(R.id.add_step_btn);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        /*
        // RecyclerView Adapter
        WorkoutAdapter mAdapter = new WorkoutAdapter(view.getContext(), workout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // Spinner Listeners
        mDuration.setOnItemSelectedListener(this);
        mSpeed.setOnItemSelectedListener(this);
        mIncl.setOnItemSelectedListener(this);
        // Spinner options lists
        ArrayList<String> durList = new ArrayList<>();
        ArrayList<String> speedList = new ArrayList<>();
        ArrayList<String> inclList = new ArrayList<>();

        double incrDur = 0.5;
        int durRange = 30;
        for (int i = 0; i < (int) (durRange); i++) {
            durList.add(String.format("%.1f%",i*incrDur+incrDur));
        }

        double incrSpeed = RunActivity.getSpeedIncrement();
        int speedRange = (int) (RunActivity.getMaxSpeed() / incrSpeed);
        for (int j = 0; j < speedRange; j++) {
            speedList.add(String.format("%.1f%",j*incrSpeed+incrSpeed));
        }

        double incrIncl = RunActivity.getInclIncrement();
        int InclRange = (int) (RunActivity.getMaxIncl() / incrIncl);
        for (int k = 0; k < InclRange; k++) {
            inclList.add(String.format("%.1f%",k*incrIncl));
        }
        // Set Spinner Adapters
        ArrayAdapter durAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,durList);
        ArrayAdapter speedAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,speedList);
        ArrayAdapter inclAdapter =
                new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,inclList);

        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inclAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDuration.setAdapter(durAdapter);
        mSpeed.setAdapter(speedAdapter);
        mIncl.setAdapter(inclAdapter);
         */

        mEntry.setOnClickListener(v -> {
            workout.setDurList(durEntry);
            workout.setSpeedList(speedEntry);
            workout.setInclList(inclEntry);
        });
//        TODO! Find a more efficient way to delete unwanted entries
//        mDelEntry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                entryList.remove(entry);
//            }
//        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.duration_spinner ){
            durEntry = (float) parent.getSelectedItem();
        }
        if(parent.getId() == R.id.speed_spinner ){
            speedEntry = (float) parent.getSelectedItem();

        }
        if(parent.getId() == R.id.incl_spinner ){
            inclEntry = (float) parent.getSelectedItem();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
