package com.android.print.demo.service;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.util.Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

@TargetApi(12)
public class USBPortService implements IPrinterPort {
    private static final String TAG = "USBPrinter";
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private static OutputStream outputStream;
    private boolean isOldUSB;
    private Handler mHandler;
    private int mState;
    private Context mContext;
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    private USBPortService.ConnectThread mConnectThread;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.w("USBPrinter", "receiver action: " + action);
            if ("com.android.usb.USB_PERMISSION".equals(action)) {
                synchronized(this) {
                    USBPortService.this.mContext.unregisterReceiver(USBPortService.this.mUsbReceiver);
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false) && USBPortService.this.mUsbDevice.equals(device)) {
                        USBPortService.this.connect();
                    } else {
                        USBPortService.this.setState(102);
                        Log.e("USBPrinter", "permission denied for device " + device);
                    }
                }
            }

        }
    };

    @SuppressLint("WrongConstant")
    public USBPortService(Context context, UsbDevice usbDevice, Handler handler) {
        this.mContext = context;
        this.mUsbManager = (UsbManager)this.mContext.getSystemService("usb");
        this.mUsbDevice = usbDevice;
        this.mHandler = handler;
        this.mState = 103;
    }

    public void open() {
        Log.d("USBPrinter", "connect to: " + this.mUsbDevice.getDeviceName());
        if (this.mState != 103) {
            this.close();
        }

        if (isUsbPrinter(this.mUsbDevice)) {
            if (this.mUsbManager.hasPermission(this.mUsbDevice)) {
                this.connect();
            } else {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.usb.USB_PERMISSION"), 0);
                IntentFilter filter = new IntentFilter("com.android.usb.USB_PERMISSION");
                this.mContext.registerReceiver(this.mUsbReceiver, filter);
                this.mUsbManager.requestPermission(this.mUsbDevice, pendingIntent);
            }
        } else {
            this.setState(102);
        }

    }

    private void connect() {
        this.mConnectThread = new USBPortService.ConnectThread((USBPortService.ConnectThread)null);
        this.mConnectThread.start();
    }

    public void close() {
        Utils.Log("USBPrinter", "close()");
        if (this.connection != null) {
            this.connection.releaseInterface(this.usbInterface);
            this.connection.close();
            this.connection = null;
        }

        this.mConnectThread = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {

        if (this.connection == null) {
            return -1;
        } else if (data.length < 64) {
            return this.connection.bulkTransfer(this.outEndpoint, data, data.length, 60000);
        } else {
            int sentLength = 0;

            try {
                byte[] buff = new byte[64];
                ByteArrayInputStream bis = new ByteArrayInputStream(data);

                byte[] realData;
                int length;
                for(sentLength = 0; (length = bis.read(buff)) != -1; sentLength += this.connection.bulkTransfer(this.outEndpoint, realData, realData.length, 60000)) {
                    realData = new byte[length];
                    System.arraycopy(buff, 0, realData, 0, length);
                }

                bis.close();
            } catch (Exception var7) {
            }

            return sentLength;
        }
    }

    public byte[] read() {
        if (this.connection != null) {
            byte[] retData = new byte[64];
            int readLen = this.connection.bulkTransfer(this.inEndpoint, retData, retData.length, 60000);
            Log.w("USBPrinter", "read length:" + readLen);
            if (readLen > 0) {
                if (readLen == 64) {
                    return retData;
                }

                byte[] realData = new byte[readLen];
                System.arraycopy(retData, 0, realData, 0, readLen);
                return realData;
            }
        }

        return null;
    }

    public boolean isOldUSB() {
        return this.isOldUSB;
    }

    public static boolean isUsbPrinter(UsbDevice device) {
        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        Utils.Log("USBPrinter", "device name: " + device.getDeviceName());
        Utils.Log("USBPrinter", "vid:" + vendorId + " pid:" + productId);
        return true;
    }

    private synchronized void setState(int state) {
        Utils.Log("USBPrinter", "setState() " + this.mState + " -> " + state);
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

    private class ConnectThread extends Thread {
        private ConnectThread(ConnectThread connectThread) {
        }

        public void run() {
            boolean hasError = true;
            if (USBPortService.this.mUsbManager.hasPermission(USBPortService.this.mUsbDevice)) {
                try {
                    USBPortService.this.usbInterface = USBPortService.this.mUsbDevice.getInterface(0);

                    for(int i = 0; i < USBPortService.this.usbInterface.getEndpointCount(); ++i) {
                        UsbEndpoint ep = USBPortService.this.usbInterface.getEndpoint(i);
                        if (ep.getType() == 2) {
                            if (ep.getDirection() == 0) {
                                USBPortService.this.outEndpoint = ep;
                            } else {
                                USBPortService.this.inEndpoint = ep;
                            }
                        }
                    }

                    USBPortService.this.connection = USBPortService.this.mUsbManager.openDevice(USBPortService.this.mUsbDevice);
                    if (USBPortService.this.connection != null && USBPortService.this.connection.claimInterface(USBPortService.this.usbInterface, true)) {
                        hasError = false;
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

            synchronized(this) {
                USBPortService.this.mConnectThread = null;
            }

            if (hasError) {
                USBPortService.this.setState(102);
                USBPortService.this.close();
            } else {
                USBPortService.this.setState(101);
            }

        }
    }
}

