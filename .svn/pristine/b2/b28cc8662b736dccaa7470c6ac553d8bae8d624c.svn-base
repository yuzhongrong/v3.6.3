package com.jinr.core.regular;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

import com.jinr.core.R;

/**
 * Created by： Ysw on 2016/6/13.16:00.
 */
public class PageBottom implements ScrollerLayout.ScrollerLayoutPage {
    private Context context;
    private View rootView = null;
    private final ScrollView mButtonScrollView;

    public PageBottom(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        mButtonScrollView = (ScrollView) rootView.findViewById(R.id.mButtonScrollView);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public boolean isAtTop() {
        int scrollY = mButtonScrollView.getScrollY();
        if (scrollY == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAtBottom() {
        return true;
    }
}
