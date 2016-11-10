package com.projects.dawid.gattclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectedArrayAdapter extends ArrayAdapter<BluetoothDeviceAdapter> {
    private static final String TAG = "ConnectedArrayAdapter";
    private HashSet<BluetoothDeviceAdapter> mConnectedDevices = new HashSet<>();
    private BluetoothDeviceAdapter mSelectedDevice;
    private Context mContext;

    public ConnectedArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    public ConnectedArrayAdapter(Context context, int resource, BluetoothDeviceAdapter[] objects) {
        super(context, resource, objects);
        mContext = context;
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId, BluetoothDeviceAdapter[] objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
    }

    public ConnectedArrayAdapter(Context context, int resource, List<BluetoothDeviceAdapter> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDeviceAdapter> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
    }

    public ConnectedArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        mContext = context;
    }

    public HashSet<BluetoothDeviceAdapter> getConnectedDevices() {
        return mConnectedDevices;
    }

    public void addConnectedDevice(BluetoothDeviceAdapter device) {
        mConnectedDevices.add(device);
        logDevices();
    }

    private void logDevices() {
        Log.i(TAG, ">>> Devices <<<");
        for (BluetoothDeviceAdapter device :
                mConnectedDevices) {
            Log.i(TAG, "Device:" + device.toString());
        }
    }

    public void addConnectedDevices(Set<BluetoothDeviceAdapter> devices) {
        mConnectedDevices.addAll(devices);
    }

    public void removeConnectedDevice(BluetoothDeviceAdapter device) {
        mConnectedDevices.remove(device);
    }

    public BluetoothDeviceAdapter getSelectedDevice() {
        return mSelectedDevice;
    }

    public void setSelectedDevice(BluetoothDeviceAdapter device) {
        mSelectedDevice = device;
    }

    public void clearConnectedDevices() {
        mConnectedDevices.clear();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView launched");

        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        String textViewText = textView.getText().toString();

        setTextColor(textView, textViewText);
        setBackgroundColor(textView, textViewText);

        return view;
    }

    private void setBackgroundColor(TextView textView, String textViewText) {
        textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDefaultBackgroundDevice));

        if (mSelectedDevice != null && textViewText.equals(mSelectedDevice.toString())) {
            textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackgroundSelectedDevice));
        }
    }

    private void setTextColor(TextView textView, String textViewText) {
        if (deviceConnected(textViewText)) {
            Log.i(TAG, "Device: " + textViewText + " is in connected devices set");
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextConnectedDevice));
        } else {
            Log.i(TAG, "Device: " + textViewText + " is NOT in connected devices set");
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextDefaultDevice));
        }
    }

    private boolean deviceConnected(String textViewText) {
        //set.contains for some reason does not work
        for (BluetoothDeviceAdapter device :
                mConnectedDevices) {
            if (textViewText.equals(device.toString())) {
                return true;
            }
        }

        return false;
    }
}
