package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;


class EnableCharacteristicNotificationTask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final BluetoothDevice mDevice;

    EnableCharacteristicNotificationTask(Context activityContext,
                                         BluetoothGattCharacteristic characteristic,
                                         BluetoothDevice device) {
        mActivityContext = activityContext;
        mCharacteristic = characteristic;
        mDevice = device;
    }

    @Override
    public void run() {
        Intent enableNotificationIntent = new Intent(mActivityContext, BLEService.class);
        enableNotificationIntent.setAction(BLEService.REQUEST);
        enableNotificationIntent.putExtra(BLEService.REQUEST, BLEService.Request.ENABLE_CHARACTERISTIC_NOTIFICATION);
        enableNotificationIntent.putExtra(BLEService.DEVICE, mDevice);
        mActivityContext.startService(enableNotificationIntent);
    }

    @Override
    public String toString() {
        return "Enable Notification";
    }

    BluetoothGattCharacteristic getCharacteristic() {
        return mCharacteristic;
    }
}
