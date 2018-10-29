package com.example.wangyuhan01.pressuretest;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;

public class SleepTestActivity extends AppCompatActivity {

    static  private String TAG  = "SLEEP_TEST";
    private PowerManager mPowerManager =  null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_test);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    public  void onClick(View v){
        switch (v.getId()){
            case R.id.do_sleep:
                offScreen();
                break;
            default:
                break;
        }
        return;
    }

    private void offScreen() {
        if (mPowerManager != null) {
            try {
                Log.i(TAG,"offScreen go to sleep");
                ToastUtils toast = new ToastUtils();
                toast.showToast(SleepTestActivity.this, "应用要经过签名才能进行此项测试");
                Class clazz = mPowerManager.getClass();
                Method offScreenMethod = clazz.getMethod("goToSleep", long.class, int.class, int.class);
                offScreenMethod.invoke(mPowerManager, SystemClock.uptimeMillis(), 4, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
