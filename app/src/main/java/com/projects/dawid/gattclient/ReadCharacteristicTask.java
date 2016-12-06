package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;

public class ReadCharacteristicTask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothGattCharacteristic mCharacteristicToRead;
    private BluetoothDevice mDevice;

    ReadCharacteristicTask(Context activityContext, BluetoothGattCharacteristic characteristic, BluetoothDevice device) {
        mActivityContext = activityContext;
        mCharacteristicToRead = characteristic;
        mDevice = device;
    }

    @Override
    public void run() {
        Intent readCharacteristicIntent = new Intent(mActivityContext, BLEService.class);
        readCharacteristicIntent.setAction(BLEService.REQUEST);
        readCharacteristicIntent.putExtra(BLEService.REQUEST, BLEService.Request.READ_CHARACTERISTIC);
        readCharacteristicIntent.putExtra(BLEService.DEVICE, mDevice);
        mActivityContext.startService(readCharacteristicIntent);
    }

    BluetoothGattCharacteristic getCharacteristic() {
        return mCharacteristicToRead;
    }

    @Override
    public String toString() {
        return "ReadCharacteristicTask";
    }
}
