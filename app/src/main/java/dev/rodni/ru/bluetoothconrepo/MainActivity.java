package dev.rodni.ru.bluetoothconrepo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private TextView startButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_btn);
        stopButton = findViewById(R.id.stop_button);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        startButton.setOnClickListener(view -> {
            enableBluetooth();
        });

        stopButton.setOnClickListener(view -> {
            disableBluetooth();
        });
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableBluetooth: Does not have capabilities.");
        }
        if (bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            createAndRegBluetoothIntent();
        }
    }

    private void disableBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

            createAndRegBluetoothIntent();
        }
    }

    private void createAndRegBluetoothIntent() {
        IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(BTBroadcastReceiver, bluetoothIntent);
    }
}
