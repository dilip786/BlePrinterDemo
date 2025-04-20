package com.android.print.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Printer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.print.demo.bean.Language;
import com.android.print.demo.databinding.ActivityMainBinding;
import com.android.print.demo.util.PrintUtils;
import com.android.print.demo.util.PrinterInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static com.android.print.demo.MainActivity.mPrinter;

public class LanguageEdit extends AppCompatActivity {

    private Spinner spinner;
    private Button setLanguage;
    private Button setCode;
    private Button exit;
    private RadioGroup selectCode;
    private RadioButton rb_codepage;
    private RadioButton rb_utf;
    private RadioButton rb_unicode;
    private List<String> lanList = new ArrayList<>();
    private HashMap<Integer, String> map = new HashMap<>();
    private ArrayAdapter<String> arr_adapter;
    private String value = "PC437[美国，欧洲标准]";
    private int key = 0;
    private int position = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.language_edit);

        //初始化数据源
        initData();

        //初始化页面
        initView();
    }

    private void initView(){

        spinner = findViewById(R.id.lan_spinner);
        setLanguage = findViewById(R.id.set_lan);
        setCode = findViewById(R.id.set_code);
        selectCode = findViewById(R.id.rg_lan);
        exit = findViewById(R.id.exit);

        rb_codepage = findViewById(R.id.rb_codepage);
        rb_utf = findViewById(R.id.rb_utf);
        rb_unicode = findViewById(R.id.rb_unicode);


        //适配器
        arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lanList);

        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //加载适配器
        spinner.setAdapter(arr_adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value = parent.getItemAtPosition(position).toString();
                key = getKey(map, value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerLanguage(key);
            }
        });

        selectCode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_unicode:
                        position = 0;
                        break;
                    case R.id.rb_utf:
                        position = 1;
                        break;
                    case R.id.rb_codepage:
                        position = 2;
                        break;
                }

            }
        });

        setCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerCode();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerExit();
            }
        });

    }

    private void initData(){

        map.put(0, "PC437[美国，欧洲标准]");
        map.put(2, "PC850[多语言，西欧语]");
        map.put(3, "PC860[葡萄牙语]");
        map.put(4, "PC863[加拿大-法语]");
        map.put(5, "PC865[北欧-德语，日耳曼语]");
        map.put(6, "PC1252[West Europe]");
        map.put(7, "PC737[Greek]");
        map.put(8, "PC862[Hebrew]");
        map.put(9, "CP755[East Europe]");
        map.put(10, "Iran[伊朗]");
        map.put(11, "CP775[波罗的海语]");
        map.put(12, "CP932[日文]");
        map.put(13, "CP949[韩文]");
        map.put(14, "CP950[繁体语言]");
        map.put(15, "CP936[简体中文]");
        map.put(16, "PC1252");
        map.put(17, "PC866[Cyrillice*2]");
        map.put(18, "PC852[Latin2]");
        map.put(19, "PC858[西欧语]");
        map.put(20, "CP861[冰岛语]");
        map.put(21, "CP866[斯拉夫语/俄语]");
        map.put(22, "CP855[斯拉夫语 保加利亚]");
        map.put(23, "CP857[土耳其语]");
        map.put(24, "CP864[阿拉伯语]");
        map.put(25, "CP869[希腊语(2)]");
        map.put(32, "CP874[泰文]");
        map.put(33, "CP1250[中欧 Latin-2]");
        map.put(34, "CP1251[西里尔文 斯拉夫语 俄语]");
        map.put(35, "CP1252[西欧(拉丁文I)]");
        map.put(36, "CP1253[希腊文]");
        map.put(37, "CP1254[土耳其文]");
        map.put(38, "CP1255[希伯来文]");
        map.put(39, "CP1256[阿拉伯文]");
        map.put(40, "CP1257[波罗的海文]");
        map.put(41, "CP1258[越南]");
        map.put(42, "CP_KANNADA[卡纳达文]");
        map.put(48, "iso8859_1");
        map.put(49, "iso8859_2");
        map.put(50, "iso8859_3");
        map.put(51, "iso8859_4");
        map.put(52, "iso8859_5");
        map.put(53, "iso8859_6");
        map.put(54, "iso8859_7");
        map.put(55, "iso8859_8");
        map.put(56, "iso8859_9");
        map.put(57, "iso8859_10");
        map.put(60, "iso8859_13");
        map.put(61, "iso8859_14");
        map.put(62, "iso8859_15");
        map.put(63, "iso8859_16");

        lanList.add("PC437[美国，欧洲标准]");
        lanList.add("PC850[多语言，西欧语]");
        lanList.add("PC860[葡萄牙语]");
        lanList.add("PC863[加拿大-法语]");
        lanList.add("PC865[北欧-德语，日耳曼语]");
        lanList.add("PC1252[West Europe]");
        lanList.add("PC737[Greek]");
        lanList.add("PC862[Hebrew]");
        lanList.add("CP755[East Europe]");
        lanList.add( "Iran[伊朗]");
        lanList.add( "CP775[波罗的海语]");
        lanList.add( "CP932[日文]");
        lanList.add( "CP949[韩文]");
        lanList.add( "CP950[繁体语言]");
        lanList.add( "CP936[简体中文]");
        lanList.add( "PC1252");
        lanList.add( "PC866[Cyrillice*2]");
        lanList.add( "PC852[Latin2]");
        lanList.add( "PC858[西欧语]");
        lanList.add( "CP861[冰岛语]");
        lanList.add( "CP866[斯拉夫语/俄语]");
        lanList.add( "CP855[斯拉夫语 保加利亚]");
        lanList.add( "CP857[土耳其语]");
        lanList.add( "CP864[阿拉伯语]");
        lanList.add( "CP869[希腊语(2)]");
        lanList.add( "CP874[泰文]");
        lanList.add( "CP1250[中欧 Latin-2]");
        lanList.add( "CP1251[西里尔文 斯拉夫语 俄语]");
        lanList.add( "CP1252[西欧(拉丁文I)]");
        lanList.add( "CP1253[希腊文]");
        lanList.add( "CP1254[土耳其文]");
        lanList.add( "CP1255[希伯来文]");
        lanList.add( "CP1256[阿拉伯文]");
        lanList.add( "CP1257[波罗的海文]");
        lanList.add( "CP1258[越南]");
        lanList.add( "CP_KANNADA[卡纳达文]");
        lanList.add( "iso8859_1");
        lanList.add( "iso8859_2");
        lanList.add( "iso8859_3");
        lanList.add( "iso8859_4");
        lanList.add( "iso8859_5");
        lanList.add( "iso8859_6");
        lanList.add( "iso8859_7");
        lanList.add( "iso8859_8");
        lanList.add( "iso8859_9");
        lanList.add( "iso8859_10");
        lanList.add( "iso8859_13");
        lanList.add( "iso8859_14");
        lanList.add( "iso8859_15");
        lanList.add( "iso8859_16");

    }


    /**
     * get 语言编码
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
     * 设置打印机语言
     * @param key1
     */
    private void printerLanguage(final int key1){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printerLanguage(mPrinter, key1);
                }

            }
        }.start();
    }

    /**
     * 设置打印机编码
     */
    private void printerCode(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printerCode(mPrinter, position);
                }

            }
        }.start();
    }

    /**
     * 退出编码unicode
     */
    private void printerExit(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printerExit(mPrinter);
                }

            }
        }.start();
    }
}
