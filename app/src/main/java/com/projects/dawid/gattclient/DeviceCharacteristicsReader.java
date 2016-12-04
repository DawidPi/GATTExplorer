package com.projects.dawid.gattclient;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class DeviceCharacteristicsReader {
    private static final String TAG = "CharacteristicsReader";
    private final Object mLock = new Object();
    private Queue<BluetoothGattCharacteristic> mCharacteristics = new LinkedList<>();
    private boolean mReadingPending = false;
    private BluetoothGatt mGatt;

    /**
     * Appends single Characteristic for further processing.
     *
     * @param characteristic characteristic to enable notifications on.
     */
    void appendCharacteristic(BluetoothGattCharacteristic characteristic) {
        mCharacteristics.add(characteristic);
    }

    /**
     * Appends multiple Characteristics for further processing
     *
     * @param characteristics set of characteristics to be appended
     */
    void appendCharacteristics(Collection<BluetoothGattCharacteristic> characteristics) {
        mCharacteristics.addAll(characteristics);
    }

    /**
     * Start processing of appended characteristics
     *
     * @param gatt BluetoothGatt to be used with processing
     */
    void start(BluetoothGatt gatt) {
        mGatt = gatt;
        tryReadNextCharacteristic(gatt);
    }

    private boolean tryReadNextCharacteristic(BluetoothGatt gatt) {
        do {
            BluetoothGattCharacteristic characteristic = mCharacteristics.poll();
            if (characteristic == null)
                continue;

            if (!gatt.readCharacteristic(characteristic)) {
                Log.i(TAG, "Reading characteristic : " + characteristic.getUuid().toString() +
                        " failed!");
                continue;
            }

            Log.i(TAG, "Characteristics read started properly!");
            mReadingPending = true;
            return true;

        } while (mCharacteristics.size() > 0);

        Log.i(TAG, "No more characteristics to process");
        return false;
    }

    /**
     * Method to be called, when characteristic read callback is executed
     *
     * @return returns true if more characteristics are being read, false if no characteristics left to read.
     */
    boolean onCharacteristicRead() {
        if (mReadingPending) {
            mReadingPending = false;
            if (tryReadNextCharacteristic(mGatt)) {
                return true;
            }
            mGatt = null;
        }

        return false;
    }
}
