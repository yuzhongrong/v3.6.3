package com.jinr.core.security.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.List;

import model.DBAddressMode;

/**
 * Created by: Ysw on 2016/12/13.
 */

public class CityListViewAdapter extends BaseAdapter {
    private Context mContext;
    public List<DBAddressMode> mList;

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.address_listview_item, null);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_name.setText(mList.get(position).getAddres_name());
        return view;
    }

    public class ViewHolder {
        private TextView tv_name;
        private TextView tv_phone;
        private TextView tv_address;
        private TextView tv_edit;
        private TextView tv_delete;
        private TextView tv_default;
    }

    public void setParent(Context context, List<DBAddressMode> list) {
        this.mContext = context;
        this.mList = list;
    }
}
