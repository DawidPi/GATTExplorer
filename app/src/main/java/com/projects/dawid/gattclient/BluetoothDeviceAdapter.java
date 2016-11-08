package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

class BluetoothDeviceAdapter implements Parcelable {
    public static final Creator<BluetoothDeviceAdapter> CREATOR = new Creator<BluetoothDeviceAdapter>() {
        @Override
        public BluetoothDeviceAdapter createFromParcel(Parcel in) {
            return new BluetoothDeviceAdapter(in);
        }

        @Override
        public BluetoothDeviceAdapter[] newArray(int size) {
            return new BluetoothDeviceAdapter[size];
        }
    };
    private BluetoothDevice mBluetoothDevice;

    BluetoothDeviceAdapter(BluetoothDevice device){
        mBluetoothDevice = device;
    }

    protected BluetoothDeviceAdapter(Parcel in) {
        mBluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        mBluetoothDevice.writeToParcel(parcel, i);
    }
}
