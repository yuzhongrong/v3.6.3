/*
 * TradeRecordAdapter.java
 * @author Andrew Lee
 * 2014-10-27 上午9:10:06
 */
package com.jinr.core.trade.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.List;

import model.Record;

/**
 * TradeRecordAdapter.java
 *
 * @author Andrew Lee
 * @version 2014-10-27 上午9:10:06
 * @description:
 */
@SuppressLint("InflateParams")
public class TradeRecordAdapter extends BaseAdapter {

    public List<Record> list;
    private Context mContext;
    private String describle = "";

    public TradeRecordAdapter(Context context, List<Record> list) {

        this.mContext = context;
        this.list = list;
    }

    public TradeRecordAdapter(Context context, List<Record> list, String describle) {
        this.describle = describle;
        this.mContext = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	/* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.trade_record_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tradeItemLinLayout = (RelativeLayout) convertView.findViewById(R.id.trade_item);
            viewHolder.dateLinearLayout = (LinearLayout) convertView.findViewById(R.id.date_title);
            viewHolder.title = (TextView) convertView.findViewById(R.id.trade_date);
            viewHolder.turn_tv = (TextView) convertView.findViewById(R.id.trade_item_tv1);
            viewHolder.img_in_or_out = (ImageView) convertView.findViewById(R.id.img_in_or_out);
            viewHolder.time_tv = (TextView) convertView.findViewById(R.id.trade_item_tv2);
            viewHolder.money_tv = (TextView) convertView.findViewById(R.id.trade_item_tv3);
            viewHolder.state = (TextView) convertView.findViewById(R.id.trade_state);
            viewHolder.line = convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == list.size() - 1) {
            viewHolder.line.setVisibility(View.GONE);
        } else {
            viewHolder.line.setVisibility(View.VISIBLE);
        }
        final Record record = list.get(position);
        if (!TextUtils.isEmpty(record.getOrder_id())) {
            if (("-1").equals(record.getOrder_id())) {//标题的标志
                viewHolder.dateLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.tradeItemLinLayout.setVisibility(View.GONE);
                viewHolder.title.setText(list.get(position).getC_time());
            } else {
                viewHolder.tradeItemLinLayout.setVisibility(View.VISIBLE);
                viewHolder.dateLinearLayout.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(record.getTitle())) {
            viewHolder.turn_tv.setText(record.getTitle());
        }
        if (!TextUtils.isEmpty(record.getC_time())) {
            viewHolder.time_tv.setText(record.getC_time());
        }

        if (!TextUtils.isEmpty(record.getStatus())) {
            viewHolder.state.setText(record.getStatus());
        }
        if (TextUtils.isEmpty(describle)) {
            viewHolder.img_in_or_out.setVisibility(View.GONE);
            if ("1".equals(record.getType())) {
                viewHolder.money_tv.setTextColor(mContext.getResources().getColor(R.color.record_money_in));
            } else {
                viewHolder.money_tv.setTextColor(mContext.getResources().getColor(R.color.record_money_out));
            }
        } else {
            if (!"6".equals(describle)) {
                if ("1".equals(record.getType())) {
                    viewHolder.img_in_or_out.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.change_into_ico));
                } else {
                    viewHolder.img_in_or_out.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.change_out));
                }
            } else {
                viewHolder.img_in_or_out.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.record_transfer_icon));
            }
        }
        if (!"6".equals(record.getGoods_type())) {
            if (!TextUtils.isEmpty(record.getMoney())) {
                viewHolder.money_tv.setText(record.getMoney());
            }
        } else {
            if (!TextUtils.isEmpty(record.getContract_num())) {
                viewHolder.money_tv.setText(record.getContract_num());
            }
        }


        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!TextUtils.isEmpty(record.getOrder_id())) {
                    if (!record.getOrder_id().equals("-1")) {
                        Intent intent = new Intent();
                        if (!TextUtils.isEmpty(record.getUrl())) {
                            intent.setClass(mContext, WebRecordGetCash.class);
                            intent.putExtra("url", record.getUrl());
                        } else {
                            intent.setClass(mContext, RecordDetailActivity.class);
                            intent.putExtra("record", record);
                        }
                        mContext.startActivity(intent);
                    }
                }
            }
        });
        return convertView;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public static class ViewHolder {
        TextView state;
        TextView title;
        TextView turn_tv;
        TextView time_tv;
        TextView money_tv;
        ImageView img_in_or_out;
        LinearLayout dateLinearLayout;
        RelativeLayout tradeItemLinLayout;
        View line;
    }
}


