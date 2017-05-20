package com.jinr.core.regular;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jinr.core.utils.UtilsTools;


/**
 * Created by： Ysw on 2016/5/22.17:28.
 */
public class MyTextView extends View {
    private String mText = "0";
    private int mTextColor = Color.parseColor("#FF9C50");
    private int mBackgroundColor = Color.parseColor("#FFFFFF");
    private int mTextSize = UtilsTools.Sp2px(getContext(), 30);
    private int mMiddleMagin = UtilsTools.Dp2Px(getContext(), 14);
    private float mRectLineWidth = UtilsTools.Dp2Px(getContext(), 0.5f);
    private int mRadius = UtilsTools.Dp2Px(getContext(), 10);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mWidth = UtilsTools.Dp2Px(getContext(), 111);
    private int mHeight = UtilsTools.Dp2Px(getContext(), 42);
    private int mSecondWidth;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mSecondWidth = (mWidth - 2 * mMiddleMagin) / 3;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawBackGround(canvas);
        DrawRectf(canvas);
    }

    private void DrawBackGround(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(mBackgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    private void DrawRectf(Canvas canvas) {
        int textLength = mText.length();
        for (int i = 0; i < 3; i++) {
            mPaint.reset();
            mPaint.setColor(mTextColor);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            RectF rectFone = new RectF(i * (mSecondWidth + mMiddleMagin), 0, i * (mSecondWidth + mMiddleMagin) + mSecondWidth, mHeight);
            canvas.drawRoundRect(rectFone, mRadius, mRadius, mPaint);
            mPaint.reset();
            mPaint.setColor(mBackgroundColor);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            RectF rectFsecond = new RectF(i * (mSecondWidth + mMiddleMagin) + mRectLineWidth, 0 + mRectLineWidth, i
                    * (mSecondWidth + mMiddleMagin) + mSecondWidth - mRectLineWidth, mHeight - mRectLineWidth);
            canvas.drawRoundRect(rectFsecond, mRadius, mRadius, mPaint);
            mPaint.reset();
            mPaint.setColor(mTextColor);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setStyle(Paint.Style.FILL);
            String text = "";
            if (textLength == 3) {
                text = mText.substring(i, i + 1);
                canvas.drawText(text, i * (mSecondWidth + mMiddleMagin) + mSecondWidth / 2 - (int) mPaint.measureText(text) / 2, mHeight / 2
                        - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            } else if (textLength == 2) {
                if (i == 0) {
                    text = "0";
                    canvas.drawText(text, i * (mSecondWidth + mMiddleMagin) + mSecondWidth / 2 - (int) mPaint.measureText(text) / 2,
                            mHeight / 2 - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
                } else {
                    text = mText.substring(i - 1, i);
                    canvas.drawText(text, i * (mSecondWidth + mMiddleMagin) + mSecondWidth / 2 - (int) mPaint.measureText(text) / 2,
                            mHeight / 2 - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
                }
            } else if (textLength == 1) {
                if (i == 2) {
                    text = mText;
                    canvas.drawText(text, i * (mSecondWidth + mMiddleMagin) + mSecondWidth / 2 - (int) mPaint.measureText(text) / 2,
                            mHeight / 2 - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
                } else {
                    text = "0";
                    canvas.drawText(text, i * (mSecondWidth + mMiddleMagin) + mSecondWidth / 2 - (int) mPaint.measureText(text) / 2,
                            mHeight / 2 - (int) (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
                }
            }
        }
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        postInvalidate();
    }

    public void setTextSize(float size) {
        mTextSize = UtilsTools.Sp2px(getContext(), size);
        postInvalidate();
    }

    public void setMiddleMagin(int magin) {
        mMiddleMagin = UtilsTools.Dp2Px(getContext(), magin);
        postInvalidate();
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        postInvalidate();
    }

    public void setRadius(float radius) {
        mRadius = UtilsTools.Dp2Px(getContext(), radius);
        postInvalidate();
    }

    public void setRectLineWidth(float width) {
        mRectLineWidth = UtilsTools.Dp2Px(getContext(), width);
        postInvalidate();
    }
}
