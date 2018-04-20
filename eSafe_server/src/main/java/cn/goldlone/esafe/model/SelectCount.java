package cn.goldlone.esafe.model;

import java.sql.Date;

public class SelectCount {
    // 用户名
    private String username;
    // 查询字段
    private String field;
    // 是否周末
    private int week;
    // 时间段（上午，中午，下午，晚上）
    private String timeDur;
    // 开始日期
    private Date startDate;
    // 结束日期
    private Date endDate;

    public SelectCount() {
    }

    public SelectCount(String username,
                       String field,
                       int week,
                       String timeDur,
                       Date startDate,
                       Date endDate) {
        this.username = username;
        this.field = field;
        this.week = week;
        this.timeDur = timeDur;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getTimeDur() {
        return timeDur;
    }

    public void setTimeDur(String timeDur) {
        this.timeDur = timeDur;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "SelectCount{" +
                "username='" + username + '\'' +
                ", field='" + field + '\'' +
                ", week=" + week +
                ", timeDur='" + timeDur + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
