package com.projects.dawid.gattclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ServiceShowActivity extends AppCompatActivity {

    private static final String PREFIX = "com.GattClient.Pilarski.";
    public static final String DEVICE = PREFIX + "DEVICE";
    public static final String SERVICE_LIST = PREFIX + "SERVICE_LIST";
    private static final String TAG = "SERVICE_ACTIVITY";

    private BluetoothDevice mBluetoothDevice;
    private ArrayList<BluetoothGattService> mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_show);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mBluetoothDevice = getIntent().getParcelableExtra(DEVICE);
        //todo finish me
//        mServices = getIntent().getParcelableArrayListExtra(SERVICE_LIST);
//
//        for (BluetoothGattService service :
//                mServices) {
//            Log.i(TAG, "service: " + service.getUuid());
//        }
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
