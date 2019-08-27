package dev.rodni.ru.bluetoothconrepo.broadcastreceivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.rodni.ru.bluetoothconrepo.MainActivity;

//broadcastReceiver for catching the bluetooth state
public class BroadcastReceiverDeviceFounder extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverDevice";

    private List<BluetoothDevice> devices = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        // when discovery finds a device
        if (action != null && action.equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(TAG, "onReceive: BluetoothDevice.EXTRA_DEVICE");
            MainActivity.addDeviceToListDevices(device);
            Log.d(TAG, "onReceive: " + Arrays.toString(device.getUuids()) + "\n" + device.getAddress());
        }
    }
}
