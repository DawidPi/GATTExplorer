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
    private Context mServiceContext;
    private String TAG = "GATT CALLBACK";
    private Map<BluetoothDevice, BluetoothGatt> mDeviceGattMap = new HashMap<>();
    private List<BluetoothGattCharacteristic> mCharacteristicsLeftToRead;
    private List<BluetoothGattDescriptor> mDescriptorsLeftToRead;
    private Integer mDescriptorsWritePending = 0;
    private boolean mNotificationsSet = false;
    private boolean mReadingCharacteristicsInProgress = false;

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
        Log.i(TAG, "Characteristics left: " + mDescriptorsLeftToRead.size());

        logCharacteristic(characteristic);

        if (mCharacteristicsLeftToRead.size() > 0) {
            Log.i(TAG, "Reading next characteristic.");
            if (!readNextCharacteristic(gatt)) {
                finishReadingCharacteristics(gatt);
            }
        } else {
            finishReadingCharacteristics(gatt);
        }
    }

    private void finishReadingCharacteristics(BluetoothGatt gatt) {
        Log.i(TAG, "No characteristics left to read.");
        notifyReadingFinished(gatt);
        mCharacteristicsLeftToRead = null;

        Log.i(TAG, "Reading device finished!");
    }

    private void notifyReadingFinished(BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        Intent characteristicsReadIntent = new Intent();
        characteristicsReadIntent.setAction(BLEService.RESPONSE);
        characteristicsReadIntent.putExtra(BLEService.RESPONSE, BLEService.Responses.READ_ALL_CHARACTERISTICS);
        characteristicsReadIntent.putExtra(BLEService.Responses.DEVICE, device);
        CharacteristicsStaticContainer characteristicsContainer = CharacteristicsStaticContainer.getInstance();
        characteristicsContainer.pushCharacteristics(gatt.getServices());

        LocalBroadcastManager.getInstance(mServiceContext).sendBroadcast(characteristicsReadIntent);
        mReadingCharacteristicsInProgress = false;
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        mDescriptorsLeftToRead.remove(0);
        Log.i(TAG, "Descriptors left: " + mDescriptorsLeftToRead.size());

        if (mDescriptorsLeftToRead.size() > 0) {
            Log.i(TAG, "Reading next descriptor.");
            gatt.readDescriptor(mDescriptorsLeftToRead.get(0));
        } else {
            Log.i(TAG, "No descriptors left to read.");
            mDescriptorsLeftToRead = null;

            if (mCharacteristicsLeftToRead != null) {
                Log.i(TAG, "starting to read Characteristics");
                gatt.readCharacteristic(mCharacteristicsLeftToRead.get(0));
            }
        }
    }

    private void logCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "Characteristic UUID: " + characteristic.getUuid());
        byte[] characteristicsValue = characteristic.getValue();
        if (characteristicsValue != null) {
            String valueInString = "";
            for (byte currentValueByte : characteristicsValue) {
                valueInString = valueInString + " " + Integer.toHexString(currentValueByte);
            }

            Log.i(TAG, "Characteristic value: " + valueInString);
        } else
            Log.e(TAG, "characteristic value is null");
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
        synchronized (mDescriptorsWritePending) {
            mDescriptorsWritePending--;
            if (mDescriptorsWritePending == 0) {
                readNextCharacteristic(gatt);
            }
        }
    }

    public BluetoothGatt getGattForDevice(BluetoothDevice device) {
        Log.i(TAG, "get gatt for device: " + device);
        return mDeviceGattMap.get(device);
    }

    public void startReadingCharacteristics(@NonNull BluetoothDevice device,
                                            @NonNull ArrayList<BluetoothGattCharacteristic> characteristics,
                                            @NonNull ArrayList<BluetoothGattDescriptor> descriptors) {
        Log.i(TAG, "Starting to read Characteristics of device: " + device.getName());
        if (mReadingCharacteristicsInProgress) {
            Log.d(TAG, "Reading Characteristics already in progress");
            return;
        }

        mReadingCharacteristicsInProgress = true;
        mDescriptorsLeftToRead = (ArrayList<BluetoothGattDescriptor>) descriptors.clone();
        mCharacteristicsLeftToRead = (ArrayList<BluetoothGattCharacteristic>) characteristics.clone();

        Log.i(TAG, "descriptors size: " + descriptors.size() + " characteristics size: " + characteristics.size());

        BluetoothGatt gatt = getGattForDevice(device);

        if (!mNotificationsSet) {
            for (BluetoothGattCharacteristic characteristic : mCharacteristicsLeftToRead) {
                setNotifications(gatt, characteristic);
            }
            mNotificationsSet = true;
        } else {
            readNextCharacteristic(gatt);
        }
    }

    private boolean readNextCharacteristic(BluetoothGatt gatt) {
        while (!gatt.readCharacteristic(mCharacteristicsLeftToRead.get(0))) {
            Log.i(TAG, "Reading characteristic : " + mCharacteristicsLeftToRead.get(0).getUuid().toString() +
                    " failed!");

            mCharacteristicsLeftToRead.remove(0);
            if (mCharacteristicsLeftToRead.size() == 0) {
                Log.d(TAG, "Unable to read any characteristic!");
                return false;
            }
        }

        mCharacteristicsLeftToRead.remove(0);
        Log.i(TAG, "Characteristic read set properly");
        return true;
    }

    private void setNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        boolean characteristicSetSuccessfully = gatt.setCharacteristicNotification(characteristic, true);
        UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);

        if (descriptor != null && characteristicSetSuccessfully) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (gatt.writeDescriptor(descriptor)) {
                synchronized (mDescriptorsWritePending) {
                    mDescriptorsWritePending++;
                    Log.i(TAG, "descriptorsWithPendingWrites: " + mDescriptorsWritePending);
                }
            }
            Log.i(TAG, "Characteristic notification set properly");
        } else {
            //this ain't error
            Log.d(TAG, "Notification set failed!");
        }
    }
}
