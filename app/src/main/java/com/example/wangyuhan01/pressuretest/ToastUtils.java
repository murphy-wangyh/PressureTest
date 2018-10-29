package com.example.wangyuhan01.pressuretest;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by wangyuhan01 on 2018/10/23.
 */


public class ToastUtils {

    private static Toast toast;

    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

}
