package com.projects.dawid.gattclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import java.util.Set;

/**
 * Callback for clicking device on Discovered devices View.
 */
class DeviceSelectedAction implements AdapterView.OnItemClickListener {
    private final Activity mActivity;
    private final Set<BluetoothDeviceAdapter> mConnectedDevices;

    DeviceSelectedAction(Activity view, Set<BluetoothDeviceAdapter> connectedDevices) {
        mActivity = view;
        mConnectedDevices = connectedDevices;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BluetoothDeviceAdapter bluetoothDeviceAdapter= (BluetoothDeviceAdapter)adapterView.getItemAtPosition(position);
        BluetoothDevice bluetoothDevice = bluetoothDeviceAdapter.getBluetoothDevice();

        updateDevicesAdapter(adapterView, bluetoothDeviceAdapter);
        createSnackBar(bluetoothDevice);
    }

    private void updateDevicesAdapter(AdapterView<?> adapterView, BluetoothDeviceAdapter bluetoothDeviceAdapter) {
        ConnectedArrayAdapter adapter = (ConnectedArrayAdapter) adapterView.getAdapter();
        adapter.setSelectedDevice(bluetoothDeviceAdapter);
        adapter.notifyDataSetChanged();
    }

    private void createSnackBar(BluetoothDevice device) {
        final SnackBarManager snack = new SnackBarManager(mActivity);

        if (deviceIsConnected(device))
            snack.showDiscoveringServices(device);
        else
            snack.showConnecting(device);
    }

    private boolean deviceIsConnected(@NonNull BluetoothDevice device) {
        for (BluetoothDeviceAdapter deviceAdapter : mConnectedDevices) {
            if (deviceAdapter.toString().equals(new BluetoothDeviceAdapter(device).toString())) {
                return true;
            }
        }

        return false;
    }
}
