package com.jinr.core.trade.purchase.product;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyParentVerticalScrollLayout extends ViewGroup {
    public static final String TAG = "MyVerticalScrollLayout";
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mCurScreen;
    private int mDefaultScreen = 0;
    private static final int SNAP_VELOCITY = 600;
    private int mTouchSlop;
    private int mLastMotionY;
    private int mLastMotionX;
    private boolean isOver = true;
    private MyChildVerticalScrollLayout mChild;
    private ParentVerticalScrollListener mListeners;

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public MyParentVerticalScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyParentVerticalScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mCurScreen = mDefaultScreen;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    public void setOnVerticalScrollListener(ParentVerticalScrollListener listener) {
        mListeners = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int HeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
        scrollTo(0, mCurScreen * height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childTop = 0;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    final int childHeight = childView.getMeasuredHeight();
                    childView.layout(0, childTop, childView.getMeasuredWidth(), childTop + childHeight);
                    childTop += childHeight;
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int childScrollY = mChild.getScrollY();
        if (childScrollY < 0) {
            return intercepted;
        }
        int y = (int) ev.getY();
        int x = (int) ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                mLastMotionX = x;
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                int deltaX = x - mLastMotionX;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercepted = false;
                } else if (deltaY < 0 && childScrollY == 0) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                if (mCurScreen == 0) {
                    mListeners.onVerticalScroll(intercepted, mCurScreen);
                } else {
                    mListeners.onVerticalScroll(true, mCurScreen);
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;
                if (deltaY < 0 && getScrollY() + deltaY <= 0) {
                } else if (mCurScreen < getChildCount() - 1 && getChildAt(mCurScreen + 1) != null
                        && getChildAt(mCurScreen + 1).getVisibility() != View.GONE) {
                    scrollBy(0, deltaY);
                } else if (mCurScreen == getChildCount() - 1 && deltaY < 0) {
                    scrollBy(0, deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
                    snapToScreen(mCurScreen - 1);
                } else if (velocityY < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1 && getChildAt(mCurScreen + 1) != null
                        && getChildAt(mCurScreen + 1).getVisibility() != View.GONE) {
                    snapToScreen(mCurScreen + 1);
                } else {
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        mLastMotionY = y;
        return true;
    }

    public void snapToDestination() {
        final int screenHeight = getHeight();
        final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollY() != (whichScreen * getHeight())) {
            final int delta = whichScreen * getHeight() - getScrollY();
            mScroller.startScroll(0, getScrollY(), 0, delta, Math.min(500, Math.abs(delta)));
            mCurScreen = whichScreen;
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if (mCurScreen == 0 && mScroller.getCurrY() == 0) mListeners.onVerticalScroll(false, mCurScreen);
            postInvalidate();
        }
    }

    public void setChild(MyChildVerticalScrollLayout child) {
        this.mChild = child;
    }
}
