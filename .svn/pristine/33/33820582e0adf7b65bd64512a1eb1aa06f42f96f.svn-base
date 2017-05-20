package com.jinr.core.my_change;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.ArrayList;

import model.Profit;

public class NewProfitAmountAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Profit> list;
    private String name;

    public NewProfitAmountAdapter(Context context, ArrayList<Profit> list, String name) {
        this.context = context;
        this.list = list;
        this.name = name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Profit profit = list.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_profit, null);
            viewHolder = new ViewHolder();
            viewHolder.profitName = (TextView) convertView.findViewById(R.id.profit_name);
            viewHolder.profitMoney = (TextView) convertView.findViewById(R.id.profit_money);
            viewHolder.profitTime = (TextView) convertView.findViewById(R.id.profit_time);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            if (!name.equals("昨日收益") && !name.equals("累计收益")) {
                viewHolder.profitTime.setVisibility(View.VISIBLE);
            }
            if (name.equals("累计收益")) {
                viewHolder.icon.setVisibility(View.VISIBLE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.profitName.setText(profit.getName());
        viewHolder.profitMoney.setText("+" + profit.getMoney());
        viewHolder.profitTime.setText(profit.getDate());
        return convertView;
    }

    static class ViewHolder {
        TextView profitName;
        TextView profitMoney;
        TextView profitTime;
        ImageView icon;
    }
}
