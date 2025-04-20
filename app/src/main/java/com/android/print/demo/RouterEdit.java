package com.android.print.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import static com.android.print.demo.MainActivity.mPrinter;
import static com.android.print.demo.MainActivity.isConnected;

public class RouterEdit extends AppCompatActivity {

    private Context context;
    private EditText routerName;
    private EditText routerPassword;
    private Button setRouter;
    private Button dhcpOn;
    private Button dhcpOff;
    private byte[] router_password_bytes;
    private byte router_password_len;
    private byte[] router_name_bytes;
    private byte router_name_len;
    private byte[] order_router_name_bytes = new byte[263];
    private byte[] order_router_password_bytes = new byte[263];
    private String router_password;
    private String router_name;
    private String wifi_name;
    private ProgressDialog dialog;
    private Timer timer;
    private TimerTask timerTask1;
    private int times = 0;
    private byte[] ip_data;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.router_exit);

        context = this;

        initView();

        get_router_name();

        initDialog();
    }

    private void initView() {

        routerName = findViewById(R.id.routerName);

        routerPassword = findViewById(R.id.routerPassword);

        setRouter = findViewById(R.id.setRouter);
        setRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected && mPrinter != null){
                    set_router_password();
                    //startTimer();
                }

            }
        });

        dhcpOn = findViewById(R.id.dhcp_on);
        dhcpOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDHCP();
            }
        });

        dhcpOff = findViewById(R.id.dhcp_off);
        dhcpOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDHCP();
            }
        });
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

    public void get_router_name(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        wifi_name = wifiInfo.getSSID();

        if (wifi_name.contains("\"")) {
            wifi_name = wifi_name.substring(1, wifi_name.length() - 1);
        }

        routerName.setText(wifi_name);
        routerName.setSelection(wifi_name.length());

    }

    public void set_router_password(){

        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    router_password = routerPassword.getText().toString().trim();
                    router_name = routerName.getText().toString().trim();

                    if (!("".equals(router_password) || "".equals(router_name))) {



                        router_name_bytes = router_name.getBytes();
                        router_name_len = (byte) router_name_bytes.length;

                        //路由器名称 指令头
                        order_router_name_bytes[0] = 0x1b;
                        order_router_name_bytes[1] = 0x23;
                        order_router_name_bytes[2] = 0x23;
                        order_router_name_bytes[3] = 0x52;
                        order_router_name_bytes[4] = 0x54;
                        order_router_name_bytes[5] = 0x4e;
                        order_router_name_bytes[6] = 0x41;
                        order_router_name_bytes[7] = router_name_len;  //路由器名称长度

                        System.arraycopy(router_name_bytes, 0, order_router_name_bytes, 8, router_name_len);


                        router_password_bytes = router_password.getBytes();
                        router_password_len = (byte) router_password_bytes.length;

                        //路由器密码 指令头
                        order_router_password_bytes[0] = 0x1b;
                        order_router_password_bytes[1] = 0x23;
                        order_router_password_bytes[2] = 0x23;
                        order_router_password_bytes[3] = 0x52;
                        order_router_password_bytes[4] = 0x54;
                        order_router_password_bytes[5] = 0x50;
                        order_router_password_bytes[6] = 0x57;
                        order_router_password_bytes[7] = router_password_len; //路由器密码长度

                        System.arraycopy(router_password_bytes, 0, order_router_password_bytes, 8, router_password_len);

                        mPrinter.init();
                        mPrinter.sendByteData(order_router_name_bytes);
                        mPrinter.sendByteData(order_router_password_bytes);

                    }
                }
            }
        }.start();
    }

    private void startTimer(){

        timer = new Timer();
        timerTask1 = new TimerTask() {
            public void run() {
                Looper.prepare();
                new Handler().post(new Runnable() {
                    public void run(){
                        get_state_ip();

                        ip_data = mPrinter.read();
                        times += 1;

                        if (ip_data != null){
                            if (Byte.toUnsignedInt(ip_data[4]) == 1){
                                value = "开启";
                            }else
                            {
                                value = "关闭";
                            }
                        }

                        if (ip_data == null && times == 2){
                            stopTimer();
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle("配网成功")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }else if (ip_data != null && ip_data[0] != 0){
                            stopTimer();
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle("配网成功")
                                    .setMessage("IP："+":"+Byte.toUnsignedInt(ip_data[0])+"."+Byte.toUnsignedInt(ip_data[1])+"."+Byte.toUnsignedInt(ip_data[2])+"."+Byte.toUnsignedInt(ip_data[3])+"\r\n"+"DHCP："+value)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                        }else if (ip_data != null && times == 6){
                            stopTimer();
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle("配网失败")
                                    .setMessage("IP："+":"+Byte.toUnsignedInt(ip_data[0])+"."+Byte.toUnsignedInt(ip_data[1])+"."+Byte.toUnsignedInt(ip_data[2])+"."+Byte.toUnsignedInt(ip_data[3])+"\r\n"+"DHCP："+value)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog1, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                    }
                });

                Looper.loop();
            }

        };
        timer.schedule(timerTask1,0,5000);
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    private void get_state_ip(){

        dialog.setTitle("配网状态");
        dialog.setMessage("please wait....");

        dialog.show();

        get_state_send();


    }

    //ip状态查询指令
    private void get_state_send(){
        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x23, 0x23, 0x47, 0x52, 0x54, 0x53});
    }


    private void OpenDHCP(){
        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x23, 0x23, 0x44, 0x48, 0x43, 0x50, 0x01});
    }

    private void CloseDHCP(){
        mPrinter.init();
        mPrinter.sendByteData(new byte[]{0x1B, 0x23, 0x23, 0x44, 0x48, 0x43, 0x50, 0x00});
    }

}
