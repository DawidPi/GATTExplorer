package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class ServiceDiscoveryTask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothDevice mDevice;
    private String TAG = "ServiceDiscoveryTask";

    ServiceDiscoveryTask(Context activityContext, BluetoothDevice device) {
        mActivityContext = activityContext;
        mDevice = device;
    }

    @Override
    public void run() {
        Log.i(TAG, "Task running");
        Intent discoverServicesIntent = new Intent(mActivityContext, BLEService.class);
        discoverServicesIntent.setAction(BLEService.REQUEST);
        discoverServicesIntent.putExtra(BLEService.REQUEST, BLEService.Request.PERFORM_SERVICE_DISCOVERY);
        discoverServicesIntent.putExtra(BLEService.DEVICE, mDevice);
        mActivityContext.startService(discoverServicesIntent);
    }

    @Override
    public String toString() {
        return "ServiceDiscoveryTask";
    }
}
