package com.projects.dawid.gattclient;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

abstract class BluetoothTask implements Runnable {
    private static String TAG = "BluetoothTask";

    void onResponse(Context context, Intent responseIntent) {
        Log.i(TAG, "Response for task: " + this);
        if (context != null && responseIntent != null)
            LocalBroadcastManager.getInstance(context).sendBroadcast(responseIntent);
        BluetoothTaskManager.getInstance().taskFinished();
    }
}
