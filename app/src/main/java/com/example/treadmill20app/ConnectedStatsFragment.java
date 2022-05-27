package com.example.treadmill20app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.treadmill20app.R;

import androidx.fragment.app.Fragment;

public class ConnectedStatsFragment extends Fragment {


    public ConnectedStatsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connected_stats, container, false);

        return view;
    }

}