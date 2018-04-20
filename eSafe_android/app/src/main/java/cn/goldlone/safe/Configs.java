package cn.goldlone.safe;

/**
 * Created by CN on 2018/4/5.
 */

public class Configs {

    /**** SharedPreferences ****/
    /** 文件名 **/
    public static final String SHARED_PREFERENCE_NAME = "eSafe";
    /** 是否开启足迹异常监测 **/
    public static final String SHARED_MONITOR = "share_monitor";



    /**** 全局变量 ****/
    /** 是否监控足迹 **/
    public static boolean isMonitor = false;
    /** 服务器地址 **/
    //    public static final String SERVER_IP = "http://192.168.1.101:8080/safe/";
    public static final String SERVER_IP = "http://115.24.13.189:8080/esafe/";

}
