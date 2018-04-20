package cn.goldlone.safe;

import android.app.ListActivity;
import android.os.Bundle;

import java.util.ArrayList;

import cn.goldlone.safe.adapter.NewsAdapter;

/**
 * Created by DUDU on 2017/7/9.
 */

public class Compulsory_service extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        getListView().setDividerHeight(0);
        getListView().setPadding(10, 10, 10, 10);
        ArrayList<News> newses = new ArrayList<>();
        newses.add(new News(0, News.TEXT, "招募信息", "由于8月20日家中成员均有事外出，需要一名志愿者帮忙照顾一上午留在家中的老人。" +
                "无特殊要求，仅需看住老人，不要走丢就好，在此感谢志愿者的帮助。" +
                "\n地点：太原市小店区北张小区23楼302" +
                "\n时间：2017年8月20日上午9点至12点" +
                "\n电话：15703574387" , 0));
        NewsAdapter adapter = new NewsAdapter(this);
        adapter.addAll(NewsToItem.newsToItems(newses));
        setListAdapter(adapter);
    }
}
