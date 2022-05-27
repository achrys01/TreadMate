package com.example.treadmill20app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.treadmill20app.R;
import com.example.treadmill20app.models.WorkoutObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

// RecyclerView for manual workout entries
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.RecyclerViewHolder> {

    private final WorkoutObject mWorkout;
    private final LayoutInflater mInflater;

    //Initializes variables
    public WorkoutAdapter(Context context, WorkoutObject workout) {
        mInflater = LayoutInflater.from(context);
        mWorkout = workout;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public final TextView stepView;
        public final TextView durationView;
        public final TextView zone_or_speed_View;
        public final TextView inclView;
        final WorkoutAdapter mAdapter;

        public RecyclerViewHolder(View itemView, WorkoutAdapter adapter){
            super(itemView);
            stepView = itemView.findViewById(R.id.step);
            durationView = itemView.findViewById(R.id.dur_step);
            zone_or_speed_View = itemView.findViewById(R.id.zone_or_speed_step);
            inclView = itemView.findViewById(R.id.incl_step);
            this.mAdapter = adapter;
        }
    }

    //Inflates the item layout and returns a ViewHolder with the layout and the adapter
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_workout,parent,false);
        return new RecyclerViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int i) {
        ArrayList<String> durList = mWorkout.getDurList();
        ArrayList<String> zoneList = mWorkout.getZoneList();
        ArrayList<String> speedList = mWorkout.getSpeedList();
        ArrayList<String> inclList = mWorkout.getInclList();
        // Add the data for that position to the view holder
        holder.stepView.setText(String.valueOf(i));
        holder.durationView.setText(durList.get(i));
        holder.inclView.setText(inclList.get(i));
        if (zoneList.size() != 0) {
            holder.zone_or_speed_View.setText(zoneList.get(i));
        } else if (speedList.size() != 0) {
            holder.zone_or_speed_View.setText(speedList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        int mWorkoutSize;
        if (mWorkout.getDurList() == null){
            mWorkoutSize = 0;
        } else {
            mWorkoutSize = mWorkout.getDurList().size();
        }
        return mWorkoutSize;
    }
}
