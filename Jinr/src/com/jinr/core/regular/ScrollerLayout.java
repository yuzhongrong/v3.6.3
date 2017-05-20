package com.jinr.core.regular;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by： Ysw on 2016/6/13.15:37.
 */
@SuppressWarnings("ResourceType")
public class ScrollerLayout extends ViewGroup {

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;
    public static int SNAP_VELOCITY = 1000;
    private int mDataScreen = 0;
    private int mCurrentScreen = 0;
    private int mNextSreen = 0;
    private Scroller mScroller = null;
    private ScrollerLayoutPage mTopPage;
    private ScrollerLayoutPage mBottomPage;
    private int mTouchSlop = 0;
    private float mLastMotionY = 0;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker = null;
    private PageSnapedListener mPageSnapedListener;
    public static final int FLIP_DIRECTION_CUR = 0;
    public static final int FLIP_DIRECTION_UP = -1;
    public static final int FLIP_DIRECTION_DOWN = 1;
    private int mFlipDrection = FLIP_DIRECTION_CUR;

    public ScrollerLayout(Context context) {
        super(context);
        init();
    }

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface ScrollerLayoutPage {
        View getRootView();

        boolean isAtTop();

        boolean isAtBottom();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startLeft = 0;
        int childTop = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != View.GONE) {
                childAt.layout(startLeft, childTop, getWidth(), childTop + getMeasuredHeight());
            }
            childTop = childTop + getMeasuredHeight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                final int yDiff = (int) (y - mLastMotionY);
                boolean yMoved = Math.abs(yDiff) > mTouchSlop;
                if (yMoved) {
                    if (yDiff < 0 && mTopPage.isAtBottom() && mCurrentScreen == 0 || yDiff > 0 && mBottomPage.isAtTop() && mCurrentScreen == 1) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState != TOUCH_STATE_SCROLLING) {
                    int diffY = (int) Math.abs(y - mLastMotionY);
                    if (diffY > mTouchSlop) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    }
                }
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    int diffY = (int) (mLastMotionY - y);
                    mLastMotionY = y;
                    int scrollY = (int) getScaleY();
                    if (mCurrentScreen == 0) {
                        if (mTopPage != null && mTopPage.isAtBottom()) {
                            scrollBy(0, Math.max(-1 * scrollY, diffY));
                        }
                    } else if (mBottomPage != null && mBottomPage.isAtTop()) {
                        scrollBy(0, diffY);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(SNAP_VELOCITY, mMaximumVelocity);
                    int yVelocity = (int) velocityTracker.getYVelocity();
                    if (Math.abs(yVelocity) > SNAP_VELOCITY) {
                        if (yVelocity > 0 && mCurrentScreen == 1 && mBottomPage.isAtTop()) {
                            scrollToScreen(mDataScreen - 1);
                        } else if (yVelocity < 0 && mCurrentScreen == 0) {
                            scrollToScreen(mDataScreen + 1);
                        } else {
                            scrollToScreen(mDataScreen);
                        }
                    } else {
                        scrollToWhichScreen();
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrY() == (mScroller.getFinalY())) {
                if (mNextSreen > mDataScreen) {
                    mFlipDrection = FLIP_DIRECTION_DOWN;
                    makePageToNext(mNextSreen);
                } else if (mNextSreen < mDataScreen) {
                    mFlipDrection = FLIP_DIRECTION_UP;
                    makePageToPrev(mNextSreen);
                } else {
                    mFlipDrection = FLIP_DIRECTION_CUR;
                }
                if (mPageSnapedListener != null) {
                    mPageSnapedListener.onSnapedCompleted(mFlipDrection);
                }
            }
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void scrollToWhichScreen() {
        final int flipHeight = getHeight() / 8;
        int whichScreen = -1;
        final int topEdge = getCurrentView().getTop();
        if (topEdge < getScrollY() && (getScrollY() - topEdge) >= flipHeight && mCurrentScreen == 0) {
            whichScreen = mCurrentScreen + 1;
        } else if (topEdge > getScrollY() && (topEdge - getScrollY()) >= flipHeight && mCurrentScreen == 1) {
            whichScreen = mCurrentScreen - 1;
        } else {
            whichScreen = mCurrentScreen;
        }
        scrollToScreen(whichScreen);
    }

    private void scrollToScreen(int screen) {
        if (!mScroller.isFinished()) return;
        final int direction = screen - mCurrentScreen;
        mNextSreen = screen;
        int newY = 0;
        switch (direction) {
            case 1:
                newY = getCurrentView().getBottom();
                break;
            case -1:
                newY = getCurrentView().getTop() - getHeight();
                break;
            case 0:
                newY = getCurrentView().getTop();
                break;
            default:
                break;
        }
        final int cy = getScrollY();
        final int delta = newY - cy;
        mScroller.startScroll(0, cy, 0, delta, Math.abs(delta));
        invalidate();
    }

    private void makePageToNext(int dataIndex) {
        mDataScreen = dataIndex;
        mCurrentScreen = getCurrentScreen();
    }

    private void makePageToPrev(int dataIndex) {
        mDataScreen = dataIndex;
        mCurrentScreen = getCurrentScreen();
    }

    public int getCurrentScreen() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getId() == mDataScreen) {
                return i;
            }
        }
        return mCurrentScreen;
    }

    private View getCurrentView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getId() == mDataScreen) {
                return getChildAt(i);
            }
        }
        return null;
    }

    public void addChilds(ScrollerLayoutPage topPage, ScrollerLayoutPage bottomPage) {
        this.mTopPage = topPage;
        this.mBottomPage = bottomPage;
        View topView = mTopPage.getRootView();
        View bottomView = mBottomPage.getRootView();
        topView.setId(0);
        bottomView.setId(1);
        addView(topView);
        addView(bottomView);
        postInvalidate();
    }


    public interface PageSnapedListener {
        void onSnapedCompleted(int derection);
    }

    public void setPageSnapListener(PageSnapedListener listener) {
        mPageSnapedListener = listener;
    }
}
