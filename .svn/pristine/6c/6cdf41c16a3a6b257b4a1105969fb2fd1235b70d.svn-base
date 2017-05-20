package com.jinr.core.bankCard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jinr.core.R;
import com.jinr.core.utils.ScreenUtils;
import com.jinr.core.utils.UtilsTools;

/**
 * Created by： Ysw on 2016/10/26.10:35.
 */
public class UserMessageKeyoardView extends View {
	private Bitmap mCancelBitmap = UtilsTools.getBitmap(getContext(),
			R.drawable.close);
	private int mCancelBitmapMaganLeft = UtilsTools.Dp2Px(getContext(), 20);
	private int mTitleSize = UtilsTools.Sp2px(getContext(), 20);
	private int mTitleColor = Color.parseColor("#000000");
	private int mLinesColor = Color.parseColor("#D5D5D5");
	private int mPasswordLineColor = Color.parseColor("#808080");
	private int mPasswordWidth = UtilsTools.Dp2Px(getContext(), 30);
	private int mPasswordLenth = 18;
	private int mPaddingTop = UtilsTools.Dp2Px(getContext(), 25);
	private int mMaginLeft = UtilsTools.Dp2Px(getContext(), 30);
	private int mNumberColor = Color.parseColor("#010101");
	private int mNumberSize = UtilsTools.Sp2px(getContext(), 28);
	private int mDeleteColor = Color.parseColor("#1F7EE8");
	private Bitmap mWhiteBitmap = UtilsTools.getBitmap(getContext(),
			R.drawable.delete_white);
	private Bitmap mDarkBitmap = UtilsTools.getBitmap(getContext(),
			R.drawable.delete_dark);
	private Bitmap mDeleteBitmap = mWhiteBitmap;
	private int mBackgroundColor = Color.parseColor("#F2F2F2");
	private int mNumberHeight = UtilsTools.Dp2Px(getContext(), 50);
	private int mNumberWidth;
	private int mNumberLines = 4;
	private int mSurfaceColor = Color.parseColor("#FFFFFF");
	private int mTopHeight = UtilsTools.Dp2Px(getContext(), 40);
	private int mPasswordHeight = UtilsTools.Dp2Px(getContext(), 0);
	private int mHeight = mTopHeight + mPasswordHeight + mNumberHeight * 4;
	private int mWidth = UtilsTools.Dp2Px(getContext(), 350);
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mNumbers[][] = new int[4][3];
	private String mPassword = "";
	private double downX;
	private double downY;
	private onPasswordOverListener mListener;

	public interface onPasswordOverListener {
		void onPasswordOverListener(String password);

		void onPasswordDismissListener();
	}

	public void setOnPasswordOverListener(onPasswordOverListener listener) {
		this.mListener = listener;
	}

	public UserMessageKeyoardView(Context context) {
		super(context);
	}

