package com.android.print.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.hardware.usb.UsbManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.print.demo.bean.Language;
import com.android.print.demo.bluetooth.BluetoothDeviceList;
import com.android.print.demo.bluetooth.BluetoothOperation;
import com.android.print.demo.databinding.ActivityMainBinding;
import com.android.print.demo.permission.EasyPermission;
import com.android.print.demo.usb.UsbOperation;
import com.android.print.demo.util.LanguageUtils;
import com.android.print.demo.util.PrintUtils;
import com.android.print.demo.util.PrinterInstance;
import com.android.print.demo.util.UriGetPath;
import com.android.print.demo.wifi.WifiOperation;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.android.print.sdk.wifi.WifiAdmin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Android 打印机SDK 开发示例 v3.0
 * 技术支持 小谢
 * 打印APP定制开发 QQ 2227421573
 *
 */
public class MainActivity extends AppCompatActivity implements EasyPermission.PermissionCallback {
    private Context context;
    private ActivityMainBinding binding;
    public static final String TAG = "MainActivity";

    protected static boolean isConnected;                 //是否已经建立了连接
    protected static IPrinterOpertion myOpertion;
    protected static PrinterInstance mPrinter;
    protected static ProgressDialog dialog;
    private MyTask myTask;
    private Timer timer;
    private TimerTask timerTask;
    private static byte read_bytes[] = null;
    private byte bytes_data[] = null;
    private static byte[] MD5_key = null;
    private static int cnt = 0;
    private static int flag = 0;
    private String filePath = null;


    private String bt_mac;
    private String bt_name;
    private String wifi_mac;
    private String wifi_name;

    public static final int CONNECT_DEVICE = 1;             //选择设备
    public static final int ENABLE_BT = 2;                  //启动蓝牙
    public static final int REQUEST_SELECT_FILE = 3;        //选择文件
    public static final int REQUEST_PERMISSION = 4;         //读写权限

