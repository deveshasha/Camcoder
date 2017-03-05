package com.beproj.camcoder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class adjust extends AppCompatActivity {

    private EditText mCodeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        Intent intent = getIntent();
        String code = intent.getStringExtra("code");


        mCodeText = (EditText) findViewById(R.id.editText);
        mCodeText.setText(code, TextView.BufferType.EDITABLE);

    }
}
