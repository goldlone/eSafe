package cn.goldlone.esafe.monitor;

public class ListUDG {
    // �ڽӱ��б��Ӧ������Ķ���
    private class ENode {
        int ivex;       // �ñ���ָ��Ķ����λ��
        ENode nextEdge; // ָ����һ������ָ��
    }

    // �ڽӱ��б�Ķ���
    private class VNode {
        int data;          // ������Ϣ
        ENode firstEdge;    // ָ���һ�������ö���Ļ�
        ENode parent;       //���ڵ�
    }

    ;

    private VNode[] mVexs;  // ������d��
    private int[][] medges;
    private int vlen;
    private int edges;
    private int[] visited;

    /*
     * ����ͼ(�����ṩ�ľ���)
     *
     * ����˵����
     *     vexs  -- ��������
     *     edges -- ������
     */
    public ListUDG(Windowp wp, int bool, GrideManage gm) {

        if (bool == 1) {
            // ��ʼ��"������"��"����"
            //int vlen = vexs.length;

            //int vlen = wp.den_node.length;
            String sdd[] = gm.sd.split(" ");
            int vlen = sdd.length;
            int elen = wp.den_edgelg;
            visited = new int[vlen];
            // ��ʼ��"����"
            mVexs = new VNode[vlen];
            for (int i = 0; i < mVexs.length; i++) {
                mVexs[i] = new VNode();
                mVexs[i].data = Integer.parseInt(sdd[i]);
                mVexs[i].firstEdge = null;
            }

            // ��ʼ��"��"
            for (int i = 0; i < elen; i++) {
                // ��ȡ�ߵ���ʼ����ͽ�������
                int c1 = wp.den_edge[i][0];
                int c2 = wp.den_edge[i][1];
                // ��ȡ�ߵ���ʼ����ͽ�������
                int p1 = getPosition(wp.den_edge[i][0], vlen);
                int p2 = getPosition(wp.den_edge[i][1], vlen);
                // ��ʼ��node1
                ENode node1 = new ENode();
                node1.ivex = p2;
                node1.nextEdge = null;
                // ��node1���ӵ�"p1���������ĩβ"
                if (mVexs[p1].firstEdge == null) {
                    mVexs[p1].firstEdge = node1;
                    mVexs[p1].parent = node1;
                } else
                    linkLast(mVexs[p1], node1);
                // ��ʼ��node2
                ENode node2 = new ENode();
                node2.ivex = p1;
                node2.nextEdge = null;
                // ��node2���ӵ�"p2���������ĩβ"
                if (mVexs[p2].firstEdge == null) {
                    mVexs[p2].firstEdge = node2;
                    mVexs[p2].parent = node2;
                } else
                    linkLast(mVexs[p2], node2);
            }
        } else if (bool == 0) {
            // ��ʼ��"������"��"����"
            //int vlen = vexs.length;
            String sec[] = gm.ss.split(" ");
            int vlen = sec.length;
            //System.out.printf("sec_node %d\n",wp.sec_node.length);
            //System.out.printf("id sec_node %d\n",wp.sec_node[0]);
            int elen = wp.sec_edgelg;
            visited = new int[vlen];
            // ��ʼ��"����"
            mVexs = new VNode[vlen];
            for (int i = 0; i < mVexs.length; i++) {
                mVexs[i] = new VNode();
                //mVexs[i].data = wp.sec_node[i];
                mVexs[i].data = Integer.parseInt(sec[i]);
                mVexs[i].firstEdge = null;
            }

            // ��ʼ��"��"
            for (int i = 0; i < elen; i++) {
                // ��ȡ�ߵ���ʼ����ͽ�������
                int c1 = wp.sec_edge[i][0];
                int c2 = wp.sec_edge[i][1];
                // ��ȡ�ߵ���ʼ����ͽ�������
                int p1 = getPosition(c1, vlen);
                int p2 = getPosition(c2, vlen);
                // ��ʼ��node1
                ENode node1 = new ENode();
                node1.ivex = p2;
                node1.nextEdge = null;
                // ��node1���ӵ�"p1���������ĩβ"
                if (mVexs[p1].firstEdge == null) {
                    mVexs[p1].firstEdge = node1;
                    mVexs[p1].parent = node1;
                } else
                    linkLast(mVexs[p1], node1);
                // ��ʼ��node2
                ENode node2 = new ENode();
                node2.ivex = p1;
                node2.nextEdge = null;
                // ��node2���ӵ�"p2���������ĩβ"
                if (mVexs[p2].firstEdge == null) {
                    mVexs[p2].firstEdge = node2;
                    mVexs[p2].parent = node2;
                } else
                    linkLast(mVexs[p2], node2);
            }
        } else if (bool == 2) {
            // ��ʼ��"������"��"����"
            //int vlen = vexs.length;
            String thin[] = gm.st.split(" ");
            int vlen = thin.length;
            //System.out.printf("thin_node %d\n",wp.thin_node.length);
            //System.out.printf("id sec_node %d\n",wp.sec_node[0]);
            int elen = wp.thin_edgelg;
            visited = new int[vlen];
            // ��ʼ��"����"
            mVexs = new VNode[vlen];
            for (int i = 0; i < mVexs.length; i++) {
                mVexs[i] = new VNode();
                //mVexs[i].data = wp.sec_node[i];
                mVexs[i].data = Integer.parseInt(thin[i]);
                mVexs[i].firstEdge = null;
            }

            // ��ʼ��"��"
            for (int i = 0; i < elen; i++) {
                // ��ȡ�ߵ���ʼ����ͽ�������
                int c1 = wp.thin_edge[i][0];
                int c2 = wp.thin_edge[i][1];
                // ��ȡ�ߵ���ʼ����ͽ�������
                int p1 = getPosition(c1, vlen);
                int p2 = getPosition(c2, vlen);
                // ��ʼ��node1
                ENode node1 = new ENode();
                node1.ivex = p2;
                node1.nextEdge = null;
                // ��node1���ӵ�"p1���������ĩβ"
                if (mVexs[p1].firstEdge == null) {
                    mVexs[p1].firstEdge = node1;
                    mVexs[p1].parent = node1;
                } else
                    linkLast(mVexs[p1], node1);
                // ��ʼ��node2
                ENode node2 = new ENode();
                node2.ivex = p1;
                node2.nextEdge = null;
                // ��node2���ӵ�"p2���������ĩβ"
                if (mVexs[p2].firstEdge == null) {
                    mVexs[p2].firstEdge = node2;
                    mVexs[p2].parent = node2;
                } else
                    linkLast(mVexs[p2], node2);
            }
        }
    }

