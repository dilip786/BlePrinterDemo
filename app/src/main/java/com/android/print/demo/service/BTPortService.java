package com.android.print.demo.service;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Build.VERSION;
import android.util.Log;
import com.android.print.demo.util.PrinterInstance;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

public class BTPortService implements IPrinterPort {
    private static final String TAG = "BTPortService";
    private BluetoothDevice mDevice;
    private static String mDeviceName;
    private static String mDeviceAddress;
    private static BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private BTPortService.ConnectThread mConnectThread;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private Context mContext;
    private Handler mHandler;
    private int mState;
    private static int readLen;
    private final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private PrinterInstance mPrinter;
    private BroadcastReceiver boundDeviceReceiver;

    public BTPortService(Context context, BluetoothDevice device, Handler handler) {
        //this.boundDeviceReceiver = new NamelessClass_1();
        this.mHandler = handler;
        this.mDevice = device;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = 103;
        this.mContext = context;
    }

    public BTPortService() {
       //this.boundDeviceReceiver = new NamelessClass_1();
    }



    public BTPortService(Context context, String address, Handler handler) {
        class NamelessClass_1 extends BroadcastReceiver {
            NamelessClass_1() {
            }

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                    BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if (!BTPortService.this.mDevice.equals(device)) {
                        return;
                    }

                    switch(device.getBondState()) {
                        case 10:
                            BTPortService.this.mContext.unregisterReceiver(BTPortService.this.boundDeviceReceiver);
                            BTPortService.this.setState(102);
                            Utils.Log("BluetoothPort", "bound cancel");
                            break;
                        case 11:
                            Utils.Log("BluetoothPort", "bounding......");
                            break;
                        case 12:
                            Utils.Log("BluetoothPort", "bound success");
                            BTPortService.this.mContext.unregisterReceiver(BTPortService.this.boundDeviceReceiver);
                            BTPortService.this.PairOrConnect(false);
                    }
                }

            }
        }

        this.boundDeviceReceiver = new NamelessClass_1();
        this.mHandler = handler;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevice = this.mAdapter.getRemoteDevice(address);
        this.mState = 103;
        this.mContext = context;
    }

    public void open() {
        Utils.Log("BluetoothPort", "connect to: " + this.mDevice.getName());
        if (this.mState != 103) {
            this.close();
        }

        if (this.mDevice.getBondState() == 10) {
            Log.i("BluetoothPort", "device.getBondState() is BluetoothDevice.BOND_NONE");
            this.PairOrConnect(true);
        } else if (this.mDevice.getBondState() == 12) {
            this.PairOrConnect(false);
        }

    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mContext.registerReceiver(this.boundDeviceReceiver, boundFilter);
            boolean success = false;

            try {
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                success = (Boolean)createBondMethod.invoke(this.mDevice);
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            } catch (IllegalArgumentException var6) {
                var6.printStackTrace();
            } catch (InvocationTargetException var7) {
                var7.printStackTrace();
            } catch (NoSuchMethodException var8) {
                var8.printStackTrace();
            }

            Log.i("BluetoothPort", "createBond is success? : " + success);
        } else {
            this.mConnectThread = new BTPortService.ConnectThread((BTPortService.ConnectThread)null);
            this.mConnectThread.start();
        }

    }

    @TargetApi(10)
    private boolean ReTryConnect() {
        Utils.Log("BluetoothPort", "android SDK version is:" + VERSION.SDK_INT);

        try {
            if (VERSION.SDK_INT >= 10) {
                mSocket = this.mDevice.createInsecureRfcommSocketToServiceRecord(this.PRINTER_UUID);
            } else {
                Method method = this.mDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE);
                mSocket = (BluetoothSocket)method.invoke(this.mDevice, 1);
            }

            mSocket.connect();
            return false;
        } catch (Exception var2) {
            Utils.Log("BluetoothPort", "connect failed:");
            var2.printStackTrace();
            return true;
        }
    }

    public void close() {
        Utils.Log("BluetoothPort", "close()");

        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException var2) {
            Utils.Log("BluetoothPort", "close socket failed");
            var2.printStackTrace();
        }

        this.mConnectThread = null;
        this.mDevice = null;
        mSocket = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {
        try {
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
                return 0;
            } else {
                return -1;
            }
        } catch (IOException var3) {
            Utils.Log("BluetoothPort", "write error.");
            var3.printStackTrace();
            return -1;
        }
    }

    public byte[] read() {
        byte[] readBuff = null;

        try {
            if (inputStream != null && (readLen = inputStream.available()) > 0) {
                readBuff = new byte[readLen];
                inputStream.read(readBuff);
            }
        } catch (IOException var3) {
            Utils.Log("BluetoothPort", "read error");
            var3.printStackTrace();
        }

        Log.w("BluetoothPort", "read length:" + readLen);
        return readBuff;
    }

    public static synchronized byte[] read(int timeout) {
        byte[] receiveBytes = null;

        try {
            do {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                if ((readLen = inputStream.available()) > 0) {
                    break;
                }

                timeout -= 50;
            } while(timeout > 0);

            if (readLen > 0) {
                receiveBytes = new byte[readLen];
                inputStream.read(receiveBytes);
            }
        } catch (IOException var4) {
            Utils.Log("BluetoothPort", "read error1");
            var4.printStackTrace();
        }

        return receiveBytes;
    }

    private synchronized void setState(int state) {
        Utils.Log("BluetoothPort", "setState() " + this.mState + " -> " + state);
        if (this.mState != state) {
            this.mState = state;
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(this.mState).sendToTarget();
            }
        }

    }

    public int getState() {
        return this.mState;
    }

    public PrinterInstance btAutoConn(Context context, BluetoothAdapter adapter, Handler mHandler) {
        Properties pro = Utils.getBtConnInfo(context);
        mDeviceAddress = pro.getProperty("mac");
        if (mDeviceAddress != null && !mDeviceAddress.equals("")) {
            Log.v("mac", mDeviceAddress);
            this.mDevice = adapter.getRemoteDevice(mDeviceAddress);
            mDeviceName = this.mDevice.getName();
            this.mPrinter = new PrinterInstance(context, this.mDevice, mHandler);
            this.mPrinter.openConnection();
            Log.v("btport", "open-success!");
            return this.mPrinter;
        } else {
            return null;
        }
    }

    public PrinterInstance btConnnect(Context context, String mac, BluetoothAdapter adapter, Handler mHandler) {
        this.mDevice = adapter.getRemoteDevice(mac);
        this.mPrinter = new PrinterInstance(context, this.mDevice, mHandler);
        this.mPrinter.openConnection();
        Log.v("btport", "open-success!");
        return this.mPrinter;
    }

    public static byte getData(int time) {
        byte data = 0;

        try {
            InputStream inputStream = mSocket.getInputStream();

            for(int j = 0; j < 5; ++j) {
                if (inputStream.available() != 0) {
                    byte[] b = new byte[inputStream.available()];
                    int i = inputStream.read(b);
                    if (j != 0) {
                        data = b[i - 1];
                        break;
                    }
                }

                Thread.sleep((long)time);
                write1(new byte[]{16, 4, 2});
            }
        } catch (Exception var6) {
            Log.e("TAG", var6.toString());
        }

        return data;
    }

    public static byte getEndData(int time) {
        byte ret = 0;

        try {
            InputStream inputStream = mSocket.getInputStream();

            for(int j = 0; j < 5; ++j) {
                if (inputStream.available() != 0) {
                    byte[] b = new byte[inputStream.available()];
                    int i = inputStream.read(b);
                    if (j != 0) {
                        ret = b[i - 1];
                        break;
                    }
                }

                Thread.sleep((long)time);
                write1(new byte[]{27, 118});
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return ret;
    }

    public static int write1(byte[] data) {
        try {
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
                return 0;
            } else {
                return -1;
            }
        } catch (IOException var2) {
            Utils.Log("BluetoothPort", "write error.");
            var2.printStackTrace();
            return -1;
        }
    }

    public static String getmDeviceName() {
        return mDeviceName;
    }

    public static void setmDeviceName(String mDeviceName) {
    }

    public static String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public static void setmDeviceAddress(String mDeviceAddress) {
    }

    private class ConnectThread extends Thread {
        private ConnectThread(ConnectThread connectThread) {
        }

        public void run() {
            boolean hasError = false;
            BTPortService.this.mAdapter.cancelDiscovery();

            try {
                BTPortService.mSocket = BTPortService.this.mDevice.createRfcommSocketToServiceRecord(BTPortService.this.PRINTER_UUID);
                BTPortService.mSocket.connect();
            } catch (IOException var5) {
                Utils.Log("BluetoothPort", "ConnectThread failed. retry.");
                var5.printStackTrace();
                hasError = BTPortService.this.ReTryConnect();
            }

            synchronized(this) {
                BTPortService.this.mConnectThread = null;
            }

            if (!hasError) {
                try {
                    BTPortService.inputStream = BTPortService.mSocket.getInputStream();
                    BTPortService.outputStream = BTPortService.mSocket.getOutputStream();
                } catch (IOException var3) {
                    hasError = true;
                    Utils.Log("BluetoothPort", "Get Stream failed");
                    var3.printStackTrace();
                }
            }

            if (hasError) {
                BTPortService.this.setState(102);
                BTPortService.this.close();
            } else {
                BTPortService.this.setState(101);
            }

        }
    }
}

