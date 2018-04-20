package cn.goldlone.safe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author : Created by CN on 2018/4/10 20:37
 */
public class ToastUtils {

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


}
