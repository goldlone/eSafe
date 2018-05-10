package cn.goldlone.safe.activity;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import com.avos.avoscloud.*;

import cn.goldlone.safe.Configs;
import cn.goldlone.safe.MyApplication;
import cn.goldlone.safe.R;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.sevenheaven.iosswitch.ShSwitchView;
import cn.goldlone.safe.adapter.FriendAdapter;
import cn.goldlone.safe.model.Cluster;
import cn.goldlone.safe.service.MapService;
import cn.goldlone.safe.utils.HttpUtils;
import cn.goldlone.safe.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xunixhuang on 05/10/2016.
 */

public class PathActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;
    private ShSwitchView shSwitchView;

    // BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    // 原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;

    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        sharedPreferences = getSharedPreferences(Configs.SHARED_PREFERENCE_NAME, MODE_PRIVATE);

        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMapView = (TextureMapView) findViewById(R.id.mapView);
        shSwitchView = (ShSwitchView) findViewById(R.id.switch_view);
        shSwitchView.setOn(Configs.isMonitor);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        initLocationOptions();

        shSwitchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                Configs.isMonitor = isOn;
                editor = sharedPreferences.edit();
                editor.putBoolean(Configs.SHARED_MONITOR, Configs.isMonitor);
                editor.apply();
                editor.commit();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.usualPath:
                        AlertDialog.Builder builder0 = new AlertDialog.Builder(PathActivity.this);
                        builder0.setTitle("请选择查看的用户");
                        if(FriendAdapter.getRemarks() == null)
                            break;
                        final String[] username0 = new String[FriendAdapter.getRemarks().size() + 1];
                        username0[FriendAdapter.getRemarks().size()] = "自己";
                        for (int i = 0; i < FriendAdapter.getRemarks().size(); i++) {
                            username0[i] = FriendAdapter.getRemarks().get(i);
                        }
                        builder0.setItems(username0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(FriendAdapter.getUsers().size()==which)
                                    username = AVUser.getCurrentUser().getUsername();
                                else
                                    username = FriendAdapter.getUsers().get(which).getUsername();
                                ToastUtils.showShortToast(PathActivity.this, "请稍等...");
                                new Thread() {
                                    @Override
                                    public void run() {
                                        mBaiduMap.clear();
                                        if(username==null || "".equals(username))
                                            return;
                                        List<Cluster> list = HttpUtils.getCluster(username);
                                        Log.e("TAG", ""+list.size());
                                        if(list.size()==0) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtils.showShortToast(PathActivity.this, "没有找到轨迹数据");
                                                }
                                            });
                                            return;
                                        }
                                        //创建OverlayOptions的集合
                                        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
//                                        // 图像
//                                        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                                .fromResource(R.drawable.ic_location_on_black_24dp);
                                        // 设置坐标点
                                        LatLng point = null;
//                                        OverlayOptions option = null;
                                        for(Cluster c: list) {
                                            point = new LatLng(c.getLatitude(), c.getLongitude());
//                                            option = new MarkerOptions()
//                                                    .position(point)
//                                                    .icon(bitmap);
//                                            options.add(option);
                                            OverlayOptions ooCircle = new CircleOptions().fillColor(0x33FF3300)
                                                    .center(point).stroke(new Stroke(1, 0x00000000))
                                                    .radius((int)c.getRadius());
                                            mBaiduMap.addOverlay(ooCircle);
                                        }
                                        //在地图上批量添加
                                        mBaiduMap.addOverlays(options);
                                    }
                                }.start();
                            }
                        });
                        builder0.create().show();
                        break;
