package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

/**
 * Workaround for Android API21, where characteristics can be stored.
 * Same as CharacteristicsStaticContainer. API21 should be cursed.
 */
class SingleCharacteristicStaticContainer {
    private static BluetoothGattCharacteristic mCharacteristic;
    private static SingleCharacteristicStaticContainer mSingleton;

    private SingleCharacteristicStaticContainer() {
    }

    @NonNull
    static SingleCharacteristicStaticContainer getInstance() {
        if (mSingleton == null)
            mSingleton = new SingleCharacteristicStaticContainer();

        return mSingleton;
    }

    void pushCharacteristic(BluetoothGattCharacteristic newCharacteristic) {
        mCharacteristic = newCharacteristic;
    }

    BluetoothGattCharacteristic pullCharacteristic() {
        return mCharacteristic;
    }

}
