package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

class BluetoothDeviceAdapter implements Parcelable, Serializable {
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
    private static final String TAG = "BluetoothDeviceAdapter";
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
        Log.i(TAG, "Comparison");
        if(object.getClass() == BluetoothDeviceAdapter.class) {
            Log.i(TAG, "Compare with another BluetoothDeviceAdapter");
            BluetoothDeviceAdapter compareDevice = (BluetoothDeviceAdapter) object;
            return this.mBluetoothDevice.equals(compareDevice.mBluetoothDevice);
        }
        else{
            Log.e(TAG, "Illegal Type!");
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
