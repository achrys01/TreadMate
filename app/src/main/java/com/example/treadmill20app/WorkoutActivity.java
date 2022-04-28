package com.example.treadmill20app;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treadmill20app.adapters.WorkoutAdapter;
import com.example.treadmill20app.models.WorkoutEntry;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class WorkoutActivity extends MenuActivity
        implements AdapterView.OnItemSelectedListener {

    TextInputEditText mFileName;

    private float durEntry = 0;
    private float speedEntry = 0;
    private float inclEntry = 0;

    private WorkoutEntry entry;
    private ArrayList<WorkoutEntry> workout;
    private Spinner mDuration;
    private Spinner mSpeed;
    private Spinner mIncl;
    private Button mEntry;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_run, contentFrameLayout);

        mFileName = findViewById(R.id.workout_name);
        mDuration = findViewById(R.id.duration_spinner);
        mSpeed = findViewById(R.id.speed_spinner);
        mIncl = findViewById(R.id.incl_spinner);
        mEntry = findViewById(R.id.add_step_btn);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void onStart(){
        super.onStart();
        // Spinner Listeners
        mDuration.setOnItemSelectedListener(this);
        mSpeed.setOnItemSelectedListener(this);
        mIncl.setOnItemSelectedListener(this);
        // Spinner options lists
        ArrayList<String> durList = new ArrayList<>();
        ArrayList<String> speedList = new ArrayList<>();
        ArrayList<String> inclList = new ArrayList<>();
        // Set maximum duration and increment manually
        double incrDur = 0.5;
        int durRange = 30;
        for (int i = 0; i < durRange; i++) {
            durList.add(String.format(Locale.ENGLISH,"%.1f",i*incrDur+incrDur));
        }
        // Set maximum speed and increment from treadmill characteristics
        double incrSpeed = RunActivity.getSpeedIncrement();
        int speedRange = (int) (RunActivity.getMaxSpeed()*incrSpeed);
        for (int j = 0; j < speedRange; j++) {
            speedList.add(String.format(Locale.ENGLISH,"%.1f",j*incrSpeed+incrSpeed));
        }
        // Set maximum inclination and increment from treadmill characteristics
        double incrIncl = RunActivity.getInclIncrement();
        int InclRange = (int) (RunActivity.getMaxIncl() / (10*incrIncl));
        for (int k = 0; k < InclRange+1; k++) {
            inclList.add(String.format(Locale.ENGLISH,"%.1f",k*incrIncl));
        }
        // Set Spinner Adapters
        ArrayAdapter durAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,durList);
        ArrayAdapter speedAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,speedList);
        ArrayAdapter inclAdapter =
                new ArrayAdapter(this,android.R.layout.simple_spinner_item,inclList);

        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inclAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDuration.setAdapter(durAdapter);
        mSpeed.setAdapter(speedAdapter);
        mIncl.setAdapter(inclAdapter);

        // Initialize objects
        entry = new WorkoutEntry();
        workout = new ArrayList<>();

        // RecyclerView Adapter
        WorkoutAdapter mAdapter = new WorkoutAdapter(this, workout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEntry.setOnClickListener(v -> {
            entry = new WorkoutEntry();
            entry.setDur(durEntry);
            entry.setSpeed(speedEntry);
            entry.setIncl(inclEntry);
            workout.add(entry);
            int workoutSize = workout.size();
            mRecyclerView.getAdapter().notifyItemInserted(workoutSize+1);
            // Scroll to the bottom.
            mRecyclerView.smoothScrollToPosition(workoutSize);
        });
    }
//    TODO! Replace with saving to  firebase method
//    // Create a custom csv file containing a workout
//    public void saveCSV(View v) {
//        if (mFileName != null) {
//            String fileName = mFileName.getText().toString() + ".csv";
//            File directory =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            File file = new File(directory,fileName);
//                CsvWriter csvWriter = new CsvWriter();
//                try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
//                    // header
//                    csvAppender.appendLine("Duration", "Speed", "Inclination");
//                    // Iterate writing csv entries
//                    for (WorkoutEntry csvEntry : workout) {
//                        csvAppender.appendLine(
//                                String.valueOf(csvEntry.getDur()),
//                                String.valueOf(csvEntry.getSpeed()),
//                                String.valueOf(csvEntry.getIncl())
//                        );
//                    }
//                     Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//    }

    // Spinner listeners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.duration_spinner ){
            durEntry = Float.parseFloat(parent.getSelectedItem().toString());
        }
        else if(parent.getId() == R.id.speed_spinner ){
            speedEntry = Float.parseFloat(parent.getSelectedItem().toString());

        }
        else if(parent.getId() == R.id.incl_spinner ){
            inclEntry = Float.parseFloat(parent.getSelectedItem().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
