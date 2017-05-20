package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.graph_view.AbstractWheelTextAdapter;
import com.jinr.graph_view.OnWheelChangedListener;
import com.jinr.graph_view.OnWheelScrollListener;
import com.jinr.graph_view.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 可用银行列表对话框
 */
public class ChangeAddressDialog extends Dialog implements android.view.View.OnClickListener {

    private WheelView wvBank;
    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private Button btnSure;
    private Button btnCancel;
    private Context context;
    private Activity currentActivity;
    private List<String> arrBank = new ArrayList<>();
    private AddressTextAdapter bankAdapter;
    private ChangeAddressDialog mDialog;
    private String strBank;
    private OnAddressCListener onAddressCListener;
    private int maxsize = 21;
    private int minsize = 20;
    private TextView childTextView;
    private String maxcolor = "#333333";
    private String mincolor = "#a7a7a7";

    public ChangeAddressDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    public ChangeAddressDialog(Context context, List<String> list) {
        super(context, R.style.ShareDialog);
        this.context = context;
        arrBank = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bank_choose);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = dialogWindow.getWindowManager();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = (int) (m.getDefaultDisplay().getHeight() * 0.37); // 高度
        dialogWindow.setAttributes(lp);
        wvBank = (WheelView) findViewById(R.id.rgroup_bank);
        lyChangeAddress = findViewById(R.id.banklist_dialog);
        lyChangeAddressChild = findViewById(R.id.ly_myinfo_changeaddress_child);
        btnSure = (Button) findViewById(R.id.btn_complete);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        lyChangeAddress.setOnClickListener(this);
        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        wvBank.setLongClickable(true);
        wvBank.setVisibleItems(5);
        bankAdapter = new AddressTextAdapter(context, arrBank, getBankItem(strBank), maxsize, minsize);
        setTextviewSize(arrBank.get(0).toString(), bankAdapter);
        wvBank.setViewAdapter(bankAdapter);
        // wvBank.setCyclic(true);//循环滚动
        wvBank.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) bankAdapter.getItemText(wheel.getCurrentItem());
                strBank = currentText;
                setTextviewSize(currentText, bankAdapter);
            }
        });
        wvBank.setFocusable(true);
        wvBank.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) bankAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, bankAdapter);
            }
        });

    }

    private class AddressTextAdapter extends AbstractWheelTextAdapter {
        List<String> list;

        protected AddressTextAdapter(Context context, List<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.child_banklist, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            View view = getLayoutInflater().inflate(R.layout.child_banklist, null);
            childTextView = (TextView) view.findViewById(R.id.tempValue);
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View convertView, ViewGroup parent) {
            if (index >= 0 && index < getItemsCount()) {
                ArrayList<View> arrayList = getTestViews();
                int currentIndex = wvBank.getCurrentItem();
                if (convertView == null) {
                    convertView = getView(itemResourceId, parent);
                }
                TextView textView = getTextView(convertView, itemTextResourceId);
                if (!arrayList.contains(textView)) {
                    arrayList.add(textView);
                }
                if (textView != null) {
                    CharSequence text = getItemText(index);
                    if (text == null) {
                        text = "";
                    }
                    textView.setText(text);
                    if (index == currentIndex) {
                        textView.setTextSize(maxsize);
                        textView.setTextColor(Color.parseColor(maxcolor));
                    } else {
                        textView.setTextSize(minsize);
                        textView.setTextColor(Color.parseColor(mincolor));
                    }
                    if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                        configureTextView(textView);
                    }
                }
                childTextView = (TextView) convertView.findViewById(R.id.tempValue);
                return convertView;
            }
            return null;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }

        @Override
        public void setTextColor(int textColor) {
            super.setTextColor(textColor);

        }

        @Override
        public int getTextColor() {
            return super.getTextColor();
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textview = (TextView) arrayList.get(i);
            currentText = textview.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textview.setTextSize(maxsize);
                textview.setTextColor(Color.parseColor(maxcolor));
            } else {
                textview.setTextColor(Color.parseColor(mincolor));
                textview.setTextSize(minsize);
            }
        }
    }

    public void setAddresskListener(OnAddressCListener onAddressCListener) {
        this.onAddressCListener = onAddressCListener;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onAddressCListener != null) {
                onAddressCListener.onClick(strBank);
            }
            dismiss();
        } else if (v == btnCancel) {
            dismiss();
        } else if (v == lyChangeAddressChild) {
            return;
        }
    }

    /**
     * 回调接口
     */
    public interface OnAddressCListener {
        void onClick(String bank);
    }

    /**
     * 初始化地点
     */
    public void setbank(String bank) {
        if (bank != null && bank.length() > 0) {
            this.strBank = bank;
        }
    }

    public int getBankItem(String bank) {
        int size = arrBank.size();
        int bankIndex = 0;
        boolean nobanklist = true;
        for (int i = 0; i < size; i++) {
            if (bank.equals(arrBank.get(i))) {
                nobanklist = false;
                return bankIndex;
            } else {
                bankIndex++;
            }
        }
        if (nobanklist) {
            strBank = "";
            return 0;
        }
        return bankIndex;
    }
}