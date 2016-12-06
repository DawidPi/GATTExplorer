package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;


class ConnectBLETask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothDevice mDevice;

    ConnectBLETask(Context activityContext, BluetoothDevice device) {
        mActivityContext = activityContext;
        mDevice = device;
    }

    @Override
    public void run() {
        Intent connectGATTIntent = new Intent(mActivityContext, BLEService.class);
        connectGATTIntent.setAction(BLEService.REQUEST);
        connectGATTIntent.putExtra(BLEService.REQUEST, BLEService.Request.CONNECT_GATT);
        connectGATTIntent.putExtra(BLEService.DEVICE, mDevice);
        mActivityContext.startService(connectGATTIntent);
    }
}
