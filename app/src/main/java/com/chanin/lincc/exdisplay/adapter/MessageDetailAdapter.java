package com.chanin.lincc.exdisplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chanin.lincc.exdisplay.R;
import com.chanin.lincc.exdisplay.model.PushMessage;
import com.chanin.lincc.exdisplay.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lincc on 2017/12/18.
 */

public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.ViewHolder> {


    public Context context;
    public ArrayList<PushMessage> items;



    public MessageDetailAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_detail, parent, false));
    }


//    public long getMaxTimestamp(){
//        if(ListUtils.isEmpty(items)){
//            return -1;
//        }else {
//            return items.get(0).getTime();
//        }
//    }
//
//    public long getMinTimestamp(){
//        if(ListUtils.isEmpty(items)){
//            return -1;
//        }else {
//            return items.get(items.size()-1).getTime();
//        }
//    }

    public void addOldData(List<PushMessage> xxxxes){
        int size = items.size();
        items.addAll(xxxxes);
        notifyItemRangeInserted(size,xxxxes.size());
    }

    public void addNewData(List<PushMessage> xxxxes){
        //int size = items.size();
        items.addAll(0,xxxxes);
        notifyItemRangeInserted(0,xxxxes.size());
    }

    public void updateData(List<PushMessage> xxxxes){
        items.clear();
        items.addAll(xxxxes);
        notifyDataSetChanged();

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PushMessage messageDetail = items.get(position);
        holder.tvMessage.setText(messageDetail.getContent());
        holder.tvTime.setText(DateUtil.formatDateToStr(messageDetail.getTime()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;
        TextView tvMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }


}
