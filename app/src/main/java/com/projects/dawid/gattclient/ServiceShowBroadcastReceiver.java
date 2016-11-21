package com.projects.dawid.gattclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dawid on 21.11.2016.
 */

public class ServiceShowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceShowReceiver";

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

        Integer responseType = intent.getIntExtra(BLEService.RESPONSE, -1);

        switch (responseType) {
            case BLEService.Responses.READ_ALL_CHARACTERISTICS:
                updateActivitiesCharacteristics();
        }
    }

    private void updateActivitiesCharacteristics() {

    }
}
