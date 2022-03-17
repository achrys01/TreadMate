package com.example.treadmill20app.adapters;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.treadmill20app.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BtDeviceAdapter extends RecyclerView.Adapter<BtDeviceAdapter.ViewHolder> {

    private List<BluetoothDevice> mDeviceList;

    // interface for callbacks when item selected
    public interface IOnItemSelectedCallBack {
        void onItemClicked(int position);
    }
    private IOnItemSelectedCallBack mOnItemSelectedCallback;

    public BtDeviceAdapter(List<BluetoothDevice> deviceList,
                           IOnItemSelectedCallBack onItemSelectedCallback) {
        super();
        mDeviceList = deviceList;
        mOnItemSelectedCallback = onItemSelectedCallback;
    }

    //Item view, and its internal views
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView deviceNameView;
        TextView deviceInfoView;

        private IOnItemSelectedCallBack mOnItemSelectedCallback;

        ViewHolder(View itemView, IOnItemSelectedCallBack onItemSelectedCallback) {
            super(itemView);
            itemView.setOnClickListener(this);
            mOnItemSelectedCallback = onItemSelectedCallback;
        }

        //Handle the item being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item (row) position
            mOnItemSelectedCallback.onItemClicked(position);
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BtDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scan_hr_item, parent, false);
        final ViewHolder vh = new ViewHolder(itemView, mOnItemSelectedCallback);

        vh.deviceNameView = itemView.findViewById(R.id.device_name);
        vh.deviceInfoView = itemView.findViewById(R.id.device_info);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        BluetoothDevice device = mDeviceList.get(position);
        String name = device.getName();
        vh.deviceNameView.setText(name == null ? "Unknown" : name);
        String deviceInfoStr = new StringBuilder().append(device.getBluetoothClass()).append(", ")
                .append(device.getAddress()).toString();
        vh.deviceInfoView.setText(deviceInfoStr);
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }
}

