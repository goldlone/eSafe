package cn.goldlone.safe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import cn.goldlone.safe.activity.HelpActivity;
import cn.goldlone.safe.activity.MainActivity;
import cn.goldlone.safe.R;
import cn.goldlone.safe.utils.FileSave;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xunixhuang on 13/09/2016.
 */
public class MapService extends Service  {
    private NotificationManager mNM;
    private String username;
    private double longitude = 0;
    private double latitude = 0;
    private String poi;
    private int need = -1;
    private ServiceBinder binder = new ServiceBinder();
    private boolean flag = true;
    private String contact;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

//    private String myUrl = "http://115.24.13.189:8080";
//    private String myUrl = "http://192.168.1.105:8080";
    private String myUrl = "http://118.89.199.103/ycej";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        username = AVUser.getCurrentUser().getUsername();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
//        MyThread myThread = new MyThread();
//        myThread.start();

        System.out.println(need);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        contact = FileSave.getContact();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    if(longitude<0.01 || latitude<0.01)
                        continue;
                    record();
                    flag = detect();
                    // 请设置紧急联系人
                    if(contact==null || "".equals(contact))
                        showContactNotification();
                    if(!flag) {
                        String sb = (username + "的位置出现异常\n") +
                                "所处位置：\n" +
                                "经度：" + longitude + "\n" +
                                "纬度" + latitude + "\n" +
                                "大致位置：" + poi;
                        // 发送短信
//                        sendSMS(contact, sb);
                        showExceptionNotification(username);
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 记录数据
    public void record() {
        JSONObject user = new JSONObject();
        Calendar c = Calendar.getInstance();
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        c.setTime(new Date());
        try {
            user.put("username", username);
            user.put("longitude", longitude);
            user.put("latitude", latitude);
            if (weekDay == 1 || weekDay == 7) {
                user.put("week", 0);
            } else {
                user.put("week", 1);
            }
            String recStr = httpPost(myUrl+"/rec", user.toString());
            if(recStr == null || "".equals(recStr)) {
                Log.e("异常：", "请求结果为空");
                return;
            }
            JSONObject rec = new JSONObject(recStr);
            boolean res = rec.getBoolean("res");
            if(res)
                Log.i("网络请求-记录数据", "成功:"+user.toString());
            else
                Log.i("网络请求-记录数据", "失败"+user.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("异常：", "服务器异常");
        }
    }

    // 训练模型
    public void train() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            String recStr = httpPost(myUrl+"/train", json.toString());
            if(recStr == null || "".equals(recStr)) {
                Log.e("异常：", "请求结果为空");
                return;
            }
            JSONObject rec = new JSONObject(recStr);
            boolean res = rec.getBoolean("res");
            if(res) {
                Log.i("网络请求-训练数据", ""+rec.getBoolean("isTrain"));
            } else{
                Log.i("网络请求-训练数据", "训练失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 异常检测 true-无异常 false-异常
    public boolean detect() {
        JSONObject json = new JSONObject();
        Calendar c = Calendar.getInstance();
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        c.setTime(new Date());
        try {
            json.put("username", username);
            json.put("longitude", longitude);
            json.put("latitude", latitude);
            if (weekDay == 1 || weekDay == 7) {
                json.put("week", 0);
            } else {
                json.put("week", 1);
            }
            String recStr = httpPost(myUrl+"/detect", json.toString());
            if(recStr == null || "".equals(recStr)) {
                Log.e("异常：", "请求结果为空");
                return true;
            }
            JSONObject rec = new JSONObject(recStr);
            boolean res = rec.getBoolean("res");
            if(res) {
                Log.i("网络请求-异常检测", "成功:" + json.toString());
                Log.i("异常检测", ""+rec.getBoolean("isUsual"));
                return !rec.getBoolean("isUsual");
            }
            else
                Log.i("网络请求-异常检测", "失败"+json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    // 发出POST请求
    public String httpPost(String urlStr, String content) {
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            // 设置http连接属性
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            // 设置http头 消息
            connection.setRequestProperty("Content-Type", "application/json");  //设定 请求格式 json，也可以设定xml格式的
            //connection.setRequestProperty("Content-Type", "text/xml");   //设定 请求格式 xml，
            connection.setRequestProperty("Accept", "application/json");//设定响应的信息的格式为 json，也可以设定xml格式的
//             connection.setRequestProperty("X-Auth-Token","xx");  //特定http服务器需要的信息，根据服务器所需要求添加
            connection.connect();

            OutputStream out = connection.getOutputStream();
            out.write(content.getBytes());
            out.flush();
            out.close();

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            String msg = new String(sb);

            Log.i("mapService,服务器接收消息:", msg);
            reader.close();
            // 断开连接
            connection.disconnect();

            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 遗弃的方法
    public String toServer(int need) throws IOException {
        HttpURLConnection connection = null;
        if (longitude < 0.01 && longitude <= 0.01) {
            return "";
        }
        try {
            // 创建连接
            URL url = new URL("http://goldlone.cn:9090");
//            URL url = new URL("http://goldlone.cn:9090");
            connection = (HttpURLConnection) url.openConnection();

            // 设置http连接属性
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            // 设置http头 消息
            connection.setRequestProperty("Content-Type", "application/json");  //设定 请求格式 json，也可以设定xml格式的
            //connection.setRequestProperty("Content-Type", "text/xml");   //设定 请求格式 xml，
            connection.setRequestProperty("Accept", "application/json");//设定响应的信息的格式为 json，也可以设定xml格式的
//             connection.setRequestProperty("X-Auth-Token","xx");  //特定http服务器需要的信息，根据服务器所需要求添加
            connection.connect();

            JSONObject user = new JSONObject();
            Calendar c = Calendar.getInstance();
            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            c.setTime(new Date());
            user.put("username", "user"+username);
            user.put("need", need);
            user.put("longitude", longitude);
            user.put("latitude", latitude);
            if (weekDay == 1 || weekDay == 7) {
                user.put("week", 0);
            } else {
                user.put("week", 1);
            }
            System.out.println(username + " " + latitude + " " + longitude + " " + need + " " + weekDay);
//            Log.i("mapService", username + " " + latitude + " " + longitude + " " + need);
            OutputStream out = connection.getOutputStream();
            out.write((user.toString()+'\n').getBytes());
            out.flush();
            out.close();

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            String msg = new String(sb);

//            System.out.println("从服务器接收：\n"+msg);

//            Log.i("mapService", msg);
            reader.close();
            // 断开连接
            connection.disconnect();

            return msg;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "";
    }


    // 遗弃的方法
    public void appadd() throws IOException {
        HttpURLConnection connection = null;
//        if (longitude < 0.01 && longitude <= 0.01) {
//            return;
//        }
        try {
            //创建连接
            URL url = new URL("http://goldlone.cn:9090");
            connection = (HttpURLConnection) url.openConnection();

            //设置http连接属性

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            //设置http头 消息
            connection.setRequestProperty("Content-Type", "application/json");  //设定 请求格式 json，也可以设定xml格式的
            //connection.setRequestProperty("Content-Type", "text/xml");   //设定 请求格式 xml，
            connection.setRequestProperty("Accept", "application/json");//设定响应的信息的格式为 json，也可以设定xml格式的
//             connection.setRequestProperty("X-Auth-Token","xx");  //特定http服务器需要的信息，根据服务器所需要求添加
            connection.connect();


            JSONObject user = new JSONObject();
            Calendar c = Calendar.getInstance();
            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            c.setTime(new Date());
            user.put("username", "user"+username);
            user.put("need", need);
            user.put("longitude", longitude);
            user.put("latitude", latitude);
            if (weekDay == 1) {
                user.put("week", 7);
            } else {
                user.put("week", weekDay - 1);
            }
            Log.i("mapService", username + " " + latitude + " " + longitude + " " + need);
            OutputStream out = connection.getOutputStream();
            out.write((user.toString()+'\n').getBytes());
            out.flush();
            out.close();

//            读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            String msg = new String(sb);
            Log.i("mapService", msg);
            reader.close();
////              断开连接
            connection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 5;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            poi = location.getPoiList().get(0).getName();
            Intent intent = new Intent("mapService");
            intent.putExtra("radius", location.getRadius());
            intent.putExtra("POI", location.getPoiList().get(0).getName());
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            sendBroadcast(intent);
        }
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.start_location);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.start_location))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        startForeground(R.string.start_location, notification);
    }

    public void showExceptionNotification(String name) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.start_location);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)  // the status icon
                .setTicker(name+"的位置出现异常")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(name+"的位置出现异常")  // the label of the entry
                .setContentText(name+"的位置出现异常")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setAutoCancel(true)
                .build();

        // Send the notification.
        startForeground(R.string.location_exception, notification);
    }

    private void showContactNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "请设置紧急联系人";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HelpActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("远程e家")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        startForeground(R.string.app_name, notification);


//        NotificationManager barmanager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notice;
//        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.logo)  // the status icon
//                .setTicker(text)  // the status text
//                .setWhen(System.currentTimeMillis())  // the time stamp
//                .setContentTitle("远程e家")  // the label of the entry
//                .setContentText(text); // the contents of the entry
//
//        Intent appIntent=null;
//        appIntent = new Intent(this, HelpActivity.class);
//        appIntent.setAction(Intent.ACTION_MAIN);
//        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
//
//        PendingIntent contentIntent =PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notice = builder.setContentIntent(contentIntent).setContentTitle(title).setContentText(content).build();
//            notice.flags=Notification.FLAG_AUTO_CANCEL;
//            barmanager.notify(10,notice);
//        }
    }

    public class ServiceBinder extends Binder {
        public void setNeed(int needs) {
            need = needs;
        }
        public int getNeed() {
            return need;
        }
    }

    public void sendSMS(String phoneNumber,String message){
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }

    @Override
    public void onDestroy() {
        if(mLocationClient!=null)
            mLocationClient.stop();
        flag = false;
        super.onDestroy();
    }
}
