package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for storing All services and characteristics.
 *
 * Class, that is a workaround for issue in Android API21,
 * where BluetoothGattService is not Parcelable, what means, that
 * it must be passed statically...
 */
class CharacteristicsStaticContainer {
    private static final Object mLock = new Object();
    private static CharacteristicsStaticContainer mStaticContainer;
    private static List<BluetoothGattService> mServices = new ArrayList<>();


    private CharacteristicsStaticContainer() {
    }

    /**
     * Standard Singleton getInstance method.
     *
     * @return reference to global object of class CharacteristicsStaticContainer.
     */
    @NonNull
    static CharacteristicsStaticContainer getInstance() {
        if (mStaticContainer == null)
            mStaticContainer = new CharacteristicsStaticContainer();

        return mStaticContainer;
    }

    /**
     * Pushes list of services to global container. Replaces previously pushed elements.
     * Creates a copy of services.
     *
     * @param services List of BluetoothGattServices to be stored.
     */
    void pushCharacteristics(List<BluetoothGattService> services) {
        synchronized (mLock) {
            //mServices.clear();
            mServices = services;
//            for (BluetoothGattService service : services)
//                mServices.add(service);
        }
    }

    /**
     * Takes previously pushed characteristics.
     *
     * @return Copy of locally stored characteristics previously pushed by pushCharacteristics.
     */
    List<BluetoothGattService> pullCharacteristics() {
        synchronized (mLock) {
//            ArrayList<BluetoothGattService> newServices = new ArrayList<>();
//            for (BluetoothGattService service : mServices) {
//                newServices.add(service);
//            }

            return mServices;
        }
    }
}
