package cn.goldlone.esafe.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Created by CN on 2018/4/20 18:23 .
 */
public class Cluster {
    // 纬度
    private double latitude;
    // 经度
    private double longitude;
    // 阈值
    private double radius;

    public Cluster(double latitude, double longitude, double radius) {
        DecimalFormat df = new DecimalFormat("#.000000");
        this.latitude = Double.parseDouble(df.format(latitude));
        this.longitude = Double.parseDouble(df.format(longitude));
//        this.radius = Double.parseDouble(df.format(radius));
        this.radius = Double.parseDouble(df.format(radius/180*Math.PI*6300000));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Cluster c = (Cluster)obj;
        if(this.latitude==c.latitude && this.longitude==c.longitude)
            return true;
        return false;
    }
}
