package cn.goldlone.safe.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import cn.goldlone.safe.R;

/**
 * @author : Created by CN on 2018/4/24 10:42
 */
public class NotificationUtils {

    public static Notification buildNotification(Context context, Intent intent, String title, String content) {
        Notification.Builder builder = new Notification.Builder(context)
            // 设置打开该通知，该通知自动消失
            .setAutoCancel(true)
            // 设置显示在状态栏的通知提示信息
            .setTicker(context.getString(R.string.app_name))
            // 设置通知的图标
            .setSmallIcon(R.drawable.logo)
            // 设置通知内容的标题
            .setContentTitle(title)
            // 设置通知内容
            .setContentText(content)
            // 设置使用系统默认的声音、默认LED灯
            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
            // 设置震动
            .setVibrate(new long[]{0, 50, 100, 150})
            // 设置通知的自定义声音
//                .setSound(Uri.parse("android.resource://cn.goldlone.safe/"+R.raw.msg))
            .setWhen(System.currentTimeMillis());
        if(intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 设置通知要启动的Intent
            builder.setContentIntent(pi);
        }
        return builder.build();
    }
}
