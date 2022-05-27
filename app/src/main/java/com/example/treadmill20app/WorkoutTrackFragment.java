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
import com.example.treadmill20app.models.WorkoutObject;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

// Activity connected to tab_track for manually creating a track
public class WorkoutTrackFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    public WorkoutTrackFragment() {
        // Required empty constructor
    }

    private String durEntry = "0";
    private String speedEntry = "0";
    private String inclEntry = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_track, container, false);

        TextInputEditText mFileName = view.findViewById(R.id.workout_name);
        Spinner mDuration = view.findViewById(R.id.duration_spinner);
        Spinner mSpeed = view.findViewById(R.id.speed_spinner);
        Spinner mIncl = view.findViewById(R.id.incl_spinner);
        Button mEntry = view.findViewById(R.id.add_step_btn);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        // Object constructor
        WorkoutObject workout = new WorkoutObject();
        /*
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
        */
        // Spinner options lists
        ArrayList<String> durList = new ArrayList<>();
        ArrayList<String> speedList = new ArrayList<>();
        ArrayList<String> inclList = new ArrayList<>();

        float incrDur = 0.5F;
        int durMin = 0;
        int durMax = 30;
        for (int i = 0; i <= (durMax-durMin)/incrDur; i++) {
            durList.add(String.format("%.1f",durMin+i*incrDur));
        }

        float incrSpeed = 0.1F;
        int SpeedMin = 1;
        int SpeedMax = 4;
        for (int i = 0; i <= (SpeedMax-SpeedMin)/incrSpeed; i++) {
            speedList.add(String.format("%.1f",SpeedMin+i*incrSpeed));
        }

        float incrIncl = 0.5F;
        int inclMin = 0;
        int inclMax = 20;
        for (int i = 0; i <= (inclMax-inclMin)/incrIncl; i++) {
            inclList.add(String.format("%.1f",inclMin+i*incrIncl));
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

        // Spinner Listeners
        mDuration.setOnItemSelectedListener(this);
        mSpeed.setOnItemSelectedListener(this);
        mIncl.setOnItemSelectedListener(this);

        // RecyclerView Adapter
        WorkoutAdapter mAdapter = new WorkoutAdapter(view.getContext(), workout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mEntry.setOnClickListener(v -> {
            workout.setDurList(durEntry);
            workout.setSpeedList(speedEntry);
            workout.setInclList(inclEntry);
            int workoutSize = workout.getDurList().size();
            mRecyclerView.getAdapter().notifyItemInserted(workoutSize+1);
            // Scroll to the bottom.
            mRecyclerView.smoothScrollToPosition(workoutSize);
        });
        /*
        TODO! Find a more efficient way to delete unwanted entries
        mDelEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryList.remove(entry);
            }
        });
         */
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.duration_spinner ){ durEntry = (String) parent.getSelectedItem(); }
        if(parent.getId() == R.id.speed_spinner ){ speedEntry = (String) parent.getSelectedItem(); }
        if(parent.getId() == R.id.incl_spinner ){ inclEntry = (String) parent.getSelectedItem(); }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
