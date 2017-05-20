package com.jinr.core.base;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * 基础数据类，用于有图片的列表数据
 *
 * @author 曾贵 时间：2012-12-4 - 下午1:26:37
 */
@SuppressWarnings("serial")
public class BaseData implements java.io.Serializable {

    public static final String PROPERTIES_FILE = "my_data.properties";

    public static final String MY_LATITUDE = "my_latitude";

    public static final String MY_LONGITUDE = "my_longitude";
    public static final String FIRST_PUSH = "firstPush";

    public static final String ORDER_BY_ASC = " ASC";

    public static final String ORDER_BY_DESC = " DESC";
    public static final String SAVE_PHOTO_TYPE = ".png";
    public static final String SAVE_PHOTO_TYPE_JPG = ".jpg";

    /**
     * SD卡工程主目录
     */
    public static final String SD_CARD_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/Jinr/";

    public String id;

    public String imgType;

    public Bitmap bitmap;

    public String image;

    /**
     * 是否下载完成
     */
    private boolean done;

    public BaseData(String imgType) {
        this.imgType = imgType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void recycle() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = null;
    }
}
