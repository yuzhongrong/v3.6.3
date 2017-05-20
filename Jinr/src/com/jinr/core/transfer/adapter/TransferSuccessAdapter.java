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

import model.TransferSuccessInfo;

public class TransferSuccessAdapter extends BaseAdapter {
    public List<TransferSuccessInfo> popList = new ArrayList<>();
    private Context context;

    public TransferSuccessAdapter(Context context, List<TransferSuccessInfo> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.transfer_success_item, null);
            viewHolder = new ViewHolder();
            viewHolder.currentAnnualRate = (TextView) convertView.findViewById(R.id.current_annual_rate);
            viewHolder.transferAmount = (TextView) convertView.findViewById(R.id.transfer_amount);
            viewHolder.residualMaturity = (TextView) convertView.findViewById(R.id.residual_maturity);
            viewHolder.allowanceAmountTv = (TextView) convertView.findViewById(R.id.allowance_amount_tv);
            viewHolder.current_annual_rate_tv = (TextView) convertView.findViewById(R.id.current_annual_rate_tv);
            viewHolder.residual_maturity_tv = (TextView) convertView.findViewById(R.id.residual_maturity_tv);
            viewHolder.current_annual_rate_tv.setText("当日收益率");
            viewHolder.residual_maturity_tv.setText("当日剩余期限");
            viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.currentAnnualRate.setText(popList.get(position).getRate());
        viewHolder.transferAmount.setText(popList.get(position).getTransfer_money());
        viewHolder.residualMaturity.setText(popList.get(position).getRemaining_days());
        viewHolder.allowanceAmountTv.setText("降价：" + popList.get(position).getDiscount() + "元");
        viewHolder.titleTv.setText(popList.get(position).getProduct_name());
        return convertView;
    }

    public static class ViewHolder {
        TextView currentAnnualRate;
        TextView transferAmount;
        TextView residualMaturity;
        TextView allowanceAmountTv;
        TextView current_annual_rate_tv;
        TextView residual_maturity_tv;
        TextView titleTv;
    }
}
