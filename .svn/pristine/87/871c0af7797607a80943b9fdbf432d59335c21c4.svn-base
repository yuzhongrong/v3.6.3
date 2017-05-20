package com.jinr.core.trade.purchase.product;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by: Administrator on 2017/3/30.
 */
public class KeyBoardListener extends LinearLayout {
    private int mChangeSize = 200;
    private SoftkeyBoardListener boardListener;

    public KeyBoardListener(Context context) {
        super(context);
    }

    public KeyBoardListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardListener(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw == 0 || oldh == 0) {
            return;
        }
        if (boardListener != null) {
            if (h - oldh < -mChangeSize) {
                boardListener.keyBoardListen(true);
            }
            if (h - oldh > mChangeSize) {
                boardListener.keyBoardListen(false);
            }
        }
    }

    public interface SoftkeyBoardListener {
        void keyBoardListen(boolean isShow);
    }

    public void setSoftKeyBoardListener(SoftkeyBoardListener boardListener) {
        this.boardListener = boardListener;
    }
}
