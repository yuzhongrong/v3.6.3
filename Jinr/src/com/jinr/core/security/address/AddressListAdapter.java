package com.jinr.core.security.address;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;

import java.util.ArrayList;
import java.util.List;

import model.AddressList;

/**
 * Created by: Ysw on 2016/12/19.
 */

public class AddressListAdapter extends BaseAdapter {
    private Context mContext;
    private OnEditAddressListener mEditListener;
    private OnDeleteAddressListener mDeleteListener;
    private OnSetDefaultAddressListener mSetDefaultListener;
    public List<AddressList> mList;

    public interface OnEditAddressListener {
        void EditAddressListener(int position);
    }

    public interface OnDeleteAddressListener {
        void DeleteAddressListener(int position);
    }

    public interface OnSetDefaultAddressListener {
        void onSetDefaultAddr(int position);
    }

    public void setOnEditAddressListener(OnEditAddressListener listener) {
        this.mEditListener = listener;
    }

    public void setOnDeleteAddressListener(OnDeleteAddressListener listener) {
        this.mDeleteListener = listener;
    }

    public void onSetDefaultAddrListener(OnSetDefaultAddressListener listener) {
        this.mSetDefaultListener = listener;
    }


    public AddressListAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<>();
    }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.address_list_item, null);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
            holder.tv_edit = (TextView) view.findViewById(R.id.tv_edit);
            holder.tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            holder.tv_default = (TextView) view.findViewById(R.id.tv_default);
            holder.image_default = (ImageView) view.findViewById(R.id.image_default);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        AddressList addrModel = mList.get(position);
        holder.tv_name.setText(addrModel.getConsignee_name());
        holder.tv_phone.setText(addrModel.getConsignee_tel());
        holder.tv_address.setText(addrModel.getRegion_name() + addrModel.getDetailed_address());
        int isDefault = mList.get(position).getIs_default();
        if (isDefault == 1) {
            holder.tv_default.setText("设为默认");
            holder.tv_default.setTextColor(Color.parseColor("#a8a8a8"));
            holder.image_default.setImageDrawable(mContext.getResources().getDrawable(R.drawable.choice));
        } else if (isDefault == 2) {
            holder.tv_default.setText("默认地址");
            holder.tv_default.setTextColor(Color.parseColor("#0c72e3"));
            holder.image_default.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected));
        }
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditListener != null) {
                    mEditListener.EditAddressListener(position);
                }
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteListener != null) {
                    mDeleteListener.DeleteAddressListener(position);
                }
            }
        });
        holder.tv_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetDefaultListener != null) {
                    mSetDefaultListener.onSetDefaultAddr(position);
                }
            }
        });
        holder.image_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetDefaultListener != null) {
                    mSetDefaultListener.onSetDefaultAddr(position);
                }
            }
        });

        return view;
    }

    public class ViewHolder {
        private TextView tv_name;
        private TextView tv_phone;
        private TextView tv_address;
        private TextView tv_edit;
        private TextView tv_delete;
        private TextView tv_default;
        private ImageView image_default;
    }

    public void setParent(List<AddressList> list) {
        this.mList = list;
    }
}
