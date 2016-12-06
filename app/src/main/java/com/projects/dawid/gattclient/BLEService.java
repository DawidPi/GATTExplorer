package com.projects.dawid.gattclient;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * IntentService, that handles Bluetooth LE action for the views. Interface for this
 * Service is provided by BLEServiceStarter class.
 */
public class BLEService extends IntentService {

    private static final String TAG = "BLEService";
    private static String PREFIX = "com.pilarski.gattclient.bleservice";
    public static final String DEVICE = PREFIX + "DEVICE";
    public static final String REQUEST = PREFIX + "REQUEST";
    public static final String RESPONSE = PREFIX + "RESPONSE";
    public static final String CHARACTERISTIC = PREFIX + "CHARACTERISTIC";

    public BLEService() {
        super("BLEService");
    }

    @NonNull
    private ServiceGATTCallback getGATTCallback() {
        return new ServiceGATTCallback(this);
    }

    @NonNull
    private BluetoothLeScanner getBluetoothLeScanner() {
        return ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getBluetoothLeScanner();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "OnHandleIntent started!");

        if (intent != null) {
            Log.i(TAG, "OnHandleIntent not null");
            Request requestType = (Request) intent.getSerializableExtra(REQUEST);
            switch (requestType) {
                case DISCOVER_DEVICES:
                    startLEScan();
                    break;

                case STOP_SCAN:
                    stopLEScan();
                    break;

                case DISCONNECT:
                    disconnectGatt(intent);
                    break;

                case CONNECT_GATT:
                    connectDevice(intent);
                    break;

                case PERFORM_SERVICE_DISCOVERY:
                    discoverServices(intent);
                    break;

                case READ_CHARACTERISTIC:
                    readCharacteristic(intent);
                    break;

                default:
                    Log.e(TAG, "Unknown request type: " + requestType);
            }
        }
    }

    private void readCharacteristic(Intent intent) {
        Log.i(TAG, "Request to read characteristic started!");
        if (!(BluetoothTaskManager.getInstance().getCurrentTask() instanceof ReadCharacteristicTask)) {
            Log.e(TAG, "Task of not expected type");
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BLEService.DEVICE);
        ReadCharacteristicTask task = (ReadCharacteristicTask) BluetoothTaskManager.getInstance().getCurrentTask();
        BluetoothGattCharacteristic characteristic = task.getCharacteristic();
        DeviceGattMap.getInstance().getGattForDevice(device).readCharacteristic(characteristic);
    }

    private void disconnectGatt(Intent intent) {
        Log.i(TAG, "disconnecting GATT");
        BluetoothDevice device = intent.getParcelableExtra(DEVICE);
        DeviceGattMap.getInstance().getGattForDevice(device).disconnect();
    }

    private ArrayList<BluetoothGattCharacteristic> prepareCharacteristicsList(BluetoothGatt gatt) {
        ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristics.add(characteristic);
            }
        }

        return characteristics;
    }

    private void discoverServices(Intent intent) {
        Log.i(TAG, "Notifying about discovered services");
        BluetoothDevice device = intent.getParcelableExtra(DEVICE);
        BluetoothGatt gatt = DeviceGattMap.getInstance().getGattForDevice(device);

        if (gatt != null) {
            gatt.discoverServices();
        } else {
            Log.e(TAG, "Gatt is null!");
        }
    }

    private void connectDevice(Intent intent) {
        Log.i(TAG, "Connecting GATT");
        BluetoothDevice device = intent.getParcelableExtra(DEVICE);

        //never set autoconnect on true, as it's known to cause errors on some android devices
        device.connectGatt(this, false, getGATTCallback());
    }

    private void stopLEScan() {
        Log.i(TAG, "Stopping LE scan");
        getBluetoothLeScanner().stopScan(new ServiceLEScanCallback(this));
    }

    private void startLEScan() {
        Log.i(TAG, "Starting LE scan");
        getBluetoothLeScanner().startScan(new ServiceLEScanCallback(this));
    }

    enum Request {
        PERFORM_SERVICE_DISCOVERY,
        DISCOVER_DEVICES,
        STOP_SCAN,
        CONNECT_GATT,
        DISCONNECT,
        READ_CHARACTERISTIC
    }

    enum Response {
        DEVICE_FOUND,
        CONNECTION_SUCCESSFUL,
        SCAN_FINISHED,
        CONNECTION_LOST,
        SERVICES_DISCOVERED,
        CHARACTERISTIC_READ,
        CHARACTERISTIC_UPDATED
    }
}
