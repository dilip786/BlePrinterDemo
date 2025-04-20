package com.android.print.demo.service;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import android.os.Handler;
import android.util.Log;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class WiFiPortService implements IPrinterPort {
    private static String TAG = "WifiPrinter";
    private String address;
    private int port;
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private WiFiPortService.ConnectThread mConnectThread;
    private Handler mHandler;
    private int mState;
    private int readLen;

    public WiFiPortService(String ipAddress, int portNumber, Handler handler) {
        this.address = ipAddress;
        this.port = portNumber;
        this.mHandler = handler;
        this.mState = 103;
    }

    public void open() {
        Utils.Log(TAG, "open connect to: " + this.address);
        if (this.mState != 103) {
            this.close();
        }

        Log.v("wifitest", "enter");
        this.mConnectThread = new WiFiPortService.ConnectThread((WiFiPortService.ConnectThread)null);
        this.mConnectThread.start();
    }

    public void close() {
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
            }

            if (this.inputStream != null) {
                this.inputStream.close();
            }

            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        this.outputStream = null;
        this.inputStream = null;
        this.mSocket = null;
        this.mConnectThread = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {
        try {
            if (this.outputStream != null) {
                this.outputStream.write(data);
                this.outputStream.flush();
                return 0;
            } else {
                return -1;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
            return -1;
        }
    }

    public byte[] read() {
        byte[] readBuff = null;
        int firstByte = 0;

        try {
            if (this.inputStream != null) {
                firstByte = this.inputStream.read();
                if (firstByte == -1) {
                    this.setState(103);
                    return null;
                }
            }

            if (this.inputStream != null && (this.readLen = this.inputStream.available()) > 0) {
                readBuff = new byte[this.readLen];
                this.inputStream.read(readBuff);
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        if (firstByte > 0) {
            if (readBuff == null) {
                return new byte[]{(byte)firstByte};
            }

            byte[] by = new byte[readBuff.length + 1];
            by[0] = (byte)firstByte;
            System.arraycopy(readBuff, 0, by, 1, readBuff.length);
            readBuff = by;
        }

        Log.w(TAG, "read length:" + this.readLen);
        return readBuff;
    }

    public int read1() {
        int readValue = -1;

        try {
            readValue = this.inputStream.read();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return readValue;
    }

    private synchronized void setState(int state) {
        Utils.Log(TAG, "setState() " + this.mState + " -> " + state);
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

    public Boolean isServerClose() {
        try {
            this.mSocket.sendUrgentData(255);
            return false;
        } catch (Exception var2) {
            return true;
        }
    }

    private class ConnectThread extends Thread {
        private ConnectThread(ConnectThread connectThread) {
        }

        public void run() {
            boolean hasError = true;
            InetSocketAddress mSocketAddress = new InetSocketAddress(WiFiPortService.this.address, WiFiPortService.this.port);

            try {
                WiFiPortService.this.mSocket = new Socket();
                WiFiPortService.this.mSocket.setSoTimeout(5000);
                WiFiPortService.this.mSocket.connect(mSocketAddress, 5000);
                WiFiPortService.this.outputStream = WiFiPortService.this.mSocket.getOutputStream();
                WiFiPortService.this.inputStream = WiFiPortService.this.mSocket.getInputStream();
                hasError = false;
            } catch (SocketException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }

            synchronized(this) {
                WiFiPortService.this.mConnectThread = null;
            }

            if (hasError) {
                WiFiPortService.this.setState(102);
                WiFiPortService.this.close();
                Log.v("wifitest", "failed");
            } else {
                WiFiPortService.this.setState(101);
                Log.v("wifitest", "success");
            }

        }
    }
}

