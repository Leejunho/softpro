package com.example.myapplication.util;

import android.app.Activity;
import android.widget.Toast;

public class util_util {
    private static Toast sToast;

    public util_util() {}

    // Toast 메시지를 출력할 경우 아무리 여러번 메시지를 출력하더라도 마지막 메시지만 노출이 됨
    public static void showToast(Activity activity, String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }
}
