package com.projects.dawid.gattclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

/**
 * Created by Dawid on 10.11.2016.
 */

public abstract class SnackBarCallbacks {

    public static class DiscoverServicesCallback implements View.OnClickListener {
        private final BluetoothDevice mDevice;
        private final Activity mActivity;
        private String TAG = "DiscoverServices";

        DiscoverServicesCallback(Activity activity, BluetoothDevice deviceToDiscoverServices) {
            mActivity = activity;
            mDevice = deviceToDiscoverServices;
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "Discover services for device: " + mDevice);
            Intent intent = new Intent(mActivity, BLEService.class);
            intent.setAction(BLEService.REQUEST);
            intent.putExtra(BLEService.REQUEST, BLEService.Requests.PERFORM_SERVICE_DISCOVERY);
            intent.putExtra(BLEService.Requests.DEVICE, mDevice);
            mActivity.startService(intent);
        }
    }

    public static class ConnectDeviceCallback implements View.OnClickListener {
        private final BluetoothDevice mDevice;
        private final Activity mActivity;

        ConnectDeviceCallback(Activity activity, BluetoothDevice deviceToDiscoverServices) {
            mActivity = activity;
            mDevice = deviceToDiscoverServices;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mActivity, BLEService.class);
            intent.setAction(BLEService.REQUEST);
            intent.putExtra(BLEService.REQUEST, BLEService.Requests.CONNECT_GATT);
            intent.putExtra(BLEService.Requests.DEVICE, mDevice);
            mActivity.startService(intent);
        }
    }

    public static class DisconnectDeviceCallback implements View.OnClickListener {
        private final BluetoothDevice mDevice;
        private final Activity mActivity;

        DisconnectDeviceCallback(Activity activity, BluetoothDevice deviceToDiscoverServices) {
            mActivity = activity;
            mDevice = deviceToDiscoverServices;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mActivity, BLEService.class);
            intent.setAction(BLEService.REQUEST);
            intent.putExtra(BLEService.REQUEST, BLEService.Requests.DISCONNECT);
            intent.putExtra(BLEService.Requests.DEVICE, mDevice);
            mActivity.startService(intent);
        }
    }

    public static class DismissCallback implements View.OnClickListener {


        private final Snackbar mSnackBar;

        DismissCallback(Snackbar snackbar) {
            mSnackBar = snackbar;
        }

        @Override
        public void onClick(View view) {
            mSnackBar.dismiss();
        }
    }
}
