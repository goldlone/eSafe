package cn.goldlone.esafe.monitor;

public class Grid {
    private int m;
    public double[] lg;
    public double[] la;
    public PathPoint[] pt;
    public PathPoint p_mean;
    public int number;
    public int status;
    public double max_d;
    public double min_d;
    public double avg_d;

    public Grid(double[] lg, double[] la, int number, int m_number) {
        this.lg = lg;
        this.la = la;
        m = m_number;
        //this.time = time;
        this.number = number;
        pt = new PathPoint[m];
        p_mean = new PathPoint();
        status = 0;
    }

}
