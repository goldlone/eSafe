package cn.goldlone.esafe.model;

public class MinMaxGps {
    // 最小经度值
    private double minLongitude;
    // 最大经度值
    private double maxLongitude;
    // 最小纬度值
    private double minLatitude;
    // 最大纬度值
    private double maxLatitude;

    public MinMaxGps() {
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    @Override
    public String toString() {
        return "MinMaxGps{" +
                "minLongitude=" + minLongitude +
                ", maxLongitude=" + maxLongitude +
                ", minLatitude=" + minLatitude +
                ", maxLatitude=" + maxLatitude +
                '}';
    }
}
