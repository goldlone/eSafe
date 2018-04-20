package cn.goldlone.esafe.monitor;

import cn.goldlone.esafe.mapper.GeoMapper;
import cn.goldlone.esafe.model.LatLng;
import cn.goldlone.esafe.model.MinMaxGps;
import cn.goldlone.esafe.model.SelectCount;
import cn.goldlone.esafe.po.GeoMsg;
import cn.goldlone.esafe.po.Temselect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class ChealPoint {
    private String[][] s_class;
    @Autowired
    private GeoMapper geoMapper;

    public ChealPoint(){
        s_class = new String[2][4];
        for(int i=0;i<2;i++) {
            for(int j=0;j<4;j++){
                s_class[i][j] = "";
            }
        }
    }

    /**
     * 保存轨迹信息
     * @param username
     * @param sclass
     */
    private void save(String username, String[][] sclass) {
        if(geoMapper.selectUserTail(username) == 0) {
            geoMapper.insertTail(new GeoMsg(username, 0, sclass[0][0], sclass[0][1], sclass[0][2], sclass[0][3]));
            geoMapper.insertTail(new GeoMsg(username, 1, sclass[1][0], sclass[1][1], sclass[1][2], sclass[1][3]));
            System.out.println("创建用户数据");
        } else {
            geoMapper.updateTail(new GeoMsg(username, 0, sclass[0][0], sclass[0][1], sclass[0][2], sclass[0][3]));
            geoMapper.updateTail(new GeoMsg(username, 1, sclass[1][0], sclass[1][1], sclass[1][2], sclass[1][3]));
            System.out.println("更新用户数据");
        }
    }

    /**
     * 训练模型，进行聚类
     * @param username 用户名
     * @return 训练是否成功
     */
    public boolean train(String username) {
        int[] weeks = {0, 1};
        String[] timeDurs = {"morn", "noon", "after", "night"};
        List<LatLng> latLngList = null;
        MinMaxGps minMaxGps = null;
        int n = 0;
        double[] lg = null;
        double[] lt = null;
        // 周末与非周末的数据依次处理
        for(int i=0; i<weeks.length; i++) {
            // 早上、中午、下午、晚上的数据依次处理
            for(int j=0; j<timeDurs.length; j++) {
                latLngList = geoMapper.selectLatLng(new SelectCount(username, "", weeks[i], timeDurs[j], Date.valueOf("2015-01-01"), new Date(System.currentTimeMillis())));
                if(latLngList == null) {
                    s_class[i][j] = "";
                    break;
                }
                n = latLngList.size();
                if(n != 0) {
                    minMaxGps = geoMapper.selectMinMax(new SelectCount(username, "", weeks[i], timeDurs[j], Date.valueOf("2015-01-01"), new Date(System.currentTimeMillis())));
                    PathPoint[] pp1 = new PathPoint[n];
                    Grid[] gd1 = new Grid[n];
                    lg =  new double[2];
                    lt = new double[2];
                    lg[0] = minMaxGps.getMinLongitude();
                    lg[1] = minMaxGps.getMaxLongitude();
                    lt[0] = minMaxGps.getMinLatitude();
                    lt[1] = minMaxGps.getMaxLatitude();
                    System.out.println("-削减前-: "+n);
                    while(n>9000) {
                        latLngList = reduce(latLngList);
                        n = latLngList.size();
                    }
                    System.out.println("-削减后-: "+n);
                    for(int ii=0; ii<n; ii++) {
                        gd1[ii] = new Grid(lg, lt, 0, n);
                    }
                    GrideManage gm = new GrideManage(gd1);
                    Windowp wd = new Windowp(pp1, 0, gm, n);
                    for(LatLng latLng: latLngList) {
                        wd.length++;
                        wd.getPP()[wd.length-1]= new PathPoint(latLng.getLongitude(), latLng.getLatitude());
                    }
                    String get_st = wd.Online(gm);
                    System.out.println(weeks[i]+" : "+timeDurs[j]);
                    s_class[i][j] += get_st;
                } else {
                    s_class[i][j] = "";
                }
            }
        }
        // 保存训练结果
        save(username, s_class);
        System.out.println("训练结束");
        return true;
    }


    /**
     * 检测轨迹点是否异常
     * @param username 用户名
     * @param week 是否周末 0-周末 1-非周末
     * @param log 纬度
     * @param lat 经度
     * @param tperiod 上午(morn)，中午(noon)，下午(after)，晚上(night)
     * @return 是否异常点
     */
    public boolean detect(String username, int week, double log, double lat, String tperiod) {
        boolean unusual = false;
        // 阈值
        double d = 0;
        // 与聚类模型间的距离
        double distance = 0;
        // 查询聚类信息
        GeoMsg geoMsg = geoMapper.selectGeoMsg(username, week);
        if(geoMsg == null) {
            unusual = false;
            System.out.println("未找到聚类模型");
            return false;
        }
        String modelData = null;
        // 时段分类
        switch (tperiod) {
            case "morn":
                modelData = geoMsg.getMornplace();
                break;
            case "noon":
                modelData = geoMsg.getNoonplace();
                break;
            case"after":
                modelData = geoMsg.getAfterplace();
                break;
            case "night":
                modelData = geoMsg.getNightplace();
                break;
        }
        if(modelData==null || "".equals(modelData)) {
            unusual = false;
            System.out.println("未找到聚类模型");
            return false;
        }
        // 切割分离的模型数据，分离出一个一个的类
        String[] sp = modelData.split("//");
        PathPoint pdata = new PathPoint(log, lat);
        for (String sp0 : sp) {
            // 再次切割 三块  （经度，纬度，距离的阈值）
            String sp_data[] = sp0.split(" ");
            PathPoint p = new PathPoint(Double.parseDouble(sp_data[0]), Double.parseDouble(sp_data[1]));//standard
            distance = pdata.distanceG(p);
            d = Double.parseDouble(sp_data[2]);
            if ((distance - d) > 0) {
                unusual = true;
                break;
            }
        }

        System.out.println("测试点与模型点的距离："+distance);
        System.out.println("阈值："+d);
        System.out.println("距离差："+(distance-d));

//        if(unusual) {
//            geoMapper.insertTemselect(new Temselect(username, log, lat));
//        }
        return unusual;
    }

    /**
     * 查询
     * @param username
     * @param timebt
     * @param week
     * @return
     */
    public boolean search(String username, String timebt, int week) {
        String[] stse = timebt.split("//");
        int n2 = 0;
        List<LatLng> latLngList = null;
        MinMaxGps minMaxGps = null;
        latLngList = geoMapper.selectLatLngByDate(new SelectCount(username, "", -1, "", Date.valueOf(stse[0]), Date.valueOf(stse[1])));
        n2 = latLngList.size();
        if(n2==0){
//            Chealunusual(username);
            return false;
        }
        minMaxGps = geoMapper.selectMinMaxByDate(new SelectCount(username, "", -1, "", Date.valueOf(stse[0]), Date.valueOf(stse[1])));
        double[] lng =  new double[2];
        double[] latse = new double[2];
        lng[0] = minMaxGps.getMinLongitude();
        lng[1] = minMaxGps.getMaxLongitude();
        latse[0] = minMaxGps.getMinLatitude();
        latse[1] = minMaxGps.getMaxLatitude();
        PathPoint[] ppse = new PathPoint[n2];
        Grid[] gdse = new Grid[n2];
        for(int i=0;i<n2;i++){
            gdse[i] = new Grid(lng,latse,0,n2);
        }
        GrideManage gmse = new GrideManage(gdse);
        Windowp wdse = new Windowp(ppse,0,gmse,n2);
        int k=0;
        for(LatLng latLng: latLngList) {
            k++;
            wdse.length++;
            wdse.getPP()[wdse.length-1]= new PathPoint(latLng.getLongitude(), latLng.getLatitude());
        }
        String get_stse = wdse.Online(gmse);
        wdse.offline(gmse);
        String[] st_class = get_stse.split("//");
        geoMapper.deleteTemselect(username);
        for (String st_clas : st_class) {
            String[] selectclass = st_clas.split(" ");
            double logts = Double.parseDouble(selectclass[0]);
            double lats = Double.parseDouble(selectclass[1]);
            geoMapper.insertTemselect(new Temselect(username, logts, lats));
        }
        return true;
    }
//
//    /**
//     * 训练，异常，查询的综合方法
//     * @param username		用户名
//     * @param timebt
//     * 						0-训练：时间段 1970-10-10//2016-10-07
//     * 						1-检测异常：数据点以及所处时段（时间段的取值为：0上午，1中午，2下午，3晚上）116.5//37.5//0
//     * 						2-查询：时间段1970-10-10//2016-10-07
//     * @param week			周末与非周末
//     * @param demand		指令（0-训练、1-异常、2-查询）
//     * @throws Exception
//     */
//    public boolean finishCheal(String username,
//                               String timebt,
//                               int week,
//                               int demand)
//            throws Exception{
//        switch (demand) {
//            case 0: // 训练
//                String[] st = timebt.split("//");
//                int[] weeks = {0, 1};
//                String[] timeDurs = {"morn", "noon", "after", "night"};
//
//                List<LatLng> latLngList = null;
//                MinMaxGps minMaxGps = null;
//                int n = 0;
//                double[] lg = null;
//                double[] lt = null;
//                // 周末与非周末的数据依次处理
//                for(int i=0; i<weeks.length; i++) {
//                    // 早上、中午、下午、晚上的数据依次处理
//                    for(int j=0; j<timeDurs.length; j++) {
//                        latLngList = geoMapper.selectLatLng(new SelectCount(username, "", weeks[i], timeDurs[j], Date.valueOf(st[0]), Date.valueOf(st[1])));
//                        n = latLngList.size();
//                        if(n != 0) {
//                            minMaxGps = geoMapper.selectMinMax(new SelectCount(username, "", weeks[i], timeDurs[j], Date.valueOf(st[0]), Date.valueOf(st[1])));
//                            PathPoint[] pp1 = new PathPoint[n];
//                            Grid[] gd1 = new Grid[n];
//                            lg =  new double[2];
//                            lt = new double[2];
//                            lg[0] = minMaxGps.getMinLongitude();
//                            lg[1] = minMaxGps.getMaxLongitude();
//                            lt[0] = minMaxGps.getMinLatitude();
//                            lt[1] = minMaxGps.getMaxLatitude();
//                            System.out.println("-削减前-: "+n);
//                            while(n>9000) {
//                                latLngList = reduce(latLngList);
//                                n = latLngList.size();
//                            }
//                            System.out.println("-削减后-: "+n);
//                            for(int ii=0; ii<n; ii++) {
//                                gd1[ii] = new Grid(lg, lt, 0, n);
//                            }
//                            GrideManage gm = new GrideManage(gd1);
//                            Windowp wd = new Windowp(pp1, 0, gm, n);
//                            for(LatLng latLng: latLngList) {
//                                wd.length++;
//                                wd.getPP()[wd.length-1]= new PathPoint(latLng.getLongitude(), latLng.getLatitude());
//                            }
//                            String get_st = wd.Online(gm);
//                            System.out.println(weeks[i]+" : "+timeDurs[j]);
//                            s_class[i][j] += get_st;
//                        } else {
//                            s_class[i][j] = "";
//                        }
//                    }
//                }
//                // 保存训练结果
//                save(username, s_class);
//                System.out.println("训练结束");
//                return true;
//
//            case 1: // 检测异常
//                boolean unusual = false;
//                String[] st_data = timebt.split("//");
//                // 获取判断异常的精度
//                Double log = Double.parseDouble(st_data[0]);
//                // 获取判断异常的纬度
//                Double lat = Double.parseDouble(st_data[1]);
//                // 获取判断异常的时段
//                int tperiod = Integer.parseInt(st_data[2]);
//                // 阈值
//                double d = 0;
//                // 与聚类模型间的距离
//                double distance = 0;
//                // 查询聚类信息
//                GeoMsg geoMsg = geoMapper.selectGeoMsg(username, week);
//                String modelData = null;
//                // 时段分类
//                switch (tperiod) {
//                    case 0:
//                        modelData = geoMsg.getMornplace();
//                        break;
//                    case 1:
//                        modelData = geoMsg.getNoonplace();
//                        break;
//                    case 2:
//                        modelData = geoMsg.getAfterplace();
//                        break;
//                    case 3:
//                        modelData = geoMsg.getNightplace();
//                        break;
//                }
//                if(modelData==null || "".equals(modelData)) {
//                    unusual = true;
//                    System.out.println("未找到聚类模型");
//                    return true;
//                }
//                // 切割分离的模型数据，分离出一个一个的类
//                String[] sp = modelData.split("//");
//                PathPoint pdata = new PathPoint(log,lat);
//                for (String sp0 : sp) {
//                    // 再次切割 三块  （经度，纬度，距离的阈值）
//                    String sp_data[] = sp0.split(" ");
//                    PathPoint p = new PathPoint(Double.parseDouble(sp_data[0]), Double.parseDouble(sp_data[1]));//standard
//                    distance = pdata.distanceG(p);
//                    d = Double.parseDouble(sp_data[2]);
//                    if ((distance - d) < 0) {
//                        unusual = true;
//                    }
//                }
//
//                System.out.println("测试点与模型点的距离："+distance);
//                System.out.println("阈值："+d);
//                System.out.println("*****距离差："+(distance-d));
//
////                if(unusual) {
////                    geoMapper.insertTemselect(new Temselect(username, log, lat));
////                }
//                return unusual;
//
//            case 2: // 查询
//                String[] stse = timebt.split("//");
//                int n2 = 0;
//                latLngList = geoMapper.selectLatLngByDate(new SelectCount(username, "", -1, "", Date.valueOf(stse[0]), Date.valueOf(stse[1])));
//                n2 = latLngList.size();
//                if(n2==0){
////                    Chealunusual(username);
//                    break;
//                }
//                minMaxGps = geoMapper.selectMinMaxByDate(new SelectCount(username, "", -1, "", Date.valueOf(stse[0]), Date.valueOf(stse[1])));
//                double[] lng =  new double[2];
//                double[] latse = new double[2];
//                lng[0] = minMaxGps.getMinLongitude();
//                lng[1] = minMaxGps.getMaxLongitude();
//                latse[0] = minMaxGps.getMinLatitude();
//                latse[1] = minMaxGps.getMaxLatitude();
//                PathPoint[] ppse = new PathPoint[n2];
//                Grid[] gdse = new Grid[n2];
//                for(int i=0;i<n2;i++){
//                    gdse[i] = new Grid(lng,latse,0,n2);
//                }
//                GrideManage gmse = new GrideManage(gdse);
//                Windowp wdse = new Windowp(ppse,0,gmse,n2);
//                int k=0;
//                for(LatLng latLng: latLngList) {
//                    k++;
//                    wdse.length++;
//                    wdse.getPP()[wdse.length-1]= new PathPoint(latLng.getLongitude(), latLng.getLatitude());
//                }
//                String get_stse = wdse.Online(gmse);
//                wdse.offline(gmse);
//                String[] st_class = get_stse.split("//");
//                geoMapper.deleteTemselect(username);
//                for(int i=0;i<st_class.length;i++) {
//                    String[] selectclass = st_class[i].split(" ");
//                    double logts = Double.parseDouble(selectclass[0]);
//                    double lats = Double.parseDouble(selectclass[1]);
//                    geoMapper.insertTemselect(new Temselect(username, logts, lats));
//                }
//                return true;
//        }
//
//        return true;
//    }

//    public void Chealunusual(String username) throws SQLException{
        //Connection conn =  DriverManager.getConnection("jdbc:sqlite:gps.db");
//        Connection conn =  DriverManager.getConnection("jdbc:sqlite:./gps.db");
//        Statement stat = conn.createStatement();

//        ResultSet rsExist = stat.executeQuery("select * from sqlite_master where type='table' and name ='temselect';"); //查询userunusual表是否存在
//        if(!rsExist.next()){
//            stat.executeUpdate(  "create table temselect(name varchar(20), longtiude double,latitude double);" );//创建一个表，两列
//            stat.executeUpdate("insert into userunusual values('"+username+"',0,0);");
//        }
//        else{
//            ResultSet rsExistname = stat.executeQuery("select * from temselect where name ='"+username+"';");
//            if(rsExistname.next()){
//                stat.executeUpdate("delete from temselect where name='"+username+"';");
//            }
//            stat.executeUpdate("insert into temselect values('"+username+"',0,0);");
//        }
//    }

    /**
     * 数据过多导致堆内存不足，等间距删除部分数据，使得堆内存装的下
     * @param list 削减前的轨迹列表
     * @return 削减后的轨迹列表
     */
    private List<LatLng> reduce(List<LatLng> list) {
        for (int i=0; i<list.size(); i++) {
            // 奇偶交替删
            if(i%2==0)
                list.remove(i);
        }
        return list;
    }
}
