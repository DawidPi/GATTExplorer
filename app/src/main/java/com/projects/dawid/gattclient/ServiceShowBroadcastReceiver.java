package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Broadcast Receiver for ServiceShowActivity.
 */
public class ServiceShowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceShowReceiver";
    public static IntentFilter ResponseIntentFilter = new IntentFilter(BLEService.RESPONSE);
    Timer mTimer = new Timer();
    boolean mTimerIsRunning = false;
    boolean mRefreshingFinished = false;
    private ServiceShowActivity mActivity;

    private TimerTask mUpdateCharacteristicsTask = new CharacteristicsUpdateTimerTask();
    private boolean mKillTimer = false;

    private class CharacteristicsUpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "Timer task started!");

            if (mKillTimer) {
                Log.i(TAG, "Killing timer");
                mTimer.cancel();
                mTimer.purge();
                mTimer = new Timer();
                mUpdateCharacteristicsTask = new CharacteristicsUpdateTimerTask();
                mKillTimer = false;
                mTimerIsRunning = false;
                return;
            }

            if (mTimerIsRunning) {
                if (mRefreshingFinished) {
                    Log.i(TAG, "starting to read all characteristic once again");
                    mActivity.updateCharacteristicsValues();
                    mRefreshingFinished = false;
                }
            }
        }
    }

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

            case BLEService.Responses.CHARACTERISTIC_UPDATED:
                updateSingleCharacteristic(intent);

        }
    }

    private void updateSingleCharacteristic(Intent intent) {
        mActivity.updateSingleCharacteristic();
    }

    private void updateActivitiesCharacteristics(Intent intent) {
        Log.i(TAG, "All characteristics updated response");
        List<BluetoothGattService> services = CharacteristicsStaticContainer.getInstance().pullCharacteristics();
        ServiceShowActivity.setServicesList(services);
        mActivity.updateCharacteristicsView();

        if (!mTimerIsRunning)
            startUpdateCharacteristicsTimer();

        mRefreshingFinished = true;
    }

    private void startUpdateCharacteristicsTimer() {
        Log.d(TAG, "Characteristics mTimer started!");
        mTimer.schedule(mUpdateCharacteristicsTask, 1000, 1000);
        mTimerIsRunning = true;
    }

    private class CharacteristicsUpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "Timer task started!");

            if (mKillTimer) {
                Log.i(TAG, "Killing timer");
                mTimer.cancel();
                mTimer.purge();
                mTimer = new Timer();
                mUpdateCharacteristicsTask = new CharacteristicsUpdateTimerTask();
                mKillTimer = false;
                mTimerIsRunning = false;
                return;
            }

            if (mTimerIsRunning) {
                if (mRefreshingFinished) {
                    Log.i(TAG, "starting to read all characteristic once again");
                    mActivity.updateCharacteristicsValues();
                    mRefreshingFinished = false;
                }
            }
        }
    }

    public void stopListeningToCharacteristics() {
        Log.i(TAG, "Cancelling timer!");
        mKillTimer = true;
        Log.i(TAG, "killing timer!");

    }
}
