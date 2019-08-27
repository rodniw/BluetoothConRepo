package dev.rodni.ru.bluetoothconrepo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.rodni.ru.bluetoothconrepo.broadcastreceivers.BroadcastReceiverBTState;
import dev.rodni.ru.bluetoothconrepo.broadcastreceivers.BroadcastReceiverBTVisibility;
import dev.rodni.ru.bluetoothconrepo.broadcastreceivers.BroadcastReceiverDeviceFounder;

import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_NONE;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9000;

    private BluetoothAdapter bluetoothAdapter;
    private TextView startButton, stopButton, makeVisibilityForDiscoverButton, startDiscoveringDevices;
    private ListView listView;

    //list of devices to connect with
    private static List<BluetoothDevice> BTDevices = new ArrayList<>();

    //for the list view
    private ListView listDevices;
    private DeviceListAdapter adapter;

    private boolean isLocationPermissionGranted = false;

    //creating a broadcast broadcastReceiver for catching the bluetooth state
    private final BroadcastReceiverBTState broadcastReceiverBTState = new BroadcastReceiverBTState();

    //creating a broadcast broadcastReceiver for catching the bluetooth state
    private final BroadcastReceiverBTVisibility broadcastReceiverBTVisibility = new BroadcastReceiverBTVisibility();

    //creating a broadcast broadcastReceiver for catching the bluetooth state
    private final BroadcastReceiverDeviceFounder broadcastReceiverDeviceFounder = new BroadcastReceiverDeviceFounder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_btn);
        stopButton = findViewById(R.id.stop_button);
        makeVisibilityForDiscoverButton = findViewById(R.id.make_BT_visibility_to_discoverable_button);
        startDiscoveringDevices = findViewById(R.id.discover_devices_button);
        listDevices = findViewById(R.id.devices_list_view);

        adapter = new DeviceListAdapter(this, R.layout.item_device_list, BTDevices);
        listDevices.setAdapter(adapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkPermissionsGranted();

        startButton.setOnClickListener(view -> enableBluetooth());
        stopButton.setOnClickListener(view -> disableBluetooth());
        makeVisibilityForDiscoverButton.setOnClickListener(view -> makeVisibleToDiscovery());
        startDiscoveringDevices.setOnClickListener(view -> discoverDevices());
    }

    //method which switch on the bluetooth
    private void enableBluetooth() {
        Log.d(TAG, "enableBluetooth: called");
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableBluetooth: Does not have capabilities.");
        }
        if (bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableBluetooth: bluetoothAdapter.isEnabled()");
            createAndRegBluetoothIntent(broadcastReceiverBTState, BluetoothAdapter.ACTION_STATE_CHANGED);
        } else {
            //if the bluetooth adapter is not enabled then switch it on.
            bluetoothAdapter.enable();
        }

    }

    //method which switch off the bluetooth
    private void disableBluetooth() {
        Log.d(TAG, "disableBluetooth: called");
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

            createAndRegBluetoothIntent(broadcastReceiverBTState, BluetoothAdapter.ACTION_STATE_CHANGED);
        }
    }

    //method which starts searching for devices to connect with
    private void discoverDevices() {
        Log.d(TAG, "discoverDevices: looking for unpaired devices.");

        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "discoverDevices: canceling discovering devices");
            bluetoothAdapter.cancelDiscovery();

            if (isLocationPermissionGranted) {
                bluetoothAdapter.startDiscovery();
                createAndRegBluetoothIntent(broadcastReceiverDeviceFounder, BluetoothDevice.ACTION_FOUND);
            }
        } else if (!bluetoothAdapter.isDiscovering()) {
            if (isLocationPermissionGranted) {
                bluetoothAdapter.startDiscovery();
                createAndRegBluetoothIntent(broadcastReceiverDeviceFounder, BluetoothDevice.ACTION_FOUND);
            }
        }
    }

    //by calling this method a device will start discovering for connections with other devices
    private void makeVisibleToDiscovery() {
        Log.d(TAG, "makeVisibleToDiscovery: called");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        createAndRegBluetoothIntent(broadcastReceiverBTVisibility, BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
    }

    //method that send broadcast about bluetooth connectivity
    //made this method more flexible for my examples
    private void createAndRegBluetoothIntent(BroadcastReceiver receiver, String state) {
        Log.d(TAG, "createAndRegBluetoothIntent: called");
        IntentFilter bluetoothIntent = new IntentFilter(state);
        registerReceiver(receiver, bluetoothIntent);
    }

    private void checkPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
        } else {
            Log.d(TAG, "requestPermissions: ");
            //this requestPermissions shows us a dialog which asks for the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        isLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
            }
        }
    }

    public static void addDeviceToListDevices(BluetoothDevice device) {
        BTDevices.add(device);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothAdapter.cancelDiscovery();
        //unregister my receiver inside onStop
        unregisterReceiver(broadcastReceiverBTState);
        unregisterReceiver(broadcastReceiverBTVisibility);
        unregisterReceiver(broadcastReceiverDeviceFounder);
    }

}
