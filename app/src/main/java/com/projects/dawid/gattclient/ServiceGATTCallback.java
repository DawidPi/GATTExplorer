package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

class ServiceGATTCallback extends BluetoothGattCallback {
    private BluetoothGatt mGattService;
    private Context mServiceContext;
    private String TAG = "GATT CALLBACK";

    ServiceGATTCallback(Context serviceContext) {
        super();
        mServiceContext = serviceContext;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            Log.i(TAG, "Success");
        }

        if(newState == BluetoothProfile.STATE_CONNECTED){
            Log.i(TAG, "Bluetooth device connected");
            notifyConnectionSuccessful(gatt.getDevice());
            mGattService = gatt;
        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED){
            Log.i(TAG, "Bluetooth device disconnected");
        }
    }

    private void notifyConnectionSuccessful(BluetoothDevice device) {
        Intent connectionSuccessfulIntent = new Intent();
        connectionSuccessfulIntent.setAction(BLEService.RESPONSE);
        connectionSuccessfulIntent.putExtra(BLEService.RESPONSE, BLEService.Responses.CONNECTION_SUCCESSFUL);
        connectionSuccessfulIntent.putExtra(BLEService.Responses.DEVICE, device);
        LocalBroadcastManager.getInstance(mServiceContext).sendBroadcast(connectionSuccessfulIntent);
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

    public BluetoothGatt getGattService() {
        return mGattService;
    }
}
