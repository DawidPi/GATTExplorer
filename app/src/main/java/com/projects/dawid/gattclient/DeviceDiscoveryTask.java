package com.projects.dawid.gattclient;

import android.content.Context;
import android.content.Intent;

public class DeviceDiscoveryTask extends BluetoothTask {

    private final Context mActivityContext;

    DeviceDiscoveryTask(Context activityContext) {
        mActivityContext = activityContext;
    }

    @Override
    public void run() {
        Intent deviceDiscoveryIntent = new Intent(mActivityContext, BLEService.class);
        deviceDiscoveryIntent.setAction(BLEService.REQUEST);
        deviceDiscoveryIntent.putExtra(BLEService.REQUEST, BLEService.Request.DISCOVER_DEVICES);
        mActivityContext.startService(deviceDiscoveryIntent);
        onResponse(null, null);
    }

    @Override
    public String toString() {
        return "DeviceDiscoveryTask";
    }
}
