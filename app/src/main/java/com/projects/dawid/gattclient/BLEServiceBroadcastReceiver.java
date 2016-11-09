package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;

public class BLEServiceBroadcastReceiver extends BroadcastReceiver {
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    private static String TAG = "Broadcast Receiver";
    private HashMap<String, BluetoothDevice> mBluetoothDevices = new HashMap<>();
    private ConnectedArrayAdapter mBluetoothDevicesAdapter;
    private Context mActivityContext;

    public BLEServiceBroadcastReceiver(Context context, ConnectedArrayAdapter devicesAdapter) {
        mBluetoothDevicesAdapter = devicesAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Null intent");
            return;
        }

        String action = intent.getAction();
        if (!action.equals(BLEService.RESPONSE)) {
            Log.e(TAG, "Wrong intent action");
            return;
        }

        int responseType = intent.getIntExtra(BLEService.RESPONSE, -1);

        switch (responseType) {
            case BLEService.Responses.DEVICE_FOUND:
                updateDeviceList(intent);
                break;

            case BLEService.Responses.CONNECTION_SUCCESSFUL:
                updateConnectedDevice(intent);
                break;

            case BLEService.Responses.SCAN_FINISHED:
                clearDevices();
                break;

            case -1:
                Log.e(TAG, "No response type specified!");
                break;
            default:
                Log.e(TAG, "Response type unknown");
        }

    }

    private void clearDevices() {
        mBluetoothDevices.clear();
        mBluetoothDevicesAdapter.clear();
    }

    private void updateConnectedDevice(Intent intent) {
        Log.i(TAG, "Updating connected Device");

        BluetoothDevice connectedBTDevice = intent.getParcelableExtra(BLEService.Responses.DEVICE);

        if (connectedBTDevice != null) {
            mBluetoothDevicesAdapter.setConnectedDevice(new BluetoothDeviceAdapter(connectedBTDevice));
            mBluetoothDevicesAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Connected device is null!");
        }
    }

    private void updateDeviceList(Intent responseIntent) {
        Log.i(TAG, "Updating device list");

        BluetoothDevice newDevice = responseIntent.getParcelableExtra(BLEService.Responses.DEVICE);
        if (newDevice == null) {
            Log.e(TAG, "New bluetooth device is null");
            return;
        }

        // some devices tend to change their address, but leave device name
        // this is used to update values in Map, but not to modify
        // user's view
        if (!mBluetoothDevices.containsValue(newDevice)) {
            if (!mBluetoothDevices.containsKey(newDevice.getName())) {
                addNewDevice(newDevice);
            } else {
                mBluetoothDevicesAdapter.remove(new
                        BluetoothDeviceAdapter(mBluetoothDevices.get(newDevice.getName())));
                addNewDevice(newDevice);
            }
        }
    }

    private void addNewDevice(BluetoothDevice newDevice) {
        mBluetoothDevices.put(newDevice.getName(), newDevice);
        mBluetoothDevicesAdapter.add(new BluetoothDeviceAdapter(newDevice));
    }
}
