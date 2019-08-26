package dev.rodni.ru.bluetoothconrepo.broadcastreceivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;

//broadcastReceiver for catching the bluetooth state
public class BroadcastReceiverBTState extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverBTStat";

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
}
