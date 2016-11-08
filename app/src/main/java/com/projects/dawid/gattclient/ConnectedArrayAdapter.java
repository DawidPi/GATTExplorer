package com.projects.dawid.gattclient;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ConnectedArrayAdapter extends ArrayAdapter<BluetoothDeviceAdapter> {
    private static final String TAG = "ConnectedArrayAdapter";
    private BluetoothDeviceAdapter mConnectedDevice;

    public ConnectedArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ConnectedArrayAdapter(Context context, int resource, BluetoothDeviceAdapter[] objects) {
        super(context, resource, objects);
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId, BluetoothDeviceAdapter[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ConnectedArrayAdapter(Context context, int resource, List<BluetoothDeviceAdapter> objects) {
        super(context, resource, objects);
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDeviceAdapter> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public BluetoothDeviceAdapter getConnectedDevice() {
        return mConnectedDevice;
    }

    public void setConnectedDevice(BluetoothDeviceAdapter device) {
        mConnectedDevice = device;
    }

    public void clearConnectedDevice() {
        mConnectedDevice = null;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView launched");

        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        if (mConnectedDevice != null) {
            Log.i(TAG, "Connected device is not null: text View = " + textView.getText() + "connected Device" + mConnectedDevice);
            if (textView.getText().toString().equals(mConnectedDevice.toString())) {
                textView.setTextColor(Color.BLUE);
            }
        }

        return view;
    }
}
