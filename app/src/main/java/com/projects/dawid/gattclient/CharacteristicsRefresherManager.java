package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;


public class CharacteristicsRefresherManager {

    private final Context mActivityContext;
    private final Integer mInterval;
    private BluetoothDevice mDevice;
    private TimerTask mRefreshTask;
    private Timer mTimer = null;
    private boolean mStopTimer = false;

    CharacteristicsRefresherManager(Context activityContext, Integer interval) {
        mActivityContext = activityContext;
        mInterval = interval;
    }

    void startRefreshing(BluetoothDevice device) {
        mDevice = device;

        mRefreshTask = new TimerTask() {
            @Override
            public void run() {
                if (mStopTimer) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                    return;
                }

                if (!BluetoothTaskManager.getInstance().isTaskTypeInQueue(ReadCharacteristicTask.class))
                    BLEServiceStarter.readAllCharacteristics(mActivityContext, mDevice);
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mRefreshTask, mInterval, mInterval);
    }


    void stopRefreshing() {
        mStopTimer = true;
        mDevice = null;
    }

}
