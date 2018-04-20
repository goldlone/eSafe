package cn.goldlone.esafe.monitor;

public class GrideManage {
    public Grid[] g;
    public int lengthg;
    public int densegrid;
    public int thingrid;
    public int secondgrid;
    public String sd;
    public String st;
    public String ss;
    public double u;

    public GrideManage(Grid[] g) {
        this.g = g;
        lengthg = 0;
        densegrid = thingrid = secondgrid = 0;
        sd = st = ss = "";
    }

    public void setU(double u) {
        this.u = u;
    }


}
