package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        outState.putParcelable(BT_CONNECTED_KEY, mBluetoothListAdapter.getConnectedDevice());
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Restoring instance");
        super.onRestoreInstanceState(savedInstanceState);
        mBLEDevices = savedInstanceState.getParcelableArrayList(BT_DEVICES_KEY);
        BluetoothDeviceAdapter connectedDeviceAdapter = savedInstanceState.getParcelable(BT_CONNECTED_KEY);
        mBluetoothListAdapter = new ConnectedArrayAdapter(this, android.R.layout.simple_list_item_1, mBLEDevices);
        mBluetoothListAdapter.setConnectedDevice(connectedDeviceAdapter);
        ((ListView) findViewById(R.id.BluetoothDevicesViewId)).setAdapter(mBluetoothListAdapter);
        mBluetoothListAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setBroadcastReceiver() {
        BLEServiceBroadcastReceiver serviceReceiver = new BLEServiceBroadcastReceiver(this, mBluetoothListAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver,
                BLEServiceBroadcastReceiver.ResponseIntentFilter);
    }

    private void updateBluetoothHelpers() {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    private void setListView() {
        final ListView listView = (ListView)findViewById(R.id.BluetoothDevicesViewId);
        mBluetoothListAdapter = new ConnectedArrayAdapter(this, android.R.layout.simple_list_item_1, mBLEDevices);
        listView.setAdapter(mBluetoothListAdapter);
        listView.setOnItemClickListener(new ItemSelectedAction(this));
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

    @Override
    protected void onPause() {
        Intent serviceIntent = new Intent(this, BLEService.class);
        stopService(serviceIntent);

        super.onPause();
    }

}
