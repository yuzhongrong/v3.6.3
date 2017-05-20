package com.jinr.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.regist.MyRotate3dAnimation;
import com.jinr.core.utils.MyLog;

import java.util.Date;

/**
 * @author jzs created 2017/4/7
 *         使用鲸鱼金币的 ScrollView
 */
public class CustomJinrScrollView extends ScrollView {
    public static final String TAG = "CustomJinrScrollView";

    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private ViewGroup parentView = null;
    private LinearLayout headView = null;
    private LinearLayout footView = null;
    private TextView tipsTextview;// 下拉刷新
    private ImageView mCoinImageView;// 金币图标

    //    private TextView lastUpdatedTextView;// 最新更新
//    private ImageView arrowImageView;// 箭头
//    private ProgressBar progressBar;// 刷新进度条
    private ProgressBar moreProgressBar;
    private TextView loadMoreView;

    private RotateAnimation animation;// 旋转特效 刷新中箭头翻转 向下变向上
    private RotateAnimation reverseAnimation; // 箭头向上变向下
    private MyRotate3dAnimation coinRotateAnimation;//金币旋转

    private int headContentHeight;// 头部高度
    private boolean isRecored;
    private int startY;// 高度起始位置，用来记录与头部距离

    private int state;// 下拉刷新中、松开刷新中、正在刷新中、完成刷新

    private boolean isBack;
    private boolean isSeeHead;
    private boolean isScroll; // 判断是否有滑动

    private final static int RATIO = 3; // 实际的padding的距离与界面上偏移距离的比例


    private OnRefreshListener refreshListener;
    private OnLoadListener loadListener;

    public CustomJinrScrollView(Context context) {
        super(context);
        initView(context);
    }

