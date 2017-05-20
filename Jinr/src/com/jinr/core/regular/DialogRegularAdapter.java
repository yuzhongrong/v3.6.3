package com.jinr.core.regular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.List;

import model.CanCarry;

public class DialogRegularAdapter extends BaseAdapter {
    public List<CanCarry> list;
    private Context mContext;

    public DialogRegularAdapter(Context context, List<CanCarry> canCarryList) {
        this.mContext = context;
        this.list = canCarryList;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_regualr_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.current_money_name);
            viewHolder.money = (TextView) convertView.findViewById(R.id.current_money);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_shortcut_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CanCarry regular = list.get(position);
        viewHolder.name.setText(regular.getDQcode());
        viewHolder.money.setText("金额：" + regular.getMoney());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView money;
        CheckBox checkBox;
    }
}


