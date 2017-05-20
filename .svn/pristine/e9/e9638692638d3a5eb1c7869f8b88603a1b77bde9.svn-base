package com.jinr.graph_view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.regist.MyRotate3dAnimation;
import com.jinr.core.utils.MyLog;

/**
 * @author jzs created 2017/4/6
 *         转让市场 XListView 头部
 */
public class XListViewJinrHeader extends LinearLayout {
    private LinearLayout mContainer;
    private ImageView mCoinImageView;//金币imageView
    private TextView mHintTextView;
    private int mState = STATE_NORMAL;
    private boolean mIsShow = false;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_REFRESH_COMPLETE = 3;
    private MyRotate3dAnimation coinRotateAnimation;
    private String mTotalMoney = "";

    public XListViewJinrHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XListViewJinrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_jinr_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
        mCoinImageView = (ImageView) findViewById(R.id.image_refresh);
        mHintTextView = (TextView) findViewById(R.id.tv_refresh);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mCoinImageView.post(new Runnable() {
            @Override
            public void run() {
                float mCenterX = mCoinImageView.getWidth() / 2.0f;
                float mCenterY = mCoinImageView.getHeight() / 2.0f;
                coinRotateAnimation = new MyRotate3dAnimation(0.0f, 360.0f, mCenterX, mCenterY, 100f, true, 1);
                coinRotateAnimation.setDuration(500);
                coinRotateAnimation.setFillAfter(true);
                coinRotateAnimation.setInterpolator(new DecelerateInterpolator());
            }
        });

    }

    public void setState(int state) {
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
                mCoinImageView.setImageResource(R.drawable.one);
                mCoinImageView.setBackgroundResource(0);
                mHintTextView.setText("下拉赚钱");
                break;
            case STATE_READY:
                if (!TextUtils.isEmpty(mTotalMoney)) {
                    mHintTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                    mHintTextView.setText(Html.fromHtml(mTotalMoney));
                } else {
                    mHintTextView.setText("释放赚钱");
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText("正在赚钱");
                break;
            case STATE_REFRESH_COMPLETE:
                mCoinImageView.clearAnimation();
                mCoinImageView.setImageResource(R.drawable.four);
                mHintTextView.setText("刷新完成");
                break;
            default:
        }
        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    public void setShowMoney(boolean isShow, String totalMoney) {
        mIsShow = isShow;
        mTotalMoney = totalMoney;
    }

    public void startCoinAnima(final CoinAnimaLister l) {
        mCoinImageView.clearAnimation();
        mCoinImageView.startAnimation(coinRotateAnimation);
        coinRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                l.onAnimationStart();
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
                        l.onAnimationEnd();
                    }
                }, 450);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public interface CoinAnimaLister {

        void onAnimationStart();

        void onAnimationEnd();
    }

}
