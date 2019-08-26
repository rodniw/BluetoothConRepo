package dev.rodni.ru.bluetoothconrepo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

    private BluetoothAdapter bluetoothAdapter;
    private TextView startButton, stopButton, startDiscoveringButton;

    //creating a broadcast broadcastReceiver for catching the bluetooth state
    private final BroadcastReceiver broadcastReceiverBTState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            // when discovery finds a device
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Log.d(TAG, "onReceive: action equals ACTION_STATE_CHANGED");
                switch (state) {
                    case STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    case STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE_TURNING_OFF");
                        break;
                    case STATE_ON:
                        Log.d(TAG, "onReceive: STATE_ON");
                        break;
                    case STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE_TURNING_ON");
                        break;
                }
            }
        }
    };

    //creating a broadcast broadcastReceiver for catching the bluetooth state
    private final BroadcastReceiver broadcastReceiverBTDiscover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            // when discovery finds a device
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                Log.d(TAG, "onReceive: action equals ACTION_STATE_CHANGED");
                switch (mode) {
                    case SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onReceive: Discoverability enabled.");
                        break;
                    case SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "onReceive: Discoverability disabled. Able to receive connections.");
                        break;
                    case SCAN_MODE_NONE:
                        Log.d(TAG, "onReceive: Discoverability disabled. Not able to receive connections.");
                        break;
                    case STATE_CONNECTING:
                        Log.d(TAG, "onReceive: Connecting.");
                        break;
                    case STATE_CONNECTED:
                        Log.d(TAG, "onReceive: Connected.");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_btn);
        stopButton = findViewById(R.id.stop_button);
        startDiscoveringButton = findViewById(R.id.start_BT_discovering_button);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        startButton.setOnClickListener(view -> enableBluetooth());

        stopButton.setOnClickListener(view -> disableBluetooth());

        startDiscoveringButton.setOnClickListener(view -> startConnectionsDiscovery());
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

    //by calling this method a device will start discovering for connections with other devices
    private void startConnectionsDiscovery() {
        Log.d(TAG, "startConnectionsDiscovery: called");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        createAndRegBluetoothIntent(broadcastReceiverBTDiscover, BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
    }

    //method that send broadcast about bluetooth connectivity
    //made this method more flexible for my examples
    private void createAndRegBluetoothIntent(BroadcastReceiver receiver, String state) {
        Log.d(TAG, "createAndRegBluetoothIntent: called");
        IntentFilter bluetoothIntent = new IntentFilter(state);
        registerReceiver(receiver, bluetoothIntent);
    }

    //unregister my receiver inside on destroy
    //maybe its better to do it inside onStop ------ FIND INFO ABOUT IT
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        unregisterReceiver(broadcastReceiverBTState);
    }
}