	public UserMessageKeyoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray Array = initAttrs(context, attrs);
		Array.recycle();
	}

	public UserMessageKeyoardView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray Array = initAttrs(context, attrs);
		Array.recycle();
	}

	private TypedArray initAttrs(Context context, AttributeSet attrs) {
		TypedArray Array = context.obtainStyledAttributes(attrs,
				R.styleable.UseridKeyboardView);
		if (Array == null)
			return null;
		int count = Array.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = Array.getIndex(i);
			switch (attr) {
			case R.styleable.UseridKeyboardView_cancerImage:
				mCancelBitmap = UtilsTools.getBitmap(Array.getDrawable(attr));
				break;
			case R.styleable.UseridKeyboardView_titlesize:
				mTitleSize = Array.getDimensionPixelSize(attr, mTitleSize);
				break;
			case R.styleable.UseridKeyboardView_titlecolor:
				mTitleColor = Array.getColor(attr, mTitleColor);
				break;
			case R.styleable.UseridKeyboardView_linescolor:
				mLinesColor = Array.getColor(attr, mLinesColor);
				break;
			case R.styleable.UseridKeyboardView_passwordlinecolor:
				mPasswordLineColor = Array.getColor(attr, mPasswordLineColor);
				break;
			case R.styleable.UseridKeyboardView_passwordwidth:
				mPasswordWidth = Array.getDimensionPixelOffset(attr,
						mPasswordWidth);
				break;
			case R.styleable.UseridKeyboardView_passwordlength:
				mPasswordLenth = Array.getInteger(attr, mPasswordLenth);
				break;
			case R.styleable.UseridKeyboardView_passwordpaddingtop:
				mPaddingTop = Array.getDimensionPixelOffset(attr, mPaddingTop);
				break;
			case R.styleable.UseridKeyboardView_passwordpaddingleft:
				mMaginLeft = Array.getDimensionPixelOffset(attr, mMaginLeft);
				break;
			case R.styleable.UseridKeyboardView_numbercolor:
				mNumberColor = Array.getColor(attr, mNumberColor);
				break;
			case R.styleable.UseridKeyboardView_numbersize:
				mNumberSize = Array.getDimensionPixelSize(attr, mNumberSize);
				break;
			case R.styleable.UseridKeyboardView_deletecolor:
				mDeleteColor = Array.getColor(attr, mDeleteColor);
				break;
			case R.styleable.UseridKeyboardView_deleteimage:
				mDeleteBitmap = UtilsTools.getBitmap(Array.getDrawable(attr));
				break;
			case R.styleable.UseridKeyboardView_mpasswordbackground:
				mBackgroundColor = Array.getColor(attr, mBackgroundColor);
				break;
			case R.styleable.UseridKeyboardView_numberlineheight:
				mNumberHeight = Array.getDimensionPixelOffset(attr,
						mNumberHeight);
				break;
			case R.styleable.UseridKeyboardView_surfacecolor:
				mSurfaceColor = Array.getColor(attr, mSurfaceColor);
				break;
			case R.styleable.UseridKeyboardView_numberlines:
				mNumberLines = Array.getInteger(attr, mNumberLines);
				break;
			default:
				break;
			}
		}
		return Array;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = widthSize;
			if (widthSize == 0) {
				mWidth = ScreenUtils.getScreenWidth(getContext());
			}
			mNumberWidth = mWidth / 3;
		} else {
			mWidth = ScreenUtils.getScreenWidth(getContext());
			mNumberWidth = mWidth / 3;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			mHeight = heightSize;
		} else {
			mHeight = mTopHeight + mPasswordHeight + mNumberHeight * 4;
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		DrawBackground(canvas);
		DrawTop(canvas);
		DrawNumber(canvas);
		ActionDown(canvas);
	}

	private void DrawBackground(Canvas canvas) {
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(mSurfaceColor);
		canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
	}

	private void DrawTop(Canvas canvas) {
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		canvas.drawBitmap(mCancelBitmap, mWidth - mCancelBitmapMaganLeft
				- mCancelBitmap.getWidth(),
				mTopHeight / 2 - mCancelBitmap.getHeight() / 2, mPaint);
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(mLinesColor);
		mPaint.setStrokeWidth(3);
		canvas.drawLine(0, mTopHeight, mWidth, mTopHeight, mPaint);
		canvas.drawLine(0, 0, mWidth, 0, mPaint);
	}

	private void DrawNumber(Canvas canvas) {
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setColor(mLinesColor);
		mPaint.setStrokeWidth(3);
		float startY = mTopHeight + mPasswordHeight;
		for (int i = 0; i < 4; i++) {
			canvas.drawLine(0, startY + mNumberHeight * i, mWidth, startY
					+ mNumberHeight * i, mPaint);
		}
		float startX = mWidth / 3;
		for (int i = 1; i < 3; i++) {
			canvas.drawLine(startX * i, startY, startX * i, mHeight, mPaint);
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 3 && j == 0) {
					mNumbers[i][j] = 100;
				} else if (i == 3 && j == 1) {
					mNumbers[i][j] = 0;
				} else if (i == 3 && j == 2) {
					mNumbers[i][j] = 100;
				} else {
					mNumbers[i][j] = 3 * i + j + 1;
				}
			}
		}
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setColor(mNumberColor);
		mPaint.setTextSize(mNumberSize);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 3 && j == 0) {
					mSelectPaint.reset();
					mSelectPaint.setColor(mBackgroundColor);
					mSelectPaint.setAntiAlias(true);
					mSelectPaint.setStyle(Paint.Style.FILL);
					canvas.drawRect(0, mHeight - mNumberHeight, startX,
							mHeight, mSelectPaint);
				} else if (i == 3 && j == 2) {
				} else {
					canvas.drawText("" + mNumbers[i][j], startX * j + startX
							/ 2 - mPaint.measureText("" + mNumbers[i][j]) / 2,
							mTopHeight + mPasswordHeight + mNumberHeight * i
									+ mNumberHeight / 2
									- (mPaint.ascent() + mPaint.descent()) / 2,
							mPaint);
				}
			}
		}
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setColor(mDeleteColor);
		canvas.drawRect(startX * 2, mHeight - mNumberHeight, mWidth, mHeight,
				mPaint);
		canvas.drawBitmap(mDeleteBitmap, startX * 3 - startX / 2
				- mDeleteBitmap.getWidth() / 2, mHeight - mNumberHeight / 2
				- mDeleteBitmap.getHeight() / 2, mPaint);
	}

	private void ActionDown(Canvas canvas) {
		if ((int) downY > (mTopHeight + mPasswordHeight)) {
			int j = (int) downX / mNumberWidth;
			int i = (int) (downY - mTopHeight - mPasswordHeight)
					/ mNumberHeight;
			if (i == 3 && j == 0)
				return;
			if (i == 3 && j == 2) {
				mPaint.reset();
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setAntiAlias(true);
				mPaint.setColor(mDeleteColor);
				canvas.drawRect(mNumberWidth * 2, mHeight - mNumberHeight,
						mWidth, mHeight, mPaint);
				mDeleteBitmap = mDarkBitmap;
				canvas.drawBitmap(mDeleteBitmap, mNumberWidth * 3
						- mNumberWidth / 2 - mDeleteBitmap.getWidth() / 2,
						mHeight - mNumberHeight / 2 - mDeleteBitmap.getHeight()
								/ 2, mPaint);
				if (mListener != null) {
					mListener.onPasswordOverListener("-1");
				}
			} else {
				mSelectPaint.reset();
				mSelectPaint.setColor(mBackgroundColor);
				mSelectPaint.setAntiAlias(true);
				mSelectPaint.setStyle(Paint.Style.FILL);
				canvas.drawRect(mNumberWidth * j, mTopHeight + mPasswordHeight
						+ mNumberHeight * i, mNumberWidth + mNumberWidth * j,
						mTopHeight + mPasswordHeight + mNumberHeight
								+ mNumberHeight * i, mSelectPaint);
				mPaint.reset();
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setAntiAlias(true);
				mPaint.setColor(mNumberColor);
				mPaint.setTextSize(mNumberSize);
				canvas.drawText(
						"" + mNumbers[i][j],
						mNumberWidth * j + mNumberWidth / 2
								- mPaint.measureText("" + mNumbers[i][j]) / 2,
						mTopHeight + mPasswordHeight + mNumberHeight * i
								+ mNumberHeight / 2
								- (mPaint.ascent() + mPaint.descent()) / 2,
						mPaint);
				mPassword = "" + mNumbers[i][j];
				if (mListener != null) {
					mListener.onPasswordOverListener(mPassword);
				}
			}
		} else if (downX > (mWidth - mCancelBitmap.getWidth() - mCancelBitmapMaganLeft)
				&& downX < mWidth && downY > 0 && downY < mTopHeight) {
			mListener.onPasswordDismissListener();
		}
		downX = 0;
		downY = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			performClick();
			postInvalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			downX = 0;
			downY = 0;
			mDeleteBitmap = mWhiteBitmap;
			performClick();
			postInvalidate();
			break;
		default:
			downX = 0;
			downY = 0;
			mDeleteBitmap = mWhiteBitmap;
			performClick();
			postInvalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	/**
	 * 外部调用方法 @author Ysw created at 2016/4/28 11:55
	 */

	public void setTopHeight(int height) {
		int i = UtilsTools.Dp2Px(getContext(), height);
		this.mTopHeight = i;
		postInvalidate();
	}

	public void setCancelBitmap(int srcId) {
		Bitmap bitmap = UtilsTools.getBitmap(getContext(), srcId);
		this.mCancelBitmap = bitmap;
		postInvalidate();
	}

	public void setLinesColor(int color) {
		this.mLinesColor = color;
		postInvalidate();
	}

	public void setPasswordColor(int color) {
		this.mPasswordLineColor = color;
		postInvalidate();
	}

	public void setPassworColor(int color) {
		postInvalidate();
	}

	public void setNumberSize(int size) {
		int i = UtilsTools.Sp2px(getContext(), size);
		this.mNumberSize = i;
		postInvalidate();
	}

	public void setNumberColor(int color) {
		this.mNumberColor = color;
		postInvalidate();
	}

	public void setBackgroundColor(int color) {
		this.mBackgroundColor = color;
		postInvalidate();
	}

	public void setSurfaceColor(int coloe) {
		this.mSurfaceColor = coloe;
		postInvalidate();
	}

	public void setDeleteBitmap(int srcId) {
		Bitmap bitmap = UtilsTools.getBitmap(getContext(), srcId);
		this.mDeleteBitmap = bitmap;
		postInvalidate();
	}

	public void setDeleteColor(int color) {
		this.mDeleteColor = color;
		postInvalidate();
	}

	public void setmPasswordHeight(int height) {
		int i = UtilsTools.Dp2Px(getContext(), height);
		this.mPasswordHeight = i;
		postInvalidate();
	}

	public void setPasswordMargLeft(int margLeft) {
		int i = UtilsTools.Dp2Px(getContext(), margLeft);
		this.mMaginLeft = i;
		postInvalidate();
	}

	public void setPasswordPaddingTop(int paddingTop) {
		int i = UtilsTools.Dp2Px(getContext(), paddingTop);
		this.mPaddingTop = i;
		postInvalidate();
	}

	public void setCancelMargLeft(int margLeft) {
		int i = UtilsTools.Dp2Px(getContext(), margLeft);
		this.mCancelBitmapMaganLeft = i;
		postInvalidate();
	}

	public void setPasswordLenth(int lenth) {
		this.mPasswordLenth = lenth;
		postInvalidate();
	}

	public void setNumberHeight(int height) {
		int i = UtilsTools.Dp2Px(getContext(), height);
		this.mNumberHeight = i;
		postInvalidate();
	}

	public void setPassword(String str) {
		mPassword = str;
	}

	public String getPassword() {
		return mPassword;
	}
}
