package com.jinr.graph_view;

import java.util.List;
import com.jinr.core.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.PopupWindow;

/**
 * 自定义键盘
 * 
 */
public class XKeyBoardView extends KeyboardView {
	XKeyBoardView keyBoardView;
	private PopupWindow mPopupKeyboard;
	Drawable delDrawable;
	Paint paint;
	Paint paint_line;
	int keyHeight;
	int keyWidth;
	int x1, y1, x2, y2, x, y, i, j;
	List<Key> keys;
	Drawable key_selector;
	Drawable del_selector;

	public XKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		keyBoardView = new XKeyBoardView(context, attrs, defStyle);
		mPopupKeyboard = new PopupWindow();
		keys = keyBoardView.getKeyboard().getKeys();
		del_selector = (Drawable) getContext().getResources().getDrawable(
				R.drawable.keyboard_del);
		key_selector = (Drawable) getContext().getResources().getDrawable(
				R.drawable.keyboardbackground);
		for (Key key : keys) {

			if (key.codes[0] == -3) {
				key.text = "";
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		// (iw.jh),(iw+w,jh+h)
		// (iw,jh,iw+w,jh+h)
		// (x/w,y/h)
		x = (int) me.getX();
		y = (int) me.getY();
		if (keyWidth!=0&&keyHeight!=0) {
			i = x / keyWidth;
			j = y / keyHeight;
			x1 = i * keyWidth;
			y1 = j * keyHeight;
			x2 = i * keyWidth + keyWidth;
			y2 = j * keyHeight + keyHeight;
			switch (me.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (i == 2 && j == 3) {
					del_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboard_del_selected);
					key_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboardbackground);
				} else if (i == 0 && j == 3) {
					del_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboard_del);
					key_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboardbackground);
				} else {
					key_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboard_gray);
					del_selector = (Drawable) getContext().getResources()
							.getDrawable(R.drawable.keyboard_del);
				}
				this.invalidate();
				break;
			case MotionEvent.ACTION_UP:
				del_selector = (Drawable) getContext().getResources().getDrawable(
						R.drawable.keyboard_del);
				key_selector = (Drawable) getContext().getResources().getDrawable(
						R.drawable.keyboardbackground);
				x1 = 0;
				y1 = 0;
				x2 = 0;
				y2 = 0;
				this.invalidate();
				break;
			default:
				break;
			}
		}
		
		return super.onTouchEvent(me);
	}

	public XKeyBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		del_selector = (Drawable) getContext().getResources().getDrawable(
				R.drawable.keyboard_del);
		key_selector = (Drawable) getContext().getResources().getDrawable(
				R.drawable.keyboardbackground);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getContext().getResources().getDisplayMetrics()));
//		paint.setTextSize(50);
		paint.setColor(Color.BLACK);
		paint_line = new Paint();
//		FontMetrics fontMetrics = paint.getFontMetrics(); //数字居中
		paint_line.setAntiAlias(true);
		paint_line.setStrokeWidth(0);
		paint_line.setColor(Color.parseColor("#d1d1d1"));
		List<Key> keys = getKeyboard().getKeys();
		keyWidth = keys.get(0).width;
		keyHeight = keys.get(0).height;
		for (Key key : keys) {
			switch (key.codes[0]) {
			case -5:
				delDrawable = del_selector;
				break;
			case -3:
				delDrawable = (Drawable) getContext().getResources()
						.getDrawable(R.drawable.keyboard_gray);
				break;
			default:
				delDrawable = (Drawable) getContext().getResources()
						.getDrawable(R.drawable.keyboardbackground);
				break;
			}
			if (!(x1 == 0 && x2 == 0) && !(i == 0 && j == 3)) {
				key_selector.setBounds(x1, y1, x2, y2);
				key_selector.draw(canvas);
			}
			delDrawable.setBounds(key.x, key.y, key.x + key.width, key.y
					+ key.height);
			delDrawable.draw(canvas);
		}
		for (Key key : keys) {
			canvas.drawLine(key.x, key.y, key.x, key.y + key.height, paint_line);// left
			canvas.drawLine(key.x, key.y + key.height, key.x + key.width, key.y
					+ key.height, paint_line);// top
			canvas.drawLine(key.x, key.y, key.x + key.width, key.y, paint_line);// bottom
			canvas.drawLine(key.x + key.width, key.y + key.height, key.x
					+ key.width, key.y, paint_line);// right
//			double height = Math.ceil(fontMetrics.bottom - fontMetrics.top);
//			float textBaseY = (float) (key.height - (key.height - height) / 2 - fontMetrics.bottom);
			canvas.drawText(key.label.toString(), key.x + (key.width / 2),
					key.y + key.height - (key.height / 3), paint);
//			canvas.drawText(key.label.toString(), key.x + (key.width / 2),
//					key.y+textBaseY, paint);
		}
	}

}
