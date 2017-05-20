package com.jinr.core.ui;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义DrawerLayout添加滑动监听，当抽屉打开时，右滑关闭抽屉
 *
 * @author 640 2015-05-26
 */
public class MenuRightView extends DrawerLayout {

    public MenuRightView(Context context) {
        super(context);
    }

    public MenuRightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuRightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float lastActionDownX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionDownX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
