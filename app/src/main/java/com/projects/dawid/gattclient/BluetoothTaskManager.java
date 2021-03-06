package com.projects.dawid.gattclient;


import android.util.Log;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

class BluetoothTaskManager {
    private static final String TAG = "TaskManager";
    private static BluetoothTaskManager mSingleton = null;
    private Queue<BluetoothTask> mTasksQueue = new ArrayDeque<>();
    private BluetoothTask mCurrentTask = null;
    private int mFailedAttempts = 0;
    private TimerTask mExecuteTask = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "Queue size: " + mTasksQueue.size());
            if (!checkRunTaskPrecoditions()) {
                Log.i(TAG, "Preconditions not fulfilled to run next task");
                selfRecovery();
                return;
            }

            BluetoothTask task = mTasksQueue.poll();
            Log.i(TAG, "Starting next task " + task);
            mCurrentTask = task;
            task.run();
        }
    };

    private BluetoothTaskManager() {
        Timer tryDoTaskTimer = new Timer();
        tryDoTaskTimer.schedule(mExecuteTask, 500, 500);
    }

    static BluetoothTaskManager getInstance() {
        if (mSingleton == null)
            mSingleton = new BluetoothTaskManager();

        return mSingleton;
    }

    private void selfRecovery() {
        if (mTasksQueue.isEmpty()) {
            mFailedAttempts = 0;
            return;
        }

        mFailedAttempts++;
        if (mFailedAttempts > 10) {
            Log.e(TAG, "Queue stuck. Cleaning");
            clear();
            mFailedAttempts = 0;
        }
    }

    void append(BluetoothTask task) {
        Log.i(TAG, "Appending task");
        mTasksQueue.add(task);
    }

    void tryExecute() {
        mExecuteTask.run();
    }

    private boolean isTaskPending() {
        if (mCurrentTask == null) {
            Log.i(TAG, "Task is not pending");
            return false;
        }

        Log.i(TAG, "Task is pending");
        return true;
    }

    private boolean checkRunTaskPrecoditions() {
        if (isTaskPending()) {
            Log.i(TAG, "Task: " + mCurrentTask + "is pending, cannot execute another one");
            return false;
        }

        if (mTasksQueue.isEmpty()) {
            Log.i(TAG, "Tasks queue is empty. Cannot execute task");
            return false;
        }

        if (mTasksQueue.peek() == null) {
            Log.e(TAG, "Task is null. Cannot execute");
            return false;
        }

        return true;
    }

    void taskFinished() {
        Log.i(TAG, "Task " + mCurrentTask + "finished. Queue size" + mTasksQueue.size());
        mFailedAttempts = 0;
        mCurrentTask = null;
        mExecuteTask.run();
    }

    BluetoothTask getCurrentTask() {
        return mCurrentTask;
    }

    boolean isTaskTypeInQueue(Class type) {

        for (BluetoothTask task : mTasksQueue) {
            if (type.isInstance(task))
                return true;
        }

        return false;
    }

    void clear() {
        mTasksQueue.clear();
        mCurrentTask = null;
    }
}