    public CustomJinrScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomJinrScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.xlistview_jinr_header, null);
        footView = (LinearLayout) inflater.inflate(R.layout.listfooter_more, null);

        tipsTextview = (TextView) headView.findViewById(R.id.tv_refresh);
        mCoinImageView = (ImageView) headView.findViewById(R.id.image_refresh);
        moreProgressBar = (ProgressBar) footView.findViewById(R.id.pull_to_refresh_progress);
        loadMoreView = (TextView) footView.findViewById(R.id.load_more);

        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRecored = false;
        isSeeHead = false;
        isScroll = false;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCoinImageView.post(new Runnable() {
            @Override
            public void run() {
                float mCenterX = mCoinImageView.getWidth() / 2.0f;
                float mCenterY = mCoinImageView.getHeight() / 2.0f;
                MyLog.e(TAG, "run: mCenterX" + mCenterX);
                MyLog.e(TAG, "run: mCenterY" + mCenterY);
                coinRotateAnimation = new MyRotate3dAnimation(0.0f, 360.0f, mCenterX, mCenterY, 100f, true, 1);
                coinRotateAnimation.setDuration(500);
                coinRotateAnimation.setFillAfter(true);
                coinRotateAnimation.setInterpolator(new DecelerateInterpolator());
                coinRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        mCoinImageView.setBackgroundResource(R.drawable.coin_refresh);
                        AnimationDrawable drawable = (AnimationDrawable) mCoinImageView.getBackground();
                        drawable.start();
                        mCoinImageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCoinImageView.setBackgroundResource(0);
                                onRefresh();
                            }
                        }, 450);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (parentView == null) {
            parentView = (ViewGroup) this.getChildAt(0);
            headView.setPadding(0, -1 * headContentHeight, 0, 0);
            headView.invalidate();
            parentView.addView(headView, 0);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getScrollY() == 0 && !isRecored) {
                    startY = (int) ev.getY();
                    isRecored = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int tempY = (int) ev.getY();
                if (tempY - startY > 10) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (!isScroll)
                    isScroll = true;
                if (!isRecored && this.getScrollY() == 0) {
                    isRecored = true;
                    startY = tempY;
                }
                if (tempY - startY < 0) {
                    startY = tempY;
                }
                if (state != REFRESHING && state != LOADING && isRecored) {
                    if (state == RELEASE_To_REFRESH) {
                        if ((tempY - startY) > 10 && (tempY - startY) / RATIO < headContentHeight + 10) {
                            state = PULL_To_REFRESH;
                            changeHeaderViewByState(true);
                        } else if (tempY - startY <= 0) {
                            state = DONE;
                            changeHeaderViewByState(true);
                        }
                    }
                    if (state == PULL_To_REFRESH) {
                        if ((tempY - startY) / RATIO >= headContentHeight + 10) {
                            state = RELEASE_To_REFRESH;
                            isBack = true;
                            changeHeaderViewByState(true);
                        } else if (tempY - startY <= 0) {
                            state = DONE;
                            changeHeaderViewByState(true);
                        }
                    }
                    if (state == DONE) {
                        if (tempY - startY > 10) {
                            state = PULL_To_REFRESH;
                            changeHeaderViewByState(true);
                            isSeeHead = true;
                        }
                    }
                    if (state == RELEASE_To_REFRESH) {
                        headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
                        headView.invalidate();
                    } else if (state == PULL_To_REFRESH) {
                        headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);
                        headView.invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == PULL_To_REFRESH) {
                    state = DONE;
                    changeHeaderViewByState(true);
                }
                if (state == RELEASE_To_REFRESH) {
                    state = REFRESHING;
                    changeHeaderViewByState(true);
                    //在动画结束进行刷新
//                    onRefresh();
                }
                isRecored = false;
                isSeeHead = false;
                isBack = false;

                if (parentView != null) {
                    int viewHeight = parentView.getHeight();
                    int y = this.getScrollY();
                    if ((viewHeight - y - this.getHeight()) == 0 && isScroll && state != REFRESHING && state != LOADING) {
                        onLoad();
                    }
                }
                isScroll = false;
                break;
        }

        if (isSeeHead) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    private void changeHeaderViewByState(boolean flag) {
        switch (state) {
            case RELEASE_To_REFRESH:
                tipsTextview.setVisibility(View.VISIBLE);
                tipsTextview.setText("释放赚钱");
                break;
            case PULL_To_REFRESH:
                tipsTextview.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状态转变来的
                mCoinImageView.clearAnimation();
                mCoinImageView.setImageResource(R.drawable.one);
                if (isBack) {
                    isBack = false;
                }
                tipsTextview.setText("下拉赚钱");
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                tipsTextview.setText("正在赚钱");
                mCoinImageView.clearAnimation();
                mCoinImageView.startAnimation(coinRotateAnimation);
                break;
            case DONE:
                if (flag) {
                    if (tipsTextview.getText().toString().trim().equals("正在赚钱")) {
                        tipsTextview.setText("刷新成功");
                        mCoinImageView.clearAnimation();
                        mCoinImageView.setImageResource(R.drawable.four);
                        new Handler().postDelayed(headHideAnimation, 500);
                    } else {
                        headView.setPadding(0, -1 * headContentHeight, 0, 0);
                    }
                } else {
                    if (tipsTextview.getText().toString().trim().equals("正在赚钱")) {
                        tipsTextview.setText("刷新失败");
                        new Handler().postDelayed(headHideAnimation, 500);
                    } else {
                        headView.setPadding(0, -1 * headContentHeight, 0, 0);
                    }
                }
                // tipsTextview.setText("下拉刷新");
                isRecored = false;
                break;
        }
    }

    /**
     * 延迟加载
     */
    Runnable headHideAnimation = new Runnable() {
        public void run() {
            headView.setPadding(0, -1 * headContentHeight, 0, 0);
        }
    };

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    /**
     * 完成刷新
     */
    public void onRefreshComplete(boolean flag) {
        state = DONE;
        changeHeaderViewByState(flag);
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface OnLoadListener {
        public void onLoad();
    }

    private void onLoad() {
        if (loadListener != null) {
            state = LOADING;
            footView.setVisibility(View.VISIBLE);
            moreProgressBar.setVisibility(View.VISIBLE);
            loadMoreView.setText("正在加载更多...");
            loadListener.onLoad();
        }
    }

    /**
     * 完成加载
     */
    public void onLoadComplete() {
        state = DONE;
        moreProgressBar.setVisibility(View.GONE);
        loadMoreView.setText("查看更多");
        footView.setVisibility(View.INVISIBLE);
    }

}