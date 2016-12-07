package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity for showing devices discoverable nearby and managing connections.
 */
public class DiscoveredDevicesActivity extends AppCompatActivity {

    private final static int BLE_ENABLED = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectedArrayAdapter mBluetoothListAdapter;
    private ArrayList<BluetoothDeviceAdapter> mBLEDevices = new ArrayList<>();
    private DiscoveredDevicesBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareActivity();
        initializeMembers();

        restartDeviceScan();
    }

    private void initializeMembers() {
        setListView();
        initializeBluetoothAdapter();
        setBroadcastReceiver();
    }

    private void prepareActivity() {
        setContentView(R.layout.activity_discovered_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bluetoothDiscover) {
            restartDeviceScan();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void setBroadcastReceiver() {
        mBroadcastReceiver = new DiscoveredDevicesBroadcastReceiver(this, mBluetoothListAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                DiscoveredDevicesBroadcastReceiver.ResponseIntentFilter);
    }

    private void initializeBluetoothAdapter() {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    private void setListView() {
        final ListView listView = (ListView)findViewById(R.id.BluetoothDevicesViewId);
        mBluetoothListAdapter = new ConnectedArrayAdapter(this, android.R.layout.simple_list_item_1, mBLEDevices);
        listView.setAdapter(mBluetoothListAdapter);
        listView.setOnItemClickListener(new DeviceSelectedAction(this, mBluetoothListAdapter.getConnectedDevices()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BLE_ENABLED && resultCode == RESULT_OK){
            restartDeviceScan();
        }
        else{
            Toast.makeText(this, R.string.BLUETOOTH_NOT_ENABLED, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void restartDeviceScan() {
        clearLocalDevices();
        requestNewScan();
        mBroadcastReceiver.clearCachedDevices();
    }

    private void requestNewScan() {
        showSearchingSnackBar();
        BLEServiceStarter.stopLEScan(this);
        startNewScan();
    }

    private void showSearchingSnackBar() {
        final SnackBarManager snack = new SnackBarManager(this);
        snack.showSearching();
    }

    private void startNewScan() {
        if (activateBluetooth()) return;

        BLEServiceStarter.startLEScan(this);
    }

    private boolean activateBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLE_ENABLED);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        BluetoothTaskManager.getInstance().clear();
        super.onResume();
    }

    private void clearLocalDevices() {
        mBLEDevices.clear();
        mBluetoothListAdapter.clear();
    }
}
