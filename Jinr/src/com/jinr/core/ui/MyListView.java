package com.jinr.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 全展开的的listView
 *
 * @author 604
 */
public class MyListView extends ListView {

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 该自定义控件只是重写了GridView的onMeasure方法，使其不会出现滚动条
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}