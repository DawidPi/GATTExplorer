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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


// issue occured, when I realized, that Intent service is killed after
// every finished intent. This is why I need to have this callback saved, to be
// univarsal. Proper solution would be to save Callback to some persistent place

class ServiceGATTCallback extends BluetoothGattCallback {
    private static ServiceGATTCallback staticCallback;
    private BluetoothGatt mGattService;
    private Context mServiceContext;
    private String TAG = "GATT CALLBACK";
    private Map<BluetoothDevice, BluetoothGatt> mDeviceGattMap = new HashMap<>();
    private List<BluetoothGattCharacteristic> mCharacteristicsToRead;
    private List<BluetoothGattDescriptor> mDescriptorsToRead;

    private ServiceGATTCallback(Context serviceContext) {
        super();
        mServiceContext = serviceContext;
    }

    static ServiceGATTCallback getInstance(Context context) {
        if (staticCallback == null)
            staticCallback = new ServiceGATTCallback(context);

        return staticCallback;
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
        notifyServicesDiscovered(gatt);
    }

    private void notifyServicesDiscovered(BluetoothGatt gatt) {
        Intent intent = new Intent();
        intent.setAction(BLEService.RESPONSE);
        intent.putExtra(BLEService.RESPONSE, BLEService.Responses.SERVICES_DISCOVERED);
        intent.putExtra(BLEService.Responses.DEVICE, gatt.getDevice());
        intent.putParcelableArrayListExtra(BLEService.Responses.SERVICES_LIST, getArrayListServices(gatt));
        LocalBroadcastManager.getInstance(mServiceContext).sendBroadcast(intent);
    }

    @NonNull
    private ArrayList<BluetoothGattService> getArrayListServices(BluetoothGatt gatt) {
        ArrayList<BluetoothGattService> services = new ArrayList<>();
        services.addAll(gatt.getServices());
        return services;
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        mCharacteristicsToRead.remove(0);
        Log.i(TAG, "Characteristics left: " + mDescriptorsToRead.size());

        logCharacteristic(characteristic);

        if (mCharacteristicsToRead.size() > 0) {
            Log.i(TAG, "Reading next characteristic.");
            gatt.readCharacteristic(mCharacteristicsToRead.get(0));
        } else {
            Log.i(TAG, "No characteristics left to read.");
            mCharacteristicsToRead = null;
        }

        Log.i(TAG, "Reading device finished!");
        //todo complete
        //notifyReadingFinished();
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        mDescriptorsToRead.remove(0);
        Log.i(TAG, "Descriptors left: " + mDescriptorsToRead.size());

        if (mDescriptorsToRead.size() > 0) {
            Log.i(TAG, "Reading next descriptor.");
            gatt.readDescriptor(mDescriptorsToRead.get(0));
        } else {
            Log.i(TAG, "No descriptors left to read.");
            mDescriptorsToRead = null;

            if (mCharacteristicsToRead != null) {
                Log.i(TAG, "starting to read Characteristics");
                gatt.readCharacteristic(mCharacteristicsToRead.get(0));
            }
        }
    }

    private void logCharacteristic(BluetoothGattCharacteristic characteristic) {
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        BluetoothGattDescriptor typeDescriptor = findTypeDescriptor(descriptors);
        int format = getTypeFormat(typeDescriptor);
    }

    private int getTypeFormat(BluetoothGattDescriptor typeDescriptor) {
        byte[] valueBytes = typeDescriptor.getValue();
        //todo complete
        return 0;
    }

    @Nullable
    private BluetoothGattDescriptor findTypeDescriptor(List<BluetoothGattDescriptor> descriptors) {
        UUID storedTypeUUID = UUID.fromString("29040000-0000-1000-8000-00805F9B34FB");
        for (BluetoothGattDescriptor descriptor : descriptors) {
            if (descriptor.getUuid().equals(storedTypeUUID)) {
                return descriptor;
            }
        }

        return null;
    }


    private int determineFormatType(BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();
        int formatMask = createCharacteristicFormatMask();
        return properties & formatMask;
    }


    private int createCharacteristicFormatMask() {
        return BluetoothGattCharacteristic.FORMAT_FLOAT | BluetoothGattCharacteristic.FORMAT_SFLOAT
                | BluetoothGattCharacteristic.FORMAT_SINT8 | BluetoothGattCharacteristic.FORMAT_SINT16 |
                BluetoothGattCharacteristic.FORMAT_SINT32 | BluetoothGattCharacteristic.FORMAT_UINT8
                | BluetoothGattCharacteristic.FORMAT_UINT16 | BluetoothGattCharacteristic.FORMAT_UINT32;
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
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "Descriptor write finished!");
    }

    public BluetoothGatt getGattForDevice(BluetoothDevice device) {
        Log.i(TAG, "get services for device: " + device);
        return mDeviceGattMap.get(device);
    }

    public void startReadingCharacteristics(BluetoothDevice device,
                                            List<BluetoothGattCharacteristic> characteristics,
                                            List<BluetoothGattDescriptor> descriptors) {

        mDescriptorsToRead = descriptors;
        mCharacteristicsToRead = characteristics;

        BluetoothGatt gatt = getGattForDevice(device);
        gatt.readDescriptor(descriptors.get(0));
    }
}
