package com.projects.dawid.gattclient;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

abstract class BluetoothTask implements Runnable {
    void onResponse(Context context, Intent responseIntent) {
        if (context != null && responseIntent != null)
            LocalBroadcastManager.getInstance(context).sendBroadcast(responseIntent);
        BluetoothTaskManager.getInstance().taskFinished(this);
    }
}
