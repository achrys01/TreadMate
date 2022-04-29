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

import java.util.ArrayList;

// RecyclerView for manual workout entries
// TODO! Modify to work for both HR and track activities?
public class WorkoutAdapter extends
        RecyclerView.Adapter<WorkoutAdapter.RecyclerViewHolder> {

    //To hold data in the adapter
    private final WorkoutObject mWorkout;
    //Inflates the xml for a list item
    private final LayoutInflater mInflater;

    //Initializes variables
    public WorkoutAdapter(Context context,
                          WorkoutObject workout)
    {
        mInflater = LayoutInflater.from(context);
        mWorkout = workout;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public final TextView durationView;
        public final TextView speedView;
        public final TextView inclView;
        public final TextView zoneView;
        final WorkoutAdapter mAdapter;

        public RecyclerViewHolder(View itemView, WorkoutAdapter adapter){
            super(itemView);
            durationView = itemView.findViewById(R.id.dur_step);
            speedView = itemView.findViewById(R.id.speed_step);
            inclView = itemView.findViewById(R.id.incl_step);
            zoneView = itemView.findViewById(R.id.zone_step);
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
        ArrayList<String> speedList = mWorkout.getSpeedList();
        ArrayList<String> inclList = mWorkout.getInclList();
        ArrayList<String> zoneList = mWorkout.getZoneList();
        // Add the data for that position to the view holder
        holder.durationView.setText(durList.get(i));
        holder.speedView.setText(speedList.get(i));
        holder.inclView.setText(inclList.get(i));
        holder.zoneView.setText(zoneList.get(i));
    }

    @Override
    public int getItemCount() {
        return mWorkout.getDurList().size();
    }
}
