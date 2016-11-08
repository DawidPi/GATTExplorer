package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

class ItemSelectedAction implements AdapterView.OnItemClickListener {
    private Context mContext;
    private String TAG = "ITEM SELECTED";

    ItemSelectedAction(Context context){
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BluetoothDeviceAdapter bluetoothDeviceAdapter= (BluetoothDeviceAdapter)adapterView.getItemAtPosition(position);
        BluetoothDevice bluetoothDevice = bluetoothDeviceAdapter.getBluetoothDevice();

        Log.i(TAG, "Device selected: " + bluetoothDevice.getName());

        disconnectDevices();
        connectDevice(bluetoothDevice);
    }

    private void disconnectDevices() {
        Intent intent = new Intent(mContext, BLEService.class);
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Requests.DISCONNECT);
    }

    private void connectDevice(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(mContext, BLEService.class);
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Requests.CONNECT_GATT);
        intent.putExtra(BLEService.Requests.DEVICE, bluetoothDevice);
        mContext.startService(intent);
    }
}
