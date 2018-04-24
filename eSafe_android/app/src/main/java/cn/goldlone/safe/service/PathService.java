package cn.goldlone.safe.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * @author : Created by CN on 2018/4/22 17:27
 */
public class PathService extends Service {

    private String username = null;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private BDLocation bdLocation = null;
    private LocationBinder locationBinder = new LocationBinder();

    @Override
    public void onCreate() {
        super.onCreate();
//        username = AVUser.getCurrentUser().getUsername();
        initLocationOptions();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return locationBinder;
    }

    public class LocationBinder extends Binder {
        public BDLocation getBDLocation() {
            return bdLocation;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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

        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        option.setIsNeedLocationPoiList(true);

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
     * 定位监听器
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null)
                return;
            bdLocation = location;
            // 此处的BDLocation为定位结果信息类，通过它的各种get方法可获+取定位相关的全部结果
            // 以下只列举部分获取经纬度相关（常用）的结果信息
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

            // 以下只列举部分获取地址相关的结果信息
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息

            logE(addr);
//            logE(country);
//            logE(province);
//            logE(city);
//            logE(district);
//            logE(street);
            logE(locationDescribe);
//
//            List<Poi> poiList = location.getPoiList();
//            if(poiList!=null) {
//                for (Poi poi : poiList) {
//                    ;
//                    logE("---------");
//                    logE("" + poi.getId());
//                    logE(poi.getName());
//                    logE("" + poi.getRank());
//                    logE("---------");
//                }
//            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(mLocationClient.isStarted())
            mLocationClient.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if(mLocationClient.isStarted())
            mLocationClient.stop();
        super.onDestroy();
    }

    private void logE(String msg) {
        Log.e("PathService", ""+msg);
    }
}
