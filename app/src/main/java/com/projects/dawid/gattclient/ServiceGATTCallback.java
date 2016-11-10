package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ServiceGATTCallback extends BluetoothGattCallback {
    private BluetoothGatt mGattService;
    private Context mServiceContext;
    private String TAG = "GATT CALLBACK";
    private Map<BluetoothDevice, BluetoothGatt> mDeviceGattMap = new HashMap<>();

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
            mDeviceGattMap.put(gatt.getDevice(), gatt);
            notifyConnectionSuccessful(gatt.getDevice());
        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED){
            notifyDisconnection(gatt.getDevice());
            Log.i(TAG, "Bluetooth device disconnected");
        }
    }

    private void notifyDisconnection(BluetoothDevice device) {
        Intent connectionLostIntent = new Intent();
        connectionLostIntent.setAction(BLEService.RESPONSE);
        connectionLostIntent.putExtra(BLEService.RESPONSE, BLEService.Responses.CONNECTION_LOST);
        connectionLostIntent.putExtra(BLEService.Responses.DEVICE, device);
        LocalBroadcastManager.getInstance(mServiceContext).sendBroadcast(connectionLostIntent);
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
        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services) {
            Log.i(TAG, "Service: " + service.toString());
        }
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

    public BluetoothGatt getGattForDevice(BluetoothDevice device) {
        return (BluetoothGatt) mDeviceGattMap.get(device);
    }

    public Set<BluetoothGatt> getAllGatts() {
        Set<BluetoothGatt> gatts = new HashSet<>();

        for (Map.Entry<BluetoothDevice, BluetoothGatt> entry : mDeviceGattMap.entrySet()) {
            gatts.add(entry.getValue());
        }

        return gatts;
    }
}
