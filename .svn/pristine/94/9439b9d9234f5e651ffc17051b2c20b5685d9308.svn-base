package com.jinr.core.regular;

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
import com.jinr.core.dayup.MyDayUpActivity;
import com.jinr.core.utils.UmUtils;

import java.util.List;

import model.ProductCommonModel;
import model.RegularRecord;

public class RegularAssetsAdaper extends BaseAdapter {
    private List<RegularRecord> list;
    private Context context;
    private ProductCommonModel lastProductCommonModel;

    public RegularAssetsAdaper(List<RegularRecord> list, ProductCommonModel productCommonModel, Context context) {
        this.list = list;
        this.context = context;
        this.lastProductCommonModel = productCommonModel;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final RegularRecord regular = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_regular_assets, null);
            viewHolder.tvYieldAmt = (TextView) convertView.findViewById(R.id.tv_yieldAmt);
            viewHolder.tvProspectiveYieldAmt = (TextView) convertView.findViewById(R.id.tv_prospectiveYieldAmt);
            viewHolder.regularNum = (TextView) convertView.findViewById(R.id.regular_num);
            viewHolder.prospectiveYieldAmt = (TextView) convertView.findViewById(R.id.prospectiveYieldAmt);
            viewHolder.yieldAmt = (TextView) convertView.findViewById(R.id.yieldAmt);
            viewHolder.prospectiveYieldAmtLay = (LinearLayout) convertView.findViewById(R.id.prospectiveYieldAmt_lay);
            viewHolder.tv_assets_status = (TextView) convertView.findViewById(R.id.tv_assets_status);
            viewHolder.line = (ImageView) convertView.findViewById(R.id.line);
            viewHolder.rl_info = (RelativeLayout) convertView.findViewById(R.id.rl_info);
            viewHolder.rl_transfer = (RelativeLayout) convertView.findViewById(R.id.rl_transfer);
            viewHolder.tv_oldcapital = (TextView) convertView.findViewById(R.id.tv_oldcapital);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_endtime = (TextView) convertView.findViewById(R.id.tv_endtime);
            viewHolder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(regular.getContractNum()) && !regular.getContractNum().equals(null)) {
            viewHolder.regularNum.setText(regular.getContractNum());
        }
        viewHolder.yieldAmt.setTextColor(context.getResources().getColor(R.color.blue_pb));
        viewHolder.yieldAmt.setText(regular.getInvestEarnings());
        viewHolder.prospectiveYieldAmt.setTextColor(context.getResources().getColor(R.color.blue_pb));
        if (lastProductCommonModel.getStatus().equals("0")) {//定期
            viewHolder.prospectiveYieldAmt.setText(regular.getProspectiveYieldAmt());
        } else if (lastProductCommonModel.getStatus().equals("1")) {//日增息
            if ("1".equals(regular.getStatus())) {
                viewHolder.prospectiveYieldAmt.setText(regular.getTodayRate() + "%");
                viewHolder.tvProspectiveYieldAmt.setText(context.getResources().getString(R.string.today_earn_rate));
            } else {
                viewHolder.tvProspectiveYieldAmt.setText(context.getResources().getString(R.string.invest_money));
                viewHolder.prospectiveYieldAmt.setText(regular.getInvestMoney());
                viewHolder.yieldAmt.setTextColor(context.getResources().getColor(R.color.blue_pb));
            }
        } else if (lastProductCommonModel.getStatus().equals("2")) {//保险箱
            if ("1".equals(regular.getStatus())) {
                viewHolder.prospectiveYieldAmt.setText(regular.getTodayRate() + "%");
                viewHolder.tvProspectiveYieldAmt.setText(context.getResources().getString(R.string.today_earn_rate));
            } else {
                viewHolder.tvProspectiveYieldAmt.setText("原始本金(元)");
                viewHolder.prospectiveYieldAmt.setText(regular.getInvestMoney());
                viewHolder.yieldAmt.setTextColor(context.getResources().getColor(R.color.blue_pb));
            }
        }

        String status = regular.getStatus();
        viewHolder.tv_assets_status.setText(regular.getTips() + "");
        if (status.equals("0")) {
            viewHolder.tv_assets_status.setBackgroundResource(R.drawable.tv_status_gray);
            viewHolder.tv_assets_status.setTextColor(context.getResources().getColor(R.color.gray_text_dark_tv));
        } else if (status.equals("1")) {
            viewHolder.tv_assets_status.setBackgroundResource(R.drawable.tv_status_yellow);
            viewHolder.tv_assets_status.setTextColor(context.getResources().getColor(R.color.c_fc9010));
        }
        String transferStatus = regular.getTransferStatus();
        if (null != transferStatus && "4".equals(transferStatus)) {
            viewHolder.rl_info.setVisibility(View.GONE);
            viewHolder.rl_transfer.setVisibility(View.GONE);
        } else if (null != transferStatus && "2".equals(transferStatus)){
            viewHolder.rl_info.setVisibility(View.VISIBLE);
            viewHolder.tv_oldcapital.setText(regular.getInvestMoney());
            viewHolder.tv_time.setText(regular.getInvestDays() + "天");
            viewHolder.tv_endtime.setText(regular.getInvestEndDate());
            viewHolder.rl_transfer.setVisibility(View.VISIBLE);
            viewHolder.tv_info.setText(regular.getLeftDays());
        }else{
            viewHolder.rl_info.setVisibility(View.VISIBLE);
            viewHolder.tv_oldcapital.setText(regular.getInvestMoney());
            viewHolder.tv_time.setText(regular.getInvestDays() + "天");
            viewHolder.tv_endtime.setText(regular.getInvestEndDate());
            viewHolder.rl_transfer.setVisibility(View.GONE);
        }
        //需要修改
        if (lastProductCommonModel.getStatus().equals("1")) {
            if (status.equals("1")) {
                viewHolder.prospectiveYieldAmtLay.setVisibility(View.VISIBLE);
                viewHolder.line.setVisibility(View.VISIBLE);
            }
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lastProductCommonModel.getStatus().equals("0")) {
                    UmUtils.customsEvent(context, UmUtils.REGULARASSETS_TRADERECORD_CLICK, UmUtils.REGULARASSETS_TRADERECORD_NUM);
                } else if (lastProductCommonModel.getStatus().equals("1")) {
                    UmUtils.customsEvent(context, UmUtils.DAYPRODUCT_TRADERECORD_CLICK, UmUtils.DAYPRODUCT_TRADERECORD_NUM);
                }
                if (!TextUtils.isEmpty(regular.getUrl())) {
                    Intent intent = new Intent(context, MyDayUpActivity.class);
                    intent.putExtra("assetsid", regular.getAssertsId());
                    intent.putExtra("name", lastProductCommonModel.getName());
                    intent.putExtra("status", lastProductCommonModel.getStatus());
                    intent.putExtra("assetid", lastProductCommonModel.getAssetid());
                    intent.putExtra("code", lastProductCommonModel.getCode());
                    intent.putExtra("product_num", lastProductCommonModel.getProduct_num());
                    intent.putExtra("regular", regular);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView regularNum;
        TextView prospectiveYieldAmt;
        TextView yieldAmt;
        LinearLayout prospectiveYieldAmtLay;
        TextView tvYieldAmt;
        TextView tvProspectiveYieldAmt;
        ImageView line;
        TextView tv_assets_status;
        TextView tv_info;
        TextView tv_oldcapital;
        TextView tv_time;
        TextView tv_endtime;
        RelativeLayout rl_info;
        RelativeLayout rl_transfer;
    }
}
