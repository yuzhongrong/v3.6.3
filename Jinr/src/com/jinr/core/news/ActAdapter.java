package com.jinr.core.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.PreferencesUtils.Keys;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashSet;

import model.Action;

/***
 *
 * */
public class ActAdapter extends BaseAdapter {
    public ArrayList<Action> list;
    private Context context;
    ViewHolder viewHolder;

    public ActAdapter(Context context, ArrayList<Action> list, HashSet<String> unreadSet) {
        this.list = list;
        this.context = context;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        list.get(position);
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.act_item, null);
            viewHolder.actImg = (ImageView) convertView.findViewById(R.id.act_image);
            viewHolder.tvFail = (TextView) convertView.findViewById(R.id.tv_fail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PreferencesUtils.getReadAction(context, Keys.READ_ACT);
        viewHolder.actImg.setTag(list.get(position).getPophoto());
        ImageLoader.getInstance().displayImage(list.get(position).getPophoto(), viewHolder.actImg, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                viewHolder.tvFail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                viewHolder.tvFail.setVisibility(View.VISIBLE);
                viewHolder.tvFail.setText("您的网络不顺畅，请稍后再试");
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                viewHolder.tvFail.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });


        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                Intent intent = new Intent(context, ActDetailActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("turn", "1");
                intent.putExtra("Action", list.get(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView actImg;
        TextView tvFail;
    }

    public interface ImageDownloadCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }

}