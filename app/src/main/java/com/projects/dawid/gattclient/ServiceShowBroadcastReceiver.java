package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Broadcast Receiver for ServiceShowActivity.
 */
public class ServiceShowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceShowReceiver";
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    private ServiceShowActivity mActivity;

    ServiceShowBroadcastReceiver(@NonNull ServiceShowActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "intent is null");
            return;
        }

        String action = intent.getAction();

        if (!action.equals(BLEService.RESPONSE)) {
            Log.e(TAG, "Wrong intent action");
            return;
        }

        BLEService.Response responseType = (BLEService.Response) intent.getSerializableExtra(BLEService.RESPONSE);

        switch (responseType) {
            case CHARACTERISTIC_READ:
                updateSingleCharacteristic(intent);
                break;
            case CHARACTERISTIC_UPDATED:
                updateSingleCharacteristic(intent);
                break;
            case CONNECTION_LOST:
                handleLostConnection(intent);
                break;

            default:
                Log.i(TAG, "Task not supported: " + responseType);

        }
    }

    private void handleLostConnection(Intent intent) {
        Log.i(TAG, "Connection lost");
        BluetoothDevice disconnectedDevice = intent.getParcelableExtra(BLEService.DEVICE);
        BluetoothTaskManager.getInstance().clear();
        if (disconnectedDevice.equals(mActivity.getDevice())) {
            Log.i(TAG, "Disconnection of currently viewed device");
            mActivity.stopRefresher();
            BluetoothTaskManager.getInstance().clear();
            mActivity.onBackPressed();
        }
    }

    private void updateSingleCharacteristic(Intent intent) {
        mActivity.updateSingleCharacteristic();
    }
}
