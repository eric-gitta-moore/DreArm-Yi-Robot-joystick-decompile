package com.edu.csuft.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/* loaded from: classes.dex */
public class BluetoothRfcommClient {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_NONE = 0;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private int mState = 0;

    public BluetoothRfcommClient(Context context, Handler handler) {
        this.mHandler = handler;
    }

    private synchronized void setState(int state) {
        this.mState = state;
        this.mHandler.obtainMessage(1, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void start() {
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        setState(0);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (this.mState == 2 && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        this.mConnectThread = new ConnectThread(device);
        this.mConnectThread.start();
        setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        this.mConnectedThread = new ConnectedThread(socket);
        this.mConnectedThread.start();
        Message msg = this.mHandler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        setState(3);
    }

    public synchronized void stop() {
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        setState(0);
    }

    public void write(byte[] out) {
        synchronized (this) {
            if (this.mState == 3) {
                this.mConnectedThread.write(out);
            }
        }
    }

    /* access modifiers changed from: private */
    public void connectionFailed() {
        setState(0);
        Message msg = this.mHandler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Unable to connect device");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }

    /* access modifiers changed from: private */
    public void connectionLost() {
        setState(0);
        Message msg = this.mHandler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }

    /* access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(BluetoothRfcommClient.MY_UUID);
            } catch (IOException e) {
            }
            this.mmSocket = tmp;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            setName("ConnectThread");
            BluetoothRfcommClient.this.mAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
                synchronized (BluetoothRfcommClient.this) {
                    BluetoothRfcommClient.this.mConnectThread = null;
                }
                BluetoothRfcommClient.this.connected(this.mmSocket, this.mmDevice);
            } catch (IOException e) {
                BluetoothRfcommClient.this.connectionFailed();
                try {
                    this.mmSocket.close();
                } catch (IOException e2) {
                }
                BluetoothRfcommClient.this.start();
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            byte[] buffer = new byte[MainActivity.BTB_L1];
            while (true) {
                try {
                    BluetoothRfcommClient.this.mHandler.obtainMessage(2, this.mmInStream.read(buffer), -1, buffer).sendToTarget();
                } catch (IOException e) {
                    BluetoothRfcommClient.this.connectionLost();
                    return;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mmOutStream.write(buffer);
                BluetoothRfcommClient.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
