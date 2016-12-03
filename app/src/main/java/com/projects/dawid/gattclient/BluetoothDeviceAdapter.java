package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * BluetoothDeviceAdapter class created for Default ListAdapter, so that Devices are displayed
 * in desired way.
 */
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

    /**
     * Default constructor.
     *
     * @param device reference for device, for which representation text is to be created
     */
    BluetoothDeviceAdapter(BluetoothDevice device){
        mBluetoothDevice = device;
    }

    private BluetoothDeviceAdapter(Parcel in) {
        mBluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    /**
     * toString returns the way, the device is to be displayed on a device discovery view.
     * @return String with proper values.
     */
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
