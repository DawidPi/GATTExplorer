package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 02.12.2016.
 */

class CharacteristicsStaticContainer {
    private static CharacteristicsStaticContainer mStaticContainer;
    private static ArrayList<BluetoothGattService> mServices = new ArrayList<>();
    private static Object lock = new Object();


    private CharacteristicsStaticContainer() {
    }

    public static CharacteristicsStaticContainer getInstance() {
        if (mStaticContainer == null)
            mStaticContainer = new CharacteristicsStaticContainer();

        return mStaticContainer;
    }

    public void pushCharacteristics(List<BluetoothGattService> services) {
        synchronized (lock) {
            mServices.clear();
            for (BluetoothGattService service : services)
                mServices.add(service);
        }
    }

    public ArrayList<BluetoothGattService> pullCharacteristics() {
        synchronized (lock) {
            ArrayList<BluetoothGattService> newServices = new ArrayList<>();
            for (BluetoothGattService service : mServices) {
                newServices.add(service);
            }

            return newServices;
        }
    }
}
