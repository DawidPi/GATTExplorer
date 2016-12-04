package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

/**
 * Class, that makes BLEService interface available.
 */
abstract class BLEServiceStarter {

    /**
     * Start LE scan.
     *
     * @param context activity on which behalf service is to be started.
     */
    static void startLEScan(Context context) {
        Intent newScanRequest = new Intent(context, BLEService.class);
        newScanRequest.setAction(BLEService.REQUEST);
        newScanRequest.putExtra(BLEService.REQUEST, BLEService.Requests.PERFORM_SCAN);
        context.startService(newScanRequest);
    }

    /**
     * Stop LE scan.
     *
     * @param context context
     *                on which behalf service is to be started.
     */
    static void stopLEScan(Context context) {
        Intent stopScanRequest = new Intent(context, BLEService.class);
        stopScanRequest.setAction(BLEService.REQUEST);
        stopScanRequest.putExtra(BLEService.REQUEST, BLEService.Requests.STOP_SCAN);
        context.startService(stopScanRequest);
    }

    /**
     * Connects to the specified device through BLE.
     *
     * @param context context
     *                on which behalf service is to be started
     * @param device  device to connect.
     */
    static void connectDevice(Context context, BluetoothDevice device) {
        Intent intent = new Intent(context, BLEService.class);
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Requests.CONNECT_GATT);
        intent.putExtra(BLEService.Requests.DEVICE, device);
        context.startService(intent);
    }
}
