package com.beproj.camcoder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    EditText outputText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String output = intent.getStringExtra("output");

        //Log.d(TAG,image_name);
        outputText = (EditText) findViewById(R.id.result_edittext);
        outputText.setText(output);

        button = (Button) findViewById(R.id.button);
    }

    public void newTask(View view) {
        Intent new_task = new Intent(Result.this,MainActivity.class);
        startActivity(new_task);
    }
}
