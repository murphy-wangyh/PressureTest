package com.example.wangyuhan01.pressuretest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


public class AudioPathActivity extends AppCompatActivity {

    private String TAG = "AUDIOPATH";
    private ToastUtils toastUtils;
    private ProgressDialog progressDialog = null;
    private int mPathIndex;
    private int mProgress = 0;
    private int testcount = 0;
    private boolean mSholdStop = false;
    private Random random;
    private NumberFormat mNumberFormat =  null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("WYH","handlemessage msg.arg1=" + msg.arg1);
            if (msg.arg1 <= 100) {
                progressDialog.setProgress(msg.arg1);
                //progressDialog.show();
            }
            if (msg.arg2 == 1) {
                progressDialog.dismiss();
                toastUtils.showToast(getApplicationContext(), "测试完成");
                return;
            }
            handler.post(runnable);
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressDialog.show();
        }
    };

    private void initProgressDialog(Context context) {
        if (progressDialog != null)
            return;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);//循环滚动
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("测试中...");
        progressDialog.setCancelable(true);//false不能取消显示，true可以取消显示
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_path);
        toastUtils =  new ToastUtils();
        //initProgressDialog(this);
        random = new Random();
        mNumberFormat = NumberFormat.getInstance();
        mNumberFormat.setMaximumFractionDigits(2);
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Activity activty = this;
            ActivityCompat.requestPermissions(activty, new String[] {Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1);
            Log.i(TAG, "wyh requeset auth and set parameter");
        }
    }

    private int isHdmiArcMode()
    {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am == null)
            return -1;

        String result = am.getParameters("audio_source");
        if (result.equals("audiosource=hdmi_arc"))
            return 1;
        else
            return 0;
    }

    private void switchHdmiArc(boolean enable)
    {
        Object mhdmiControlManager =  getSystemService("hdmi_control");
        if (mhdmiControlManager != null) {
            try {
                Log.i(TAG,"mhdmiControlManager Class=" + mhdmiControlManager.getClass());
                /*
                Method hcmMethod [] = mhdmiControlManager.getClass().getMethods();
                for (int i=0;i<hcmMethod.length;i++) {
                    Log.i(TAG,"mhdmiControlManager method:" + hcmMethod[i].getName());
                }
                */
                Class<?> clsManager = Class.forName("android.hardware.hdmi.HdmiControlManager");
                Log.i(TAG,"class manager:" + clsManager.toString());
                Method getPlaybackClientMethod = clsManager.getMethod("getPlaybackClient");
                Object objPlaybackClinet = getPlaybackClientMethod.invoke(mhdmiControlManager);
                if (objPlaybackClinet != null) {
                    Log.i(TAG, "get objPlaybackClient sucess");
                    Log.i(TAG, "objPlaybackClinet Class=" + objPlaybackClinet.getClass());
                    Class<?> clsclient = Class.forName("android.hardware.hdmi.HdmiPlaybackClient");
                    Log.i(TAG, "class manager:" + clsclient.toString());
                    /*
                    Method clsMethod[] = clsclient.getMethods();
                    for (int i = 0; i < clsMethod.length; i++) {
                        Log.i(TAG, "HdmiPlaybackClient method:" + clsMethod[i].getName());
                    }
                    */
                    Method setAudioSystemDeviceEnabledMethod = clsclient.getMethod("setAudioSystemDeviceEnabled", boolean.class);
                    Log.i(TAG, "cslclient get method " + setAudioSystemDeviceEnabledMethod.getName());
                    setAudioSystemDeviceEnabledMethod.invoke(objPlaybackClinet, Boolean.valueOf(enable));
                } else {
                    Log.e(TAG,"can not get playbackclient.");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else
            Log.i(TAG,"mhdmiControlManager is null");
    }
/*
    DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Log.i(TAG,"should stop test thread");
                mSholdStop = true;
            }
            return true;
        }
    };
    */
    public void beginTest(View view) throws InterruptedException {
        EditText text_1 = (EditText) findViewById(R.id.text_1);
        try{
            testcount = Integer.parseInt(text_1.getText().toString());
        }catch(NumberFormatException e){
            Log.e(TAG, "not a integer.");
            return;
        }
        initProgressDialog(this);
        progressDialog.setProgress(0);
        mSholdStop = false;
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i("WYH","setgetprogress = " + progressDialog.getProgress());
                for (int i = 0; i < testcount; i++) {
                    if (random == null) {
                        return;
                    }
                    if (mSholdStop) {
                        Log.i(TAG,"Stop thread now");
                        i = testcount;
                        break;
                    }
                    String result = mNumberFormat.format(((float)i/(float)testcount)*100);
                    Log.i("WYH","result" + result);
                    mProgress = Integer.parseInt(result);

                    if ( (mProgress >= 0) && (mProgress <= 100)) {
                        //Log.i("WYH","mProgress=" + mProgress);
                        //Log.i("WYH","i=" + i + " testcount=" + testcount);
                        Message msg = new Message();
                        msg.arg1 = mProgress;
                        handler.sendMessage(msg);
                    }
                    mPathIndex = random.nextInt(4);
                    Log.i("WYH","i=" + i + " mPathIndex=" + mPathIndex);
                    switch (mPathIndex) {
                        case 0:
                            if (isHdmiArcMode() == 1) {
                                switchHdmiArc(false);
                            }
                            audioManager.setParameters("audio_source=system");
                            progressDialog.setMessage("system");
                            break;
                        case 1:
                            if (isHdmiArcMode() == 1) {
                                switchHdmiArc(false);
                            }
                            progressDialog.setMessage("spdif");
                            audioManager.setParameters("audio_source=spdif");
                            break;
                        case 2:
                            if (isHdmiArcMode() == 1) {
                                switchHdmiArc(false);
                            }
                            progressDialog.setMessage("aux_in");
                            audioManager.setParameters("audio_source=aux_in");
                            break;
                        case 3:
                            if (isHdmiArcMode() == 0) {
                                switchHdmiArc(true);
                            }
                            progressDialog.setMessage("hdmi_arc");
                            audioManager.setParameters("audio_source=hdmi_arc");
                            break;
                        default:
                            Log.e(TAG,"Invaild path");
                    }

                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.arg2 = 1;
                handler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();

    }

}
