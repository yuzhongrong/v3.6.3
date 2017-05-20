package com.jinr.graph_view;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.jinr.core.JinrApp;
import com.jinr.core.R;

/**
 * Created by: 966 on 2016/11/14.
 */
public class TimeButton extends Button implements View.OnClickListener{

    private Context mContext;
    private TimeCount timeCount;
    private OnClickListener mOnclickListener;
    private int intColor = R.color.phone_font_color;
    private long millislFinished;

    public TimeButton(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {

        long timer = JinrApp.timer;
        setOnClickListener(this);
        this.setClickable(false);
        timeCount = new TimeCount(timer, 1000, this);
        timeCount.start();
    }

    public void setBtnTextColor(int intColor){
        this.intColor = intColor;
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null){
            mOnclickListener.onClick(v);
        }
        timeCount = new TimeCount(60000, 1000, this);
        this.setClickable(false);
        timeCount.start();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof TimeButton) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    /** 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {

        Button btn;
        public TimeCount(long millisInFuture, long countDownInterval,Button btn) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            this.btn = btn;
        }

        @Override
        public void onFinish() {// 计时完毕时触发

            JinrApp.timer = 60000;

            btn.setTextColor(mContext.getResources().getColor(intColor));

            btn.setText(mContext.getResources().getString(R.string.agin_modify_code));

            btn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            btn.setClickable(false);
            btn.setTextColor(mContext.getResources().getColor(intColor));
            String newMessageInfo = "";
            newMessageInfo =  millisUntilFinished / 1000 + "s后重发";
            btn.setText(Html.fromHtml(newMessageInfo));
            millislFinished = millisUntilFinished;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(!hasWindowFocus){
            if(millislFinished/1000 <= 1){
                JinrApp.timer = 60000;
            }else{
                JinrApp.timer = millislFinished;
            }
        }
    }
}
