package com.jinr.core.regular;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.regist.MyRotate3dAnimation;

/**
 * 目前尚未采用这中方式来显示注册红包效果
 * Created by: Ysw on 2017/3/13.
 */
public class RegisterDialogFragment extends DialogFragment implements DialogInterface.OnKeyListener, View.OnClickListener {

    private ImageView image_open;
    private View mStartAnimView;
    private float mOpenCenterX = 0.0f;
    private float mOpenCenterY = 0.0f;
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private int mDuration = 500;
    private View mContainer;
    private FrameLayout fl_registerOne;
    private FrameLayout fl_registerTwo;
    private TextView tv_money;

    public RegisterDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_register, null);
        initUI(view);
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(this);
        dialog.setContentView(view);
        initData();
        return dialog;
    }

    private void initUI(View view) {
        mContainer = view.findViewById(R.id.container);
        fl_registerOne = (FrameLayout) view.findViewById(R.id.fl_registerOne);
        fl_registerTwo = (FrameLayout) view.findViewById(R.id.fl_registerTwo);
        image_open = (ImageView) view.findViewById(R.id.image_open);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        SpannableString rateText = new SpannableString("5,000.00" + "元");
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.register_redpackage_one), 0, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.register_redpackage_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_money.setText(rateText, TextView.BufferType.SPANNABLE);
        image_open.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_open:
                openRotation();
                break;
            default:
                break;
        }
    }

    public void openRotation() {
        mOpenCenterX = image_open.getWidth() / 2.0f;
        mOpenCenterY = image_open.getHeight() / 2.0f;
        MyRotate3dAnimation animation = new MyRotate3dAnimation(0.0f, -270.0f, mOpenCenterX, mOpenCenterY, 0, true, 1);
        animation.setDuration(mDuration);
        animation.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new DisplayNextView(1));
        image_open.startAnimation(animation);
    }

    private final class DisplayNextView implements Animation.AnimationListener {
        private int mNum;

        public DisplayNextView(int num) {
            mNum = num;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (mNum == 1) {
                mContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mCenterX = fl_registerOne.getWidth() / 2.0f;
                        mCenterY = fl_registerOne.getHeight() / 2.0f;
                        MyRotate3dAnimation animation = new MyRotate3dAnimation(0.0f, -90.0f, mCenterX, mCenterY, 100f, true, 2);
                        animation.setDuration(300);
                        animation.setFillAfter(true);
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.setAnimationListener(new DisplayNextView(2));
                        fl_registerOne.startAnimation(animation);
                    }
                });
            } else if (mNum == 2) {
                mContainer.post(new SwapViews());
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        @Override
        public void run() {
            fl_registerOne.setVisibility(View.GONE);
            fl_registerTwo.setVisibility(View.GONE);
            mStartAnimView = fl_registerTwo;
            mStartAnimView.setVisibility(View.VISIBLE);
            mStartAnimView.requestFocus();
            MyRotate3dAnimation rotation = new MyRotate3dAnimation(90, 0, mCenterX, mCenterY, 100f, false, 2);
            rotation.setDuration(300);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mStartAnimView.startAnimation(rotation);
        }
    }
}
