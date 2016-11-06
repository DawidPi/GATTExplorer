package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import java.util.ArrayList;

public class DiscoveredDevicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovered_devices);

        setListView();
        setLeCallback();
        updateBluetoothHelpers();
        setSwitchListener();
        setLoadingStatus(false);
    }

    private void setLoadingStatus(boolean status) {
        if(status)
            findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.loadingCircle).setVisibility(View.GONE);
    }

    private void setLeCallback() {
        mLECallback = new LeScanCallback(this, mBluetoothListAdapter);
    }

    private void updateBluetoothHelpers() {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
    }

    private void setListView() {
        final ListView listView = (ListView)findViewById(R.id.BluetoothDevicesViewId);
        mBluetoothListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mBLEDevices);
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
        Toast.makeText(this, "Scanning started", Toast.LENGTH_SHORT).show();
        mBluetoothScanner.startScan(mLECallback);
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;

    private ArrayAdapter<BluetoothDeviceAdapter> mBluetoothListAdapter;
    private ArrayList<BluetoothDeviceAdapter> mBLEDevices = new ArrayList<>();
    private LeScanCallback mLECallback;
    private final static int BLE_ENABLED = 1;

}
