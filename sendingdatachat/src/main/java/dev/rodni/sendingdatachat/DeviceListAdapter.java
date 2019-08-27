package dev.rodni.sendingdatachat;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> devices;
    private int  viewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, List<BluetoothDevice> devices){
        super(context, tvResourceId,devices);
        this.devices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = tvResourceId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        view = layoutInflater.inflate(viewResourceId, null);

        BluetoothDevice device = devices.get(position);

        if (device != null) {
            TextView deviceName = view.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = view.findViewById(R.id.tvDeviceAddress);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }

        return view;
    }

}
