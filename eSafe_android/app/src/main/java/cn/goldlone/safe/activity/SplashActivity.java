package cn.goldlone.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.avos.avoscloud.AVUser;

import cn.goldlone.safe.Configs;
import cn.goldlone.safe.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xunixhuang on 02/10/2016.
 */

public class SplashActivity extends Activity {
    // 延时时间（毫秒）
    private static final long DELAY = 1000;
    private boolean scheduled = false;
    private Timer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        getPersimmions();

        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.finish();
                if(AVUser.getCurrentUser() == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else{
                    if((int)AVUser.getCurrentUser().get("userType")==0){
                        startActivity(new Intent(SplashActivity.this,MainActivityYoung.class));
                    } else if((int)AVUser.getCurrentUser().get("userType")==1) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else if((int)AVUser.getCurrentUser().get("userType")==2){
                        startActivity(new Intent(SplashActivity.this,MainActivityOld.class));
                    }
                }
            }
        }, DELAY);
        scheduled = true;

        Configs.isMonitor = getSharedPreferences(Configs.SHARED_PREFERENCE_NAME, MODE_PRIVATE).getBoolean(Configs.SHARED_MONITOR, false);
    }

//    @TargetApi(23)
//    private void getPersimmions() {
//        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
//        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
//        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS);
//        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
//        if(checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_WIFI_STATE);
//        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO);
//        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduled)
            splashTimer.cancel();
        splashTimer.purge();
    }
}
