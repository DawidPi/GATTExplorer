package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DiscoveredDevicesActivity extends AppCompatActivity {

    private final static int BLE_ENABLED = 1;
    private final String TAG = "ACTIVITY";
    private String BT_DEVICES_KEY = "com.Pilarski.BT_DEVICES";
    private String BT_CONNECTED_KEY = "com.Pilarski.BT_CONNECTED";
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectedArrayAdapter mBluetoothListAdapter;
    private ArrayList<BluetoothDeviceAdapter> mBLEDevices = new ArrayList<>();
    private BLEServiceBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovered_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setListView();
        updateBluetoothHelpers();
        setBroadcastReceiver();

        clearDevices();
        requestNewScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving instance");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BT_DEVICES_KEY, mBLEDevices);
        Parcelable[] connectedDevices = new Parcelable[mBluetoothListAdapter.getConnectedDevices().size()];
        logDevices(connectedDevices);
        mBluetoothListAdapter.getConnectedDevices().toArray(connectedDevices);
        outState.putParcelableArray(BT_CONNECTED_KEY, connectedDevices);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Restoring instance");
        super.onRestoreInstanceState(savedInstanceState);
        mBLEDevices = savedInstanceState.getParcelableArrayList(BT_DEVICES_KEY);
        Log.i(TAG, ">>> Devices <<<");
        for (BluetoothDeviceAdapter device : mBLEDevices) {
            Log.i(TAG, "Device: " + device.toString());
        }
        Parcelable[] connectedDevices = savedInstanceState.getParcelableArray(BT_CONNECTED_KEY);
        mBluetoothListAdapter.addAll(mBLEDevices);
        ((ListView) findViewById(R.id.BluetoothDevicesViewId)).setAdapter(mBluetoothListAdapter);
        logDevices(connectedDevices);

        mBluetoothListAdapter.notifyDataSetChanged();
    }

    private void logDevices(Parcelable[] connectedDevices) {
        Log.i(TAG, ">>> DEVICES <<<");
        for (Parcelable device :
                connectedDevices) {
            Log.i(TAG, "Device: " + device.toString());
            mBluetoothListAdapter.addConnectedDevice((BluetoothDeviceAdapter) device);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bluetoothDiscover) {
            clearDevices();
            requestNewScan();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setBroadcastReceiver() {
        mBroadcastReceiver = new BLEServiceBroadcastReceiver(this, mBluetoothListAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                BLEServiceBroadcastReceiver.ResponseIntentFilter);
    }

    private void updateBluetoothHelpers() {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    private void setListView() {
        final ListView listView = (ListView)findViewById(R.id.BluetoothDevicesViewId);
        mBluetoothListAdapter = new ConnectedArrayAdapter(this, android.R.layout.simple_list_item_1, mBLEDevices);
        listView.setAdapter(mBluetoothListAdapter);
        listView.setOnItemClickListener(new ItemSelectedAction(this, this, mBluetoothListAdapter.getConnectedDevices()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BLE_ENABLED && resultCode == RESULT_OK){
            clearDevices();
        }
        else{
            Toast.makeText(this, R.string.BLUETOOTH_NOT_ENABLED, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearDevices() {
        clearLocalDevices();
        requestNewScan();
        mBroadcastReceiver.clearCachedDevices();
    }

    private void requestNewScan() {
        stopCurrentScan();
        startNewScan();
    }

    private void startNewScan() {
        if (activateBluetooth()) return;

        Intent newScanRequest = new Intent(this, BLEService.class);
        newScanRequest.setAction(BLEService.REQUEST);
        newScanRequest.putExtra(BLEService.REQUEST, BLEService.Requests.PERFORM_SCAN);
        startService(newScanRequest);
    }

    private boolean activateBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLE_ENABLED);
            return true;
        }
        return false;
    }

    private void stopCurrentScan() {
        Intent stopScanRequest = new Intent(this, BLEService.class);
        stopScanRequest.setAction(BLEService.REQUEST);
        stopScanRequest.putExtra(BLEService.REQUEST, BLEService.Requests.STOP_SCAN);
        startService(stopScanRequest);
    }

    private void clearLocalDevices() {
        mBLEDevices.clear();
        mBluetoothListAdapter.clear();

    }

    private void disconnectDevices() {
        Intent intent = new Intent(this, BLEService.class);
        intent.setAction(BLEService.REQUEST);
        intent.putExtra(BLEService.REQUEST, BLEService.Requests.DISCONNECT);
        startService(intent);
    }

    @Override
    protected void onPause() {
        //disconnectDevices();
        //clearDevices();
        //Intent serviceIntent = new Intent(this, BLEService.class);
        //should I stop the service?

        super.onPause();
    }

}
