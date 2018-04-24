package cn.goldlone.safe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import cn.goldlone.safe.R;
import android.widget.Toast;
import com.avos.avoscloud.*;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.goldlone.safe.service.MessageService;
import cn.goldlone.safe.utils.CheckUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialEditText usernameEditText;
    private MaterialEditText passwordEditText;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.buttonRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.buttonLogin:
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(CheckUtils.isEffectiveStr(new String[] {username, password})) {
                    progressView.start();
                    AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser user, AVException e) {
                        progressView.stop();
                        if (e == null) {
                            startService(new Intent(LoginActivity.this, MessageService.class));
                            if((int)user.get("userType")==1){
                                PushService.setDefaultPushCallback(LoginActivity.this, MainActivity.class);       //设置推送
                                PushService.subscribe(LoginActivity.this, AVUser.getCurrentUser().getUsername(), MainActivity.class);
                                PushService.subscribe(LoginActivity.this, "public", MainActivity.class);
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }
                            else if((int)user.get("userType")==0){
                                PushService.setDefaultPushCallback(LoginActivity.this, MainActivityYoung.class);       //设置推送
                                PushService.subscribe(LoginActivity.this, AVUser.getCurrentUser().getUsername(), MainActivityYoung.class);
                                PushService.subscribe(LoginActivity.this, "public", MainActivity.class);
                                startActivity(new Intent(LoginActivity.this,MainActivityYoung.class));
                            }
                            else if((int)user.get("userType")==2){
                                PushService.setDefaultPushCallback(LoginActivity.this, MainActivityOld.class);       //设置推送
                                PushService.subscribe(LoginActivity.this, AVUser.getCurrentUser().getUsername(), MainActivityOld.class);
                                PushService.subscribe(LoginActivity.this, "public", MainActivity.class);
                                startActivity(new Intent(LoginActivity.this,MainActivityOld.class));
                            }
                            AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                                public void done(AVException e) {
                                    if (e == null) {
                                        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                                        AVUser.getCurrentUser().put("installationId",installationId);
                                        AVUser.getCurrentUser().saveInBackground();
                                    }
                                }
                            });
                            finish();
                        } else{
                            // {"code":211,"error":"Could not find user."}
                            try {
                                JSONTokener jt = new JSONTokener(e.getMessage());
                                JSONObject json = new JSONObject(jt);
                                switch (json.getInt("code")) {
                                    case 211:
                                        // 用户不存在
                                        Toast.makeText(LoginActivity.this, "用户不存在，请注册", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 210:
                                        // The username and password mismatch.
                                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(LoginActivity.this, json.getString("error"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        }
                    });
                }
        }
    }

    private void initView() {
        usernameEditText = (MaterialEditText) findViewById(R.id.usernameEditText);
        passwordEditText = (MaterialEditText) findViewById(R.id.passwordEditText);
        Button registButton = (Button) findViewById(R.id.buttonRegister);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        progressView = (ProgressView) findViewById(R.id.progressView);
        registButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }
}
