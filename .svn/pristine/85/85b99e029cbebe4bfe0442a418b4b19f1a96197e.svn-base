package com.jinr.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.jinr.core.utils.PreferencesUtils;

import java.util.ArrayList;

import model.SystemInfo;

public class MenuRightFragment extends Fragment implements OnClickListener {
    public static MenuRightFragment instance = null;
    public SystemInfo sys;
    public ArrayList<SystemInfo> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        View inflate = inflater.inflate(R.layout.menu_layout_right, container, false);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    // 保存版本edition
    private void SaveEdition(SystemInfo inf) {
        String oldDataStr = PreferencesUtils.getredValueFromSPMap(this.getActivity(), PreferencesUtils.Keys.IS_SYS_NEWS);
        String newDataStr = "";
        // 无记录
        if (oldDataStr.equals("") || oldDataStr == null) {
            newDataStr += inf.edition + ",";
            PreferencesUtils.putredValueToSPMap(this.getActivity(), PreferencesUtils.Keys.IS_SYS_NEWS, newDataStr);
            return;
        }
        String[] updates = oldDataStr.split(",");
        //当id记录超过300条，清空之前数据，只保存本次数据
        if (updates.length >= 300) {
            newDataStr += inf.edition + ",";
            PreferencesUtils.putredValueToSPMap(this.getActivity(), PreferencesUtils.Keys.IS_SYS_NEWS, newDataStr);
            return;
        } else {
            if (!oldDataStr.contains(inf.edition)) {
                oldDataStr += inf.edition + ",";
            }
            PreferencesUtils.putredValueToSPMap(this.getActivity(), PreferencesUtils.Keys.IS_SYS_NEWS, oldDataStr);
        }
    }
}
