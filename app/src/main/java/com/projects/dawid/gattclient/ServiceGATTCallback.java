package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


// issue occured, when I realized, that Intent service is killed after
// every finished intent. This is why I need to have this callback saved, to be
// univarsal. Proper solution would be to save Callback to some persistent place

/**
 * Callback for all the GATT events.
 */
class ServiceGATTCallback extends BluetoothGattCallback {
    private Context mServiceContext;
    private String TAG = "GATT CALLBACK";
    private NotificationsEnabler mNotificationsManager = new NotificationsEnabler();
    private DeviceCharacteristicsReader mCharacteristicsUpdater = new DeviceCharacteristicsReader();

    ServiceGATTCallback(Context serviceContext) {
        mServiceContext = serviceContext;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(newState == BluetoothProfile.STATE_CONNECTED){
            Log.i(TAG, "Bluetooth device connected");
            DeviceGattMap.getInstance().putGattForDevice(gatt, gatt.getDevice());
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
        connectionLostIntent.putExtra(BLEService.RESPONSE, BLEService.Response.CONNECTION_LOST);
        connectionLostIntent.putExtra(BLEService.DEVICE, device);
        BluetoothTaskManager.getInstance().getCurrentTask().onResponse(mServiceContext, connectionLostIntent);
    }

    private void notifyConnectionSuccessful(BluetoothDevice device) {
        Intent connectionSuccessfulIntent = new Intent();
        connectionSuccessfulIntent.setAction(BLEService.RESPONSE);
        connectionSuccessfulIntent.putExtra(BLEService.RESPONSE, BLEService.Response.CONNECTION_SUCCESSFUL);
        connectionSuccessfulIntent.putExtra(BLEService.DEVICE, device);
        BluetoothTaskManager.getInstance().getCurrentTask().onResponse(mServiceContext, connectionSuccessfulIntent);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.i(TAG, "Services discovered");
        notifyServicesDiscovered(gatt);
    }

    private void notifyServicesDiscovered(BluetoothGatt gatt) {
        Intent intent = new Intent();
        intent.setAction(BLEService.RESPONSE);
        intent.putExtra(BLEService.RESPONSE, BLEService.Response.SERVICES_DISCOVERED);
        intent.putExtra(BLEService.DEVICE, gatt.getDevice());
        CharacteristicsStaticContainer.getInstance().pushCharacteristics(gatt.getServices());
        BluetoothTaskManager.getInstance().getCurrentTask().onResponse(mServiceContext, intent);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "Characteristic Read callback");
        logCharacteristic(characteristic);
        Intent characteristicReadIntent = new Intent();
        characteristicReadIntent.setAction(BLEService.RESPONSE);
        characteristicReadIntent.putExtra(BLEService.RESPONSE, BLEService.Response.CHARACTERISTIC_READ);
        SingleCharacteristicStaticContainer.getInstance().pushCharacteristic(characteristic);
        BluetoothTaskManager.getInstance().getCurrentTask().onResponse(mServiceContext, characteristicReadIntent);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.e(TAG, "Not implemented");
    }

    private void logCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "Characteristic UUID: " + characteristic.getUuid());
        byte[] characteristicsValue = characteristic.getValue();

        Log.i(TAG, "Value of the characteristic: " +
                CharacteristicValueRepresentation.translateToString(characteristicsValue));
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "Characteristic write finished");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "Characteristic changed");
        if (characteristic == null) {
            Log.i(TAG, "Skip characteristic changed!");
            return;
        }

        Log.i(TAG, "Notify Activity about characteristic change!");
        SingleCharacteristicStaticContainer.getInstance().pushCharacteristic(characteristic);
        notifyCharacteristicChanged();
    }

    private void notifyCharacteristicChanged() {
        Intent characteristicUpdatedIntent = new Intent();
        characteristicUpdatedIntent.setAction(BLEService.RESPONSE);
        characteristicUpdatedIntent.putExtra(BLEService.RESPONSE, BLEService.Response.CHARACTERISTIC_UPDATED);
        BluetoothTaskManager.getInstance().getCurrentTask().onResponse(mServiceContext, characteristicUpdatedIntent);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "Descriptor write finished successfully");
        if (!mNotificationsManager.onDescriptorWriteFinished()) {
            mCharacteristicsUpdater.start(gatt);
        }
    }

}
