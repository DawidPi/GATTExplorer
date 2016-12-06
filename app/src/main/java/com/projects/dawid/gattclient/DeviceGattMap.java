package com.projects.dawid.gattclient;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.util.HashMap;

class DeviceGattMap {
    private static DeviceGattMap mSingleton;
    private static String TAG = "DeviceGattMAP";
    private HashMap<BluetoothDevice, BluetoothGatt> mMap = new HashMap<>();

    private DeviceGattMap() {

    }

    static DeviceGattMap getInstance() {
        if (mSingleton == null) {
            Log.i(TAG, "Creating device gatt map!");
            mSingleton = new DeviceGattMap();
        }

        return mSingleton;
    }

    BluetoothGatt getGattForDevice(BluetoothDevice device) {
        Log.i(TAG, "Getting gatt for device:" + device.getName());
        return mMap.get(device);
    }

    void putGattForDevice(BluetoothGatt gatt, BluetoothDevice device) {
        Log.i(TAG, "appending Gatt for device: " + device.getName());
        mMap.put(device, gatt);
    }

    void clear() {
        mMap.clear();
    }

}
