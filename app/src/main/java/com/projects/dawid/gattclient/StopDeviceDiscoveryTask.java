package com.projects.dawid.gattclient;


import android.content.Context;
import android.content.Intent;

public class StopDeviceDiscoveryTask extends BluetoothTask {

    private final Context mActivityContext;

    StopDeviceDiscoveryTask(Context activityContext) {
        mActivityContext = activityContext;
    }

    @Override
    public void run() {
        Intent stopDeviceDiscoveryIntent = new Intent(mActivityContext, BLEService.class);
        stopDeviceDiscoveryIntent.setAction(BLEService.REQUEST);
        stopDeviceDiscoveryIntent.putExtra(BLEService.REQUEST, BLEService.Request.STOP_SCAN);
        mActivityContext.startService(stopDeviceDiscoveryIntent);
        onResponse(null, null);
    }

    @Override
    public String toString() {
        return "StopDeviceDiscoveryTask";
    }
}
