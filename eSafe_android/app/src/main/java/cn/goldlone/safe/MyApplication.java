package cn.goldlone.safe;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import cn.goldlone.safe.message.AVIMPDFMessage;
import cn.goldlone.safe.message.AVIMStoryMessage;
import cn.goldlone.safe.utils.CustomMessageHandler;
import cn.goldlone.safe.utils.FileSave;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        // 初始化存储
        FileSave.init();
        // 初始化LeanCloud
//        AVOSCloud.initialize(this,"5EUflKG1JGQaEqFChQYPL3f7-gzGzoHsz","fl3aUu5GO59LzObh2TKQh9Of");
        AVOSCloud.initialize(this,"IWjRgtEblJaLBPoRbTeJIBbY-gzGzoHsz","J0PNtKMTr23R8ktxfysrJLiG");
        // 开启调试
        AVOSCloud.setDebugLogEnabled(true);
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new CustomMessageHandler());

        AVIMMessageManager.registerAVIMMessageType(AVIMPDFMessage.class);
        AVIMMessageManager.registerAVIMMessageType(AVIMStoryMessage.class);
        CustomMessageHandler customMessageHandler = new CustomMessageHandler();
        AVIMMessageManager.registerMessageHandler(AVIMStoryMessage.class,customMessageHandler);
        AVIMMessageManager.registerMessageHandler(AVIMPDFMessage.class,customMessageHandler);
        AVIMClient.setOfflineMessagePush(false);
        initImage();
    }

    private void initImage(){
        Fresco.initialize(this);
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(android.R.color.darker_gray).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(android.R.color.holo_red_light).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }
}
