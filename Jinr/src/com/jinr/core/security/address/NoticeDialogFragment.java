package com.jinr.core.security.address;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;

/**
 * Created by: Ysw on 2016/12/21.
 */

public class NoticeDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView tv_notice;
    private RelativeLayout rl_true;
    private RelativeLayout rl_cancel;
    private OnNoticeDialogChooseListener mListener;

    public NoticeDialogFragment() {
    }

    public interface OnNoticeDialogChooseListener {
        void onChooseClick(boolean isTrue);
    }

    public void setOnNoticeDialogChooseListener(OnNoticeDialogChooseListener listener) {
        this.mListener = listener;
    }

    public static NoticeDialogFragment getInstance(String notice) {
        NoticeDialogFragment instance = new NoticeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("notice", notice);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_notice_dialog, null);
        tv_notice = (TextView) view.findViewById(R.id.tv_notice);
        rl_true = (RelativeLayout) view.findViewById(R.id.rl_true);
        rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        rl_true.setOnClickListener(this);
        rl_cancel.setOnClickListener(this);
        Bundle bundle = getArguments();
        String notice = bundle.getString("notice");
        tv_notice.setText(notice);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_true:
                if (mListener != null) {
                    mListener.onChooseClick(true);
                    NoticeDialogFragment.this.dismiss();
                }
                break;
            case R.id.rl_cancel:
                if (mListener != null) {
                    mListener.onChooseClick(false);
                    NoticeDialogFragment.this.dismiss();
                }
                break;
            default:
                break;
        }
    }
}
