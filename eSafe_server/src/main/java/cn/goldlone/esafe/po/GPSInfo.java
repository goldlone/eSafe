package cn.goldlone.esafe.po;

import java.sql.Date;

public class GPSInfo {
    private String username;
    private double longitude;
    private double latitude;
    private Date time;
    private String time_dur;
    private int week;

    public GPSInfo() {
    }

    public GPSInfo(String username,
                   double longitude,
                   double latitude,
                   Date time,
                   String time_dur,
                   int week) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.time_dur = time_dur;
        this.week = week;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTime_dur() {
        return time_dur;
    }

    public void setTime_dur(String time_dur) {
        this.time_dur = time_dur;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return "GPSInfo{" +
                "username='" + username + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", time=" + time +
                ", time_dur='" + time_dur + '\'' +
                ", week=" + week +
                '}';
    }
}
