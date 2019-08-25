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

import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private Button startButton, stopButton;

    //creating a broadcast broadcastReceiver
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            // when discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_btn);
        stopButton = findViewById(R.id.stop_button);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        startButton.setOnClickListener(view -> enableBluetooth());

        stopButton.setOnClickListener(view -> disableBluetooth());
    }

    //method which switch on the bluetooth
    private void enableBluetooth() {
        Log.d(TAG, "enableBluetooth: called");
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableBluetooth: Does not have capabilities.");
        }
        if (bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            createAndRegBluetoothIntent();
        }
    }

    //method which switch off the bluetooth
    private void disableBluetooth() {
        Log.d(TAG, "disableBluetooth: called");
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

            createAndRegBluetoothIntent();
        }
    }

    //method that send broadcast about bluetooth connectivity
    private void createAndRegBluetoothIntent() {
        Log.d(TAG, "createAndRegBluetoothIntent: called");
        IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, bluetoothIntent);
    }

    //unregister my receiver inside on destroy
    //maybe its better to do it inside onStop ------ FIND INFO ABOUT IT
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
