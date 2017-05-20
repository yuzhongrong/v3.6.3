package com.jinr.core.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import model.PayInfo;

public class PayListAdapter extends BaseAdapter {

    private ArrayList<PayInfo> payList;
    private Context mContext;
    private Double mMoney;

    public PayListAdapter(Context context, ArrayList<PayInfo> list, Double money) {
        payList = list;
        mContext = context;
        mMoney = money;
    }

    @Override
    public int getCount() {
        return payList.size();
    }

    @Override
    public Object getItem(int position) {
        return payList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_pay_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.currentAvailableImg = (ImageView) convertView.findViewById(R.id.current_available_img);
            viewHolder.availableCapital = (TextView) convertView.findViewById(R.id.available_capital);
            viewHolder.availableCapitalTv = (TextView) convertView.findViewById(R.id.available_capital_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PayInfo payInfo = payList.get(position);
        if (mMoney > Double.valueOf(payInfo.getMoney())) {//如果大于输入金额显示灰色图标
            ImageLoader.getInstance().displayImage(payInfo.getImg_disable(), viewHolder.currentAvailableImg);
            viewHolder.availableCapital.setTextColor(mContext.getResources().getColor(R.color.redbag_black));
        } else {
            ImageLoader.getInstance().displayImage(payInfo.getImg(), viewHolder.currentAvailableImg);
            viewHolder.availableCapital.setTextColor(mContext.getResources().getColor(R.color.title_security));
        }
//        if (payInfo.getGoods_type().equals("1")) {//如果是银行卡显示银行图标
//            ImageLoader.getInstance().displayImage(payInfo.getImg(), viewHolder.currentAvailableImg);
//        }
        viewHolder.availableCapital.setText(payInfo.getTitle());
        viewHolder.availableCapitalTv.setText(payInfo.getBewrite());
        return convertView;
    }

    public static class ViewHolder {
        ImageView currentAvailableImg;
        TextView availableCapital;
        TextView availableCapitalTv;
    }
}
