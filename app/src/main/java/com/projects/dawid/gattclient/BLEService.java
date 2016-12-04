package com.projects.dawid.gattclient;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * IntentService, that handles Bluetooth LE action for the views.
 * <p>
 * <h>Intent Service for Bluetooth Low energy asynchronous actions</h>
 * <p>
 * <p>This intent service is responsible for handling all low-level BLE actions for the view.</p>
 * <p>
 * <p>Intent, that start this IntentService must have action of BLEService.REQUEST</p>
 * <p>Possible values of requests are:
 * <ul>
 * <li>PERFORM_SERVICE_DISCOVERY</li>
 * <li>PERFORM_SCAN</li>
 * <li>STOP_SCAN</li>
 * <li>CONNECT_GATT</li>
 * <li>DISCONNECT</li>
 * <li>READ_ALL_CHARACTERISTICS</li>
 * </ul>
 * <p>
 * Kind of request must be given as extra like
 * intent.putExtra(BLEService.REQUEST, BLEService.Requests.REQUEST_TYPE);
 * <p>
 * Some of requests require Additional parameters like BLE device. Dor this we put Extra like this:
 * intent.putExtra(BLEService.Requests.DEVICE, bleDevice);
 * </p>
 * <p>
 * <p>Responses from BLEService have action of BLEService.RESPONSE</p>
 * <p>
 * <p>Tyle of response correspond to the type of request</p>
 */
public class BLEService extends IntentService {

    private static final String TAG = "BLEService";
    private static String PREFIX = "com.pilarski.";
    public static final String REQUEST = PREFIX + "REQUEST";
    public static final String RESPONSE = PREFIX + "RESPONSE";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private ServiceLEScanCallback mScanCallback;
    private ServiceGATTCallback mGATTCallback;

    public BLEService() {
        super("BLEService");
    }

    private void initializeBluetooth() {
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (mBluetoothScanner == null)
            mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (mScanCallback == null)
            mScanCallback = new ServiceLEScanCallback(this);

        if (mGATTCallback == null)
            mGATTCallback = ServiceGATTCallback.getInstance(this);


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "OnHandleIntent started!");

        initializeBluetooth();

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
        BluetoothGatt gatt = mGATTCallback.getGattForDevice(device);

        if (device == null || gatt == null) {
            Log.e(TAG, "device or gatt is null");
            return;
        }

        ArrayList<BluetoothGattCharacteristic> characteristics = prepareCharacteristicsList(gatt);
        ArrayList<BluetoothGattDescriptor> descriptors = prepareDescriptorsList(characteristics);

        mGATTCallback.startReadingCharacteristics(device, characteristics, descriptors);
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
        BluetoothGatt gatt = mGATTCallback.getGattForDevice(device);

        if (gatt != null) {
            gatt.discoverServices();
        } else {
            Log.e(TAG, "Gatt is null!");
        }
    }

    private void connectDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(Requests.DEVICE);

        //never set autoconnect on true, as it's known to cause errors on some android devices
        device.connectGatt(this, false, mGATTCallback);
    }

    private void stopLEScan() {
        mBluetoothScanner.stopScan(mScanCallback);
        Intent scanFinishedResponse = new Intent();
        scanFinishedResponse.setAction(RESPONSE);
        scanFinishedResponse.putExtra(RESPONSE, Responses.SCAN_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(scanFinishedResponse);
    }

    private void startLEScan() {
        mBluetoothScanner.startScan(mScanCallback);
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
