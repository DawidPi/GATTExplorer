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

/**
 * ConnectedArrayAdapter implements ListViewAdapter for discovered devices.
 * Is capable to show which devices are connected and which ones are selected.
 */
class ConnectedArrayAdapter extends ArrayAdapter<BluetoothDeviceAdapter> {
    private static final String TAG = "ConnectedArrayAdapter";
    private HashSet<BluetoothDeviceAdapter> mConnectedDevices = new HashSet<>();
    private BluetoothDeviceAdapter mSelectedDevice;
    private Context mContext;

    /**
     * Default constructor.
     */
    ConnectedArrayAdapter(Context context, int resource, List<BluetoothDeviceAdapter> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    /**
     * @return Set of all connected BLE devices.
     */
    HashSet<BluetoothDeviceAdapter> getConnectedDevices() {
        return mConnectedDevices;
    }

    /**
     * Adds BLE device to the collection of connected devices.
     *
     * @param device new connected device.
     */
    void addConnectedDevice(BluetoothDeviceAdapter device) {
        mConnectedDevices.add(device);
        logDevices();
    }

    /**
     * Sets selected device. Makes selected device highlighted.
     *
     * @param device device, that is to be highlighted.
     */
    void setSelectedDevice(BluetoothDeviceAdapter device) {
        mSelectedDevice = device;
    }

    /**
     * Sets appropriate colors for background and text of selected and connected devices.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

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
        if (deviceConnected(textViewText))
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextConnectedDevice));
        else
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextDefaultDevice));
    }

    private boolean deviceConnected(String textViewText) {
        for (BluetoothDeviceAdapter device : mConnectedDevices) {
            if (textViewText.equals(device.toString()))
                return true;
        }

        return false;
    }

    private void logDevices() {
        Log.i(TAG, ">>> Devices <<<");
        for (BluetoothDeviceAdapter device :
                mConnectedDevices) {
            Log.i(TAG, "Device:" + device.toString());
        }
    }
}
