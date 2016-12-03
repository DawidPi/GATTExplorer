package com.projects.dawid.gattclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;

/**
 * Handles displaying Snacks.
 */
class SnackBarManager {
    private Activity mActivity;

    /**
     * Default constructor
     *
     * @param activity activity on whose behalf snack is displayed.
     */
    SnackBarManager(Activity activity) {
        mActivity = activity;
    }

    /**
     * Displays Snack indicating, that BLE is in Scanning mode.
     */
    void showSearching() {
        Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content),
                R.string.SnackbarSearchingForDevices, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction(R.string.SnackbarSearchingForDevicesAction, new SnackBarCallbacks.DismissCallback(snackbar));
        snackbar.setActionTextColor(ContextCompat.getColor(mActivity, R.color.colorDeviceSearchSnackbarAction));
        snackbar.show();
    }

    /**
     * Displays snack indicating, that connection can be established.
     *
     * @param device device to which connection can be established.
     */
    void showConnecting(BluetoothDevice device) {
        Snackbar.make(mActivity.findViewById(android.R.id.content), R.string.SnackbarDisconnectedDeviceText, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.SnackbarDisconnectedDeviceActionText, new SnackBarCallbacks.ConnectDeviceCallback(mActivity, device))
                .setActionTextColor(ContextCompat.getColor(mActivity, R.color.colorDeviceDisconnectedSnackbarAction))
                .show();
    }

    /**
     * Displays snack, indicating, that device is connected and GATT scan can be performed
     *
     * @param device Device with which BLE services discovery can be performed.
     */
    void showDiscoveringServices(BluetoothDevice device) {
        Snackbar.make(mActivity.findViewById(android.R.id.content), R.string.SnackbarConnectedDeviceText, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.SnackbarConnectedDeviceActionText, new SnackBarCallbacks.DiscoverServicesCallback(mActivity, device))
                .setActionTextColor(ContextCompat.getColor(mActivity, R.color.colorDeviceConnectedSnackbarAction))
                .show();
    }
}
