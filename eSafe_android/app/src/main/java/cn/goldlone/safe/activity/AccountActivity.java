package cn.goldlone.safe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;

import cn.goldlone.safe.R;

/**
 * Created by xunixhuang on 09/10/2016.
 */

public class AccountActivity extends AppCompatActivity {
    private SimpleDraweeView simpleDraweeView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        simpleDraweeView=(SimpleDraweeView)findViewById(R.id.portraitView);
        Button button=(Button)findViewById(R.id.changePortrait);
        TextView textView=(TextView)findViewById(R.id.username);
        setSupportActionBar(toolbar);
        setTitle("账号设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView.setText(AVUser.getCurrentUser().getUsername());
        simpleDraweeView.setImageURI((String)AVUser.getCurrentUser().get("avatur"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(AccountActivity.this);
            }
        });
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(AccountActivity.this);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        }
        else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void beginCrop(Uri source) {
        Uri imageUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, imageUri).asSquare().start(this);
    }
    private void handleCrop(int resultCode, final Intent result) {
        if (resultCode == RESULT_OK) {
            File theFile=new File(Crop.getOutput(result).getPath());
            final AVFile file;
            try {
                file = AVFile.withAbsoluteLocalPath(AVUser.getCurrentUser().getUsername()+"_Portrait.jpg",theFile.getAbsolutePath());
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        AVUser.getCurrentUser().put("avatur",file.getUrl());
                        AVUser.getCurrentUser().saveInBackground();
                        simpleDraweeView.setImageURI(file.getUrl());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
