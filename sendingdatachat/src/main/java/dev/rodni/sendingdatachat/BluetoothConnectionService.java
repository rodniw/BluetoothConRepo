package dev.rodni.sendingdatachat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "MYAPP";

    //took this from google samples
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private ProgressDialog progressDialog;

    private BluetoothDevice device;
    private UUID deviceUUID;

    //threads
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;

    //set this properties through the constructor
    private final BluetoothAdapter bluetoothAdapter;
    private Context context;

    public BluetoothConnectionService(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        //the local server socket
        private final BluetoothServerSocket bluetoothServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                //create a new listening server socket
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: setting up a server socket with " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: " + e.getMessage());
            }
            bluetoothServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread running.");

            BluetoothSocket bluetoothSocket = null;

            try{
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                //wait until a connection is made
                bluetoothSocket = bluetoothServerSocket.accept();
                //if a connection is successful then i will see this log output
                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            //checking for bluetoothSocket nullability
            //if (bluetoothSocket != null) {
            //    connected(bluetoothSocket, device);
            //}
        }

        //this cancel method is simply close server socket
        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            device = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = device.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            bluetoothSocket = tmp;

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                bluetoothSocket.connect();

                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket
                try {
                    bluetoothSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }
            //connected(bluetoothSocket, device);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread();
            insecureAcceptThread.start();
        }
    }

    /**
     AcceptThread starts and sits waiting for a connection.
     Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/
    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        //initprogress dialog
        progressDialog = ProgressDialog.show(context,"Connecting Bluetooth"
                ,"Please Wait...",true);

        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }
}
