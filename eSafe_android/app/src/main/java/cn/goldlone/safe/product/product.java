package cn.goldlone.safe.product;

/**
 * Created by Tracy on 2017/6/6.
 */

public class product {
    private  String name;
    private int imageId;
    public product(String name,int id){
        this.imageId=id;
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
