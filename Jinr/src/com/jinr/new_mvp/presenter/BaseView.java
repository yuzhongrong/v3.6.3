package com.jinr.new_mvp.presenter;

import cn.droidlover.xdroidmvp.mvp.IView;

/**
 * Created by:yuzhongrong on 2017/5/31.
 */

public interface BaseView extends IView<CommonPresenter> {

    void showError(Exception e);
}
