package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Callback for LEScan.
 */
class ServiceLEScanCallback extends ScanCallback {
    private Context mContext;

    ServiceLEScanCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        Intent deviceFoundResponseIntent = createDeviceFoundIntent(result.getDevice());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceFoundResponseIntent);
    }

    @NonNull
    private Intent createDeviceFoundIntent(BluetoothDevice device) {
        Intent deviceFoundResponseIntent = new Intent();
        deviceFoundResponseIntent.setAction(BLEService.RESPONSE);
        deviceFoundResponseIntent.putExtra(BLEService.RESPONSE, BLEService.Responses.DEVICE_FOUND);
        deviceFoundResponseIntent.putExtra(BLEService.Responses.DEVICE, device);
        return deviceFoundResponseIntent;
    }
}
