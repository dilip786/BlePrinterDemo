package com.android.print.demo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.android.print.demo.R;
import com.android.print.demo.permission.Utils;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterConstants.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.datatype.DatatypeConstants;


public class PrintUtils {


    public static void printText(Resources resources, PrinterInstance mPrinter) {
        mPrinter.init();

        mPrinter.printText(resources.getString(R.string.str_text));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.printText(resources.getString(R.string.str_text_left));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行


        mPrinter.setPrinter(Command.ALIGN, 1);
        mPrinter.printText(resources.getString(R.string.str_text_center));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行

        mPrinter.setPrinter(Command.ALIGN, 2);
        mPrinter.printText(resources.getString(R.string.str_text_right));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3); // 换3行

        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setFont(0, 0, 1, 0);
        mPrinter.printText(resources.getString(R.string.str_text_strong));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

        mPrinter.setFont(0, 0, 0, 1);
        mPrinter.sendByteData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x80});
        mPrinter.printText(resources.getString(R.string.str_text_underline));
        mPrinter.sendByteData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x00});
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.printText(resources.getString(R.string.str_text_height));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        for (int i = 0; i < 4; i++) {
            mPrinter.setFont(i, i, 0, 0);
            mPrinter.printText((i + 1) + resources.getString(R.string.times));

        }
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        for (int i = 0; i < 4; i++) {
            mPrinter.setFont(i, i, 0, 0);
            mPrinter.printText(resources.getString(R.string.bigger) + (i + 1) + resources.getString(R.string.bigger1));
            mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
        }

        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrintModel(true, false, false, false);
        mPrinter.printText(resources.getString(R.string.str_text_overstriking)+"\n");

        mPrinter.setPrintModel(true, true, false, false);
        mPrinter.printText(resources.getString(R.string.str_text_heigth_show)+"\n");

        mPrinter.setPrintModel(true, false, true, false);
        mPrinter.printText(resources.getString(R.string.str_text_width)+"\n");

        mPrinter.setPrintModel(true, true, true, false);
        mPrinter.printText(resources.getString(R.string.str_text_heigth_width)+"\n");

        mPrinter.setPrintModel(false, false, false, true);
        mPrinter.printText(resources.getString(R.string.str_text_underline_show)+"\n");


        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getimage(String srcPath, Float len) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 480
        float hh = len;
        float ww = 300f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 2;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }


    public static void printImage(Resources resources, PrinterInstance mPrinter, String path, Float len){
        mPrinter.init();
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        Bitmap bitmap = getimage(path,len);
        //Bitmap bitmap1 = compressBitmap(bitmap, 0);

        //Bitmap bitmap = BitmapFactory.decodeFile(path);   //.decodeStream(resources.getAssets().open("android.png"));


        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);   //设置打印居中
        mPrinter.printImage(bitmap);
        mPrinter.printText("\n\n\n\n");                     //换4行
    }

    public static void printDefImage(Resources resources, PrinterInstance mPrinter){
        mPrinter.init();
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        Bitmap bitmap1 = null;
        try {
            bitmap1 = BitmapFactory.decodeStream(resources.getAssets().open("android.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mPrinter.printImage(bitmap1);
        mPrinter.printText("\n\n\n\n");                     //换4行

        try {
            bitmap1 = BitmapFactory.decodeStream(resources.getAssets().open("support.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);   //设置打印居中
        mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.printImage(bitmap1);
        mPrinter.printText("\n\n\n\n");                     //换4行
    }


    public static void printBarcode(Resources resources, PrinterInstance mPrinter) {

        mPrinter.init();
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.CODE39" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode0 = new Barcode(PrinterConstants.BarcodeType.CODE39, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.CODABAR" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODABAR, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode1);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.ITF" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.ITF, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode2);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.CODE93" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode3 = new Barcode(PrinterConstants.BarcodeType.CODE93, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode3);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.CODE128" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode4 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode4);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.UPC_A" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode5 = new Barcode(PrinterConstants.BarcodeType.UPC_A, 2, 63, 2, "000000000000");
        mPrinter.printBarCode(barcode5);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.UPC_E" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode6 = new Barcode(PrinterConstants.BarcodeType.UPC_E, 2, 63, 2, "000000000000");
        mPrinter.printBarCode(barcode6);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.QRCODE" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode10 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 49, 80, 48, "baidu.com");
//        mPrinter.printBarCode(barcode10);
//        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 49, 81, 48);
//        mPrinter.printBarCode(barcode);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//
//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.JAN13" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode7 = new Barcode(PrinterConstants.BarcodeType.JAN13, 2, 63, 2, "000000000000");
//        mPrinter.printBarCode(barcode7);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//
//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.JAN8" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode8 = new Barcode(PrinterConstants.BarcodeType.JAN8, 2, 63, 2, "0000000");
//        mPrinter.printBarCode(barcode8);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.PDF417" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode9 = new Barcode(PrinterConstants.BarcodeType.PDF417, 2, 3, 6, "123456");
//        mPrinter.printBarCode(barcode9);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//

//
//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.DATAMATRIX" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode11 = new Barcode(PrinterConstants.BarcodeType.DATAMATRIX, 2, 3, 6, "123456");
//        mPrinter.printBarCode(barcode11);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
    }

    public static void printCurve(Resources resources, PrinterInstance mPrinter) {
        try {
            InputStream is = resources.getAssets().open("curve.bin");
            int length = is.available();
            byte[] fileByte = new byte[length];
            is.read(fileByte);
            mPrinter.init();
            mPrinter.sendByteData(fileByte);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printBigData(Resources resources, PrinterInstance mPrinter) {
        try {
            InputStream is = resources.getAssets().open("58-big-data-test.bin");
            int length = is.available();
            byte[] fileByte = new byte[length];
            is.read(fileByte);
            mPrinter.init();
            mPrinter.sendByteData(fileByte);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printCashbox(PrinterInstance mPrinter) {

        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x70, 0x00, 0x01, 0x01});
    }

    public static void printCut(PrinterInstance mPrinter){
        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1D, 0x56, 0x41, 0x00});
    }

    public static void printReset(PrinterInstance mPrinter){
        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x23, 0x23, 0x52, 0x54, 0x46, 0x41});
    }

    public static void printUpdate(Resources resources, PrinterInstance mPrinter, String filePath) {
        try {

            FileInputStream fis = new FileInputStream(new File(filePath));
            //InputStream is = resources.getAssets().open("PT8761-HT-BAT-9170.bin");
            int length = fis.available();
            byte[] fileByte = new byte[length];
            fis.read(fileByte);
            mPrinter.init();
            mPrinter.updatePrint(fileByte);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printBlSet(Resources resources, PrinterInstance mPrinter, Boolean state){

        mPrinter.init();
        if (state){
            mPrinter.sendByteData(new byte[]{0x1F, 0x1b, 0x1F, (byte) 0x80, 0x04, 0x05, 0x06, 0x44});
        }else {
            mPrinter.sendByteData(new byte[]{0x1F, 0x1b, 0x1F, (byte) 0x80, 0x04, 0x05, 0x06, 0x66});
        }

    }


    public static void printSendQR(PrinterInstance mPrinter, String str) {
        mPrinter.init();
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 49, 80, 48, str);
        mPrinter.printBarCode(barcode);
    }

    public static void printQR(Resources resources, PrinterInstance mPrinter, long pixel, long cell, long err){

        byte[] pixel_bytes = new byte[8];
        byte[] cell_bytes = new byte[8];
        byte[] err_bytes = new byte[8];

        mPrinter.init();

        //设置像素点宽
        pixel_bytes[0] = 0x1B;
        pixel_bytes[1] = 0x23;
        pixel_bytes[2] = 0x23;
        pixel_bytes[3] = 0x51;
        pixel_bytes[4] = 0x50;
        pixel_bytes[5] = 0x49;
        pixel_bytes[6] = 0x58;
        pixel_bytes[7] = (byte)pixel;

        //设置单元大小
        cell_bytes[0] = 0x1D;
        cell_bytes[1] = 0x28;
        cell_bytes[2] = 0x6B;
        cell_bytes[3] = 0x03;
        cell_bytes[4] = 0x00;
        cell_bytes[5] = 0x31;
        cell_bytes[6] = 0x43;
        cell_bytes[7] = (byte)cell;

        //设置纠错等级
        err_bytes[0] = 0x1D;
        err_bytes[1] = 0x28;
        err_bytes[2] = 0x6B;
        err_bytes[3] = 0x03;
        err_bytes[4] = 0x00;
        err_bytes[5] = 0x31;
        err_bytes[6] = 0x45;
        err_bytes[7] = (byte)err;

        mPrinter.sendByteData(pixel_bytes);
        mPrinter.sendByteData(cell_bytes);
        mPrinter.sendByteData(err_bytes);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.QRCODE" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 49, 81, 48);
        mPrinter.printBarCode(barcode);
    }

    public static void printerSelfTest(Resources resources, PrinterInstance mPrinter){

        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x23, 0x23, 0x53, 0x45, 0x4C, 0x46});
    }

    public static void printerLanguage(PrinterInstance mPrinter, int key){

        byte[] bt = new byte[8];
        bt[0] = 0x1B;
        bt[1] = 0x23;
        bt[2] = 0x23;
        bt[3] = 0x53;
        bt[4] = 0x4C;
        bt[5] = 0x41;
        bt[6] = 0x4E;
        bt[7] = (byte)key;
        mPrinter.init();
        mPrinter.sendByteData(bt);
    }

    public static void printerCode(PrinterInstance mPrinter, int position){

        byte[] bt = new byte[8];
        bt[0] = 0x1B;
        bt[1] = 0x23;
        bt[2] = 0x23;
        bt[3] = 0x43;
        bt[4] = 0x44;
        bt[5] = 0x54;
        bt[6] = 0x59;
        bt[7] = (byte)(position + 1);
        mPrinter.init();
        mPrinter.sendByteData(bt);
    }

    public static void printerExit(PrinterInstance mPrinter){

        byte[] bt = new byte[9];
        bt[0] = 0x1B;
        bt[1] = 0x00;
        bt[2] = 0x23;
        bt[3] = 0x23;
        bt[4] = 0x43;
        bt[5] = 0x44;
        bt[6] = 0x54;
        bt[7] = 0x59;
        bt[8] = 0x03;
        mPrinter.init();
        mPrinter.sendByteData(bt);
    }
}
