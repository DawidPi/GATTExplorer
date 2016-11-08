package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;


public class LeScanCallback extends ScanCallback {
    private DiscoveredDevicesActivity mContext;
    private HashSet<BluetoothDeviceAdapter> mBluetoothDevicesSet = new HashSet<>();
    private ArrayAdapter<BluetoothDeviceAdapter> mAdapter;

    public LeScanCallback(DiscoveredDevicesActivity context, ArrayAdapter<BluetoothDeviceAdapter> devices) {
        super();

        mContext = context;
        mAdapter = devices;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addDevice(result.getDevice());
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult singleResult : results) {
            addDevice(singleResult.getDevice());
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        Toast.makeText(mContext, "Scan failed with err: " + errorCode, Toast.LENGTH_SHORT).show();
    }

    private void addDevice(BluetoothDevice newDevice){
        if(mBluetoothDevicesSet.contains(new BluetoothDeviceAdapter(newDevice)))
            return;

        mBluetoothDevicesSet.add(new BluetoothDeviceAdapter(newDevice));
        mAdapter.add(new BluetoothDeviceAdapter(newDevice));
    }
}
