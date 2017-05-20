package com.jinr.core.regular;

import com.jinr.core.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by： Ysw on 2016/6/13.16:00.
 */
public class PageTop implements ScrollerLayout.ScrollerLayoutPage {
    private Context context;
    private View rootView = null;
    private final ScrollView mTopScrollView;

    public PageTop(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        mTopScrollView = (ScrollView) rootView.findViewById(R.id.mTopScrollView);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public boolean isAtTop() {
        return true;
    }

    @Override
    public boolean isAtBottom() {
        int scrollY = mTopScrollView.getScrollY();
        int height = mTopScrollView.getHeight();
        int scrollViewMeasuredHeight = mTopScrollView.getChildAt(0).getMeasuredHeight();
        if ((scrollY + height) >= scrollViewMeasuredHeight) {
            return true;
        }
        return false;
    }
}
