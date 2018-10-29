package com.example.wangyuhan01.pressuretest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShutDownActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartButton;
    private Button mStopButton;
    private static final String SHUTDOWN_SHARED_PREFERENCES = "shutdown_times";
    private static final String TEST_COUNT = "testcount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shut_down);
        mStartButton = findViewById(R.id.button_start);
        mStartButton.setOnClickListener(this);
        mStopButton = findViewById(R.id.button_stop);
        mStopButton.setOnClickListener(this);
        countinue_test();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_start:
                    StartTest();
                    break;
                case R.id.button_stop:
                    StopTest();
                    break;
            }
    };

    public void update_shutdown_setting(String key, int value)
    {
        SharedPreferences setting = getSharedPreferences(SHUTDOWN_SHARED_PREFERENCES, MODE_PRIVATE);
        setting.edit().putInt(key, value);
        setting.edit().commit();
    }

    public int get_shutdown_setting(String key)
    {
        SharedPreferences setting = getSharedPreferences(SHUTDOWN_SHARED_PREFERENCES, MODE_PRIVATE);
        int count = setting.getInt(TEST_COUNT, -1);
        return count;
    }

    public void shutdown() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            try {
                Class clazz = powerManager.getClass();
                Method shutdownMethod = clazz.getMethod("shutdown", boolean.class, String.class, boolean.class);
                shutdownMethod.invoke(powerManager, false, "userrequested", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void StartTest()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences setting = getSharedPreferences(SHUTDOWN_SHARED_PREFERENCES, MODE_PRIVATE);
                setting.edit().clear().apply();
                EditText text = findViewById(R.id.test_count);
                String mtestcount = text.getText().toString();
                int count = Integer.parseInt(mtestcount);
                update_shutdown_setting(TEST_COUNT, count);
                Toast.makeText(ShutDownActivity.this, "即将关机", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1000);
                    shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void StopTest()
    {
        Toast.makeText(ShutDownActivity.this, "停止测试", Toast.LENGTH_LONG).show();
        update_shutdown_setting(TEST_COUNT, 0);
    }

    public void countinue_test()
    {
        final int times = get_shutdown_setting(TEST_COUNT);
        if (times > 0) {
            Toast.makeText(ShutDownActivity.this, "15s后关机 " + "剩余次数" + times, Toast.LENGTH_LONG);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                        int nowtimes = get_shutdown_setting(TEST_COUNT);
                        if (nowtimes > 0){
                            update_shutdown_setting(TEST_COUNT, times-1);
                            shutdown();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

