package com.example.treadmill20app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ConnectedControlsFragment extends Fragment {

    public ConnectedControlsFragment() {
        // Required empty constructor
    }

    private TextView mHeartRateView;
    private int mHeartRateMeas;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connected_controls, container, false);

        mHeartRateView = view.findViewById(R.id.heart_rate_view);

        mHandler = new Handler();
        //post update hr every 2000ms
        mHandler.post(updateHeartRateMeas);

        return view;
    }

    private final Runnable updateHeartRateMeas = new Runnable(){
        @Override
        public void run() {
            try{
                mHeartRateMeas = MenuActivity.getHeartRateMeas();
                mHeartRateView.setText(String.valueOf(mHeartRateMeas));
                mHandler.postDelayed(this, 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}

