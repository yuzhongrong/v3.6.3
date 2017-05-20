package com.jinr.core.regist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.jinr.core.utils.ScreenUtils;
import com.jinr.core.utils.UtilsTools;

/**
 * Created by: Ysw on 2017/3/3.
 */

public class BezierCurve extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mWidth;
    private int mHeight;
    private float mRadius;
    private Context mContext;
    private LinearGradient mLG;
    private int mScreenWidth;
    private float mState;
    private float mValue = 2f;
    private boolean mDelete;
    private Canvas mCanvas;

    public BezierCurve(Context context) {
        super(context);
        mContext = context;
    }

    public BezierCurve(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BezierCurve(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = UtilsTools.Dp2Px(mContext, 50);
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = mScreenWidth;
        }
        float one = 0.5f * mHeight - 4;
        float two = mWidth / 19f;
        mRadius = Math.min(one, two);
        // 创建渐变色 @author Ysw created at 2017/3/6 12:42
        mLG = new LinearGradient(0f, 0f, mWidth, 2 * mRadius, Color.parseColor("#7286F6"),
                Color.parseColor("#518DEF"), Shader.TileMode.REPEAT);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        drawBankground(true);
        drawProgrelss();
        if (mDelete) {
            deleteProgrelss();
        }
        drawBankground(false);
        drawText();
    }

    private void drawProgrelss() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2 * mRadius);
        mPaint.setDither(true);
        mPaint.setShader(mLG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        if (mDelete) {
            mCanvas.drawLine(0, mHeight / 2, 21 * mRadius - mRadius, mHeight / 2, mPaint);
        } else if (mState == 0f) {
            mCanvas.drawLine(0, mHeight / 2, mValue * mRadius - mRadius, mHeight / 2, mPaint);
        } else if (mState == 1f) {
            mCanvas.drawLine(0, mHeight / 2, 2 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(2 * mRadius - mRadius, mHeight / 2, mValue * mRadius - mRadius, mHeight / 2, mPaint);
        } else if (mState == 2f) {
            mCanvas.drawLine(0, mHeight / 2, 2 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(2 * mRadius - mRadius, mHeight / 2, 7 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(7 * mRadius - mRadius, mHeight / 2, mValue * mRadius - mRadius, mHeight / 2, mPaint);
        } else if (mState == 3f) {
            mCanvas.drawLine(0, mHeight / 2, 2 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(2 * mRadius - mRadius, mHeight / 2, 7 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(7 * mRadius - mRadius, mHeight / 2, 12 * mRadius - mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(12 * mRadius - mRadius, mHeight / 2, mValue * mRadius - mRadius, mHeight / 2, mPaint);
        }
    }

    private void deleteProgrelss() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2 * mRadius);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#E9F6FF"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        if (mState == 2f) {
            mCanvas.drawLine(21 * mRadius + mRadius, mHeight / 2, mValue * mRadius + mRadius, mHeight / 2, mPaint);
        } else if (mState == 1f) {
            mCanvas.drawLine(21 * mRadius + mRadius, mHeight / 2, 12 * mRadius + mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(12 * mRadius + mRadius, mHeight / 2, mValue * mRadius + mRadius, mHeight / 2, mPaint);
        } else if (mState == 0f) {
            mCanvas.drawLine(21 * mRadius + mRadius, mHeight / 2, 12 * mRadius + mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(12 * mRadius + mRadius, mHeight / 2, 7 * mRadius + mRadius, mHeight / 2, mPaint);
            mCanvas.drawLine(7 * mRadius + mRadius, mHeight / 2, mValue * mRadius + mRadius, mHeight / 2, mPaint);
        }
    }

    public void setProgrelss(float value, float state, boolean delete) {
        mValue = value;
        mState = state;
        mDelete = delete;
        invalidate();
    }

    /**
     * 绘制底层默认背景色和顶层的反向路径白色层 @author Ysw created at 2017/3/7 8:32
     */
    private void drawBankground(boolean isBottom) {
        Path one = new Path();
        one.addCircle(mRadius, mHeight / 2, mRadius, Path.Direction.CW);
        Path two = new Path();
        two.addRect(2 * mRadius - 10, mHeight / 2 - 10, 5 * mRadius + 10, mHeight / 2 + 10, Path.Direction.CW);
        Path three = new Path();
        three.addCircle(6 * mRadius, mHeight / 2, mRadius, Path.Direction.CW);
        Path four = new Path();
        four.addRect(7 * mRadius - 10, mHeight / 2 - 10, 10 * mRadius + 10, mHeight / 2 + 10, Path.Direction.CW);
        Path five = new Path();
        five.addCircle(11 * mRadius, mHeight / 2, mRadius, Path.Direction.CW);
        Path six = new Path();
        six.addRect(12 * mRadius - 10, mHeight / 2 - 10, 15 * mRadius + 10, mHeight / 2 + 10, Path.Direction.CW);
        Path seven = new Path();
        seven.addCircle(16 * mRadius, mHeight / 2, mRadius, Path.Direction.CW);
        Path eight = new Path();
        eight.addRect(16 * mRadius, mHeight / 2 - mRadius, mWidth, mHeight / 2 + mRadius, Path.Direction.CW);
        Path nine = new Path();
        nine.addRect(0, 0, mWidth, mHeight, Path.Direction.CW);
        if (isBottom) {
            mPaint.reset();
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#e9f6ff"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                one.op(two, Path.Op.UNION);
                one.op(three, Path.Op.UNION);
                one.op(four, Path.Op.UNION);
                one.op(five, Path.Op.UNION);
                one.op(six, Path.Op.UNION);
                one.op(seven, Path.Op.UNION);
                one.op(eight, Path.Op.UNION);
                mCanvas.drawPath(one, mPaint);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mCanvas.save();
                mCanvas.clipPath(one);
                mCanvas.clipPath(two, Region.Op.UNION);
                mCanvas.clipPath(three, Region.Op.UNION);
                mCanvas.clipPath(four, Region.Op.UNION);
                mCanvas.clipPath(five, Region.Op.UNION);
                mCanvas.clipPath(six, Region.Op.UNION);
                mCanvas.clipPath(seven, Region.Op.UNION);
                mCanvas.clipPath(eight, Region.Op.UNION);
                mCanvas.drawColor(Color.parseColor("#e9f6ff"));
                mCanvas.restore();
            }
        } else {
            mPaint.reset();
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#F7F7F7"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                one.op(two, Path.Op.UNION);
                one.op(three, Path.Op.UNION);
                one.op(four, Path.Op.UNION);
                one.op(five, Path.Op.UNION);
                one.op(six, Path.Op.UNION);
                one.op(seven, Path.Op.UNION);
                one.op(eight, Path.Op.UNION);
                nine.op(one, Path.Op.DIFFERENCE);
                mCanvas.drawPath(nine, mPaint);
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mCanvas.save();
                mCanvas.clipPath(nine);
                mCanvas.clipPath(one, Region.Op.DIFFERENCE);
                mCanvas.clipPath(two, Region.Op.DIFFERENCE);
                mCanvas.clipPath(three, Region.Op.DIFFERENCE);
                mCanvas.clipPath(four, Region.Op.DIFFERENCE);
                mCanvas.clipPath(five, Region.Op.DIFFERENCE);
                mCanvas.clipPath(six, Region.Op.DIFFERENCE);
                mCanvas.clipPath(seven, Region.Op.DIFFERENCE);
                mCanvas.clipPath(eight, Region.Op.DIFFERENCE);
                mCanvas.drawColor(Color.parseColor("#F7F7F7"));
                mCanvas.restore();
            }
        }
    }

    private void drawText() {
        mPaint.reset();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setTextSize(UtilsTools.Sp2px(mContext, 18));
        if (mState == 0) {
            mCanvas.drawText("1", mRadius - mPaint.measureText("1") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mPaint.setColor(Color.parseColor("#D8D8D8"));
            mCanvas.drawText("2", 6 * mRadius - mPaint.measureText("2") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("3", 11 * mRadius - mPaint.measureText("3") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("完成", 17 * mRadius - mPaint.measureText("完成") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        } else if (mState == 1) {
            mCanvas.drawText("1", mRadius - mPaint.measureText("1") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("2", 6 * mRadius - mPaint.measureText("2") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mPaint.setColor(Color.parseColor("#D8D8D8"));
            mCanvas.drawText("3", 11 * mRadius - mPaint.measureText("3") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("完成", 17 * mRadius - mPaint.measureText("完成") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        } else if (mState == 2) {
            mCanvas.drawText("1", mRadius - mPaint.measureText("1") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("2", 6 * mRadius - mPaint.measureText("2") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("3", 11 * mRadius - mPaint.measureText("3") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mPaint.setColor(Color.parseColor("#D8D8D8"));
            mCanvas.drawText("完成", 17 * mRadius - mPaint.measureText("完成") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        } else if (mState == 3) {
            mCanvas.drawText("1", mRadius - mPaint.measureText("1") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("2", 6 * mRadius - mPaint.measureText("2") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("3", 11 * mRadius - mPaint.measureText("3") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
            mCanvas.drawText("完成", 17 * mRadius - mPaint.measureText("完成") / 2, mHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        }
    }
}
