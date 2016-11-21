package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceShowActivity extends AppCompatActivity {

    private static final String PREFIX = "com.GattClient.Pilarski.";
    public static final String DEVICE = PREFIX + "DEVICE";
    public static final String SERVICE_LIST = PREFIX + "SERVICE_LIST";
    private static final String TAG = "SERVICE_ACTIVITY";
    public static final String ACTION = TAG;
    private static List<BluetoothGattService> mServices;
    private BluetoothDevice mBluetoothDevice;
    private ExpandableListAdapter mExpandableListAdapter;


    public static void setServicesList(ArrayList<BluetoothGattService> services) {
        mServices = services;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_show);

        setupActionBar();
        mBluetoothDevice = getIntent().getParcelableExtra(DEVICE);

        for (BluetoothGattService service :
                mServices) {
            Log.i(TAG, "service: " + service.getUuid());
        }

        askForReadingAllCharacteristics();
        fillServicesView();
    }

    private void askForReadingAllCharacteristics() {
        Intent registerDeviceNotificationsIntent = new Intent(this, BLEService.class);
        registerDeviceNotificationsIntent.setAction(BLEService.REQUEST);
        registerDeviceNotificationsIntent.putExtra(BLEService.REQUEST, BLEService.Requests.READ_ALL_CHARACTERISTICS);
        registerDeviceNotificationsIntent.putExtra(BLEService.Requests.DEVICE, mBluetoothDevice);
        startService(registerDeviceNotificationsIntent);
    }

    private void fillServicesView() {
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.servicesView);
        mExpandableListAdapter = prepareListAdapter();
        listView.setAdapter(mExpandableListAdapter);
    }

    private ExpandableListAdapter prepareListAdapter() {
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childrenData = new ArrayList<>();
        GATTUUIDTranslator translator = new GATTUUIDTranslator();

        for (BluetoothGattService service : mServices) {
            Map<String, String> currentServices = new HashMap<>();
            groupData.add(currentServices);
            currentServices.put("NAME", translator.standardUUID(service.getUuid()));

            List<Map<String, String>> characteristics = new ArrayList<>();
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                Map<String, String> currentCharacteristicMap = new HashMap<>();
                currentCharacteristicMap.put("NAME", translator.standardUUID(characteristic.getUuid()));
                logCharacteristic(characteristic);
                //currentCharacteristicMap.put("NAME1", characteristic.getStringValue(0));
                characteristics.add(currentCharacteristicMap);
            }
            childrenData.add(characteristics);
        }

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groupData,
                android.R.layout.simple_expandable_list_item_1, new String[]{"NAME"},
                new int[]{android.R.id.text1}, childrenData, android.R.layout.simple_expandable_list_item_2,
                new String[]{"NAME"}, new int[]{android.R.id.text1});

        return adapter;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.backButton) {
            onBackPressed();
        }

        return true;
    }
}
