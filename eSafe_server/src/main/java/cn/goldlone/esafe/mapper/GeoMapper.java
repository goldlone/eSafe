package cn.goldlone.esafe.mapper;

import cn.goldlone.esafe.model.LatLng;
import cn.goldlone.esafe.model.MinMaxGps;
import cn.goldlone.esafe.po.GPSInfo;
import cn.goldlone.esafe.model.SelectCount;
import cn.goldlone.esafe.po.GeoMsg;
import cn.goldlone.esafe.po.Temselect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GeoMapper {

    // 插入用户数据
    public Integer insertGPSInfo(GPSInfo info);

    // 查询是否已存在轨迹
    public Integer selectUserTail(String username);
    // 在geo_msg中插入周末和非周末的数据
    public Integer insertTail(GeoMsg msg);
    // 更新轨迹信息
    public Integer updateTail(GeoMsg msg);

    // 查询某字段在某个时间段的行数(没用了)
//    public Integer getCounts(SelectCount count);
    // 查询某段时间里的坐标集合
    public List<LatLng> selectLatLng(SelectCount count);
    // 查询某个时间段中经纬度的最大、最小值
    public MinMaxGps selectMinMax(SelectCount count);


    // 查询聚类数据
    public GeoMsg selectGeoMsg(@Param("username") String username,
                               @Param("week") int week);


    // 查询某段时间里的坐标集合
    public List<LatLng> selectLatLngByDate(SelectCount count);
    // 查询某个时间段中经纬度的最大、最小值
    public MinMaxGps selectMinMaxByDate(SelectCount count);


    // 添加异常数据
    public Integer insertTemselect(Temselect temselect);
    // 删除异常数据
    public Integer deleteTemselect(String username);

}
