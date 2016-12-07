package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

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
        BluetoothTask startLEDiscovery = new DeviceDiscoveryTask(context);
        BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();
        taskManager.append(startLEDiscovery);
        taskManager.tryExecute();
    }

    /**
     * Stop LE scan.
     *
     * @param context context
     *                on which behalf service is to be started.
     */
    static void stopLEScan(Context context) {
        BluetoothTask stopLEDiscovery = new StopDeviceDiscoveryTask(context);
        BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();
        taskManager.append(stopLEDiscovery);
        taskManager.tryExecute();
    }

    /**
     * Connects to the specified device through BLE.
     *
     * @param context context
     *                on which behalf service is to be started
     * @param device  device to connect.
     */
    static void connectDevice(Context context, BluetoothDevice device) {
        BluetoothTask connectDevice = new ConnectBLETask(context, device);
        BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();
        taskManager.append(connectDevice);
        taskManager.tryExecute();
    }

    static void serviceDiscovery(Context context, BluetoothDevice device) {
        BluetoothTask serviceDiscovery = new ServiceDiscoveryTask(context, device);
        BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();
        taskManager.append(serviceDiscovery);
        taskManager.tryExecute();
    }

    static void readAllCharacteristics(Context context, BluetoothDevice device) {
        setAllCharacteristicsNotifications(context, device);
        Runnable readAllCharacteristics = new ReadAllCharacteristicsTask(context, device);
        readAllCharacteristics.run();
    }

    private static void setAllCharacteristicsNotifications(Context context, BluetoothDevice device) {
        Runnable allNotificationsOnTask = new SetAllNotificationsTask(context, device);
        allNotificationsOnTask.run();
    }

    static void disconnectDevice(Context context, BluetoothDevice device) {
        BluetoothTask disconnectDevice = new DisconnectTask(context, device);
        BluetoothTaskManager taskManager = BluetoothTaskManager.getInstance();
        taskManager.append(disconnectDevice);
        taskManager.tryExecute();
    }
}
