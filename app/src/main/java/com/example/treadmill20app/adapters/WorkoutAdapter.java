package com.example.treadmill20app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treadmill20app.R;
import com.example.treadmill20app.WorkoutEntry;

import java.util.List;

public class WorkoutAdapter extends
        RecyclerView.Adapter<WorkoutAdapter.RecyclerViewHolder> {

    //To hold data in the adapter
    private final List<WorkoutEntry> mWorkout;
    //Inflates the xml for a list item
    private final LayoutInflater mInflater;

    //Initializes variables
    public WorkoutAdapter(Context context,
                          List<WorkoutEntry> workout)
    {
        mInflater = LayoutInflater.from(context);
        this.mWorkout = workout;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public final TextView durationView;
        public final TextView speedView;
        public final TextView inclView;
        final WorkoutAdapter mAdapter;

        public RecyclerViewHolder(View itemView, WorkoutAdapter adapter){
            super(itemView);
            durationView = itemView.findViewById(R.id.dur_step);
            speedView = itemView.findViewById(R.id.speed_step);
            inclView = itemView.findViewById(R.id.incl_step);
            this.mAdapter = adapter;
        }
    }

    //Inflates the item layout and returns a ViewHolder with the layout and the adapter
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.workout_item,parent,false);
        return new RecyclerViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int i) {
        // Retrieve the data for that position.
        WorkoutEntry mCurrent = mWorkout.get(i);
        // Add the data to the view holder.
        float dur = mCurrent.getDur();
        holder.durationView.setText(String.valueOf(dur));
        float speed = mCurrent.getSpeed();
        holder.speedView.setText(String.valueOf(speed));
        float incl = mCurrent.getIncl();
        holder.inclView.setText(String.valueOf(incl));
    }

    @Override
    public int getItemCount() {
        return mWorkout.size();
    }
}
