package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Receives and handles Responses from BLEService regarding device discovery and device connection.
 */
public class DiscoveredDevicesBroadcastReceiver extends BroadcastReceiver {
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    private static String TAG = "DiscoveredReceiver";
    private HashMap<String, BluetoothDevice> mBluetoothDevices = new HashMap<>();
    private ConnectedArrayAdapter mBluetoothDevicesAdapter;
    private Context mActivityContext;

    public DiscoveredDevicesBroadcastReceiver(Context context, ConnectedArrayAdapter devicesAdapter) {
        mBluetoothDevicesAdapter = devicesAdapter;
        mActivityContext = context;
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

            case BLEService.Responses.CONNECTION_LOST:
                notifyOnConnectionLost(intent);
                break;

            case BLEService.Responses.SERVICES_DISCOVERED:
                stopCurrentScan();
                createViewWithServices(intent);
                break;

            case -1:
                Log.e(TAG, "No response type specified!");
                break;
            default:
                Log.d(TAG, "Response type unknown");
        }

    }

    private void stopCurrentScan() {
        Intent stopScanIntent = new Intent(mActivityContext, BLEService.class);
        stopScanIntent.setAction(BLEService.REQUEST);
        stopScanIntent.putExtra(BLEService.REQUEST, BLEService.Requests.STOP_SCAN);

        mActivityContext.startService(stopScanIntent);
    }

    private void createViewWithServices(Intent intent) {
        Log.i(TAG, "creating new activity!");

        BluetoothDevice device = intent.getParcelableExtra(BLEService.Responses.DEVICE);
        ArrayList<BluetoothGattService> services = intent.getParcelableArrayListExtra(BLEService.Responses.SERVICES_LIST);

        Intent serviceShowIntent = new Intent(mActivityContext, ServiceShowActivity.class);
        serviceShowIntent.putExtra(ServiceShowActivity.DEVICE, device);
        ServiceShowActivity.setServicesList(services);
        mActivityContext.startActivity(serviceShowIntent);
    }

    private void notifyOnConnectionLost(Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BLEService.Responses.DEVICE);
            if (device != null) {
                mBluetoothDevices.remove(device.getName());
                mBluetoothDevicesAdapter.remove(new BluetoothDeviceAdapter(device));
                mBluetoothDevicesAdapter.getConnectedDevices().remove(new BluetoothDeviceAdapter(device));
            }
    }

    private void clearDevices() {
        mBluetoothDevices.clear();
        mBluetoothDevicesAdapter.clear();
    }

    private void updateConnectedDevice(Intent intent) {
        BluetoothDevice connectedBTDevice = intent.getParcelableExtra(BLEService.Responses.DEVICE);

        if (connectedBTDevice != null) {
            mBluetoothDevicesAdapter.addConnectedDevice(new BluetoothDeviceAdapter(connectedBTDevice));
            mBluetoothDevicesAdapter.notifyDataSetChanged();
        }
    }

    private void updateDeviceList(Intent responseIntent) {
        BluetoothDevice newDevice = responseIntent.getParcelableExtra(BLEService.Responses.DEVICE);
        if (newDevice == null) {
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

    public void clearCachedDevices() {
        mBluetoothDevices.clear();
    }
}
