package com.jinr.core.transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.ArrayList;
import java.util.List;

import model.TransferInfo;

public class TransferAdapter extends BaseAdapter {
    public List<TransferInfo> popList = new ArrayList<>();
    private Context context;

    public TransferAdapter(Context context, List<TransferInfo> list) {
        this.context = context;
        popList = list;
    }

    @Override
    public int getCount() {
        return popList.size();
    }

    @Override
    public Object getItem(int position) {
        return popList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_transfered_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_rate = (TextView) convertView.findViewById(R.id.tv_rate);
            viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_days = (TextView) convertView.findViewById(R.id.tv_days);
            viewHolder.tv_discount = (TextView) convertView.findViewById(R.id.tv_discount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_rate.setText(popList.get(position).getToday_rate());
        viewHolder.tv_money.setText(popList.get(position).getTransfer_money());
        viewHolder.tv_name.setText(popList.get(position).getProduct_name());
        viewHolder.tv_days.setText(popList.get(position).getRemain_time());
        viewHolder.tv_discount.setText(popList.get(position).getDiscount() + "元");
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_rate;
        TextView tv_money;
        TextView tv_name;
        TextView tv_discount;
        TextView tv_days;
    }
}
