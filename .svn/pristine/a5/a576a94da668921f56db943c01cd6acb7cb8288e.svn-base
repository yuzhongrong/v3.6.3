package com.jinr.core.regular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.List;

import model.RegularAccount;

public class DialogCurrentAdapter extends BaseAdapter {

    public List<RegularAccount> list;
    private Context mContext;

    public DialogCurrentAdapter(Context context, List<RegularAccount> gularList) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_current_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.current_money_name);
            viewHolder.money = (TextView) convertView.findViewById(R.id.current_money);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RegularAccount regular = list.get(position);
        if (regular.getStatus() == null) {
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.dialog_text_color));
            viewHolder.money.setTextColor(mContext.getResources().getColor(R.color.dialog_text_color));
        } else {
            if (Integer.valueOf(regular.getStatus()) == 1) {
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.my_bank_card));
                viewHolder.money.setTextColor(mContext.getResources().getColor(R.color.my_bank_card));
            } else {
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.dialog_text_color));
                viewHolder.money.setTextColor(mContext.getResources().getColor(R.color.dialog_text_color));
            }
        }
        if (regular.getMoney() == null) {
            viewHolder.money.setText(regular.getRate());
        } else {
            viewHolder.money.setText(regular.getMoney());
        }
        viewHolder.name.setText(regular.getName());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView money;
    }
}
