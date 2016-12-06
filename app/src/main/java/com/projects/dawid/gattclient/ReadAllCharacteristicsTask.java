package com.projects.dawid.gattclient;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReadAllCharacteristicsTask extends BluetoothTask {

    private final Context mActivityContext;
    private final BluetoothDevice mDevice;
    private String TAG = "AllCharacteristicsTask";

    ReadAllCharacteristicsTask(Context activityContext, BluetoothDevice device) {
        mActivityContext = activityContext;
        mDevice = device;
    }

    @Override
    public void run() {
        Collection<BluetoothGattCharacteristic> characteristics = prepareCharacteristics();

        if (characteristics == null) {
            Log.e(TAG, "Could not prepare characteristics. Aborting");
            onResponse(null, null);
            return;
        }

        appendAllCharacteristics(characteristics);
        onResponse(null, null);
    }

    private void appendAllCharacteristics(Collection<BluetoothGattCharacteristic> characteristics) {
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();

            BluetoothTask newTask = new ReadCharacteristicTask(mActivityContext, characteristic, mDevice);
            taskManager.append(newTask);
        }
    }

    private Collection<BluetoothGattCharacteristic> prepareCharacteristics() {
        BluetoothGatt gatt = DeviceGattMap.getInstance().getGattForDevice(mDevice);

        if (gatt == null) {
            Log.e(TAG, "gatt is null. Aborting");
            return null;
        }

        Set<BluetoothGattCharacteristic> characteristics = new HashSet<>();
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
        BluetoothTaskManager.getInstance().taskFinished(this);
    }
}
