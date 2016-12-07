package com.projects.dawid.gattclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Receives and handles Response from BLEService regarding device discovery and device connection.
 */
public class DiscoveredDevicesBroadcastReceiver extends BroadcastReceiver {
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    private static String TAG = "DiscoveredReceiver";
    private HashMap<String, BluetoothDevice> mBluetoothDevices = new HashMap<>();
    private ConnectedArrayAdapter mBluetoothDevicesAdapter;
    private Activity mActivityContext;

    public DiscoveredDevicesBroadcastReceiver(Activity context, ConnectedArrayAdapter devicesAdapter) {
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

        BLEService.Response responseType = (BLEService.Response) intent.getSerializableExtra(BLEService.RESPONSE);

        switch (responseType) {
            case DEVICE_FOUND:
                updateDeviceList(intent);
                break;

            case CONNECTION_SUCCESSFUL:
                updateConnectedDevice(intent);
                break;

            case SCAN_FINISHED:
                clearDevices();
                break;

            case CONNECTION_LOST:
                notifyOnConnectionLost(intent);
                break;

            case SERVICES_DISCOVERED:
                BLEServiceStarter.stopLEScan(mActivityContext);
                createViewWithServices(intent);
                break;

            default:

                final BluetoothTask currentTask = BluetoothTaskManager.getInstance().getCurrentTask();
                if (currentTask != null)
                    currentTask.onResponse(null, null);
                Log.d(TAG, "Response type unknown" + responseType);
        }

    }

    private void createViewWithServices(Intent intent) {
        Log.i(TAG, "creating new activity!");

        BluetoothDevice device = intent.getParcelableExtra(BLEService.DEVICE);
        List<BluetoothGattService> services = CharacteristicsStaticContainer.getInstance().pullCharacteristics();

        Intent serviceShowIntent = new Intent(mActivityContext, ServiceShowActivity.class);
        serviceShowIntent.putExtra(ServiceShowActivity.DEVICE, device);
        ServiceShowActivity.setServicesList(services);
        try {
            mActivityContext.unregisterReceiver(this);
        } catch (Exception e) {
            Log.i(TAG, "unregister receiver exception");
        }
        mActivityContext.finish();
        mActivityContext.startActivity(serviceShowIntent);
    }

    private void notifyOnConnectionLost(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BLEService.DEVICE);
            if (device != null) {
                BluetoothTaskManager.getInstance().clear();
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
        BluetoothDevice connectedBTDevice = intent.getParcelableExtra(BLEService.DEVICE);

        if (connectedBTDevice != null) {
            mBluetoothDevicesAdapter.addConnectedDevice(new BluetoothDeviceAdapter(connectedBTDevice));
            mBluetoothDevicesAdapter.notifyDataSetChanged();
        }
    }

    private void updateDeviceList(Intent responseIntent) {
        BluetoothDevice newDevice = responseIntent.getParcelableExtra(BLEService.DEVICE);
        if (newDevice == null) {
            return;
        }

        addFilteredDevice(newDevice);
    }

    private void addFilteredDevice(BluetoothDevice newDevice) {
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
