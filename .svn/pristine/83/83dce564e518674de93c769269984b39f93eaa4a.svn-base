package com.jinr.core.regular;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by： Ysw on 2016/5/20.23:44.
 */
public class MyScrollView extends ScrollView {
    private OnScrollListener onScrollListener;
    private int lastScrollY;
    private int scrollY;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            scrollY = MyScrollView.this.getScrollY();
            // 此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 50);
            }
            if (onScrollListener != null) {
                onScrollListener.onScroll(scrollY);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (onScrollListener != null) {
                    lastScrollY = this.getScrollY();
                    handler.sendMessageDelayed(handler.obtainMessage(), 5);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnScrollListener {
        void onScroll(int scrollY);
    }
}
