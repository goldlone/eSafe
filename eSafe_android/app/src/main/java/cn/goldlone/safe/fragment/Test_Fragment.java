package cn.goldlone.safe.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.goldlone.safe.R;
import cn.goldlone.safe.product.Product_Adapter;
import cn.goldlone.safe.product.product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tracy on 2017/6/6.
 */

public class Test_Fragment extends Fragment {
    private List<product> productList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_test, container, false);
        initproduct();
        Product_Adapter adapter=new Product_Adapter(getContext(),R.layout.product_item,productList);
        ListView listView=(ListView)v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        String url = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=131259851&spm=a230r.7195193.1997079397.8.Pp3ZMM&point";
                        Uri uri = Uri.parse(url);
                        intent.setData(uri);
                        startActivity(intent);
                    case 1:
                        Intent intent2 = new Intent();
                        intent2.setAction("android.intent.action.VIEW");
                        String url2 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=116987438&spm=2013.1.1000126.2.6P85Rh&point";
                        Uri uri2 = Uri.parse(url2);
                        intent2.setData(uri2);
                        startActivity(intent2);
                    case 3:
                        Intent intent3 = new Intent();
                        intent3.setAction("android.intent.action.VIEW");
                        String url3 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=xiaoxuanjia516&spm=2013.1.1000126.3.7COE60&point";
                        Uri uri3 = Uri.parse(url3);
                        intent3.setData(uri3);
                        startActivity(intent3);
                    case 4:
                        Intent intent4 = new Intent();
                        intent4.setAction("android.intent.action.VIEW");
                        String url4 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=135892465&spm=spm=2013.1.1000126.2.TNfAjx";
                        Uri uri4 = Uri.parse(url4);
                        intent4.setData(uri4);
                        startActivity(intent4);

                    case 5:
                        Intent intent5 = new Intent();
                        intent5.setAction("android.intent.action.VIEW");
                        String url5 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=tjxsm&spm=spm=a220o.1000855.1997427721.d4918089.HQpOsA";
                        Uri uri5 = Uri.parse(url5);
                        intent5.setData(uri5);
                        startActivity(intent5);
                }

            }
        });
        return v;
    }

    private  void initproduct(){
        for (int i=0;i<5;i++){
            product _1=new product("1",R.drawable.a);
            productList.add(_1);
            product _2=new product("2",R.drawable.b);
            productList.add(_2);
            product _3=new product("3",R.drawable.c);
            productList.add(_3);
            product _4=new product("4",R.drawable.d);
            productList.add(_4);
            product _5=new product("5",R.drawable.e);
            productList.add(_5);
        }
    }
}
