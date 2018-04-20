package cn.goldlone.esafe.monitor;

import Jama.Matrix;

public class PCA {
	public PCA() {
	}

	/**
	 * 将原始数据标准化
	 * */
	public double[][] Standardlizer(double[][] x){
		int n=x.length;		//二维矩阵的行号
		int p=x[0].length;	//二维矩阵的列号
		double[] average=new double[p];	//每一列的平均值
		double[][] result=new double[n][p];	//标准化后的向量
		double[] var=new double[p];      //方差
		//取得每一列的平均值
		for(int k=0;k<p;k++){
			double temp=0;
			for(int i=0;i<n;i++){
				temp+=x[i][k];
			}
			average[k]=temp/n;
		}
		//取得方差
		for(int k=0;k<p;k++){
			double temp=0;
			for(int i=0;i<n;i++){
				temp+=(x[i][k]-average[k])*(x[i][k]-average[k]);
			}
			var[k]=temp/(n-1);
		}
		//获得标准化的矩阵
		for(int i=0;i<n;i++){
			for(int j=0;j<p;j++){
				result[i][j]=(double) ((x[i][j]-average[j])/Math.sqrt(var[j]));
			}
		}
		return result;
		
	}
	/**
	 * 计算样本相关系数矩阵
	 * @param x 处理后的标准矩阵
	 * @return 系数矩阵
	 * */
	public double[][] CoefficientOfAssociation(double[][] x){
		int n=x.length;		//二维矩阵的行号
		int p=x[0].length;	//二维矩阵的列号
		double[][] result=new double[p][p];//相关系数矩阵
		for(int i=0;i<p;i++){
			for(int j=0;j<p;j++){
				double temp=0;
				for(int k=0;k<n;k++){
					temp+=x[k][i]*x[k][j];
				}
				result[i][j]=temp/(n-1);
			}
		}
		return result;
		
	}
	/**
	 * 计算相关系数矩阵的特征值
	 * @param x 相关系数举证
	 * @return 矩阵特征值
	 * */
	public double[][] FlagValue(double[][] x){
		//定义一个矩阵
	      Matrix A = new Matrix(x);
	      //由特征值组成的对角矩阵
	     Matrix B=A.eig().getD();
	     double[][] result=B.getArray();
	     return result;
	
		
	}
	
	public double maxvalue(double[][] value){
		double max1 = 0;
		double max2 = 0;
		for(int i=0;i<value.length;i++){
			//for(int j=0;j<value[0].length;j++){
				if(value[i][i]>max1)
					max1 = value[i][i];
			//}
		}
		for(int i=0;i<value.length;i++){
			//for(int j=0;j<value[0].length;j++){
				if(value[i][i]>max2&&value[i][i]!=max1)
					max2 = value[i][i];
			//}
		}
		return max1/max2;
	}

}
