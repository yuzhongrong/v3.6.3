package com.jinr.graph_view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {
    private float xDistance, yDistance, xLast, yLast;
    private InitCallback mCallback;
    private boolean isFirst = true;
    private boolean isCallback = true;

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                if (isCallback) {
                    if (yLast - curY > 50) {
                        if (isFirst) {
                            mCallback.onInitFinish(isFirst);
                            isFirst = false;
                        }
                    }
                }
                xLast = curX;
                yLast = curY;
                if (xDistance > yDistance) {
                    return true;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public void setCallback(InitCallback down) {
        mCallback = down;
    }

    public void setNoCallback(boolean isCallback) {
        this.isCallback = isCallback;
    }

    public interface InitCallback {
        public void onInitFinish(boolean isFirst);
    }
}
