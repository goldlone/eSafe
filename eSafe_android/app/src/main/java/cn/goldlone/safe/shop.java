package cn.goldlone.safe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.goldlone.safe.R;
import cn.goldlone.safe.product.Product_Adapter;
import cn.goldlone.safe.product.product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Created by DUDU on 2017/7/9.
 */

public class shop extends AppCompatActivity {
    private List<product> productList=new ArrayList<>();
    private ViewPager mViewPaper;
    private List<ImageView> images;
    private List<View> dots;
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;
    //存放图片的id
    private int[] imageIds = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };
    private String[]  titles = new String[]{
            "变形精钢等你来拿",
            "白毛衣引领时尚",
            "宝宝的牛仔裤",
            "紫色连衣裙",
            "老年必备手机"
    };
    private TextView title;
    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);
        mViewPaper = (ViewPager) findViewById(R.id.vp);

        //显示的图片
        images = new ArrayList<ImageView>();
        for(int i = 0; i < imageIds.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }
        //显示的小点
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));

        title = (TextView) findViewById(R.id.title);
        title.setText(titles[0]);

        adapter = new ViewPagerAdapter();
        mViewPaper.setAdapter(adapter);

        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);

                oldPosition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        initproduct();

        Product_Adapter adapter=new Product_Adapter(getContext(), R.layout.product_item,productList);
        ListView listView=(ListView)findViewById(R.id.shop_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(shop.this,"hello",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        String url = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=131259851&spm=a230r.7195193.1997079397.8.Pp3ZMM&point";
                        Uri uri = Uri.parse(url);
                        intent.setData(uri);
                        startActivity(intent);
                    case 1:
                        Toast.makeText(shop.this,"hello",Toast.LENGTH_SHORT).show();
//                        Intent intent2 = new Intent();
//                        intent2.setAction("android.intent.action.VIEW");
//                        String url2 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=116987438&spm=2013.1.1000126.2.6P85Rh&point";
//                        Uri uri2 = Uri.parse(url2);
//                        intent2.setData(uri2);
//                        startActivity(intent2);
                    case 3:
                        Toast.makeText(shop.this,"hello",Toast.LENGTH_SHORT).show();
//                        Intent intent3 = new Intent();
//                        intent3.setAction("android.intent.action.VIEW");
//                        String url3 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=xiaoxuanjia516&spm=2013.1.1000126.3.7COE60&point";
//                        Uri uri3 = Uri.parse(url3);
//                        intent3.setData(uri3);
//                        startActivity(intent3);
                    case 4:
                        Toast.makeText(shop.this,"hello",Toast.LENGTH_SHORT).show();
//                        Intent intent4 = new Intent();
//                        intent4.setAction("android.intent.action.VIEW");
//                        String url4 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=135892465&spm=spm=2013.1.1000126.2.TNfAjx";
//                        Uri uri4 = Uri.parse(url4);
//                        intent4.setData(uri4);
//                        startActivity(intent4);

                    case 5:
                        Toast.makeText(shop.this,"hello",Toast.LENGTH_SHORT).show();
//                        Intent intent5 = new Intent();
//                        intent5.setAction("android.intent.action.VIEW");
//                        String url5 = "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=tjxsm&spm=spm=a220o.1000855.1997427721.d4918089.HQpOsA";
//                        Uri uri5 = Uri.parse(url5);
//                        intent5.setData(uri5);
//                        startActivity(intent5);
                }

            }
        });
    }
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
//			super.destroyItem(container, position, object);
//			view.removeView(view.getChildAt(position));
//			view.removeViewAt(position);
            view.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            return images.get(position);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                2,
                2,
                TimeUnit.SECONDS);
    }
    private class ViewPageTask implements Runnable{

        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            mHandler.sendEmptyMessage(0);
        }
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mViewPaper.setCurrentItem(currentItem);
        };
    };
    @Override
    protected void onStop() {
        super.onStop();
    }
    private  void initproduct(){
        for (int i=0;i<5;i++){
            product _1=new product("儿童玩具", R.drawable.p1);
            productList.add(_1);
            product _2=new product("儿童玩具", R.drawable.p2);
            productList.add(_2);
            product _3=new product("儿童玩具", R.drawable.p3);
            productList.add(_3);
            product _4=new product("儿童玩具", R.drawable.p4);
            productList.add(_4);
            product _5=new product("儿童玩具", R.drawable.p5);
            productList.add(_5);
        }
    }
}
