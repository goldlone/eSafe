package cn.goldlone.esafe.monitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Windowp {
    PathPoint[] pp;
    int length;
    int classk;
    int finalclass;
    int abclass;
    int numbergrid;
    int dm;
    int dl;
    final static double u = 0.5;
    final static double b = 3;
    //final static int N = 999;
    int n;
    public int[][] edg_edg;
    //�������񼯺�
    public int[] den_node;
    public int den_edgelg;
    public int[][] den_edge;

    //�������񼯺�
    public int[] sec_node;
    public int sec_edgelg;
    public int[][] sec_edge;

    public int[] thin_node;
    public int thin_edgelg;
    public int[][] thin_edge;
    //�洢���
    public String[] s_grid;
    int[] s_gridlab;//格簇类型1,0,2
    int[] max_grid;
    double[] sgrid_u;

    //
    public String[] final_class;
    public String[] abnormalclass;


    public Windowp(PathPoint[] pp, int lg, GrideManage gm, int n_number) {
        this.pp = pp;
        n = n_number;
        length = lg;
        den_node = sec_node = null;
        den_edge = sec_edge = null;
        numbergrid = 0;
        den_edgelg = classk = sec_edgelg = finalclass = 0;
        abclass = 0;
        //s_grid = new String[n];
        s_gridlab = new int[n];
        max_grid = new int[n];
        sgrid_u = new double[n];
        final_class = new String[n / 2];
        abnormalclass = new String[n / 4];
        for (int j = 0; j < n / 2; j++) {
            final_class[j] = "";
        }
        for (int i = 0; i < gm.lengthg; i++) {
            //s_grid[i] = "";
            max_grid[i] = 0;
            sgrid_u[i] = 0;
            s_gridlab[i] = 0;
        }
    }

    public int getLength() {
        return length;
    }

    public PathPoint getPPoint(int n) {
        return pp[n - 1];
    }

    public PathPoint[] getPP() {
        return pp;
    }

    public void setLength(int lg) {
        length = lg;
    }

    public String Online(GrideManage gm) {

        double[] x = new double[2];//xiave
        double[] e = new double[2];//Epsi
        int m = 0, c = 0, l = 0, s = 0;//m为网格的个数，c为稠密网格的个数，l为过渡网格的个数，s为稀疏网格的个数
        //System.out.printf("pp.0 %f\n",pp[0].GetLongitude());
        for (int i = 0; i < 2; i++) {//setp2
            double sum1 = 0, sum2 = 0;
            for (int j = 0; j < n; j++) {
                if (i == 0) {
                    sum1 += pp[j].GetLongitude();
                    //System.out.printf("pp[j] %d %f\n",j,pp[j].GetLongitude());
                } else if (i == 1)
                    sum1 += pp[j].GetLatitude();
            }

            x[i] = sum1 / n;
            for (int k = 0; k < n; k++) {
                if (i == 0)
                    sum2 += Math.pow(pp[k].GetLongitude() - x[i], 2);
                else if (i == 1)
                    sum2 += Math.pow(pp[k].GetLatitude() - x[i], 2);
            }
            e[i] = Math.sqrt(sum2 / n);


        }
        for (int i = 0; i < n; i++) {//生成动�?�网�?
            if (gm.lengthg == 0) {
                createGrid(pp[i], e, gm);
            } else {
                PathPoint pt = new PathPoint(pp[i].GetLongitude(), pp[i].GetLatitude(), pp[i].GetId(), pp[i].GetTime());
                int flag = 0;
                int j = 0;

                while (j < gm.lengthg && flag == 0) {
                    //System.out.printf("the gm length: %d", gm.lengthg);
                    if ((pt.GetLongitude() >= gm.g[j].lg[0]) && (pt.GetLongitude() <= gm.g[j].lg[1]) && (pt.GetLatitude() >= gm.g[j].la[0]) && (pt.GetLatitude() <= gm.g[j].la[1])) {
                        gm.g[j].number++;
                        gm.g[j].pt[gm.g[j].number - 1] = new PathPoint(pt.GetLongitude(), pt.GetLatitude(), pt.GetId(), pt.GetTime());
                        flag = 1;
                        //break;
                        pp[i].pgrid += j + " ";//
                    }
                    j++;
                }

                if (flag == 0)
                    createGrid(pp[i], e, gm);
            }
        }

        int sumd = 0;
        int d_max = 0, d_min = 0;
        for (int i = 0; i < gm.lengthg; i++) {//计算dm,dl
            sumd += gm.g[i].number;
            if (gm.g[i].number > d_max)
                d_max = gm.g[i].number;
            if (gm.g[i].number < d_min)
                d_min = gm.g[i].number;
            maxdisGrid(gm.g[i]);//计算距网格中心点的最大距离
            mindisGrid(gm.g[i]);
            avgdisGrid(gm.g[i]);
        }
        int da = sumd / gm.lengthg;
        dm = (da + d_max) / 2;
        dl = (da + d_min) / 2;
        for (int i = 0; i < gm.lengthg; i++) {
            double[] sum_mean = {0, 0};
            for (int j = 0; j < gm.g[i].number; j++) {
                sum_mean[0] += gm.g[i].pt[j].GetLongitude();
                sum_mean[1] += gm.g[i].pt[j].GetLatitude();
                //sum_mean[2] += gm.g[i].pt[j].GetLtime();
            }

            //求质�?
            gm.g[i].p_mean.setLgt(sum_mean[0] / gm.g[i].number);
            gm.g[i].p_mean.setLat(sum_mean[1] / gm.g[i].number);
            //gm.g[i].p_mean.setLtime(sum_mean[2]/gm.g[i].number);

            //求特殊网格的个数
            if (gm.g[i].number >= dm) {
                gm.densegrid++;
                gm.sd = gm.sd + i + " ";
            } else if (gm.g[i].number < dl) {
                gm.thingrid++;
                gm.st = gm.st + i + " ";
            } else {
                gm.secondgrid++;
                gm.ss = gm.ss + i + " ";
            }
        }

        //求阈�?
        double sum_e = 0;
        for (int k = 0; k < 2; k++) {
            sum_e += Math.pow(e[k], 2);
        }

        double u = Math.sqrt(sum_e);
        gm.setU(u);

        //createVE(gm);
        //ListUDG udg = new ListUDG(this);
        //udg.DFSTraverse();
        s_grid = new String[gm.lengthg];
        //����������
        edg_edg = new int[gm.lengthg][gm.lengthg];
        neighGrid(gm);
        if (gm.sd != "") {
            creategdVE(gm);
            ListUDG udg_gd = new ListUDG(this, 1, gm);
            udg_gd.DFSTraverse(gm, this, 1);
        }
        if (gm.ss != "") {
            //����������
            dealSG(gm);
            if (gm.ss != "") {
                creategsVE(gm);
                ListUDG udg_gs = new ListUDG(this, 0, gm);
                udg_gs.DFSTraverse(gm, this, 0);
            }

        }

        if (gm.st != "") {
            //ϡ��������
            dealTG(gm);
            if (gm.st != "") {
                creategtVE(gm);
                ListUDG udg_gt = new ListUDG(this, 2, gm);
                udg_gt.DFSTraverse(gm, this, 2);
            }
        }

        //������
        int n_number = 0;
        //System.out.println("the end is :\n");
        for (int i = 0; i < classk; i++) {
            //System.out.printf("class %d is %s\n", i+1,s_grid[i]);
            String[] s_t = s_grid[i].split(" ");
            for (int j = 0; j < s_t.length; j++) {
                n_number += gm.g[Integer.parseInt(s_t[j])].number;
            }
        }

        consgrid(gm);

				/*for(int i=0;i<classk;i++){//Ѱ�Ҹ�����ܶ���������
                    String[] s_t = s_grid[i].split(" ") ;
					int max = gm.g[Integer.parseInt(s_t[0])].number;
					max_grid[i] = Integer.parseInt(s_t[0]);
					//System.out.printf("classkk %d %s\n",i,s_grid[i]);
					for(int j=1;j<s_t.length;j++){
						//System.out.printf("mm %d %d\n",j,Integer.parseInt(s_t[j]));
						if(gm.g[Integer.parseInt(s_t[j])].number>max)
							max_grid[i] = Integer.parseInt(s_t[j]);
						}
					}
				for(int i=0;i<classk;i++){
					String[] st=s_grid[i].split(" ");
					double max = 0;
					double d = 0;
					if(st.length==1){
						//System.out.printf("class %d is is %s\n",i,st[0]);
						int n=gm.g[Integer.parseInt(st[0])].number;
						for(int j=0;j<n;j++){
							d=gm.g[Integer.parseInt(st[0])].pt[j].distanceG(gm.g[Integer.parseInt(st[0])].p_mean);
							if(d>max)
								max = d;
						}
						sgrid_u[i]=max;
					}
					else{
						for(int k=0;k<st.length;k++){
							int n=gm.g[Integer.parseInt(st[k])].number;
							for(int j=0;j<n;j++){
								d=gm.g[Integer.parseInt(st[k])].pt[j].distanceG(gm.g[Integer.parseInt(st[k])].p_mean);
								if(d>max)
									max = d;
							}
						}
						sgrid_u[i]=max;
					}
				}*/

				/*for(int i=0;i<classk;i++){//���������������ľ�ؼ����ƽ������
					String[] s_t = s_grid[i].split(" ");
					int max = max_grid[i];
					double sum = 0;
					double d = -1;
					for(int j=0;j<s_t.length;j++){
						if(Integer.parseInt(s_t[j])!=max){
							d = gm.g[max].p_mean.distanceG(gm.g[Integer.parseInt(s_t[j])].p_mean);
							sum +=d;
						}
					}
					sgrid_u[i] = sum/s_t.length;
				}*/
        String sreturn = "";

        for (int i = 0; i < classk; i++) {
            sreturn += gm.g[max_grid[i]].p_mean.GetLongitude() + " " + gm.g[max_grid[i]].p_mean.GetLatitude() + " " + sgrid_u[i] + "//";
            //System.out.printf("class %d is %s\n", i+1,s_grid[i]);
            //System.out.printf("%f %f %d\n",gm.g[max_grid[i]].p_mean.GetLongitude(),gm.g[max_grid[i]].p_mean.GetLatitude(),i+1);
            String[] s_t = s_grid[i].split(" ");
					/*for(int j=0;j<s_t.length;j++){
						sreturn += gm.g[Integer.parseInt(s_t[j])].p_mean.GetLongitude()+" "+gm.g[Integer.parseInt(s_t[j])].p_mean.GetLatitude()+"//";
						for(int k=0;k<gm.g[Integer.parseInt(s_t[j])].number;k++){
							System.out.printf("%f %f %d\n",gm.g[Integer.parseInt(s_t[j])].pt[k].GetLongitude(),gm.g[Integer.parseInt(s_t[j])].pt[k].GetLatitude(),i+1);
						}
						//System.out.println("//");
					}*/

        }
        //System.out.printf("sd %s  ss %s  st %s\n", gm.sd,gm.ss,gm.st);
				/*for(int i=0;i<gm.lengthg;i++){
					for(int j=0;j<gm.lengthg;j++){
						System.out.printf("edg_edg i %d j%d is %d\n",i,j,edg_edg[i][j]);
					}
				}*/
        return sreturn;

    }

    public void consgrid(GrideManage gm) {
        for (int i = 0; i < classk; i++) {//Ѱ�Ҹ�����ܶ���������
            String[] s_t = s_grid[i].split(" ");
            int max = gm.g[Integer.parseInt(s_t[0])].number;
            max_grid[i] = Integer.parseInt(s_t[0]);
            //System.out.printf("classkk %d %s\n",i,s_grid[i]);
            for (int j = 1; j < s_t.length; j++) {
                //System.out.printf("mm %d %d\n",j,Integer.parseInt(s_t[j]));
                if (gm.g[Integer.parseInt(s_t[j])].number > max)
                    max_grid[i] = Integer.parseInt(s_t[j]);
            }
        }
        for (int i = 0; i < classk; i++) {
            String[] st = s_grid[i].split(" ");
            double max = 0;
            double min = 100;
            double d = 0;
            if (st.length == 1) {
                //System.out.printf("class %d is is %s\n",i,st[0]);
                int n = gm.g[Integer.parseInt(st[0])].number;
                for (int j = 0; j < n; j++) {
                    d = gm.g[Integer.parseInt(st[0])].pt[j].distanceG(gm.g[Integer.parseInt(st[0])].p_mean);
                    if (d > max)
                        max = d;
                }
                sgrid_u[i] = max;
				/*if(s_gridlab[i]==1){
					for(int j=0;j<n;j++){
						d=gm.g[Integer.parseInt(st[0])].pt[j].distanceG(gm.g[Integer.parseInt(st[0])].p_mean);
						if(d>max)
							max = d;
					}
					sgrid_u[i]=max;
				}
				else if(s_gridlab[i]==0){
					double ssum = 0;
					for(int j=0;j<n;j++){
						d=gm.g[Integer.parseInt(st[0])].pt[j].distanceG(gm.g[Integer.parseInt(st[0])].p_mean);
						ssum +=d;
						if(d>max){
							max = d;
						}
					}
					double ssmean = ssum/n;
					sgrid_u[i]=(max+ssmean)/2;
				}
				else if(s_gridlab[i]==2){
					double ssum = 0;
					for(int j=0;j<n;j++){
						d=gm.g[Integer.parseInt(st[0])].pt[j].distanceG(gm.g[Integer.parseInt(st[0])].p_mean);
						ssum +=d;
						if(d<min){
							min = d;
						}
					}
					double ssmean = ssum/n;
					sgrid_u[i]=(min+ssmean)/2;
				}*/
            } else {
                double sum = 0;
                for (int k = 0; k < st.length; k++) {
                    //int n=gm.g[Integer.parseInt(st[k])].number;
                    for (int j = 1; j < st.length; j++) {
                        d = gm.g[Integer.parseInt(st[k])].p_mean.distanceG(gm.g[Integer.parseInt(st[j])].p_mean);
                        sum += d;
                        if (d > max)
                            max = d;
                        if (d < min)
                            min = d;
                    }
                }
                double smean = sum / (st.length - 1);
                sgrid_u[i] = max;
                ;
				
				/*if(s_gridlab[i]==1){
							sgrid_u[i]=max;
				}
				else if(s_gridlab[i]==0){
							sgrid_u[i]=(smean+max)/2;
				}
				else if(s_gridlab[i]==2){
							sgrid_u[i]=(smean+min)/2;
				}*/

            }
        }
    }

    public void maxdisGrid(Grid g) {
        double max_d = 0;
        double d = 0;
        for (int i = 0; i < g.number; i++) {
            d = g.pt[i].distanceG(g.p_mean);
            if (d > max_d)
                max_d = d;
        }
        g.max_d = max_d;
    }

    public void mindisGrid(Grid g) {
        double min_d = 1000;
        double d = 0;
        for (int i = 0; i < g.number; i++) {
            d = g.pt[i].distanceG(g.p_mean);
            if (d < min_d)
                min_d = d;
        }
        g.min_d = min_d;
    }

    public void avgdisGrid(Grid g) {
        double sum = 0;
        double d = 0;
        for (int i = 0; i < g.number; i++) {
            d = g.pt[i].distanceG(g.p_mean);
            sum += d;
        }
        g.avg_d = sum / g.number;
    }

    public void createGrid(PathPoint p, double[] e, GrideManage gm) {//创建网格
        numbergrid++;
        double[] lg = new double[2];
        double[] la = new double[2];
        //double[] time = new double[2];
        lg[0] = p.GetLongitude() - e[0] / 2;
        lg[1] = p.GetLongitude() + e[0] / 2;
        la[0] = p.GetLatitude() - e[1] / 2;
        la[1] = p.GetLatitude() + e[1] / 2;
        //time[0] = p.GetLtime()-e[2]/2;
        //time[1] = p.GetLtime()-e[2]/2;
        gm.g[gm.lengthg] = new Grid(lg, la, 0, n);

        //Grid g = new Grid(lg,la,1);
        gm.g[gm.lengthg].pt[gm.g[gm.lengthg].number] = new PathPoint(p.GetLongitude(), p.GetLatitude(), p.GetId(), p.GetTime());
        gm.g[gm.lengthg].number++;
        p.pgrid += gm.lengthg + " ";
        gm.lengthg++;

        //gm.g[gm.lengthg-1] = g;
    }

    public void neighGrid(GrideManage gm) {
        //int[][]den_den = new int[den_node.length][den_node.length];
        for (int i = 0; i < n; i++) {
            //System.out.printf("pp[i] i %d is %s\n",i,pp[i].pgrid);
            if (pp[i].pgrid.length() / 2 == 0)
                break;
            else {
                String[] spgrid = pp[i].pgrid.split(" ");

                for (int j = 0; j < spgrid.length; j++) {
                    for (int k = j + 1; k < spgrid.length; k++) {
                        if (edg_edg[Integer.parseInt(spgrid[j])][Integer.parseInt(spgrid[k])] != 1) {
                            edg_edg[Integer.parseInt(spgrid[j])][Integer.parseInt(spgrid[k])] = 1;
                            edg_edg[Integer.parseInt(spgrid[k])][Integer.parseInt(spgrid[j])] = 1;
                        }
                        if (gm.g[Integer.parseInt(spgrid[j])].number >= dm && gm.g[Integer.parseInt(spgrid[k])].number >= dm)
                            den_edgelg++;
                        else if (gm.g[Integer.parseInt(spgrid[j])].number > dl && gm.g[Integer.parseInt(spgrid[j])].number < dm && gm.g[Integer.parseInt(spgrid[k])].number > dl && gm.g[Integer.parseInt(spgrid[k])].number < dm) {
                            sec_edgelg++;
                        } else if (gm.g[Integer.parseInt(spgrid[j])].number <= dl && gm.g[Integer.parseInt(spgrid[k])].number <= dl) {
                            thin_edgelg++;
                        }
                    }
                }
            }

        }
    }

    public void creategdVE(GrideManage gm) {
        den_edgelg = 0;
        int edg = 0;
        String[] s = gm.sd.split(" ");
        den_node = new int[s.length];
        //int[][] edg_edg = new int[den_node.length][den_node.length];
        //int[][] edg_edg = new int[gm.lengthg][gm.lengthg];
        //neighGrid(gm,0);
        den_edge = new int[den_edgelg][2];
        for (int i = 0; i < den_node.length; i++) {
            for (int j = i + 1; j < den_node.length; j++) {
                if (edg_edg[den_node[i]][den_node[j]] == 1) {
                    den_edge[edg][0] = den_node[i];
                    den_edge[edg][1] = den_node[j];
                    edg++;
                }
            }
        }
        //String[] s = gm.sd.split(" ");
		/*den_node = new int[s.length];
		int el = den_node.length*(den_node.length-1)/2;
		den_edgelg = 0;
		den_edge = new int[el][2];
		for(int i=0;i<den_node.length;i++){
			den_node[i] = Integer.parseInt(s[i]);
		}*/
		/*for(int i=0;i<den_node.length;i++){
			boolean bl = false;
			for(int j=i+1;j<den_node.length;j++){
					double d = Math.sqrt(Math.pow(gm.g[den_node[i]].p_mean.GetLongitude()-gm.g[den_node[j]].p_mean.GetLongitude(),2)+Math.pow(gm.g[den_node[i]].p_mean.GetLatitude()-gm.g[den_node[j]].p_mean.GetLatitude(),2));
					if(d<=gm.u){
						den_edge[den_edgelg][0] = den_node[i];
						den_edge[den_edgelg][1] = den_node[j];
						den_edgelg++;
						bl = true;
					}
					if(d<=(gm.g[den_node[i]].max_d+gm.g[den_node[j]].max_d)){
						den_edge[den_edgelg][0] = den_node[i];
						den_edge[den_edgelg][1] = den_node[j];
						den_edgelg++;
						bl = true;
					}
			}
			
		}*/
    }
	
	/*public void dealSG(GrideManage gm){
		//int m = gm.sd.length();
		int ks = -1;
		double min_d = gm.u+1;
		String[] ss = gm.ss.split(" ");
		String[] sd = gm.sd.split(" ");
		int n = ss.length;
		String s = "";
		String sm = "";
		for(int i=0;i<n;i++){
			double g_lg = gm.g[Integer.parseInt(ss[i])].p_mean.GetLongitude();
			double g_la = gm.g[Integer.parseInt(ss[i])].p_mean.GetLatitude();
			PathPoint pp = new PathPoint(g_lg,g_la,0,0);
			for(int j=0;j<sd.length-1;j++){
				if(edg_edg[Integer.parseInt(ss[i])][Integer.parseInt(sd[j])]==1||edg_edg[Integer.parseInt(sd[j])][Integer.parseInt(ss[i])]==1){
					ks = Integer.parseInt(sd[j]);
					gm.g[Integer.parseInt(ss[i])].status = gm.g[ks].status;
					//s_grid[ks] +=ss[i]+" ";
					s_grid[gm.g[ks].status-1] +=ss[i]+" ";
					//System.out.println("SS-----"+ss[i]);
					//System.out.println("gm.ss"+gm.ss);
					s = s+ss[i]+" ";
					//gm.ss = s.substring(0, 2*i)+s.substring(2*(i+1));//�ӹ������񼯺���ɾ��
					//System.out.println("-------"+gm.ss);
					break;
				}
			}
			
		}
		String[] s_s = s.split(" ");
		for(int i=0;i<n;i++){
			boolean bl = false;
			for(int j=0;j<s_s.length;j++){
				if(ss[i].equals(s_s[j])){
					bl = true;
					break;
				}
			}
			if(!bl)
				sm +=ss[i]+" ";
		}
		gm.ss = sm;
	}*/

    public void dealSG(GrideManage gm) {
        //int m = gm.sd.length();
        int ks = -1;
        double min_d = gm.u + 1;
        String[] ss = gm.ss.split(" ");
        String[] sd = gm.sd.split(" ");
        int n = ss.length;
        String s = "";
        String sm = "";

        for (int i = 0; i < n; i++) {
            double g_lg = gm.g[Integer.parseInt(ss[i])].p_mean.GetLongitude();
            double g_la = gm.g[Integer.parseInt(ss[i])].p_mean.GetLatitude();
            PathPoint pp = new PathPoint(g_lg, g_la, 0, 0);
            for (int j = 0; j < sd.length - 1; j++) {
                if (edg_edg[Integer.parseInt(ss[i])][Integer.parseInt(sd[j])] == 1 || edg_edg[Integer.parseInt(sd[j])][Integer.parseInt(ss[i])] == 1) {
                    ks = Integer.parseInt(sd[j]);
                    gm.g[Integer.parseInt(ss[i])].status = gm.g[ks].status;
                    //s_grid[ks] +=ss[i]+" ";
                    s_grid[gm.g[ks].status - 1] += ss[i] + " ";
                    //System.out.println("SS-----"+ss[i]);
                    //System.out.println("gm.ss"+gm.ss);
                    s = s + ss[i] + " ";
                    //gm.ss = s.substring(0, 2*i)+s.substring(2*(i+1));//�ӹ������񼯺���ɾ��
                    //System.out.println("-------"+gm.ss);
                    break;
                }
            }

        }
        String[] s_s = s.split(" ");
        for (int i = 0; i < n; i++) {
            boolean bl = false;
            for (int j = 0; j < s_s.length; j++) {
                if (ss[i].equals(s_s[j])) {
                    bl = true;
                    break;
                }
            }
            if (!bl)
                sm += ss[i] + " ";
        }
        while (s != "") {
            String[] temp = s.split(" ");
            String[] smt = sm.split(" ");
            s = "";
            sm = "";
            for (int i = 0; i < smt.length; i++) {
                for (int j = 0; j < temp.length; j++) {
                    if (edg_edg[Integer.parseInt(temp[j])][Integer.parseInt(smt[i])] == 1 || edg_edg[Integer.parseInt(temp[j])][Integer.parseInt(smt[i])] == 1) {
                        ks = Integer.parseInt(sd[j]);
                        gm.g[Integer.parseInt(ss[i])].status = gm.g[ks].status;
                        //s_grid[ks] +=ss[i]+" ";
                        s_grid[gm.g[ks].status - 1] += smt[i] + " ";
                        //System.out.println("SS-----"+ss[i]);
                        //System.out.println("gm.ss"+gm.ss);
                        s = s + smt[i] + " ";
                        //gm.ss = s.substring(0, 2*i)+s.substring(2*(i+1));//�ӹ������񼯺���ɾ��
                        //System.out.println("-------"+gm.ss);
                        break;
                    }
                }
            }
            String[] sst = s.split(" ");
            for (int i = 0; i < smt.length; i++) {
                boolean bl = false;
                for (int j = 0; j < sst.length; j++) {
                    if (smt[i].equals(sst[j])) {
                        bl = true;
                        break;
                    }
                }
                if (!bl)
                    sm += smt[i] + " ";
            }
        }
        gm.ss = sm;
        //System.out.println(gm.ss);
    }

    public void creategsVE(GrideManage gm) {//������������ĵ�����ͱ�����
        sec_edgelg = 0;
        int edg = 0;
        String[] s = gm.ss.split(" ");
        sec_node = new int[s.length];
        //int[][] edg_edg = new int[sec_node.length][sec_node.length];
        //int[][] edg_edg = new int[gm.lengthg][gm.lengthg];
        //neighGrid(gm,edg_edg,1);
        sec_edge = new int[sec_edgelg][2];
        for (int i = 0; i < sec_node.length; i++) {
            for (int j = i + 1; j < sec_node.length; j++) {
                if (edg_edg[sec_node[i]][sec_node[j]] == 1) {
                    sec_edge[edg][0] = sec_node[i];
                    sec_edge[edg][1] = sec_node[j];
                    edg++;
                }
            }
        }
		
		/*String[] s = gm.ss.split(" ");
		sec_node = new int[s.length-1];
		int el = sec_node.length*(sec_node.length-1)/2;
		sec_edgelg = 0;
		sec_edge = new int[el][2];
		for(int i=0;i<sec_node.length;i++){
			sec_node[i] = Integer.parseInt(s[i]);
		}
		for(int i=0;i<sec_node.length;i++){
			for(int j=i+1;j<sec_node.length;j++){
					double d = Math.sqrt(Math.pow(gm.g[sec_node[i]].p_mean.GetLongitude()-gm.g[sec_node[j]].p_mean.GetLongitude(),2)+Math.pow(gm.g[sec_node[i]].p_mean.GetLatitude()-gm.g[sec_node[j]].p_mean.GetLatitude(),2));
					if(d<=gm.u){
						sec_edge[sec_edgelg][0] = sec_node[i];
						sec_edge[sec_edgelg][1] = sec_node[j];
						sec_edgelg++;
					}
					if(d<=(gm.g[sec_node[i]].max_d+gm.g[sec_node[j]].max_d)){
						sec_edge[sec_edgelg][0] = sec_node[i];
						sec_edge[sec_edgelg][1] = sec_node[j];
						sec_edgelg++;
					}
			}
		}*/
    }

    public void creategtVE(GrideManage gm) {//������������ĵ�����ͱ�����
        thin_edgelg = 0;
        int edg = 0;
        String[] s = gm.st.split(" ");
        thin_node = new int[s.length];
        //int[][] edg_edg = new int[gm.lengthg][gm.lengthg];
        //neighGrid(gm,edg_edg,2);
        thin_edge = new int[thin_edgelg][2];
        for (int i = 0; i < thin_node.length; i++) {
            for (int j = i + 1; j < thin_node.length; j++) {
                if (edg_edg[thin_node[i]][thin_node[j]] == 1) {
                    thin_edge[edg][0] = thin_node[i];
                    thin_edge[edg][1] = thin_node[j];
                    edg++;
                }
            }
        }
    }

    public void dealTG(GrideManage gm) {
        int ks = -1;
        String[] st = gm.st.split(" ");
        String[] sd = gm.sd.split(" ");
        int n = st.length;
        String s = "";
        String sm = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < sd.length - 1; j++) {
                if (edg_edg[Integer.parseInt(st[i])][Integer.parseInt(sd[j])] == 1 || edg_edg[Integer.parseInt(sd[j])][Integer.parseInt(st[i])] == 1) {
                    ks = Integer.parseInt(sd[j]);
                    gm.g[Integer.parseInt(st[i])].status = gm.g[ks].status;
                    s_grid[gm.g[ks].status - 1] += st[i] + " ";
                    //s_grid[ks] +=st[i]+" ";
                    //System.out.println("St-----"+st[i]);
                    s = s + st[i] + " ";
                    break;
                }
            }

        }
        String[] s_s = s.split(" ");
        for (int i = 0; i < n; i++) {
            boolean bl = false;
            for (int j = 0; j < s_s.length; j++) {
                if (st[i].equals(s_s[j])) {
                    bl = true;
                    break;
                }
            }
            if (!bl)
                sm += st[i] + " ";
        }
        gm.st = sm;
    }

    public void offline(GrideManage gm) {
        //��ȡʱ��T�ڵĸ��

        //
        //consgrid(gm);
        double[] sumclass = new double[classk];
        for (int i = 0; i < classk; i++) {
            sumclass[i] = 0;
            //System.out.printf("%d %s\n",i,s_grid[i]);
        }
        for (int i = 0; i < classk; i++) {
            String[] sk = s_grid[i].split(" ");
            for (int j = 0; j < sk.length; j++) {
                sumclass[i] += gm.g[Integer.parseInt(sk[j])].number;
            }
        }
        for (int i = 0; i < classk; i++) {
            if (s_grid[i] != "/") {
                double min = 1000;
                int k = -1;
                double d = 0;
                int j;
                final_class[finalclass] += s_grid[i];
                for (j = i + 1; j < classk; j++) {
                    if (s_grid[j] == "/")
                        continue;
                    //d = gm.g[max_grid[i]].p_mean.distanceG(gm.g[max_grid[j]].p_mean);
                    d = gm.g[max_grid[i]].p_mean.distanceG(gm.g[max_grid[j]].p_mean);
				
				/*if(d<min){
					min = d;
					System.out.println("right");
					k = j;
				}*/
                    //System.out.printf("%f  %f \n", gm.g[Integer.parseInt(s_grid[i])].number,gm.g[Integer.parseInt(s_grid[j])].number);
                    double cg1 = changek(s_grid[i], gm);
                    double cg2 = changek(s_grid[j], gm);
                    double ut = sumclass[i] / sumclass[j];
                    //System.out.printf("sgi sgj %d %d %f %f %s %s\n",i,j,sgrid_u[i],sgrid_u[j],s_grid[i],s_grid[j]);
                    if ((d <= sgrid_u[i] + sgrid_u[j]) && (cg1 <= b * cg2 || cg2 <= b * cg1)) {
                        final_class[finalclass] += s_grid[j];
                        s_grid[j] = "/";

                    }
				/*if((d<=sgrid_u[i]+sgrid_u[j])&&ut>=1-u&&ut<=1+u){
					final_class[finalclass] += s_grid[j];
					s_grid[j] = "/";
					//finalclass++;
				}*/
                }
                finalclass++;


            }


            //System.out.printf("k %d\n", k);
			/*if(min<=sgrid_u[i]+sgrid_u[k]){
				final_class[finalclass] = s_grid[i]+" "+s_grid[k];
				finalclass++;
			}
			else{
				final_class[finalclass] = s_grid[i];
				finalclass++;
				final_class[finalclass] = s_grid[k];
				finalclass++;			
			}
			max_grid[k] = -1;*/

        }

        String sclass = "";
        for (int i = 0; i < finalclass; i++) {
            String[] st = final_class[i].split(" ");
            int sum = 0;
            for (int j = 0; j < st.length; j++) {
                sum += gm.g[Integer.parseInt(st[j])].number;
            }
            if (sum < 0.3 * dl) {
                final_class[i] = "";
                abnormalclass[abclass] = final_class[i];
                abclass++;
            }
        }
		
		/*for(int i=0;i<finalclass;i++){
			if(final_class[i]==""){
				System.out.printf("abnormal %f %f ",gm.g[max_grid[i]].p_mean.GetLongitude(),gm.g[max_grid[i]].p_mean.GetLatitude());
			}
			else{
				System.out.printf("%f %f ",gm.g[max_grid[i]].p_mean.GetLongitude(),gm.g[max_grid[i]].p_mean.GetLatitude());
			}
		}*/

        for (int i = 0; i < finalclass; i++) {
            //System.out.printf("%f %f %d\n",gm.g[max_grid[i]].p_mean.GetLongitude(),gm.g[max_grid[i]].p_mean.GetLatitude(),i+1);
            //System.out.printf("finalclass %d is %s\n", i+1,final_class[i]);
            if (final_class[i] == "")
                continue;
            String[] s_t = final_class[i].split(" ");
            //System.out.printf("finalclass %s\n",final_class[i]);
			
			/*for(int j=0;j<s_t.length;j++){
				//sreturn += gm.g[Integer.parseInt(s_t[j])].p_mean.GetLongitude()+" "+gm.g[Integer.parseInt(s_t[j])].p_mean.GetLatitude()+"//";
				if(s_t[j]=="")
					break;
				//System.out.printf("s_t j %d %s\n",j+1,s_t[j]);
				for(int k=0;k<gm.g[Integer.parseInt(s_t[j])].number;k++){
					System.out.printf("%f %f %d\n",gm.g[Integer.parseInt(s_t[j])].pt[k].GetLongitude(),gm.g[Integer.parseInt(s_t[j])].pt[k].GetLatitude(),i+1);
				}
				//System.out.println("//");
			}*/
            //System.out.println("\n");
            //System.out.println("//");
            //System.out.println("\n");
        }
		/*for(int i=0;i<classk;i++){
			//sreturn += gm.g[max_grid[i]].p_mean.GetLongitude()+" "+gm.g[max_grid[i]].p_mean.GetLatitude()+" "+sgrid_u[i]+"//";
			System.out.printf("class %d is %s\n", i+1,s_grid[i]);
			//System.out.printf("%f %f %d\n",gm.g[max_grid[i]].p_mean.GetLongitude(),gm.g[max_grid[i]].p_mean.GetLatitude(),i+1);
			String[] s_t = s_grid[i].split(" ");
			for(int j=0;j<s_t.length;j++){
				//sreturn += gm.g[Integer.parseInt(s_t[j])].p_mean.GetLongitude()+" "+gm.g[Integer.parseInt(s_t[j])].p_mean.GetLatitude()+"//";
				for(int k=0;k<gm.g[Integer.parseInt(s_t[j])].number;k++){
					System.out.printf("%f %f %d\n",gm.g[Integer.parseInt(s_t[j])].pt[k].GetLongitude(),gm.g[Integer.parseInt(s_t[j])].pt[k].GetLatitude(),i+1);
				}
				System.out.println("//");
			}
			System.out.println("\n");
			System.out.println("//");
		}*/


    }

    public double changek(String k1, GrideManage gm) {
        String[] st1 = k1.split(" ");
        double[][] data1 = new double[st1.length][2];

        for (int i = 0; i < st1.length; i++) {
            data1[i][0] = gm.g[Integer.parseInt(st1[i])].p_mean.GetLongitude();
            data1[i][1] = gm.g[Integer.parseInt(st1[i])].p_mean.GetLatitude();
        }
        PCA pca = new PCA();
        double max1 = pca.maxvalue(data1);
        return max1;
    }

    public void save(String username, String[] s) {
        try {

            //����SQLite��JDBC

            // Class.forName("org.sqlite.JDBC");
            Class.forName("org.sqlite.JDBC");

            //����һ�����ݿ���zieckey.db�����ӣ���������ھ��ڵ�ǰĿ¼�´���֮

            Connection conn = DriverManager.getConnection("jdbc:sqlite:zieckey.db");

            Statement stat = conn.createStatement();

            stat.executeUpdate("create table user_place(name varchar(20),week varchar(10),mornplace varchar(200),noonplace varchar(200),pmplace varchar(200),nightplace varchar(200);");//����һ��������


            stat.executeUpdate("insert into user_place values(username,'not weekend',s[0][0],s[0][1],s[0][2],s[0][3]);"); //��������

            ResultSet rs = stat.executeQuery("select * from tbl1 where name=username and week='not week';"); //��ѯ����

            while (rs.next()) { //����ѯ�������ݴ�ӡ����

                System.out.print("name = " + rs.getString("name") + " "); //������һ
                System.out.println("week = " + rs.getString("week")); //�����Զ�
                System.out.println("mornplace = " + rs.getString("mornplace")); //��������
                System.out.println("noonplace = " + rs.getString("noonplace")); //��������
                System.out.println("pmplace = " + rs.getString("pmplace")); //��������
                System.out.println("nightplace = " + rs.getString("nightplace")); //��������

            }
            rs.close();
            conn.close(); //�������ݿ������
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
