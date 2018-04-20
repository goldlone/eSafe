package cn.goldlone.esafe.po;

public class GeoMsg {
    private String username;
    private int week;
    private String mornplace;
    private String noonplace;
    private String afterplace;
    private String nightplace;

    public GeoMsg() {
    }

    public GeoMsg(String username,
                  int week) {
        this.username = username;
        this.week = week;
    }

    public GeoMsg(String username,
                  int week,
                  String mornplace,
                  String noonplace,
                  String afterplace,
                  String nightplace) {
        this.username = username;
        this.week = week;
        this.mornplace = mornplace;
        this.noonplace = noonplace;
        this.afterplace = afterplace;
        this.nightplace = nightplace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getMornplace() {
        return mornplace;
    }

    public void setMornplace(String mornplace) {
        this.mornplace = mornplace;
    }

    public String getNoonplace() {
        return noonplace;
    }

    public void setNoonplace(String noonplace) {
        this.noonplace = noonplace;
    }

    public String getAfterplace() {
        return afterplace;
    }

    public void setAfterplace(String afterplace) {
        this.afterplace = afterplace;
    }

    public String getNightplace() {
        return nightplace;
    }

    public void setNightplace(String nightplace) {
        this.nightplace = nightplace;
    }

    @Override
    public String toString() {
        return "GeoMsg{" +
                "username='" + username + '\'' +
                ", week=" + week +
                ", mornplace='" + mornplace + '\'' +
                ", noonplace='" + noonplace + '\'' +
                ", afterplace='" + afterplace + '\'' +
                ", nightplace='" + nightplace + '\'' +
                '}';
    }
}
