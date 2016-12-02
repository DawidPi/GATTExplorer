package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dawid on 21.11.2016.
 */

public class ServiceShowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceShowReceiver";
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    Timer timer = new Timer();
    boolean timerIsRunning = false;
    boolean refreshingFinished = false;
    private ServiceShowActivity mActivity;
    private TimerTask mUpdateCharacteristicsTask = new TimerTask() {
        @Override
        public void run() {
            Log.d(TAG, "Timer task started!");
            if (refreshingFinished) {
                mActivity.updateCharacteristicsValues();
                refreshingFinished = false;
            }
        }
    };

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

        Integer responseType = intent.getIntExtra(BLEService.RESPONSE, -1);

        switch (responseType) {
            case BLEService.Responses.READ_ALL_CHARACTERISTICS:
                updateActivitiesCharacteristics(intent);
        }
    }

    private void updateActivitiesCharacteristics(Intent intent) {
        Log.i(TAG, "All characteristics updated response");
        ArrayList<BluetoothGattService> services = CharacteristicsStaticContainer.getInstance().pullCharacteristics();
        ServiceShowActivity.setServicesList(services);
        mActivity.updateCharacteristicsView();

        if (!timerIsRunning)
            startUpdateCharacteristicsTimer();

        refreshingFinished = true;
    }

    private void startUpdateCharacteristicsTimer() {
        Log.d(TAG, "Characteristics timer started!");
        timer.schedule(mUpdateCharacteristicsTask, 500, 500);
        timerIsRunning = true;
    }
}