//                    case R.id.findPathWeek:
//                        AlertDialog.Builder builder = new AlertDialog.Builder(PathActivity.this);
//                        builder.setTitle("请选择查看的用户");
//                        if(FriendAdapter.getRemarks() == null)
//                            break;
//                        final String[] username = new String[FriendAdapter.getRemarks().size() + 1];
//                        username[FriendAdapter.getRemarks().size()] = "自己";
//                        for (int i = 0; i < FriendAdapter.getRemarks().size(); i++) {
//                            username[i] = FriendAdapter.getRemarks().get(i);
//                        }
//                        builder.setItems(username, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                TaskWeek task = new TaskWeek();
////                                if (which < FriendAdapter.getRemarks().size()) {
////                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FriendAdapter.getUsers().get(which).getUsername());
////                                } else {
////                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, AVUser.getCurrentUser().getUsername());
////                                }
//                            }
//                        });
//                        builder.create().show();
//                        break;
//                    case R.id.findPathMonth:
//                        AlertDialog.Builder builder2 = new AlertDialog.Builder(PathActivity.this);
//                        builder2.setTitle("请选择查看的用户");
//                        if(FriendAdapter.getRemarks() == null)
//                            break;
//                        final String[] username2 = new String[FriendAdapter.getRemarks().size() + 1];
//                        username2[FriendAdapter.getRemarks().size()] = "自己";
//                        for (int i = 0; i < FriendAdapter.getRemarks().size(); i++) {
//                            username2[i] = FriendAdapter.getRemarks().get(i);
//                        }
//                        builder2.setItems(username2, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                if (which < FriendAdapter.getRemarks().size()) {
////                                    TaskMonth task2 = new TaskMonth();
////                                    task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FriendAdapter.getUsers().get(which).getUsername());
////                                } else {
////                                    TaskMonth task2 = new TaskMonth();
////                                    task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, AVUser.getCurrentUser().getUsername());
////                                }
//                            }
//                        });
//                        builder2.create().show();
//                        break;
                }
                return true;
            }
        });
    }

    /**
     * 初始化定位选项
     */
    private void initLocationOptions() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        // 可选，设置定位模式，默认高精度
        // LocationMode.Hight_Accuracy：高精度；
        // LocationMode. Battery_Saving：低功耗；
        // LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        // 可选，设置返回经纬度坐标类型，默认gcj02
        // gcj02：国测局坐标；
        // bd09ll：百度经纬度坐标；
        // bd09：百度墨卡托坐标；
        // 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setCoorType("bd09ll");

        // 可选，设置发起定位请求的间隔，int类型，单位ms
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(5000);

        // 可选，设置是否使用gps，默认false
        // 使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        // 可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        // 可选，定位SDK内部是一个service，并放到了独立进程。
        // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        // 可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        // 可选，7.2版本新增能力
        // 如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setWifiCacheTimeOut(5*60*1000);

        // 可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        // mLocationClient为第二步初始化过的LocationClient对象
        // 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        // 更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);

        // mLocationClient为第二步初始化过的LocationClient对象
        // 调用LocationClient的start()方法，便可发起定位请求
        mLocationClient.start();
    }

    /**
     * 位置监听内部类
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            // 此处的BDLocation为定位结果信息类，通过它的各种get方法可获+取定位相关的全部结果
            // 以下只列举部分获取经纬度相关（常用）的结果信息
            // 更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            // 获取纬度信息
            double latitude = location.getLatitude();
            // 获取经度信息
            double longitude = location.getLongitude();
            // 获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            // 获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            // 获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

            switch (errorCode) {
                case 61:
                    Log.e("定位结果", "GPS定位结果，GPS定位成功");
                    break;
                case 66:
                    Log.e("定位结果", "离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果");
                    break;
                case 161:
                    Log.e("定位结果", "网络定位结果，网络定位成功");
                    break;
                case 67:
                    Log.e("定位失败", "请检查网络连接");
                    return;
                default:
                    Log.e("定位结果", "errorCode = "+errorCode);
                    break;
            }
            Log.e("定位结果", latitude+" : "+longitude+" -> "+radius);
            if(location.hasAddr())
                Log.e("定位结果", location.getAddrStr());
            Log.e("定位结果", location.getProvince()+"/"+location.getCity()+"/"+location.getDistrict());

            MyLocationData locData = new MyLocationData.Builder()
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            // 设置定位数据
            if (mBaiduMap != null) {
                mBaiduMap.setMyLocationData(locData);
                LatLng centerPoint = new LatLng(latitude, longitude);
                // 定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .zoom(18)
                        .target(centerPoint)
                        .build();
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.setMapStatus(mMapStatusUpdate);
            }
            if (mLocationClient.isStarted())
                mLocationClient.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient.isStarted())
            mLocationClient.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.path_menu, menu);
        return true;
    }
}
