package com.chanin.lincc.exdisplay.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.chanin.lincc.exdisplay.DetailActivity;
import com.chanin.lincc.exdisplay.R;
import com.chanin.lincc.exdisplay.model.ExGroup;
import com.chanin.lincc.exdisplay.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {


    public Context context;
    public ArrayList<ExGroup> datas;


    public ContentAdapter(Context context) {
        this.context = context;
        this.datas = new ArrayList<>();
    }


    public void addData(ArrayList<ExGroup> datas) {
        if (!ListUtils.isEmpty(datas)) {
            int start = this.datas.size();
            this.datas.addAll(datas);
            int addCount = datas.size();
            notifyItemRangeInserted(start, addCount);
        }
    }

    public void setDatas(ArrayList<ExGroup> datas) {
        if (!ListUtils.isEmpty(datas)) {
            this.datas.clear();
            this.datas.addAll(datas);
            Collections.sort(this.datas, new Comparator<ExGroup>() {
                @Override
                public int compare(ExGroup o1, ExGroup o2) {
                    if (o2 == null || o1 == null) {
                        return 0;
                    }
                    try {
                        String s1 = o1.getDealState().substring(0, 1);
                        String s2 = o2.getDealState().substring(0, 1);
                        int i1 = Integer.parseInt(s1);
                        int i2 = Integer.parseInt(s2);
                        if (i1 == i2) {
                            return o1.getRecordTime().compareToIgnoreCase(o2.getRecordTime());
                        } else {
                            return i1 - i2;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

        } else {
            this.datas.clear();
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false));
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {

        final ExGroup exGroup = datas.get(position);
        holder.tvName.setText((position + 1) + "、");
        holder.tvState.setText(exGroup.getRecordTime() + "");
        String dealState = exGroup.getDealState();
        holder.tvDeal.setText(TextUtils.isEmpty(dealState) ? "" : dealState.substring(1));
        if(!TextUtils.isEmpty(dealState)&&"0".equalsIgnoreCase(dealState.substring(0,1))){
            holder.tvDeal.setTextColor(ContextCompat.getColor(context,R.color.red));
        }else {
            holder.tvDeal.setTextColor(ContextCompat.getColor(context,R.color.textColorPrimary));
        }

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.color3));
        } else {
            holder.llItem.setBackgroundColor(context.getResources().getColor(R.color.color4));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.startContent(context, exGroup);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public static class ContentViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout llItem;
        private TextView tvName;
        private TextView tvState;
        private TextView tvDeal;

        public ContentViewHolder(View itemView) {
            super(itemView);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvState = (TextView) itemView.findViewById(R.id.tv_state);
            tvDeal = (TextView) itemView.findViewById(R.id.tv_deal);
        }
    }

}
