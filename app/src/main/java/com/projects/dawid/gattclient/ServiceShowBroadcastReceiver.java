package com.projects.dawid.gattclient;

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
            case CHARACTERISTIC_UPDATED:
                updateSingleCharacteristic(intent);

        }
    }

    private void updateSingleCharacteristic(Intent intent) {
        mActivity.updateSingleCharacteristic();
    }
}
