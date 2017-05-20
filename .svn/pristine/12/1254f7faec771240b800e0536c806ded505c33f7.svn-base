package com.jinr.core.updata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jinr.core.utils.UtilsTools;

public class MyProgressBar extends View {
    private int mBackgroundColor = Color.parseColor("#CDCDCD");
    private int mColor = Color.parseColor("#1F7EE9");
    private int mWidth = UtilsTools.Dp2Px(getContext(), 350);
    private int mHeight = UtilsTools.Dp2Px(getContext(), 4);
    private float mPercent = 0f;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        DrawBackground(canvas);
        DrawLine(canvas);
    }

    private void DrawLine(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mPercent / 100f * mWidth, mHeight, mPaint);
    }

    private void DrawBackground(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(mBackgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    public void setBackground(int color) {
        mBackgroundColor = color;
        postInvalidate();
    }

    public void setColor(int color) {
        mColor = color;
        postInvalidate();
    }

    public void setPersent(float percent) {
        mPercent = percent;
        postInvalidate();
    }
}
