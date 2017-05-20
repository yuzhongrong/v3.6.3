package com.jinr.core.regular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.List;

import model.RegularAccount;

/**
 * 定期活期总资产适配器
 *
 * @author 1048
 */
public class TotalAssetsAdapter extends BaseAdapter {

    public List<RegularAccount> list;
    private Context mContext;

    public TotalAssetsAdapter(Context context, List<RegularAccount> gularList) {
        this.mContext = context;
        this.list = gularList;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_assets_item, null);
            viewHolder = new ViewHolder();
            viewHolder.current_assets = (TextView) convertView.findViewById(R.id.current_assets);
            viewHolder.current_assets_tv = (TextView) convertView.findViewById(R.id.current_assets_tv);
            viewHolder.current_assets_lay = (RelativeLayout) convertView.findViewById(R.id.current_assets_lay);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RegularAccount regular = list.get(position);
        viewHolder.current_assets_tv.setText(regular.getName());
        if (regular.getMoney().equals("") || regular.getMoney() == null) {
            viewHolder.current_assets.setText("0.00");
        } else {
            viewHolder.current_assets.setText(regular.getMoney());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView current_assets;
        TextView current_assets_tv;
        RelativeLayout current_assets_lay;
    }
}
