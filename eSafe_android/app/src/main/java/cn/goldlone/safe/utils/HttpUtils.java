package cn.goldlone.safe.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.goldlone.safe.Configs;
import cn.goldlone.safe.model.Cluster;
import cn.goldlone.safe.utils.FileSave;

/**
 * @author : Created by CN on 2018/4/9 23:25
 */
public class HttpUtils {

    public static String post(String urlPath, String json) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Charset", "UTF-8");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            conn.connect();
            conn.setConnectTimeout(10000);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            if(conn.getResponseCode() == 200){
                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                int len = 0;
                byte[] buf = new byte[128];
                while((len = is.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上传求救视频
     * @return
     */
    public static String sendFile() {
        try {
            String urlPath = Configs.SERVER_IP+"help/video/upload";
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "video/mpeg");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Charset", "UTF-8");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            conn.connect();
            conn.setConnectTimeout(10000);

            OutputStream os = conn.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(new File(FileSave.getRootPath()+"video.mp4"));
            byte[] bytes = new byte[1024];
            int len;
            while((len=fileInputStream.read(bytes))!=-1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            os.close();

            if(conn.getResponseCode() == 200){
                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                len = 0;
                byte[] buf = new byte[128];
                while((len = is.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                System.out.println("求救上传成功");
                System.out.println(sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Cluster> getCluster(String username) {
        List<Cluster> list = new ArrayList<>();
        try {
            URL url = new URL(Configs.SERVER_IP+"/cluster/data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Charset", "UTF-8");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            conn.connect();
            conn.setConnectTimeout(10000);

            OutputStream os = conn.getOutputStream();
            os.write(("username="+URLEncoder.encode(username)).getBytes());
            os.flush();
            os.close();

            if(conn.getResponseCode() == 200){
                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                int len = 0;
                byte[] buf = new byte[128];
                while((len = is.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                try {
                    JSONTokener jt = new JSONTokener(sb.toString());
                    JSONObject json = new JSONObject(jt);
                    if(json.getInt("code") == 1001) {
                        JSONArray arr = json.getJSONArray("data");
                        JSONObject tmp = null;
                        Cluster cluster = null;
                        for(int i=0; i<arr.length(); i++) {
                            tmp = arr.getJSONObject(i);
                            cluster = new Cluster(tmp.getDouble("latitude"),
                                    tmp.getDouble("longitude"), tmp.getDouble("radius"));
                            list.add(cluster);
                        }
                    } else {
                        Log.e("获取聚类数据", "【失败】:"+json.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }



}
