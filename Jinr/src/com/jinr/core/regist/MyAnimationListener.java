package com.jinr.core.regist;

import android.animation.Animator;

/**
 * 当一个监听接口有较多的方法需要重写时，我们可以写一个抽象类来实现这个接口并
 * 重写一些我们不想再实际的代码中重写的方法，我们想要重写的方法这边不重写
 * Created by: Ysw on 2017/3/8.
 */

public abstract class MyAnimationListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
