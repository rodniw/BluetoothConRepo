package dev.rodni.ru.bluetoothconrepo.broadcastreceivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_NONE;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;

//broadcastReceiver for catching the bluetooth state
public class BroadcastReceiverBTVisibility extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverBTVisi";

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
}
