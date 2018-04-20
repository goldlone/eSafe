package cn.goldlone.esafe.service;

import cn.goldlone.esafe.mapper.GeoMapper;
import cn.goldlone.esafe.model.Cluster;
import cn.goldlone.esafe.model.Result;
import cn.goldlone.esafe.monitor.ChealPoint;
import cn.goldlone.esafe.po.GPSInfo;
import cn.goldlone.esafe.po.GeoMsg;
import cn.goldlone.esafe.utils.ResultUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Created by CN on 2018/3/23 17:55 .
 */
@Service
public class GeoService {

    @Autowired
    private GeoMapper gm;

    @Autowired
    private ChealPoint cp;

    /**
     * 记录数据
     * @param object
     * @return
     */
    public String receive(JSONObject object) {
        String username = object.getString("username");
        int week = object.getInt("week");
        double longitude = object.getDouble("longitude");
        double latitude = object.getDouble("latitude");
        String time_dur = null;
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(df.format(date));
        if(hour>=0 && hour<8)
            time_dur = "morn";
        else if(hour>=8 && hour<12)
            time_dur = "noon";
        else if(hour>=12 && hour<18)
            time_dur = "after";
        else
            time_dur = "night";

        JSONObject res = new JSONObject();
        if(gm.insertGPSInfo(new GPSInfo(username, longitude, latitude, new java.sql.Date(System.currentTimeMillis()), time_dur, week))>0) {
            res.put("res", true);
        }else {
            res.put("res", false);
        }

        return res.toString();
    }


    /**
     * 训练
     * @param object
     * @return
     */
    public String train(JSONObject object) {
        String username = object.getString("username");
        boolean isTrain = false;
//        ChealPoint cp = new ChealPoint();
        isTrain = cp.train(username);
        JSONObject res = new JSONObject();
        res.put("isTrain", isTrain);
        res.put("res", true);
        return res.toString();
    }


    /**
     * 检测轨迹点是否异常
     * @param object
     * @return
     */
    public String detect(JSONObject object) {
        String username = object.getString("username");
        int week = object.getInt("week");
        double longitude = object.getDouble("longitude");
        double latitude = object.getDouble("latitude");
        boolean isUsual = false;
        String time_dur = null;
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(df.format(date));
        if(hour>=0 && hour<8)
            time_dur = "morn";
        else if(hour>=8 && hour<12)
            time_dur = "noon";
        else if(hour>=12 && hour<18)
            time_dur = "after";
        else
            time_dur = "night";
//        ChealPoint cp = new ChealPoint();
        isUsual = cp.detect(username, week, longitude, latitude, time_dur);
        JSONObject res = new JSONObject();
        res.put("isUsual", isUsual);
        res.put("res", true);
        return res.toString();
    }

    public Result getClusterCenter(String username) {
        GeoMsg gm1 = gm.selectGeoMsg(username, 1);
        GeoMsg gm2 = gm.selectGeoMsg(username, 0);

        Set<Cluster> set = new HashSet<>();
        String geos =  null;

        if(gm1 != null) {
            geos = gm1.getMornplace();
            set.addAll(insertCluster(geos));
            geos = gm1.getNoonplace();
            set.addAll(insertCluster(geos));
            geos = gm1.getAfterplace();
            set.addAll(insertCluster(geos));
            geos = gm1.getNightplace();
            set.addAll(insertCluster(geos));
        }

        if(gm2 != null) {
            geos = gm2.getMornplace();
            set.addAll(insertCluster(geos));
            geos = gm2.getNoonplace();
            set.addAll(insertCluster(geos));
            geos = gm2.getAfterplace();
            set.addAll(insertCluster(geos));
            geos = gm2.getNightplace();
            set.addAll(insertCluster(geos));
        }

        Iterator<Cluster> it = set.iterator();
        while(it.hasNext()) {
            System.out.println(it.next());
        }
        return ResultUtils.success(set, "获取成功");
    }

    private Set<Cluster> insertCluster(String geos) {
        Set<Cluster> set = new HashSet<>();
        if(geos!=null && !"".equals(geos)) {
            String[] s1 = geos.split("//");
            String[] s2 = null;
            for (String str : s1) {
                s2 = str.split(" ");
                if (s2.length == 3)
                    set.add(new Cluster(Double.parseDouble(s2[1]), Double.parseDouble(s2[0]), Double.parseDouble(s2[2])));
            }
        }
        return set;
    }
}