    public int getPosition(int nd, int vlen) {

        int k = -1;
        for (int i = 0; i < vlen; i++) {
            if (mVexs[i].data == nd) {
                k = i;
                return k;
            }
        }
        return k;
    }

    public void linkLast(VNode vnode, ENode node) {
        vnode.parent.nextEdge = node;
        vnode.parent = node;
    }

    void DFS(int i, int k, GrideManage gm, Windowp wp, int lab) {
        visited[i] = 1;
        //System.out.printf("node %d\n",i);
        gm.g[mVexs[i].data].status = k;
        wp.s_grid[k - 1] += mVexs[i].data + " ";
        wp.s_gridlab[k - 1] = lab;
        //System.out.printf("s_grid[] %d %s\n",k-1,wp.s_grid[k-1]);

        ENode p = mVexs[i].firstEdge;
        while (p != null) {
            if (visited[p.ivex] == 0) {
                DFS(p.ivex, k, gm, wp, lab); //�ݹ���ȱ���
            }
            p = p.nextEdge;
        }
    }


    /**
     * ������ȱ���
     */
    void DFSTraverse(GrideManage gm, Windowp wp, int lab) {
        int i;
        //System.out.printf("node.length %d\n",mVexs.length);
        for (i = 0; i < mVexs.length; ++i) {
            visited[i] = 0;  //��ʼ����������visited��Ԫ��ֵΪfalse
        }
        for (i = 0; i < mVexs.length; ++i) {
            if (visited[i] == 0) { //�ڵ���δ����
                int t = i + 1;
                wp.classk++;
                //System.out.printf("class %d\n",wp.classk);
                wp.s_grid[wp.classk - 1] = "";
                //System.out.printf("s_grid[ ] %s\n",wp.s_grid[wp.classk-1] );
                DFS(i, wp.classk, gm, wp, lab);
            }
        }
    }

    void DFSTo(GrideManage gm, Windowp wp) {

    }

}