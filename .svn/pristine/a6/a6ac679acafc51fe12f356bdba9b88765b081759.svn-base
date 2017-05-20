package com.jinr.core.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.jinr.core.utils.UtilsTools;


public class PercentRest extends View {
    private int mWidth;
    private int mHeight;
    private float mPercent = 0f;
    private int mMax = 1;
    private int mBackgroundColor = 0xff545654;
    private int mColor = Color.parseColor("#00FF00");
    private int mTextColor = Color.parseColor("#ffffff");
    private int mTextSize = UtilsTools.Dp2Px(getContext(), 12);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String str = "";

    public PercentRest(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    public PercentRest(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
    }

    public PercentRest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        mPaint.setColor(mBackgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        canvas.drawRoundRect(rectF, mHeight / 2, mHeight / 2, mPaint);
        mPaint.reset();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        float currentWidth = mPercent / mMax * mWidth;
        if (currentWidth < mHeight) {
            RectF rectFs = new RectF(0, 0, mHeight, mHeight);
            canvas.drawArc(rectFs, (float) (-90 - (Math.asin((mHeight - currentWidth) / mHeight) * (180 / Math.PI))), (float) (-180 + (2 * Math.asin((mHeight - currentWidth) / mHeight) * (180 / Math.PI))), false, mPaint);
            RectF rectFs1 = new RectF(-(mHeight - currentWidth) - 1, 0, mHeight - (mHeight - currentWidth) - 1, mHeight);
            canvas.drawArc(rectFs1, (float) (-90 + (Math.asin((mHeight - currentWidth) / mHeight) * (180 / Math.PI))), (float) (180 - (2 * Math.asin((mHeight - currentWidth) / mHeight) * (180 / Math.PI))), false, mPaint);
        } else {
            RectF rectFs = new RectF(0, 0, mPercent / mMax * mWidth, mHeight);
            canvas.drawRoundRect(rectFs, mHeight / 2, mHeight / 2, mPaint);
        }
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.str, 0, this.str.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();// 让现实的字体处于中心位置;;
        int y = (getHeight() / 2) - rect.centerY();// 让显示的字体处于中心位置;;
        canvas.drawText(this.str, x, y, this.mPaint);
    }

    public void setmTime(String time) {
        postInvalidate();
    }

    public void setmIncome(String income) {
        postInvalidate();
    }

    public void setmTextColor(int color) {
        this.mTextColor = color;
        postInvalidate();
    }

    public void setmTextsize(int size) {
        this.mTextSize = UtilsTools.Sp2px(getContext(), size);
        postInvalidate();
    }

    public void setmBackgroundColor(int color) {
        this.mBackgroundColor = color;
        postInvalidate();
    }

    public void setmColor(int color) {
        this.mColor = color;
        postInvalidate();
    }

    public void setmPercent(float percent) {
        this.mPercent = percent;
        postInvalidate();
    }

    public void setText(String str) {
        this.str = str;
        postInvalidate();
    }

    public void setMax(int max) {
        this.mMax = max;
        postInvalidate();
    }

    public void setPorgess() {

    }
}
