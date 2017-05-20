package com.jinr.core.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimUtil {
    public static TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);

    public static void startAnim(View view) {
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(200);
        animation.setRepeatCount(6);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }

    public static void closeAnim(View view) {
        view.clearAnimation();
    }
}
