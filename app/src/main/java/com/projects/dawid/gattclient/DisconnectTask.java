package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;


class DisconnectTask extends BluetoothTask {
    private final BluetoothDevice mDevice;
    private Context mActivityContext;

    DisconnectTask(Context activityContext, BluetoothDevice device) {
        mActivityContext = activityContext;
        mDevice = device;
    }

    @Override
    public void run() {
        Intent intent = new Intent();
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Request.DISCOVER_DEVICES);
        intent.putExtra(BLEService.DEVICE, mDevice);
        mActivityContext.startService(intent);
    }
}
