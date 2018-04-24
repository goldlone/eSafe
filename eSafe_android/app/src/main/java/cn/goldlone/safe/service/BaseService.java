package cn.goldlone.safe.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.goldlone.safe.Configs;
import cn.goldlone.safe.R;
import cn.goldlone.safe.activity.HelpActivity;
import cn.goldlone.safe.utils.FileSave;
import cn.goldlone.safe.utils.NotificationUtils;

/**
 * @author : Created by CN on 2018/4/24 10:17
 */
public class BaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BaseService", "启动baseService");
        String contact = FileSave.getContact();
        if (contact == null || "".equals(contact)) {
            showContactNotification();
        }
    }

    /**
     * 提醒添加紧急联系人
     */
    private void showContactNotification() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("contact", true);
        Notification nf = NotificationUtils.buildNotification(this, intent,
                getString(R.string.app_name), "请设置紧急联系人");
        startForeground(Configs.NOTIFICATION_CONTACT_ID, nf);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
