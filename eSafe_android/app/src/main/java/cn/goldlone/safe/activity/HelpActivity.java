package cn.goldlone.safe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.goldlone.safe.R;
import cn.goldlone.safe.help.RecordActivity;
import cn.goldlone.safe.service.PathService;
import cn.goldlone.safe.service.PathServiceConnection;
import cn.goldlone.safe.utils.CheckUtils;
import cn.goldlone.safe.utils.FileSave;
import cn.goldlone.safe.utils.ToastUtils;

/**
 * @author : Created by CN on 2018/4/21 16:30
 */
public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    private long firstClickTime = 0;
    private long volumeDownFirst = 0;
    private PathServiceConnection conn = new PathServiceConnection();

    // 闪光灯
    private Camera camera = null;
    boolean isPreview = false;
    private int cnum;
    private TimerTask mTimerTask = null;
    private Timer mTimer = null;
    private boolean isSOSOn = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help1);

        initView();
        if(getIntent().hasExtra("contact")) {
            if(getIntent().getBooleanExtra("contact", false)) {
                showEditContact();
            }
        }

//        startService(new Intent(HelpActivity.this, PathService.class));
        bindService(new Intent(HelpActivity.this, PathService.class), conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("紧急求救");
        findViewById(R.id.cv_help_location).setOnClickListener(this);
        findViewById(R.id.cv_help_light).setOnClickListener(this);
        findViewById(R.id.cv_help_video).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cv_help_location:
                // 发送定位求救信息
                sendLocationMessage();
                break;
            case R.id.cv_help_video:
                // 隐秘录像
                Intent intent = new Intent(this, RecordActivity.class);
                startActivity(intent);
                break;
            case R.id.cv_help_light:
                // 打开或关闭闪光灯
                openOrCloseLight();
                break;
        }
    }

    /**
     * 发送定位信息
     */
    private void sendLocationMessage() {
        String msg = "";
        BDLocation location = conn.getBinder().getBDLocation();
        if(location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("【求救】我所在的位置为，");
            sb.append("纬度："+location.getLatitude());
            sb.append("，经度："+location.getLongitude());
            if(location.hasAddr()) {
                sb.append(","+location.getAddrStr()+"，");
                if(CheckUtils.isEffectiveStr(location.getLocationDescribe())) {
                    sb.append(location.getLocationDescribe());
                }
                msg = sb.toString();
            } else {
                msg = "【求救】但定位失败，无法获取到定位信息";
            }
        } else {
            msg = "定位失败，没有获取到定位信息";
        }
        // send message
        String contact = FileSave.getContact();
        if(CheckUtils.isEffectiveStr(contact)) {
            sendSMS(contact, msg);
            ToastUtils.showShortToast(this, "发送成功");
        } else
            ToastUtils.showShortToast(this, "请先设置紧急联系人");
        Log.e("求救信息", msg);
    }

    /**
     * 打开、关闭闪光灯
     */
    private void openOrCloseLight() {
        if (isSOSOn) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            isSOSOn = false;
            cnum = 0;
        } else {
            if (mTimer == null) {
                mTimer = new Timer();
            }
            if (mTimerTask == null) {
                setTimerTask();
            }
            if (mTimer != null && mTimerTask != null) {
                mTimer.schedule(mTimerTask, 0, 50);
                isSOSOn = true;
                cnum = 0;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact:
                showEditContact();
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示编辑紧急联系人的提示框
     */
    private void showEditContact() {
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.dialog_contact ,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置电话紧急联系人");
        //之前inflate的View 放到dialog中
        builder.setView(view);
        EditText editText=(EditText)view.findViewById(R.id.editText_prompt);
        editText.setText(FileSave.getContact());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ad = (AlertDialog) dialog;
                EditText editText=(EditText)ad.findViewById(R.id.editText_prompt);
                String contact=editText.getText().toString();
                if(!contact.equals("")) {
                    try {
                        FileSave.saveContact(contact);
                        Toast.makeText(HelpActivity.this,"存储成功",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

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
            sendLocationMessage();
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
            sendLocationMessage();
        } else{
            volumeDownFirst = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        if (isSOSOn) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            isSOSOn = false;
            cnum = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 闪烁闪光灯
     */
    void setTimerTask() {
        if (camera != null) {
            if (isPreview)
                camera.stopPreview();
            camera.release();
            camera = null;
        }
        camera = android.hardware.Camera.open();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                android.hardware.Camera.Parameters p = camera.getParameters();
                switch (cnum) {
                    case 0:
                    case 4:
                    case 8:
                    case 14:
                    case 22:
                    case 30:
                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        camera.startPreview();
                        break;
                    case 2:
                    case 6:
                    case 10:
                    case 18:
                    case 26:
                    case 34:
                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        camera.startPreview();
                        break;
                    default:
                        break;
                }
                if (cnum == 35)
                    cnum = 0;
                else
                    cnum++;
            }
        };
    }

    /**
     * 发送短信
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message){
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }
}
