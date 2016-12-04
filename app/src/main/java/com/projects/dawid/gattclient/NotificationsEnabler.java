package com.projects.dawid.gattclient;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * Helps to enable notifications on multiple characteristics one by one
 */
public class NotificationsEnabler {
    private static final String TAG = "Notifications Enabler";
    private final Object mLock = new Object();
    private Queue<BluetoothGattCharacteristic> mCharacteristics = new LinkedList<>();
    private boolean mWritePending = false;
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
        trySetNotificationOnNextPossibleElement(gatt);
    }

    /**
     * Callback from GATTCallback, that Descriptor write has been successfully finished.
     *
     * @return true if more Descriptors are being written, false if no descriptors left to write to.
     */
    boolean onDescriptorWriteFinished() {
        if (mWritePending) {
            mWritePending = false;
            if (trySetNotificationOnNextPossibleElement(mGatt)) {
                return true;
            }
            mGatt = null;
        }

        return false;
    }

    private boolean trySetNotificationOnNextPossibleElement(BluetoothGatt gatt) {

        if (gatt == null) {
            throw new IllegalArgumentException("null argument in trySetNotificationOnNextPossibleElement");
        }

        while (mCharacteristics.size() > 0) {
            BluetoothGattCharacteristic characteristic = mCharacteristics.poll();
            if (!gatt.setCharacteristicNotification(characteristic, true))
                continue;

            BluetoothGattDescriptor descriptor = findNotificationDescriptor(characteristic);
            if (descriptor == null)
                continue;

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!gatt.writeDescriptor(descriptor))
                continue;

            Log.i(TAG, "Notification set properly");
            mWritePending = true;
            return true;
        }

        return false;
    }

    @Nullable
    private BluetoothGattDescriptor findNotificationDescriptor(BluetoothGattCharacteristic characteristic) {
        UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        return characteristic.getDescriptor(uuid);
    }
}
