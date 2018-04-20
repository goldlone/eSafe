package cn.goldlone.safe.product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.goldlone.safe.R;

import java.util.List;

/**
 * Created by Tracy on 2017/6/6.
 */


public class Product_Adapter extends ArrayAdapter<product> {
    private int resourceId;
    public Product_Adapter(Context context, int textViewResourceId, List<product> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        product pro=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView proImage=(ImageView)view.findViewById(R.id.product_image);
        TextView proName=(TextView)view.findViewById(R.id.product_name);
        proImage.setImageResource(pro.getImageId());
        proName.setText(pro.getName());
        return view;
    }
}

