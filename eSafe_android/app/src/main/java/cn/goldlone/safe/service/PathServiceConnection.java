package cn.goldlone.safe.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @author : Created by CN on 2018/4/22 18:45
 */
public class PathServiceConnection implements ServiceConnection {
    private PathService.LocationBinder binder;

    /**
     * 当Activity与Service连接成功时回调
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (PathService.LocationBinder) service;
    }

    /**
     * 当Activity与Service断开连接时回调
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public PathService.LocationBinder getBinder() {
        return binder;
    }
}
