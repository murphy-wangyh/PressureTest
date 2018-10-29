package com.example.wangyuhan01.pressuretest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_audio_test;
    private Button button_shutdown_test;
    private Button button_sleep_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_audio_test = (Button) findViewById(R.id.button_audio);
        button_audio_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AudioPathActivity.class);
                startActivity(intent);
            }
        });

        button_shutdown_test = findViewById(R.id.button_sdown);
        button_shutdown_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intet = new Intent(MainActivity.this, ShutDownActivity.class);
                startActivity(intet);
            }
        });

        button_sleep_test = findViewById(R.id.go_sleep);
        button_sleep_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SleepTestActivity.class);
                startActivity(intent);
            }
        });
    }
}
