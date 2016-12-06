package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Dawid on 06.12.2016.
 */

public class SetAllNotificationsTask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothDevice mDevice;
    private final String TAG = "AllNotificationsTask";

    SetAllNotificationsTask(Context activityContext, BluetoothDevice device) {
        mActivityContext = activityContext;
        mDevice = device;
    }

    @Override
    public void run() {
        Collection<BluetoothGattCharacteristic> characteristics = prepareAllCharacteristics();

        if (characteristics == null) {
            Log.e(TAG, "Could not prepare characteristics. Aborting");
            onResponse(null, null);
            return;
        }

        appendSubtasks(characteristics);
        onResponse(null, null);
    }

    private void appendSubtasks(Collection<BluetoothGattCharacteristic> characteristics) {
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            BluetoothTask task = new EnableCharacteristicNotificationTask(mActivityContext, characteristic, mDevice);
            BluetoothTaskManager.getInstance().append(task);
        }
    }

    private Collection<BluetoothGattCharacteristic> prepareAllCharacteristics() {
        ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

        final BluetoothGatt gatt = DeviceGattMap.getInstance().getGattForDevice(mDevice);

        if (gatt == null) {
            Log.e(TAG, "gatt is null. Aborting");
            return null;
        }

        for (BluetoothGattService service : gatt.getServices()) {
            if (service == null)
                continue;

            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic == null)
                    continue;

                characteristics.add(characteristic);
            }
        }

        return characteristics;
    }

    @Override
    void onResponse(Context context, Intent responseIntent) {
        //empty not to be used by BluetoothTaskManager
    }

    @Override
    public String toString() {
        return "SetAllNotificationsTask";
    }
}
