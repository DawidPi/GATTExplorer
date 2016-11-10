package com.projects.dawid.gattclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.Set;

class ItemSelectedAction implements AdapterView.OnItemClickListener {
    private final Activity mActivity;
    private final Context mContext;
    private final Set<BluetoothDeviceAdapter> mConnectedDevices;
    private String TAG = "ITEM SELECTED";

    ItemSelectedAction(Context context, Activity view, Set<BluetoothDeviceAdapter> connectedDevices) {
        mContext = context;
        mActivity = view;
        mConnectedDevices = connectedDevices;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BluetoothDeviceAdapter bluetoothDeviceAdapter= (BluetoothDeviceAdapter)adapterView.getItemAtPosition(position);
        BluetoothDevice bluetoothDevice = bluetoothDeviceAdapter.getBluetoothDevice();

        Log.i(TAG, "Device selected: " + bluetoothDevice.getName());

        ConnectedArrayAdapter adapter = (ConnectedArrayAdapter) adapterView.getAdapter();
        adapter.setSelectedDevice(bluetoothDeviceAdapter);
        adapter.notifyDataSetChanged();
        createSnackBar(bluetoothDevice);
    }

    private void createSnackBar(BluetoothDevice device) {

        if (deviceIsConnected(device)) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), R.string.SnackbarConnectedDeviceText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.SnackbarConnectedDeviceActionText, new SnackBarCallbacks.DiscoverServicesCallback(mActivity, device))
                    .setActionTextColor(ContextCompat.getColor(mContext, R.color.colorDeviceConnectedSnackbarAction))
                    .show();
        } else {
            Snackbar.make(mActivity.findViewById(android.R.id.content), R.string.SnackbarDisconnectedDeviceText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.SnackbarDisconnectedDeviceActionText, new SnackBarCallbacks.ConnectDeviceCallback(mActivity, device))
                    .setActionTextColor(ContextCompat.getColor(mContext, R.color.colorDeviceDisconnectedSnackbarAction))
                    .show();
        }
    }

    private boolean deviceIsConnected(BluetoothDevice device) {
        for (BluetoothDeviceAdapter deviceAdapter :
                mConnectedDevices) {
            if (deviceAdapter.toString().equals(new BluetoothDeviceAdapter(device).toString())) {
                return true;
            }
        }

        return false;
    }


    private void connectDevice(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(mContext, BLEService.class);
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Requests.CONNECT_GATT);
        intent.putExtra(BLEService.Requests.DEVICE, bluetoothDevice);
        mContext.startService(intent);
    }
}
