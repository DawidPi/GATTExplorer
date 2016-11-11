package com.projects.dawid.gattclient;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
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

                case Requests.DISCONNECT:
                    disconnectDevices();
                    break;

                case Requests.PERFORM_SERVICE_DISCOVERY:
                    discoverServices(intent);
                    break;

                case -1:
                    Log.e(TAG, "No request sent!");
                default:
                    Log.e(TAG, "Unknown request type!");
            }
        }
    }

    private void discoverServices(Intent intent) {
        Log.i(TAG, "Notifying about services");
        BluetoothDevice device = intent.getParcelableExtra(Requests.DEVICE);
        BluetoothGatt gatt = mGATTCallback.getGattForDevice(device);

        if (gatt != null) {
            gatt.discoverServices();
        } else {
            Log.e(TAG, "Gatt is null!");
        }
    }

    private void disconnectDevices() {
        //todo implement
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
        public static final int PERFORM_SERVICE_DISCOVERY = 0;
        public static final int PERFORM_SCAN = 1;
        public static final int STOP_SCAN = 2;
        public static final int CONNECT_GATT = 3;
        public static final int DISCONNECT = 4;

        public static final String DEVICE = PREFIX + "DEVICE";
    }

    public static final class Responses {
        public static final int DEVICE_FOUND = 0;
        public static final int CONNECTION_SUCCESSFUL = 1;
        public static final int SCAN_FINISHED = 2;
        public static final int CONNECTION_LOST = 3;
        public static final int SERVICES_DISCOVERED = 4;

        public static final String DEVICE = Requests.DEVICE;
        public static final String SERVICES_LIST = PREFIX + "SERVICES";
    }
}
