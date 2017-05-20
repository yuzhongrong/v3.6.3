package com.jinr.core.dayup;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ScaleTransformer implements ViewPager.PageTransformer {
    private static final float defaultScale = 0.85f;
    private float zoomScale = defaultScale;
    public static final float defaultCenter = 0.5f;

    public ScaleTransformer() {
    }

    public ScaleTransformer(float minScale) {
        zoomScale = minScale;
    }

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        view.setPivotY(pageHeight / 2);
        view.setPivotX(pageWidth / 2);
        if (position < -1) {
            view.setScaleX(zoomScale);
            view.setScaleY(zoomScale);
            view.setPivotX(pageWidth);
        } else if (position <= 1) {
            if (position < 0) {
                float scale = (1 + position) * (1 - zoomScale) + zoomScale;
                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setPivotX(pageWidth * (defaultCenter + (defaultCenter * -position)));
            } else {
                float scale = (1 - position) * (1 - zoomScale) + zoomScale;
                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setPivotX(pageWidth * ((1 - position) * defaultCenter));
            }
        } else {
            view.setPivotX(0);
            view.setScaleX(zoomScale);
            view.setScaleY(zoomScale);
        }
    }
}
