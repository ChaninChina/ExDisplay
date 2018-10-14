package com.chanin.lincc.exdisplay.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.chanin.lincc.exdisplay.ContentActivity;
import com.chanin.lincc.exdisplay.R;
import com.chanin.lincc.exdisplay.model.ExClass;
import com.chanin.lincc.exdisplay.utils.ListUtils;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {


    public Context context;
    public ArrayList<ExClass> datas;
    public static final int [] colors = {R.color.color4,R.color.color3,R.color.color2,R.color.color1,R.color.color0,R.color.white};



    public MainAdapter(Context context) {
        this.context = context;
        this.datas = new ArrayList<>();
    }


    public void addData(ArrayList<ExClass> datas) {
        if (!ListUtils.isEmpty(datas)) {
            int start = this.datas.size();
            this.datas.addAll(datas);
            int addCount = datas.size();
            notifyItemRangeInserted(start, addCount);
        }
    }

    public void setDatas(ArrayList<ExClass> datas) {
        if (datas!=null) {
            this.datas.clear();
            this.datas.addAll(datas);
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mian_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        final ExClass exClass = datas.get(position);
        holder.tvClass.setText(exClass.getName());
        holder.tvCount.setText(exClass.getCount()+"");
        holder.tvUnCount.setText(exClass.getUnCount()+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentActivity.startContent(context,exClass);
            }
        });
        if(position==0){
            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.color0));
        }else if (position ==1){
            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.color1));
        }else {
            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.color2));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        private TextView tvClass;
        private TextView tvCount;
        private TextView tvUnCount;
        private LinearLayout llItem;

        public MainViewHolder(View itemView) {
            super(itemView);

            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
            tvClass = (TextView) itemView.findViewById(R.id.tv_class);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            tvUnCount = (TextView) itemView.findViewById(R.id.tv_un_count);
        }
    }

}
