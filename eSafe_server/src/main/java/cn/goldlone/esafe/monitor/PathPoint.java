package cn.goldlone.esafe.monitor;

public class PathPoint {
    private double longitude;
    private double latitude;
    //private double speed;
    private double ltime;
    private int id;
    public String pgrid;

    public PathPoint(double lgt, double ltt, int id, double time) {
        longitude = lgt;
        latitude = ltt;
        //speed = sp;
        this.ltime = time;
        this.id = id;
        pgrid = "";
    }

    public PathPoint(double lgt, double ltt) {
        longitude = lgt;
        latitude = ltt;
        //speed = sp;
        this.ltime = 0;
        this.id = 0;
        pgrid = "";
    }

    public PathPoint() {
        longitude = latitude = 0;
    }

    public double GetLongitude() {
        return longitude;

    }

    public double GetLatitude() {
        return latitude;
    }


    public int GetId() {
        return id;
    }

    public double GetTime() {
        return ltime;
    }

    public void setLgt(double lgt) {
        longitude = lgt;
    }

    public void setLat(double lat) {
        latitude = lat;
    }


    public void setId(int id) {
        this.id = id;
    }

    public double distanceG(PathPoint p) {
        return Math.sqrt(Math.pow(longitude - p.GetLongitude(), 2) + Math.pow(latitude - p.GetLatitude(), 2));
    }

}
