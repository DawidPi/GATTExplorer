package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Dawid on 03.11.2016.
 */

public class BluetoothDeviceAdapter {
    public BluetoothDeviceAdapter(BluetoothDevice device){
        mBluetoothDevice = device;
    }

    public String toString(){
        return mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress();
    }

    public boolean equals(Object object){
        BluetoothDeviceAdapter compareDevice = (BluetoothDeviceAdapter) object;
        return this.mBluetoothDevice.equals(compareDevice.mBluetoothDevice);
    }

    public int hashCode(){
        return mBluetoothDevice.hashCode();
    }

    private BluetoothDevice mBluetoothDevice;
}
