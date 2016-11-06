package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

class GATTCallback extends BluetoothGattCallback {
    GATTCallback() {
        super();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            Log.i(TAG, "Success");
        }

        if(newState == BluetoothProfile.STATE_CONNECTED){
            Log.i(TAG, "Bluetooth device connected");
            gatt.discoverServices();
        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED){
            Log.i(TAG, "Bluetooth device disconnected");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.i(TAG, "Services discovered");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "Characteristic read finished");
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "Characteristic write finished");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "Characteristic changed");
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "Descriptor read finished!");
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "Descriptor write finished!");
    }

    private String TAG = "GATT CALLBACK";
}
