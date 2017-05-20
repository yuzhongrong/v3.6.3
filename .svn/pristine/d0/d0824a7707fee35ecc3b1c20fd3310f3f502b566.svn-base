package com.jinr.core.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jinr.core.R;

public class KeyboardUtil {
    private Context ctx;
    private Activity act;
    private KeyboardView keyboardView;
    private Keyboard k1;// 数字键盘
    ImageView close_keyboard;
    private EditText ed;
    private View view;
    private PopupWindow window;
    Editable editable;
    RelativeLayout loading_page;
    ProgressBar progress;
    LinearLayout keyboard_parts;

    public KeyboardUtil(View view, PopupWindow window, Context ctx, EditText edit) {

        this.ctx = ctx;
        this.ed = edit;
        this.view = view;
        this.window = window;
        k1 = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);
        keyboard_parts = (LinearLayout) view.findViewById(R.id.keyboard_parts);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (ed != null) {
                editable = ed.getText();
                int start = ed.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// // 左下角限制
                    // editable.append("1");
                    // editable.delete(start - 1, start);

                    // if (editable.length() < 6) {
                    // System.out.println("-----请输入完整6位密码！-----");
                    // } else {
                    // // System.out.println(editable);
                    // window.dismiss();
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                } else {
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }
        }
    };

    public String backcode() {
        if (editable.toString() == null || editable.toString() == "") {
            return "";
        }
        return editable.toString();
    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    public boolean getvisibility() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void sendloading() {

    }
}
