package com.jinr.core.dayup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.EventBusKey;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import model.CommonProjectMode;

public class CommonComparedFragment extends Fragment {
    private View view;
    private ImageView image_compared;
    private TextView tv_compare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            //EventBus.getDefault().register(this);
            view = inflater.inflate(R.layout.fragment_regular_compared, container, false);
            initUI(view);
        }
        return view;
    }

    private void initUI(View view) {
        image_compared = (ImageView) view.findViewById(R.id.image_compared);
        tv_compare = (TextView) view.findViewById(R.id.tv_compare);
    }

    @Subscriber(tag = EventBusKey.PROJECT_DATAUP)
    public void LogoRed(CommonProjectMode data) {
        String type = data.getType();
        try {
            if (type != null && (type.equals("0")||type.equals("1"))) {
                // 定期对比图 @author Ysw created at 2017/1/5 10:40
                tv_compare.setVisibility(View.VISIBLE);
                tv_compare.setText(data.getImgtitle());
            }
//            else if(type != null && type.equals("1")){//定期
//
//            }else if (type != null && type.equals("2")) {
//                tv_compare.setVisibility(View.VISIBLE);
//                tv_compare.setText("");
//            } else if (type != null && type.equals("3")) {
//                // 保险箱对比图 @author Ysw created at 2017/1/5 10:40
//                tv_compare.setVisibility(View.VISIBLE);
//                tv_compare.setText("");
//            }
            ImageLoader.getInstance().displayImage(data.getLogo(), image_compared);
        } catch (Exception e) {
        }
    }
}
