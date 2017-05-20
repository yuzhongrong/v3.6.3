package com.jinr.core.my_change;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.utils.DensityUtil;

import java.util.ArrayList;

import model.Profit;

/**
 * 累计收益adapter
 *
 * @author Administrator
 */
public class ProfitAmountAdapter extends BaseAdapter {

    private ArrayList<Profit> mList;
    private Context mContext;
    int width = 10;
    int height = 10;

    public ProfitAmountAdapter(Context context, ArrayList<Profit> list) {
        this.mContext = context;
        this.mList = list;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return this.mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_profit_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_y_date = (TextView) convertView.findViewById(R.id.tv_y_date);
            viewHolder.tv_y_amount = (TextView) convertView.findViewById(R.id.tv_y_amount);
            viewHolder.pb_jdt = (TextView) convertView.findViewById(R.id.pb_jdt);
            convertView.setTag(viewHolder);// setTag的作用才是把查找的view通过ViewHolder封装好缓存起来方便多次重用，当需要时可以getTag拿出来
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Profit profit = mList.get(position);
        viewHolder.tv_y_date.setText(profit.getY_date() + "");
        String str = profit.getY_shouyu();
        str = str.replace(",", "");
        viewHolder.tv_y_amount.setText(str);
        String jdt = profit.getJdt().replace(",", "");
        float f = Float.parseFloat(jdt);
        if (f > 100) {
            f = 100;
        }
        int dp2px = DensityUtil.dip2px(mContext, 26);
        LayoutParams layoutParams = new LayoutParams((int) ((width - dp2px) * f / 100), LayoutParams.MATCH_PARENT);
        viewHolder.pb_jdt.setLayoutParams(layoutParams);
        if (mList.get(position).getTag() == 0) {
            viewHolder.pb_jdt.setBackgroundResource(R.color.blue_btn_bg);
        } else {
            viewHolder.pb_jdt.setBackgroundResource(R.color.grey_pb);
        }
        return convertView;
    }

    protected static class ViewHolder {
        TextView tv_y_date, tv_y_amount;// 时间，每日收益金额
        TextView pb_jdt;
    }
}
