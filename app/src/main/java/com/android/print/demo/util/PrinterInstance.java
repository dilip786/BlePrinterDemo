package com.android.print.demo.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Handler;

import com.android.print.demo.service.USBPortService;
import com.android.print.demo.service.WiFiPortService;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.LabelPrint;
import com.android.print.sdk.Table;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.android.print.sdk.util.Command;
import com.android.print.sdk.util.Utils;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PrinterInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    public static boolean DEBUG = true;
    private static String TAG = "USBPrinterSInstance";
    private IPrinterPort myPrinter;
    private String charsetName = "gbk";
    private final String SDK_VERSION = "3.0";
    private int sendSleep = 250;

    public PrinterInstance(Context context, BluetoothDevice bluetoothDevice, Handler handler) {
        this.myPrinter = new BluetoothPort(context, bluetoothDevice, handler);
    }

    public PrinterInstance(Context context, UsbDevice usbDevice, Handler handler) {
        this.myPrinter = new USBPortService(context, usbDevice, handler);
        this.sendSleep = 10;
    }

    public PrinterInstance(String ipAddress, int portNumber, Handler handler) {
        this.myPrinter = new WiFiPortService(ipAddress, portNumber, handler);
    }

    public String getEncoding() {
        return this.charsetName;
    }

    public void setEncoding(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getSDK_Vesion() {
        return "3.0";
    }

    public boolean isConnected() {
        return this.myPrinter.getState() == 101;
    }

    public void openConnection() {
        this.myPrinter.open();
    }

    public void closeConnection() {
        this.myPrinter.close();
    }

    public int printText(String content) {
        byte[] data = null;

        try {
            if (this.charsetName != "") {
                data = content.getBytes(this.charsetName);
            } else {
                data = content.getBytes();
            }
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return this.sendByteData(data);
    }

    public int printTextinRussian(String content) {
        Map map = new HashMap();
        map.put(1040, -128);
        map.put(1041, -127);
        map.put(1042, -126);
        map.put(1043, -125);
        map.put(1044, -124);
        map.put(1045, -123);
        map.put(1046, -122);
        map.put(1047, -121);
        map.put(1048, -120);
        map.put(1049, -119);
        map.put(1050, -118);
        map.put(1051, -117);
        map.put(1052, -116);
        map.put(1053, -115);
        map.put(1054, -114);
        map.put(1055, -113);
        map.put(1056, -112);
        map.put(1057, -111);
        map.put(1058, -110);
        map.put(1059, -109);
        map.put(1060, -108);
        map.put(1061, -107);
        map.put(1062, -106);
        map.put(1063, -105);
        map.put(1064, -104);
        map.put(1065, -103);
        map.put(1066, -102);
        map.put(1067, -101);
        map.put(1068, -100);
        map.put(1069, -99);
        map.put(1070, -98);
        map.put(1071, -97);
        map.put(1072, -96);
        map.put(1073, -95);
        map.put(1074, -94);
        map.put(1075, -93);
        map.put(1076, -92);
        map.put(1077, -91);
        map.put(1078, -90);
        map.put(1079, -89);
        map.put(1080, -88);
        map.put(1081, -87);
        map.put(1082, -86);
        map.put(1083, -85);
        map.put(1084, -84);
        map.put(1085, -83);
        map.put(1086, -82);
        map.put(1087, -81);
        map.put(9617, -80);
        map.put(9618, -79);
        map.put(9619, -78);
        map.put(9474, -77);
        map.put(9508, -76);
        map.put(9569, -75);
        map.put(9670, -74);
        map.put(9558, -73);
        map.put(9557, -72);
        map.put(9571, -71);
        map.put(9553, -70);
        map.put(9559, -69);
        map.put(9565, -68);
        map.put(9564, -67);
        map.put(9553, -66);
        map.put(9488, -65);
        map.put(9492, -64);
        map.put(9524, -63);
        map.put(9516, -62);
        map.put(9500, -61);
        map.put(9472, -60);
        map.put(9532, -59);
        map.put(9566, -58);
        map.put(9557, -57);
        map.put(9562, -56);
        map.put(9556, -55);
        map.put(9577, -54);
        map.put(9574, -53);
        map.put(9568, -52);
        map.put(9552, -51);
        map.put(9580, -50);
        map.put(9575, -49);
        map.put(9576, -48);
        map.put(9572, -47);
        map.put(9573, -46);
        map.put(9561, -45);
        map.put(9560, -44);
        map.put(9554, -43);
        map.put(9555, -42);
        map.put(9579, -41);
        map.put(9578, -40);
        map.put(9496, -39);
        map.put(9484, -38);
        map.put(9608, -37);
        map.put(9604, -36);
        map.put(9612, -35);
        map.put(9616, -34);
        map.put(9600, -33);
        map.put(1088, -32);
        map.put(1089, -31);
        map.put(1090, -30);
        map.put(1091, -29);
        map.put(1092, -28);
        map.put(1093, -27);
        map.put(1094, -26);
        map.put(1095, -25);
        map.put(1096, -24);
        map.put(1097, -23);
        map.put(1098, -22);
        map.put(1099, -21);
        map.put(1100, -20);
        map.put(1101, -19);
        map.put(1102, -18);
        map.put(1103, -17);
        map.put(1025, -16);
        map.put(1105, -15);
        map.put(1028, -14);
        map.put(1108, -13);
        map.put(1031, -12);
        map.put(1111, -11);
        map.put(1038, -10);
        map.put(1118, -9);
        map.put(176, -8);
        map.put(8729, -7);
        map.put(183, -6);
        map.put(8730, -5);
        map.put(8470, -4);
        map.put(164, -3);
        map.put(9632, -2);
        map.put(160, -1);
        byte[] realData = new byte[5000];

        try {
            byte[] data = content.getBytes("UNICODE");
            int k = 3;
            realData[0] = 27;
            realData[1] = 116;
            realData[2] = 7;

            for(int i = 2; i < data.length; i += 2) {
                int c = data[i + 1] * 256 + data[i];
                if (map.get(c) != null) {
                    realData[k] = (Byte)map.get(c);
                    ++k;
                    byte[] var10000 = new byte[]{(Byte)map.get(c)};
                } else {
                    realData[k] = data[i];
                    ++k;
                }
            }

            realData[k] = 10;
        } catch (Exception var9) {
        }

        return this.sendByteData(realData);
    }

    public int sendByteData(byte[] data) {
        if (data != null) {
            Utils.Log(TAG, "sendByteData length is: " + data.length);
            return this.myPrinter.write(data);
        } else {
            return -1;
        }
    }

    public int printImage(Bitmap bitmap) {
        return this.sendByteData((new Command()).addBitImage(bitmap).getCommand());
    }

    public int printImage(Bitmap bitmap, int width, int mode) {
        return this.sendByteData((new Command()).addRastBitImage(bitmap, width, mode).getCommand());
    }

    public int updatePrint(byte[] fileByte) {
        byte[] buff = (new Command()).addApplication(fileByte).getCommand();

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buff);
            byte[] by = new byte[1024];

            int len;
            while((len = bis.read(by, 0, by.length)) != -1) {
                byte[] sendData = new byte[len];
                System.arraycopy(by, 0, sendData, 0, len);
                this.sendByteData(sendData);
                Thread.sleep((long)this.sendSleep);
            }

            bis.close();
            return buff.length;
        } catch (Exception var7) {
            return -1;
        }
    }

    public void prn_PageSetup(int pageWidth, int pageHeight) {
        this.printText(LabelPrint.label_set_page(pageWidth, pageHeight, 0));
    }

    public void prn_PagePrint(int rotate) {
        this.printText(LabelPrint.label_print(rotate));
    }

    public void prn_DrawLine(int lineWidth, int x0, int y0, int x1, int y1) {
        this.printText(LabelPrint.label_put_lines(lineWidth, x0, y0, x1, y1));
    }

    public void prn_DrawText(int x, int y, String text, String fontName, int fontsize, int rotate, int bold, int underline, int reverse) {
        this.printText(LabelPrint.label_put_text(x, y, text, fontName, fontsize, rotate, bold, underline, reverse));
    }

    public void prn_DrawBarcode(int x, int y, String text, int barcodetype, int rotate, int linewidth, int height) {
        this.printText(LabelPrint.label_put_barcode(x, y, text, barcodetype, rotate, linewidth, height));
    }

    public int setFont(int mWidth, int mHeight, int mBold, int mUnderline) {
        int mFontSize = 0;
        int mFontMode = 0;
        int mRetVal = 0;
        if (mBold != 0 && mBold != 1) {
            mRetVal = 3;
        } else {
            mFontMode |= mBold << 3;
        }

        if (mUnderline != 0 && mUnderline != 1) {
            mRetVal = 4;
        } else {
            mFontMode |= mUnderline << 7;
        }

        this.setPrinter(16, mFontMode);
        if (mWidth >= 0 && mWidth <= 7) {
            mFontSize |= mWidth << 4;
        } else {
            mRetVal = 1;
        }

        if (mHeight >= 0 && mHeight <= 7) {
            mFontSize |= mHeight;
        } else {
            mRetVal = 2;
        }

        this.setPrinter(17, mFontSize);
        return mRetVal;
    }

    public int printTable(Table table) {
        return this.printText(table.getTableText());
    }

    public int printBarCode(Barcode barcode) {
        return this.sendByteData(barcode.getBarcodeData());
    }

    public void init() {
        this.setPrinter(0);
    }

    public byte[] read() {
        return this.myPrinter.read();
    }

    public boolean setPrinter(int command) {
        byte[] arrayOfByte = null;
        switch(command) {
            case 0:
                arrayOfByte = new byte[]{27, 64};
                break;
            case 1:
                arrayOfByte = new byte[]{0};
                break;
            case 2:
                arrayOfByte = new byte[]{12};
                break;
            case 3:
                arrayOfByte = new byte[]{10};
                break;
            case 4:
                arrayOfByte = new byte[]{13};
                break;
            case 5:
                arrayOfByte = new byte[]{9};
                break;
            case 6:
                arrayOfByte = new byte[]{27, 50};
        }

        this.sendByteData(arrayOfByte);
        return true;
    }

    public boolean setPrinter(int command, int value) {
        byte[] arrayOfByte = new byte[3];
        switch(command) {
            case 0:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 74;
                break;
            case 1:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 100;
                break;
            case 2:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 33;
                break;
            case 3:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 85;
                break;
            case 4:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 86;
                break;
            case 5:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 87;
                break;
            case 6:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 45;
                break;
            case 7:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 43;
                break;
            case 8:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 105;
                break;
            case 9:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 99;
                break;
            case 10:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 51;
                break;
            case 11:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 32;
                break;
            case 12:
                arrayOfByte[0] = 28;
                arrayOfByte[1] = 80;
                break;
            case 13:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 97;
                if (value > 2 || value < 0) {
                    return false;
                }
            case 14:
            case 15:
            default:
                break;
            case 16:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 33;
                break;
            case 17:
                arrayOfByte[0] = 29;
                arrayOfByte[1] = 33;
        }

        arrayOfByte[2] = (byte)value;
        this.sendByteData(arrayOfByte);
        return true;
    }

    public void setCharacterMultiple(int x, int y) {
        byte[] arrayOfByte = new byte[]{29, 33, 0};
        if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            arrayOfByte[2] = (byte)(x * 16 + y);
            this.sendByteData(arrayOfByte);
        }

    }

    public void setLeftMargin(int nL, int nH) {
        byte[] arrayOfByte = new byte[]{29, 76, (byte)nL, (byte)nH};
        this.sendByteData(arrayOfByte);
    }

    public void setPrintModel(boolean isBold, boolean isDoubleHeight, boolean isDoubleWidth, boolean isUnderLine) {
        byte[] arrayOfByte = new byte[]{27, 33, 0};
        int temp = 0;
        if (isBold) {
            temp = (byte)(temp | 8);
        }

        if (isDoubleHeight) {
            temp = (byte)(temp | 16);
        }

        if (isDoubleWidth) {
            temp = (byte)(temp | 32);
        }

        if (isUnderLine) {
            temp = (byte)(temp | 128);
        }

        arrayOfByte[2] = (byte)temp;
        this.sendByteData(arrayOfByte);
    }

    public void cutPaper() {
        byte[] cutCommand = new byte[]{29, 86, 66, 0};
        this.sendByteData(cutCommand);
    }

    public void ringBuzzer(byte time) {
        byte[] buzzerCommand = new byte[]{29, 105, time};
        this.sendByteData(buzzerCommand);
    }

    public void openCashbox(boolean cashbox1, boolean cashbox2) {
        byte[] drawCommand;
        if (cashbox1) {
            drawCommand = new byte[]{27, 112, 0, 50, 50};
            this.sendByteData(drawCommand);
        }

        if (cashbox2) {
            drawCommand = new byte[]{27, 112, 1, 50, 50};
            this.sendByteData(drawCommand);
        }

    }
}
