package com.android.print.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.android.print.demo.util.PrintUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.android.print.demo.MainActivity.mPrinter;

public class QREdit extends AppCompatActivity {

    private Spinner pixel;
    private Spinner cell_size;
    private Spinner error_level;
    private EditText qr_data;
    private Context context;
    private Button qr_send;
    private Button qr_print;
    private String qr_value = null;
    private List<Integer> pixel_list;
    private List<Integer> cell_list;
    private List<String> level_list;
    private HashMap<Integer, String> level_kv;
    private ArrayAdapter<Integer> pixel_adapter;
    private ArrayAdapter<Integer> cell_adapter;
    private ArrayAdapter<String> err_adapter;
    private long pixel_val;
    private long cell_val;
    private String level_val;
    private int key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qr_edit);

        context = this;

        initData();

        initView();

    }

    /**
     * 初始化数据
     */
    private void initData(){

        pixel_list = new ArrayList<>();
        cell_list = new ArrayList<>();
        level_list = new ArrayList<>();
        level_kv = new HashMap<>();

        for (int i = 1; i < 25; i++) {
            pixel_list.add(i);
        }

        for (int i = 1; i < 17; i++) {
            cell_list.add(i);
        }

        level_list.add("L");
        level_list.add("M");
        level_list.add("Q");
        level_list.add("H");

        level_kv.put(48, "L");
        level_kv.put(49, "M");
        level_kv.put(50, "Q");
        level_kv.put(51, "H");

    }

    /**
     * 初始化页面
     */
    private void initView(){

        qr_data = findViewById(R.id.qrData);
        qr_send = findViewById(R.id.qr_send);
        qr_print = findViewById(R.id.qr_print);
        pixel =  findViewById(R.id.pixel);
        cell_size = findViewById(R.id.cell_size);
        error_level = findViewById(R.id.error_level);


        //适配器
        pixel_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pixel_list);
        cell_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cell_list);
        err_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, level_list);


        //设置样式
        pixel_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cell_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        err_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //设置适配器
        pixel.setAdapter(pixel_adapter);
        cell_size.setAdapter(cell_adapter);
        error_level.setAdapter(err_adapter);

        //设置默认值
        pixel.setSelection(pixel_adapter.getPosition(8));


        pixel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pixel_val = parent.getItemIdAtPosition(position) + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cell_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cell_val = parent.getItemIdAtPosition(position) + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        error_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level_val = parent.getItemAtPosition(position).toString();
                key = getKey(level_kv, level_val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        qr_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_value = qr_data.getText().toString().trim();

                if (!"".equals(qr_value)){
                    sendQR();
                    Toast.makeText(QREdit.this, "已发送", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(QREdit.this, "内容为空！！！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        qr_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printQR();
            }
        });
    }


    /**
     * 获取纠错等级的对应key
     * @param map
     * @param v
     * @return
     */
    public static int getKey(HashMap<Integer, String> map, String v) {

        Iterator<Integer> it = map.keySet().iterator();

        while (it.hasNext()) {

            int key_val = it.next();

            if (map.get(key_val).equals(v))
                return key_val;

        }

        return 1;

    }

    /**
     * 发送二维码数据
     */
    private void sendQR(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printSendQR(mPrinter, qr_value);
                }

            }
        }.start();
    }

    /**
     * 打印二维码
     */
    private void printQR(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printQR(context.getResources(), mPrinter, pixel_val, cell_val, key);
                }

            }
        }.start();
    }

}
