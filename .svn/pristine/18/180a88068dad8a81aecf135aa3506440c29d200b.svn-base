package com.jinr.core.regist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by: Ysw on 2017/3/10.
 */

public class RegisterViewGroup extends ViewGroup {

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public RegisterViewGroup(Context context) {
        super(context);
    }

    public RegisterViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RegisterViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            width = +childWidth;
            if (childHeight > height) {
                height = childHeight;
            }
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? width : width, (heightMode == MeasureSpec.EXACTLY) ? height : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int lineWidth = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int lc = width - childWidth - lineWidth;
            int tc = 0;
            int rc = width - lineWidth;
            int bc = childHeight;
            child.layout(lc, tc, rc, bc);
            lineWidth += childWidth;
        }
    }
}
