package com.projects.dawid.gattclient;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * IntentService, that handles Bluetooth LE action for the views. Interface for this
 * Service is provided by BLEServiceStarter class.
 */
public class BLEService extends IntentService {

    private static final String TAG = "BLEService";
    private static String PREFIX = "com.pilarski.GATTClient";
    public static final String REQUEST = PREFIX + "REQUEST";
    public static final String RESPONSE = PREFIX + "RESPONSE";

    public BLEService() {
        super("BLEService");
    }

    @NonNull
    private ServiceGATTCallback getGATTCallback() {
        return ServiceGATTCallback.getInstance(this);
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
            Integer requestType = intent.getIntExtra(REQUEST, -1);
            switch (requestType) {
                case Requests.PERFORM_SCAN:
                    startLEScan();
                    break;

                case Requests.STOP_SCAN:
                    stopLEScan();
                    break;

                case Requests.CONNECT_GATT:
                    connectDevice(intent);
                    break;

                case Requests.PERFORM_SERVICE_DISCOVERY:
                    discoverServices(intent);
                    break;

                case Requests.READ_ALL_CHARACTERISTICS:
                    readAllCharacteristics(intent);
                    break;
                
                case -1:
                    Log.e(TAG, "No request sent!");
                    break;
                default:
                    Log.e(TAG, "Unknown request type!");
            }
        }
    }

    private void readAllCharacteristics(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(Requests.DEVICE);
        BluetoothGatt gatt = getGATTCallback().getGattForDevice(device);

        if (device == null || gatt == null) {
            Log.e(TAG, "device or gatt is null");
            return;
        }

        ArrayList<BluetoothGattCharacteristic> characteristics = prepareCharacteristicsList(gatt);
        ArrayList<BluetoothGattDescriptor> descriptors = prepareDescriptorsList(characteristics);

        getGATTCallback().startReadingCharacteristics(device, characteristics, descriptors);
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

    private ArrayList<BluetoothGattDescriptor> prepareDescriptorsList(List<BluetoothGattCharacteristic> characteristics) {
        ArrayList<BluetoothGattDescriptor> descriptors = new ArrayList<>();

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                descriptors.add(descriptor);
            }
        }

        return descriptors;
    }

    private void discoverServices(Intent intent) {
        Log.i(TAG, "Notifying about discovered services");
        BluetoothDevice device = intent.getParcelableExtra(Requests.DEVICE);
        BluetoothGatt gatt = getGATTCallback().getGattForDevice(device);

        if (gatt != null) {
            gatt.discoverServices();
        } else {
            Log.e(TAG, "Gatt is null!");
        }
    }

    private void connectDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(Requests.DEVICE);

        //never set autoconnect on true, as it's known to cause errors on some android devices
        device.connectGatt(this, false, getGATTCallback());
    }

    private void stopLEScan() {
        getBluetoothLeScanner().stopScan(new ServiceLEScanCallback(this));
        Intent scanFinishedResponse = new Intent();
        scanFinishedResponse.setAction(RESPONSE);
        scanFinishedResponse.putExtra(RESPONSE, Responses.SCAN_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(scanFinishedResponse);
    }

    private void startLEScan() {
        getBluetoothLeScanner().startScan(new ServiceLEScanCallback(this));
    }

    public static final class Requests {
        static final int PERFORM_SERVICE_DISCOVERY = 0;
        static final int PERFORM_SCAN = 1;
        static final int STOP_SCAN = 2;
        static final int CONNECT_GATT = 3;
        static final int DISCONNECT = 4;
        static final int READ_ALL_CHARACTERISTICS = 5;

        static final String DEVICE = PREFIX + "DEVICE";
    }

    public static final class Responses {
        static final int DEVICE_FOUND = 0;
        static final int CONNECTION_SUCCESSFUL = 1;
        static final int SCAN_FINISHED = 2;
        static final int CONNECTION_LOST = 3;
        static final int SERVICES_DISCOVERED = 4;
        static final int READ_ALL_CHARACTERISTICS = Requests.READ_ALL_CHARACTERISTICS;

        static final String DEVICE = Requests.DEVICE;
        static final String SERVICES_LIST = PREFIX + "SERVICES";
    }
}
