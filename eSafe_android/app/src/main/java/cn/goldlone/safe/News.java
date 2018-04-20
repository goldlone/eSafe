package cn.goldlone.safe;

/**
 * Created by DUDU on 2017/7/12.
 */

public class News {
    public static final int TEXT = 1;
    public static final int IMAGE = 2;

    private int newsId;
    private int type;
    private String title;
    private String body;
    private int imageSource;

    public News(int newsId, int type, String title, String body, int imageSource) {
        this.newsId = newsId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.imageSource = imageSource;
    }

    public int getType(){
        return type;
    }

    public String getTitle(){
        return title;
    }
    public int getImageSource(){
        return imageSource;
    }
    public String getBody(){
        return body;
    }
}