    private String[] permisions = new String[]{             //SD卡读写权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        context = this;
        hasSDcardPermissions();
        initView();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtils.attachBaseContext(newBase));
    }


    /**
     * 界面初始化
     */
    private void initView(){
        binding.connectLayout.setOnClickListener(onClickListener);
        binding.rbText.setOnClickListener(onClickListener);
        binding.rbPictrue.setOnClickListener(onClickListener);
        binding.rbQr.setOnClickListener(onClickListener);
        binding.rbBar.setOnClickListener(onClickListener);
        binding.rbCurve.setOnClickListener(onClickListener);
        binding.rbBt.setOnClickListener(onClickListener);
        binding.rbSet.setOnClickListener(onClickListener);
        binding.rbBox.setOnClickListener(onClickListener);
        binding.rbCut.setOnClickListener(onClickListener);
        binding.rbBlack.setOnClickListener(onClickListener);
        binding.rbSelfTest.setOnClickListener(onClickListener);
        binding.rbUpdate.setOnClickListener(onClickListener);
        binding.rbLanguage.setOnClickListener(onClickListener);
        binding.rbRouter.setOnClickListener(onClickListener);
        binding.getStatus.setOnClickListener(onClickListener);
        binding.printerCheck.setOnClickListener(onClickListener);

        //初始化状态信号
        int radio_button_on = R.drawable.radio_button_on;
        binding.printerPaper.setButtonDrawable(radio_button_on);
        binding.printerHot.setButtonDrawable(radio_button_on);
        binding.printerPress.setButtonDrawable(radio_button_on);
        binding.printerOpen.setButtonDrawable(radio_button_on);
        binding.printerCut.setButtonDrawable(radio_button_on);

        initDialog();
    }

    private void initDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connecting");
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == binding.connectLayout) {      //点击连接
                connClick();
            }else if (v == binding.rbText) {              //打印文字

                if (!isConnected && mPrinter == null) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, TextEdit.class);
                startActivity(intent);
            }else if (v == binding.rbPictrue) {         //打印图片
                if(!isConnected && mPrinter == null) {
                    return;
                }
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_tip)
                        .setMessage(R.string.toast_photo_size)
                        .setPositiveButton(context.getResources().getString(R.string.dialog_choose), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startSelectImage();
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.dialog_default), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                printDefImage();
                            }
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .setCancelable(false)
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }else if (v == binding.rbQr) {                //打印二维码
                if(!isConnected && mPrinter == null) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, QREdit.class);
                startActivity(intent);
            }else if (v == binding.rbBar) {                  //打印条码
                if(!isConnected && mPrinter == null) {
                    return;
                }
                printBarcode();
            }else if (v == binding.rbCurve) {                //打印曲线
                if(!isConnected && mPrinter == null) {
                    return;
                }
                printCurve();
            }else if (v == binding.rbBt) {                   //大数据打印测试
                if(!isConnected && mPrinter == null) {
                    return;
                }
                printBigData();
            }else if (v == binding.rbSet){        //恢复出厂设置
                if(!isConnected && mPrinter == null) {
                    return;
                }

                printReset();
            }else if (v == binding.rbBox){        //钱箱
                if(!isConnected && mPrinter == null) {
                    return;
                }
                printCashbox();
            }else if (v == binding.rbCut){        //切刀
                if(!isConnected && mPrinter == null) {
                    return;
                }

                printCut();

            }else if (v == binding.rbBlack){      //黑标
                if(!isConnected && mPrinter == null) {
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_tip)
                        .setMessage(R.string.dialog_content_black)
                        .setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                printBlSet(false);
                            }
                        })
                        .setNegativeButton(R.string.dialog_open, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                printBlSet(true);
                            }
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .setCancelable(false)
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }else if (v == binding.rbSelfTest){   //自检页
                if(!isConnected && mPrinter == null) {
                    return;
                }

                printerSelfTest();

            }else if (v == binding.rbUpdate){     //软件更新
                if(!isConnected && mPrinter == null) {
                    return;
                }
                startSelectFile();
            }else if (v == binding.rbLanguage){   //语言/编码
                if(!isConnected && mPrinter == null) {
                    return;
                }

                Intent intent = new Intent(MainActivity.this, LanguageEdit.class);
                startActivity(intent);

            }else if (v == binding.rbRouter){   //路由器
                if(!isConnected && mPrinter == null) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, RouterEdit.class);
                startActivity(intent);
            }else if (v == binding.getStatus){    //返回 10 04 04 查询状态
                if(!isConnected && mPrinter == null) {
                    return;
                }
                startTimer();
                getStatus();
            }else if (v == binding.printerCheck){   //返回 GS a n 查询状态
                if(!isConnected && mPrinter == null) {
                    return;
                }
                startTimer();
                printerCheck();
            }
        }
    };


    /**
     * 权限开启
     * @return
     */
    private boolean hasSDcardPermissions() {
        //判断是否有权限
        if (EasyPermission.hasPermissions(context, permisions)) {
            return true;
        } else {
            EasyPermission.with(this)
                    .rationale(context.getResources().getString(R.string.dialog_permission))
                    .addRequestCode(REQUEST_PERMISSION)
                    .permissions(permisions)
                    .request();
        }
        return false;
    }

    /**
     * 文件选择
     */
    private void startSelectFile(){
        if(!isConnected && mPrinter == null) {
            return;
        }

        if(hasSDcardPermissions()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        }
    }

    /**
     * 图片选择
     */
    private void startSelectImage(){
        if(!isConnected && mPrinter == null) {
            return;
        }

        if(hasSDcardPermissions()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        }
    }

    private void printDefImage(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){

                    PrintUtils.printDefImage(context.getResources(),mPrinter);
                }
            }
        }.start();

    }


    private void tipUpdate(final String filePath){

        String[] data = filePath.split("/");
        int length = data.length;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_tip)
                .setMessage(data[length-1] + "\n"+R.string.dialog_content_upadte)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printUpdate(filePath);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 打印图片
     * WIIF打印机 API涉及网络通讯, 建议异步调用
     *
     */
    private void printImage(final String path, final Float len){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printImage(context.getResources(), mPrinter, path, len);
                }

            }
        }.start();
    }


    /**
     * 打印条码
     */
    private void printBarcode(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printBarcode(context.getResources(), mPrinter);
                }

            }
        }.start();
    }

    /**
     * 打印曲线
     */
    private void printCurve(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printCurve(context.getResources(), mPrinter);
                }

            }
        }.start();
    }

    /**
     * 大数据测试
     */
    private void printBigData(){

        dialog.setTitle(null);
        dialog.setMessage(context.getResources().getString(R.string.dialog_print));
        dialog.show();


        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printBigData(context.getResources(), mPrinter);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (MainActivity.class){
                            initDialog();
                        }

                    }
                });
            }
        }.start();
    }

    /**
     * 开钱箱
     */
    private void printCashbox(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printCashbox(mPrinter);
                }

            }
        }.start();
    }

    /**
     * 切刀
     */
    private void printCut(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printCut(mPrinter);
                }

            }
        }.start();
    }

    /**
     * 恢复默认设置
     */
    private void printReset(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printReset(mPrinter);
                }

            }
        }.start();
    }

    /**
     * 打印机升级是耗时操作, 请开发者在异步线程中操作
     */
    private void printUpdate(final String filePath){

        dialog.setTitle(null);
        dialog.setMessage(context.getResources().getString(R.string.dialog_update));
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printUpdate(context.getResources(), mPrinter, filePath);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (MainActivity.class){
                            Toast.makeText(context, R.string.toast_upadte_finish, Toast.LENGTH_LONG).show();
                            initDialog();
                        }

                    }
                });
            }
        }.start();

    }

    /**
     * 黑标
     * @param state
     */
    private void printBlSet(final Boolean state){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printBlSet(context.getResources(), mPrinter, state);
                }

            }
        }.start();
    }

    /**
     * 打印自检页
     */
    private void printerSelfTest(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printerSelfTest(context.getResources(), mPrinter);
                }

            }
        }.start();
    }


    /**
     * 返回 10 04 04 查询状态
     */
    private void getStatus(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    mPrinter.init();
                    mPrinter.sendByteData(new byte[]{0x10, 0x04, 0x04});
                }

            }
        }.start();
    }

    /**
     * 返回 GS a n 查询状态
     */
    private void printerCheck(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    mPrinter.init();
                    mPrinter.sendByteData(new byte[]{0x1D, 0x61, 0x00});
                }

            }
        }.start();
    }


    /**
     * 判断MD5是否正确
     */
    Boolean is_MD5_correct(){
        int i = 0;
        if (read_bytes != null){
            for (i=0; i<16; i++){
                if (read_bytes[i] != MD5_key[i]){
                    return false;
                }
            }
        }
        return true;
    }

    private String decToHex(byte by){

        int result = 0;
        int data = Integer.parseInt(String.valueOf(by));
        int val = data/16;

        while (val != 0){
            result = result*10 + val;
            data = data%16;
            val = data/16;
        }

        result = result*10 + data;

        return String.valueOf(result);
    }

    /**
     * 定时接收打印机的数据
     */
    private void startTimer(){

        timer = new Timer();
        timerTask = new TimerTask(){
            public void run(){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        bytes_data = mPrinter.read();
                        System.out.println(Arrays.toString(bytes_data));
                        if (bytes_data != null) {
                            if (bytes_data.length == 1) {
                                binding.vStatus.setText("0x" + decToHex(bytes_data[0]));
                            } else {
                                int radio_button_err = R.drawable.radio_button_err;
                                int radio_button_on = R.drawable.radio_button_on;

                                //是否开盖
                                if (bytes_data[0] == 0x34){
                                    binding.printerOpen.setButtonDrawable(radio_button_err);
                                }else {
                                    binding.printerOpen.setButtonDrawable(radio_button_on);
                                }

                                //是否切刀错误和过热和过压
                                switch (bytes_data[1]){
                                    case (byte)0x08:
                                        binding.printerCut.setButtonDrawable(radio_button_err);
                                        break;
                                    case (byte)0x80:
                                        binding.printerPress.setButtonDrawable(radio_button_err);
                                        break;
                                    case 0x40:
                                        binding.printerHot.setButtonDrawable(radio_button_err);
                                        break;
                                    case (byte)0x88:
                                        binding.printerPress.setButtonDrawable(radio_button_err);
                                        binding.printerCut.setButtonDrawable(radio_button_err);
                                        break;
                                    case (byte)0x48:
                                        binding.printerCut.setButtonDrawable(radio_button_err);
                                        binding.printerHot.setButtonDrawable(radio_button_err);
                                        break;
                                    case (byte)0xC8:
                                        binding.printerCut.setButtonDrawable(radio_button_err);
                                        binding.printerHot.setButtonDrawable(radio_button_err);
                                        binding.printerPress.setButtonDrawable(radio_button_err);
                                    default:
                                        binding.printerCut.setButtonDrawable(radio_button_on);
                                        binding.printerHot.setButtonDrawable(radio_button_on);
                                        break;
                                }

                                //是否有纸
                                if (bytes_data[2] == 0x0C){
                                    binding.printerPaper.setButtonDrawable(radio_button_err);
                                }else {
                                    binding.printerPaper.setButtonDrawable(radio_button_on);
                                }
                            }
                            stopTimer();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    /**
     * 关闭定时器
     */
    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    //用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    flag = 0;
                    mPrinter = myOpertion.getPrinter();
                    MD5_permission();
                    java.util.Timer timer = new Timer();
                    myTask = new MyTask();
                    timer.schedule(myTask, 0, 1000);
                    break;
                case PrinterConstants.Connect.FAILED:
                    if (myTask != null) {
                        myTask.cancel();
                    }
                    isConnected = false;
                    Toast.makeText(context, R.string.toast_failed, Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    if (myTask != null) {
                        myTask.cancel();
                    }
                    isConnected = false;
                    if (flag == 0)
                    Toast.makeText(context, R.string.toast_close, Toast.LENGTH_SHORT).show();
                    flag++;
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(context, R.string.toast_no, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

            updateButtonState();

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    };

    /**
     * 更新界面状态
     */
    private void updateButtonState() {
        if(!isConnected){
            binding.connectAddress.setText(R.string.no_conn_address);
            binding.connectState.setText(R.string.connect);
            binding.connectName.setText(R.string.no_conn_name);
        }else{
            switch (binding.tabLayout.getSelectedTabPosition()){
                case 0:{
                    if( bt_mac!=null && !bt_mac.equals("")){
                        binding.connectAddress.setText(getString(R.string.str_address)+ bt_mac);
                        binding.connectState.setText(R.string.disconnect);
                        binding.connectName.setText(getString(R.string.str_name)+bt_name);
                    }else if(bt_mac==null ) {
                        bt_mac= BluetoothPort.getmDeviceAddress();
                        System.out.println(bt_mac);
                        bt_name=BluetoothPort.getmDeviceName();
                        binding.connectAddress.setText(getString(R.string.str_address)+bt_mac);
                        binding.connectState.setText(R.string.disconnect);
                        binding.connectName.setText(getString(R.string.str_name)+bt_name.substring(1, bt_name.indexOf("（")));
                    }
                    break;
                }
                case 1:{
                    binding.connectAddress.setText(getString(R.string.disconnect));
                    binding.connectState.setText(R.string.disconnect);
                    binding.connectName.setText(getString(R.string.disconnect));
                    break;
                }
            }
        }
    }


    /**
     * 连接设备
     */
    private void connClick(){
        if(isConnected){        //如果已经连接了, 则断开
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;
        }else{
                                //如果没有连接, 则提示
            new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_tip)
                    .setMessage(R.string.dialog_connlast)
                    .setPositiveButton(R.string.dialog_conn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            openConn();
                        }
                    })
                    .setNegativeButton(R.string.dialog_resel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reselConn();

                        }
                    })
                    .show();
        }
    }
    

    /**
     * 打开连接
     */
    private void openConn(){
        switch (binding.tabLayout.getSelectedTabPosition()){
            case 0:{        //蓝牙
                myOpertion = new BluetoothOperation(context, mHandler);
                myOpertion.btAutoConn(context,  mHandler);
                break;
            }
            case 1:{            //USB
                myOpertion = new UsbOperation(MainActivity.this, mHandler);
                UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
                myOpertion.usbAutoConn(manager);
                break;
            }
        }
    }


    /**
     * 重新连接
     */
    private void reselConn(){
        switch (binding.tabLayout.getSelectedTabPosition()){
            case 0:{
                myOpertion = new BluetoothOperation(context, mHandler);
                myOpertion.chooseDevice();
                break;
            }
            case 1:{
                myOpertion = new UsbOperation(context, mHandler);
                myOpertion.chooseDevice();
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //选择连接设备返回处理
        if(requestCode == CONNECT_DEVICE && resultCode == RESULT_OK){
            switch (binding.tabLayout.getSelectedTabPosition()){
                case 0:{        //蓝牙
                    bt_mac = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                    bt_name = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_NAME);
                    dialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            synchronized (MainActivity.class){
                                myOpertion.open(data);
                            }

                        }
                    }).start();
                    break;
                }
                case 1:{        //USB
                    myOpertion.open(data);
                    break;
                }
            }


        //请求打开蓝牙返回
        }else if(requestCode == ENABLE_BT){
            if (resultCode == Activity.RESULT_OK) {
                myOpertion.chooseDevice();
            } else {
                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
            }


        //选择升级文件返回
        }else if(requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK){
            filePath = new UriGetPath().getUriToPath(context, data.getData());
            if (filePath == null) {
                Toast.makeText(this, R.string.toast_upadte_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            if(filePath.endsWith("bin")){
                tipUpdate(filePath);
                Toast.makeText(this, R.string.toast_upadte_error, Toast.LENGTH_SHORT).show();
            }
            else{
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_tip)
                        .setMessage(R.string.toast_photo_size)
                        .setPositiveButton("58mm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                printImage(filePath, 480f);
                            }
                        })
                        .setNegativeButton("80mm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                printImage(filePath, 640f);
                            }
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .setCancelable(false)
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }

    }



    /**
     * wifi机器需要定时读取打印机数据, 如下代码
     * 当连接断开时, 读取数据 read() 会触发断开连接的消息
     *
     * USB 蓝牙 可忽略
     */

    private class MyTask extends java.util.TimerTask{
        @Override
        public void run() {
            if(isConnected && mPrinter != null) {
                read_bytes = mPrinter.read();

                if (read_bytes != null) {
                    System.out.println(mPrinter.isConnected() + " read byte " + Arrays.toString(read_bytes));
                    if (is_MD5_correct())
                    {
                        Looper.prepare();
                        Toast.makeText(context, R.string.dialog_conn, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        myOpertion.close();
                        myOpertion = null;
                        mPrinter = null;
                        Looper.prepare();
                        Toast.makeText(context, R.string.toast_failed, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }

                if (cnt == 2 && read_bytes == null){
                    myOpertion.close();
                    myOpertion = null;
                    mPrinter = null;
                    cnt = 0;
                }

                if (cnt == 2){
                    cnt = 0;
                }
                cnt++;

                System.out.println(cnt);
            }
        }
    }

    public static Boolean MD5_permission(){
        int i = 0;
        mPrinter.init();
        byte[] cmd_random = new byte[]{0x1b, 0x23, 0x23, 0x44, 0x4c, 0x50, 0x57, 0x10};
        byte[] get_random_bytes = new byte[16];
        byte[] end_bytes = new byte[24];


        double num = 0;

        for (i = 0; i < 16; i++) {
            num = Math.random()*100;
            get_random_bytes[i] = (byte)num;
        }

        end_bytes[0] = 0x67;
        end_bytes[1] = 0x65;
        end_bytes[2] = 0x7a;
        end_bytes[3] = 0x68;
        end_bytes[4] = 0x69;
        end_bytes[5] = 0x34;
        end_bytes[6] = 0x31;
        end_bytes[7] = 0x39;


        for (i = 0; i < 16; i++) {
            end_bytes[i+8] = get_random_bytes[i];
        }

        mPrinter.sendByteData(cmd_random);

        mPrinter.sendByteData(get_random_bytes);

        mPrinter.sendByteData(new byte[]{(byte) 0x1B, (byte) 0x23, (byte) 0X23, (byte) 'M', (byte) 'M', (byte) 'D', (byte) '5'});

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MD5_key = md5.digest(end_bytes);
            System.out.println(mPrinter.isConnected() + " change byte " + Arrays.toString(MD5_key));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    //有权限
    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        startSelectFile();
    }


    //没有权限
    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {

        // 是否用户拒绝,不在提示
        boolean isAskAgain = EasyPermission.checkDeniedPermissionsNeverAskAgain(
                this,
                context.getResources().getString(R.string.toast_open_permission),
                R.string.gotoSettings, R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, perms);
    }
}
