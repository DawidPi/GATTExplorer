package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying and managing services and it's
 * characteristics on one BLE device.
 */
public class ServiceShowActivity extends AppCompatActivity {

    private static final String PREFIX = "com.GattClient.Pilarski.";
    public static final String DEVICE = PREFIX + "DEVICE";
    private static final String TAG = "SERVICE_ACTIVITY";
    private static List<BluetoothGattService> mServices = new ArrayList<>();
    private final List<List<Map<String, String>>> mChildrenData = new ArrayList<>();
    private final List<Map<String, String>> mGroupData = new ArrayList<>();
    private BluetoothDevice mBluetoothDevice;
    private SimpleExpandableListAdapter mExpandableListAdapter;
    private ServiceShowBroadcastReceiver mBroadcastReceiver;
    private CharacteristicsRefresherManager mCharacteristicsRefresher =
            new CharacteristicsRefresherManager(this, 2000);

    /**
     * Sets services lists as services, that should be displayed
     *
     * @param services ArrayList of services, that are to be displayed.
     */
    public static void setServicesList(@NonNull List<BluetoothGattService> services) {
        mServices = services;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_show);

        setupActionBar();

        Log.i(TAG, "On create!");

        mBluetoothDevice = getIntent().getParcelableExtra(DEVICE);
        if (mBluetoothDevice == null)
            Log.e(TAG, "Bluetooth device is null!");
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume!");

        setBroadcastReceiver();

        for (BluetoothGattService service :
                mServices) {
            Log.i(TAG, "service: " + service.getUuid());
        }

        BluetoothTaskManager.getInstance().clear();
        updateCharacteristicsValues();
        fillServicesView();
        startConstantlyRefreshingCharacteristics();
        super.onResume();
    }

    private void startConstantlyRefreshingCharacteristics() {
        mCharacteristicsRefresher = new CharacteristicsRefresherManager(this, 1000);
        mCharacteristicsRefresher.startRefreshing(mBluetoothDevice);
    }

    private void setBroadcastReceiver() {
        Log.i(TAG, "starting broadcastReceiver");
        mBroadcastReceiver = new ServiceShowBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                ServiceShowBroadcastReceiver.ResponseIntentFilter);
    }

    /**
     * Starts request for refreshing characteristics Values.
     */
    public void updateCharacteristicsValues() {
        BLEServiceStarter.readAllCharacteristics(this, mBluetoothDevice);
    }

    private void fillServicesView() {
        Log.i(TAG, "Filling services view");
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.servicesView);
        mExpandableListAdapter = createAdapter();
        updateCharacteristicsView();
        listView.setAdapter(mExpandableListAdapter);
    }

    /**
     * Updates view with Characteristics value.
     */
    public void updateCharacteristicsView() {
        GATTUUIDTranslator translator = new GATTUUIDTranslator();
        mGroupData.clear();
        mChildrenData.clear();

        for (BluetoothGattService service : mServices) {
            Map<String, String> currentServices = new HashMap<>();
            mGroupData.add(currentServices);
            currentServices.put("NAME", translator.standardUUID(service.getUuid()));

            List<Map<String, String>> characteristics = new ArrayList<>();
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                Map<String, String> currentCharacteristicMap = new HashMap<>();
                currentCharacteristicMap.put("NAME", translator.standardUUID(characteristic.getUuid())
                        + ":\n" + CharacteristicValueRepresentation.translateToString(characteristic.getValue()));
                logCharacteristic(characteristic);
                characteristics.add(currentCharacteristicMap);
            }
            mChildrenData.add(characteristics);
        }

        mExpandableListAdapter.notifyDataSetChanged();
    }

    @NonNull
    private SimpleExpandableListAdapter createAdapter() {

        return new SimpleExpandableListAdapter(this, mGroupData,
                android.R.layout.simple_expandable_list_item_1, new String[]{"NAME"},
                new int[]{android.R.id.text1}, mChildrenData, android.R.layout.simple_expandable_list_item_2,
                new String[]{"NAME"}, new int[]{android.R.id.text1});
    }

    private void logCharacteristic(BluetoothGattCharacteristic characteristic) {
        GATTUUIDTranslator translator = new GATTUUIDTranslator();
        Log.d(TAG, "Characteristic " + translator.standardUUID(characteristic.getUuid()));
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_show_menu, menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mBluetoothDevice.getName());
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back Pressed");
        BluetoothTaskManager.getInstance().clear();

        Intent deviceDiscoveryIntent = new Intent(this, DiscoveredDevicesActivity.class);
        deviceDiscoveryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(deviceDiscoveryIntent);
        finish();
    }

    @Override
    protected void onPause() {
        mCharacteristicsRefresher.stopRefreshing();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.backButton) {
            onBackPressed();
        }

        return true;
    }

    public void updateSingleCharacteristic() {
        Log.i(TAG, "Single characteristic update!");
        BluetoothGattCharacteristic newCharacteristic = SingleCharacteristicStaticContainer.getInstance().pullCharacteristic();

        if (newCharacteristic == null) {
            Log.i(TAG, "Characteristic is null. Do nothing");
            return;
        }

        Log.d(TAG, ">>>NEW CHARACTERISTIC<<<");
        logCharacteristic(newCharacteristic);

        BluetoothGattService serviceToRemove = findServiceToSwap(newCharacteristic);

        Log.i(TAG, "appending service");
        if (serviceToRemove != null) {
            int idx = mServices.indexOf(serviceToRemove);
            mServices.set(idx, newCharacteristic.getService());
        } else
            Log.d(TAG, "Service not found");

        updateCharacteristicsView();
    }

    @Nullable
    private BluetoothGattService findServiceToSwap(BluetoothGattCharacteristic newCharacteristic) {
        BluetoothGattService serviceToSwap = null;
        for (BluetoothGattService currentService : mServices) {
            if (currentService.equals(newCharacteristic.getService())) {
                Log.i(TAG, "Service found");
                serviceToSwap = currentService;
            }
        }
        return serviceToSwap;
    }

    public BluetoothDevice getDevice() {
        return mBluetoothDevice;
    }

}
