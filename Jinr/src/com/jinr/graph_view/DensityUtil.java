package com.jinr.graph_view;


import android.content.Context;

public class DensityUtil {

  /**
   * 根据手机的分辨率从dp的单位转成为px
   */
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从px的单位转成为dp
   */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * 将px值转换为sp值 保证文字大小不变
   * 
   * @param pxValue
   * @param fontScale
   * @return
   */
  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * 将sp值转换为px值 保证文字大小不变
   * 
   * @param spValue
   * @param fontScale
   * @return
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().density;
    return (int) (spValue * fontScale + 0.5f);
  }
}
