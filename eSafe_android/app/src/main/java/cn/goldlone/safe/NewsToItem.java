package cn.goldlone.safe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DUDU on 2017/7/12.
 */

public class NewsToItem {
    public static ArrayList<Item> newsToItems(List<News> list) {
        ArrayList<Item> items = new ArrayList<Item>();
        for (News news : list) {
            items.add(createTitle(news));
            if (News.TEXT == news.getType()) {
                items.add(createBody(news));
            }else {
                items.add(createImage(news));
            }
            items.add(createBottom(news));
        }
        return items;
    }

    private static Item createTitle(News news) {
        Item item = new Item();
        item.setNewsType(news.getType());
        item.setStyleType(Item.TITLE);
        item.setText(news.getTitle());
        return item;
    }

    private static Item createImage(News news) {
        Item item = new Item();
        item.setNewsType(news.getType());
        item.setStyleType(Item.IMAGE);
        item.setImageSource(news.getImageSource());
        return item;
    }

    private static Item createBody(News news) {
        Item item = new Item();
        item.setNewsType(news.getType());
        item.setStyleType(Item.BODY);
        item.setText(news.getBody());
        return item;
    }

    private static Item createBottom(News news) {
        Item item = new Item();
        item.setNewsType(news.getType());
        item.setStyleType(Item.BOTTOM);
        return item;
    }
}
