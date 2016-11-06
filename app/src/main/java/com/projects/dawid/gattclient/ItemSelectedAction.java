package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

class ItemSelectedAction implements AdapterView.OnItemClickListener {
    ItemSelectedAction(Context context){
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BluetoothDeviceAdapter bluetoothDeviceAdapter= (BluetoothDeviceAdapter)adapterView.getItemAtPosition(position);
        BluetoothDevice bluetoothDevice = bluetoothDeviceAdapter.getBluetoothDevice();

        Log.i(TAG,"Device selected: " + bluetoothDevice.getName());

        bluetoothDevice.connectGatt(mContext, false, mBLECallback);
    }

    private Context mContext;
    private GATTCallback mBLECallback = new GATTCallback();
    private String TAG = "ITEM SELECTED";
}
