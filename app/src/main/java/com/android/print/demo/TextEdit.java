package com.android.print.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.print.demo.util.PrintUtils;

import static com.android.print.demo.MainActivity.mPrinter;

public class TextEdit extends AppCompatActivity {

    private Context context;
    private EditText textData;
    private Button sendButton;
    private Button sendDefault;
    private String text_data;
    private RadioButton rb_text;
    private RadioButton rb_hex;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.text_edit);

        context = this;

        initView();
    }

    private void initView() {

        textData = findViewById(R.id.textData);

        rb_hex = findViewById(R.id.rb_hex);
        rb_text = findViewById(R.id.rb_text);

        sendButton = findViewById(R.id.textSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text_data = textData.getText().toString().trim();

                if (mPrinter != null && (!"".equals(text_data))) {
                    if (rb_text.isChecked()){
                        send_text_data();
                    }else{
                        orderSend();
                    }

                }

            }
        });

        sendDefault = findViewById(R.id.sendDefault);
        sendDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrinter != null)
                    send_default_data();
            }
        });

    }

    public void send_text_data(){
        new Thread(){
            @Override
            public void run() {

                synchronized (MainActivity.class){
                    mPrinter.init();
                    text_data = textData.getText().toString().trim()+"\r\n";
                    mPrinter.printText(text_data);
                }

            }
        }.start();
    }

    private void orderSend(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    byte[] ord_bytes = new byte[32];

                    String[] ord_str = textData.getText().toString().trim().split(" ");

                    for (int i=0; i<ord_str.length; i++){
                        ord_bytes[i] = (byte)Integer.parseInt(ord_str[i],16);
                    }

                    mPrinter.init();
                    mPrinter.sendByteData(ord_bytes);
                }

            }
        }.start();
    }


    public void send_default_data(){
        new Thread(){
            @Override
            public void run() {
                synchronized (MainActivity.class){
                    PrintUtils.printText(context.getResources(), mPrinter);
                }
            }
        }.start();
    }
}
