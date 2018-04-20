package cn.goldlone.safe;

/**
 * Created by DUDU on 2017/7/12.
 */

public class Item {
    public static final int TITLE=0;
    public static final int IMAGE=1;
    public static final int BODY=2;
    public static final int BOTTOM=3;

    private int newsType;//News类型
    private int newsId;
    private int styleType;//显示类型
    private String text;
    private int imageSource;

    public void setNewsType(int newsType){
        this.newsType=newsType;
    }

    public void setStyleType(int styleType){
        this.styleType=styleType;
    }
    public void setText(String text){
        this.text=text;
    }
    public void setImageSource(int imageSource){
        this.imageSource=imageSource;
    }
    public String getText(){
        return text;
    }
    public int getImageSource(){
        return imageSource;
    }
    public int getStyleType(){
        return styleType;
    }
}
