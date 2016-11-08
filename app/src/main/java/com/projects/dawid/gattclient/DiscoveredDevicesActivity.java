package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
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

        setListView();
        updateBluetoothHelpers();
        setSwitchListener();
        setLoadingStatus(false);
        setBroadcastReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving instance");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BT_DEVICES_KEY, mBLEDevices);
        outState.putParcelable(BT_CONNECTED_KEY, mBluetoothListAdapter.getConnectedDevice());
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

    private void setBroadcastReceiver() {
        BLEServiceBroadcastReceiver serviceReceiver = new BLEServiceBroadcastReceiver(mBluetoothListAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver,
                BLEServiceBroadcastReceiver.ResponseIntentFilter);
    }

    private void setLoadingStatus(boolean status) {
        if(status)
            findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.loadingCircle).setVisibility(View.GONE);
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

    private void setSwitchListener() {
        final Switch viewSwitch = (Switch)findViewById(R.id.ScanSwitchId);
        viewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean scanningON) {
                if(compoundButton == viewSwitch){
                    if (scanningON){
                        activateBluetoothAndStartScanning();
                    }
                    else{
                        stopScanning();
                    }
                }
            }
        });
    }

    private void stopScanning() {
        Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show();
    }

    private void activateBluetoothAndStartScanning() {
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLE_ENABLED);
            return;
        }

        startScanning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BLE_ENABLED && resultCode == RESULT_OK){
            startScanning();
        }
        else{
            Toast.makeText(this, R.string.BLUETOOTH_NOT_ENABLED, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startScanning() {
        Intent bleIntent = new Intent(this, BLEService.class);
        bleIntent.setAction(BLEService.REQUEST);
        bleIntent.putExtra(BLEService.REQUEST, BLEService.Requests.PERFORM_SCAN);
        startService(bleIntent);
    }

    @Override
    protected void onPause() {
        Intent serviceIntent = new Intent(this, BLEService.class);
        stopService(serviceIntent);

        super.onPause();
    }

}
