package com.jinr.graph_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jinr.core.utils.UtilsTools;

/**
 * Created by： Ysw on 2016/6/27.16:51.
 */
public class MyView extends View {

    private int mWidth;
    private int mHeight;
    private String mText = "0.000000";
    private String mFirstText;
    private String mSecondText;
    private int mTextColor = Color.parseColor("#FFFFFF");
    private int mFistTextSize = UtilsTools.Sp2px(getContext(), 72);
    private int mSecondTextSize = UtilsTools.Sp2px(getContext(), 27);
    private int mMiddleMagin = UtilsTools.Dp2Px(getContext(), 10);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mFirstText = mText.substring(0, 4);
        this.mSecondText = mText.substring(4, mText.length());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFistTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mFirstText, mWidth / 2 - mPaint.measureText(mFirstText) * 3 / 4 + mMiddleMagin, mHeight / 2 - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        int y = (int) (mPaint.ascent() + mPaint.descent()) / 2;
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mSecondTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mSecondText, mWidth / 2 + 6 * mMiddleMagin, mHeight / 2 - y, mPaint);
    }

    public void setText(String text) {
        this.mText = text;
        float x = Float.parseFloat(mText);
        if (x > 10) {
            this.mFirstText = mText.substring(0, 5);
            this.mSecondText = mText.substring(5, mText.length());
        } else {
            this.mFirstText = mText.substring(0, 4);
            this.mSecondText = mText.substring(4, mText.length());
        }
        postInvalidate();
    }

    public void setFirstTextSize(int size) {
        this.mFistTextSize = UtilsTools.Sp2px(getContext(), size);
        postInvalidate();
    }

    public void setSecondTextSize(int size) {
        this.mSecondTextSize = UtilsTools.Sp2px(getContext(), size);
        postInvalidate();
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        postInvalidate();
    }

    public void setMiddleMagin(int magin) {
        this.mMiddleMagin = UtilsTools.Dp2Px(getContext(), magin);
        postInvalidate();
    }
}
