package cn.goldlone.safe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import cn.goldlone.safe.R;
import cn.goldlone.safe.utils.CheckUtils;
import cn.goldlone.safe.utils.ToastUtils;

import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 注册页面
 * Created by xunixhuang on 02/10/2016.
 */

public class RegisterActivity extends AppCompatActivity {
    private int currentType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Spinner spinner = (Spinner)findViewById(R.id.spinnerType);
        Button buttonRegister = (Button)findViewById(R.id.buttonRegister);
        final MaterialEditText usernameEdit = (MaterialEditText)findViewById(R.id.usernameEditText);
        final MaterialEditText passwdEdit = (MaterialEditText)findViewById(R.id.passwordEditText);
        final MaterialEditText passwdRepeat = (MaterialEditText)findViewById(R.id.passwordRepeatEditText);
        final ProgressView progressView = (ProgressView)findViewById(R.id.progressView);

        String[] items = new String[3];
        items[0]="儿童";
        items[1]="家长";
        items[2]="老人";
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, items);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                currentType = position;
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usernameEdit.getText().toString();
                String password = passwdEdit.getText().toString();
                String passwordRepeat=passwdRepeat.getText().toString();

                if(!CheckUtils.isEffectiveStr(new String[]{username, password, passwordRepeat})) {
                    ToastUtils.showShortToast(RegisterActivity.this, "缺少必要信息");
                    return;
                }

                if(password.equals(passwordRepeat)) {
                    progressView.start();
                    AVUser avUser = new AVUser();
                    avUser.setUsername(username);
                    avUser.put("userType", currentType);
                    avUser.setPassword(password);
                    avUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                        progressView.stop();
                        if(e == null){
                            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            finish();
                        } else{
                            // {"code":202,"error":"Username has already been taken."}
                            try {
                                JSONTokener jt = new JSONTokener(e.getMessage());
                                JSONObject json = new JSONObject(jt);
                                switch (json.getInt("code")) {
                                    case 202:
                                        ToastUtils.showShortToast(RegisterActivity.this, "该用户已存在");
                                        break;
                                    default:
                                        ToastUtils.showShortToast(RegisterActivity.this, json.getString("error"));
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        }
                    });
                } else {
                    ToastUtils.showShortToast(RegisterActivity.this, "两次密码不一致");
                }
            }
        });
    }
}
