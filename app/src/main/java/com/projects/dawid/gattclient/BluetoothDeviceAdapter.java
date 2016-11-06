package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;

class BluetoothDeviceAdapter {
    BluetoothDeviceAdapter(BluetoothDevice device){
        mBluetoothDevice = device;
    }

    public String toString(){
        return mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress();
    }

    public boolean equals(Object object){
        if(object.getClass() == BluetoothDeviceAdapter.class) {
            BluetoothDeviceAdapter compareDevice = (BluetoothDeviceAdapter) object;
            return this.mBluetoothDevice.equals(compareDevice.mBluetoothDevice);
        }
        else{
            throw new IllegalArgumentException("BluetoothDeviceAdapter type can be compared only" +
                    " with another BluetoothDeviceAdapter type");
        }
    }

    public int hashCode(){
        return mBluetoothDevice.hashCode();
    }

    BluetoothDevice getBluetoothDevice(){
        return mBluetoothDevice;
    }

    private BluetoothDevice mBluetoothDevice;
}
