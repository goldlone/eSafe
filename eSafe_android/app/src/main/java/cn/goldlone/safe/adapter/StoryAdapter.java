package cn.goldlone.safe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.goldlone.safe.R;
import cn.goldlone.safe.utils.FileSave;

import java.io.File;
import java.util.List;

/**
 * Created by xunixhuang on 06/10/2016.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<File> fileList;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public StoryAdapter(Context context) {
        mContext = context;
        fileList = FileSave.getStoryFiles();
        mLayoutInflater = LayoutInflater.from(context);
    }
    public File getFile(int position){
        return fileList.get(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.card_homework, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.homeworkName.setText(fileList.get(position).getName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView homeworkName;
        CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.homeworkName=(TextView)itemView.findViewById(R.id.homeworkTextview);
            this.cardView=(CardView)itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(itemView, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickListener != null) {
                longClickListener.onLongClick(itemView, getAdapterPosition());
            }
            return true;
        }
    }
    public void onRefresh(){
        fileList=FileSave.getStoryFiles();
        super.notifyDataSetChanged();
    }
    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnItemLongClickListener clickListener) {
        this.longClickListener = clickListener;
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
    public boolean deleteFile(int position){
        boolean ans=fileList.get(position).delete();
        onRefresh();
        return ans;
    }
}