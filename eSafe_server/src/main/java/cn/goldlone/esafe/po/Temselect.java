package cn.goldlone.esafe.po;

public class Temselect {
    private String username;
    private double longitude;
    private double latitude;

    public Temselect() {
    }

    public Temselect(String username,
                     double longitude,
                     double latitude) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Temselect{" +
                "username='" + username + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
