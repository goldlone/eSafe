package cn.goldlone.safe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import cn.goldlone.safe.help.RecordActivity;

/**
 * @author : Created by CN on 2018/5/10 17:13
 */
public class BaseActivity extends AppCompatActivity {

    private long firstClickTime = 0;
    private long volumeDownFirst = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP: // 隐秘录像
                clickEvent();
            case KeyEvent.KEYCODE_VOLUME_DOWN: // 定位求救
                locationHelp();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clickEvent(){
        Log.i("HelpActivity","volume up click "+firstClickTime);
        long secondClickTime = System.currentTimeMillis();
        long dtime = secondClickTime - firstClickTime;
        if(dtime < 500){
//            sendLocationMessage();
            // 实现双击
            Intent intent=new Intent(this, RecordActivity.class);
            startActivity(intent);
        } else{
            firstClickTime = System.currentTimeMillis();
        }
    }

    public void locationHelp() {
        long volumeDownSecond = System.currentTimeMillis();
        long dtime = volumeDownSecond - volumeDownFirst;
        if(dtime < 500){
//            sendLocationMessage();
        } else{
            volumeDownFirst = System.currentTimeMillis();
        }
    }
}
