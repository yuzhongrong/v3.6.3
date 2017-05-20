package com.jinr.core.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ParseException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jinr.core.base.BaseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作工具
 *
 * @author wsy
 * @date 2016-6-3
 */
public final class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final int BUFFER_SIZE = 4 * 1024;

    public static native int getFatVolumeId(String mountPoint);

    public static void checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获取SDCARD的DCIM目录
     *
     * @return
     */
    public static File getSDCardPath() {
        if (Build.VERSION.SDK_INT >= 8) {
            return Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        } else {
            return new File(Environment.getExternalStorageDirectory() + "/dcim");
        }
    }

    /**
     * 判断是否有SDCARD
     *
     * @return
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * SDCARD的根目录
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().toString();
    }

    /**
     * 判断某个文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        if (!isSDCardExist()) {
            return false;
        }
        File file = new File(filePath);
        if (file != null && file.exists()) {
            if (file.length() == 0) {
                file.delete();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取存储卡的可用空间
     *
     * @return
     */
    public static long getStorageSize() {
        if (isSDCardExist()) {
            StatFs stat = new StatFs(getSDCardPath().getAbsolutePath());
            long blockSize = stat.getBlockSize();
            return stat.getAvailableBlocks() * blockSize;
        }
        return 0;
    }

    /**
     * 更新文件修改时间
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static long getUpdateFileTime(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return -1;
        }
        return file.lastModified();
    }

    /**
     * 更新文件修改时间
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean updateFileTime(String dir, String fileName) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            return false;
        }
        long newModifiedTime = System.currentTimeMillis();
        return file.setLastModified(newModifiedTime);
    }

    /**
     * 创建文件目录
     *
     * @param path
     */
    public static boolean createFileDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            return true;
        }
        file = null;
        return false;
    }

    /**
     * 检测文件路径的目录是否存在，不存在就尝试创建目录
     *
     * @param path 文件路径
     * @return 目录是否可用
     */
    public static boolean checkPathDir(String path) {
        if (TextUtils.isEmpty(path) || path.charAt(path.length() - 1) == '/') {
            return false;
        }
        String dirStr = path;
        int end = dirStr.lastIndexOf('/') + 1;
        if (end != -1) {
            dirStr = dirStr.substring(0, end);
        } else {
            return false;
        }
        return checkDir(dirStr);
    }

    /**
     * 检测目录是否存在，不存在就尝试创建目录
     *
     * @param path 文件目录
     * @return 目录是否可用
     */
    public static boolean checkDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return false;
        }
        File dir = new File(dirPath);
        if (dir.exists() || dir.mkdirs()) {
            return true;
        }
        return false;
    }

    /**
     * 写文件到SDCARD指定的位置
     *
     * @param str
     * @param filePath
     * @return
     */
    public static boolean outPutToFile(String str, String filePath) {
        if (!isSDCardExist()) {
            return false;
        }
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            FileWriter fw = new FileWriter(filePath, true);
            fw.write(str);
            fw.flush();
            fw.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean outCrashToFile(String str, String filePath,
                                         int maxSize) {
        if (!isSDCardExist()) {
            return false;
        }
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (maxSize < file.length()) {
                    file.delete();
                }
            }
            FileWriter fw = new FileWriter(filePath, true);
            fw.write(str);
            fw.flush();
            fw.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static void copyFileToSdcard(Context context, String fileName,
                                        String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.isFile()) {
                RandomAccessFile oSavedFile = new RandomAccessFile(file, "rw");
                oSavedFile.seek(0);
                int bufferSize = 4096;
                byte[] b = new byte[bufferSize];
                int nRead;
                int currentBytes = 0;
                int bytesNotified = currentBytes;
                long timeLastNotification = 0;
                for (; ; ) {
                    nRead = bis.read(b, 0, bufferSize);

                    if (nRead == -1) {
                        break;
                    }
                    currentBytes += nRead;
                    oSavedFile.write(b, 0, nRead);
                    long now = System.currentTimeMillis();
                    if (currentBytes - bytesNotified > bufferSize
                            && now - timeLastNotification > 1500) {
                        bytesNotified = currentBytes;
                        timeLastNotification = now;
                    }
                }
                oSavedFile.close();
            }
            is.close();
        } catch (Exception e) {

        }
    }

    public static void copyFileToFile(Context context, String fileName,
                                      InputStream is) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_WORLD_WRITEABLE);

            int bufferSize = 4096;
            byte[] b = new byte[bufferSize];
            int nRead;
            int currentBytes = 0;
            int bytesNotified = currentBytes;
            long timeLastNotification = 0;
            for (; ; ) {
                nRead = bis.read(b, 0, bufferSize);

                if (nRead == -1) {
                    break;
                }
                currentBytes += nRead;
                fos.write(b, 0, nRead);
                long now = System.currentTimeMillis();
                if (currentBytes - bytesNotified > bufferSize
                        && now - timeLastNotification > 1500) {
                    bytesNotified = currentBytes;
                    timeLastNotification = now;
                }
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (Exception e) {

        }
    }

    public static boolean copyFile(String filePath, String targetFilePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        if (!FileUtil.isSDCardExist()) {
            return false;
        }
        File file = new File(filePath);
        File targetFile = new File(filePath);
        try {
            FileOutputStream os = new FileOutputStream(targetFile, false);
            FileInputStream is = new FileInputStream(file);
            byte[] responseByteArray = new byte[BUFFER_SIZE];
            int line = -1;
            while ((line = is.read(responseByteArray)) != -1) {
                os.write(responseByteArray, 0, line);
            }
            os.close();
            is.close();
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    public static int getFileSize(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        int fileLen = 0;
        try {
            fis = new FileInputStream(file);
            fileLen = fis.available(); // 这就是文件大小
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return fileLen;
    }

    /**
     * 获取SDCARD的ID
     *
     * @return
     */
    public static int getSdcardID() {
        return getFatVolumeId(getSDCardPath().getName());
    }

    public static boolean delFiles(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }

        if (file.isFile() && file.delete()) { // 若是文件且删除成功返回 true
            return true;
        } else if (file.isDirectory()) {
            return delFolder(filePath);
        }
        return false;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath String 文件夹路径及名称 如:/mmt/SDCard
     * @return boolean
     */
    public static boolean delFolder(String folderPath) {
        boolean delSuccessed = false;
        try {
            if (!delAllFiles(folderPath)) { // 删除完里面所有内容
                return delSuccessed;
            }

            File file = new File(folderPath);
            if (file != null && (!file.exists() || file.delete())) { // 删除文件夹
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param folderPath String 文件夹路径 如 /mmt/SDCard
     */
    public static boolean delAllFiles(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return true;
        }

        File file = new File(folderPath);
        if (!file.exists()) { // 文件或文件夹不存在
            return true;
        }

        if (!file.isDirectory()) { // 不是文件夹
            return true;
        }

        boolean delSuccessed = false;
        String[] tempList = file.list();
        if (tempList == null) {
            return true;
        }

        File temp = null;
        for (int i = 0; i < tempList.length; i++) { // 遍历文件夹里的所有文件夹和文件
            if (folderPath.endsWith(File.separator)) {
                temp = new File(folderPath + tempList[i]);
            } else {
                temp = new File(folderPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) { // 如果是文件直接删除
                if (!temp.delete()) {
                    return false; // 删除文件失败返回 false
                }
            } else if (temp.isDirectory()) { // 文件夹
                delSuccessed = delFolder(folderPath + "/" + tempList[i]); // 再删除空文件夹
                if (!delSuccessed) {
                    return false; // 删除文件夹失败
                }
            }
        }

        return true;
    }

    // 删除指定文件
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            // 如果它是一个目录
        }
    }

    /**
     * 字符型转双精度型
     *
     * @param content
     * @return
     */
    public static double stringToDouble(String content) {
        content = clearStringSpace(content);

        if (content != null) {
            try {
                double result = Double.parseDouble(content);

                return result;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * Function:OBJEct转String
     *
     * @param content
     * @return
     */
    public static String ObjecttoString(Object object) {
        String content = "";
        if (object != null) {
            try {
                content = String.valueOf(object);
                return content;
            } catch (NumberFormatException e) {
                return content;
            }
        } else {
            return content;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF retcF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(retcF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /*
     * 剪切图片切正方形，并作圆角处理
     *
     * author kevin 20130220
     *
     * param bitmap 源图片
     *
     * 为使图片圆角一致，根据图片大小设置圆角的半径
     *
     * return 新图片
     */
    public static Bitmap getSquareRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        final Rect srcRect = new Rect();
        final Rect dstRect = new Rect();
        if (width > height) {
            int offset = (width - height) / 2;

            srcRect.set(offset, 0, offset + height, height);
            dstRect.set(0, 0, height, height);

            width = height;
        } else {
            int offset = (height - width) / 2;

            srcRect.set(0, offset, width, width + offset);
            dstRect.set(0, 0, width, width);

            height = width;
        }

        Bitmap output = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int roundPx = height / 20;
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final RectF retcF = new RectF(dstRect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(retcF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }

    /**
     * 字符型转整型
     *
     * @param content
     * @return
     */
    public static int stringToInteger(String content) {
        content = clearStringSpace(content);

        if (content != null) {
            try {
                int result = Integer.parseInt(content);

                return result;
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 字符串转长整型
     *
     * @param content
     * @return
     */
    public static long stringToLong(String content) {
        content = clearStringSpace(content);

        if (content != null) {
            try {
                long result = Long.parseLong(content);

                return result;
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 去掉首尾空格，并判断是否空字符
     *
     * @param content
     * @return
     */
    public static String clearStringSpace(String content) {
        if (content == null || content.trim().length() < 1) {
            return null;
        } else {
            return content.trim();
        }
    }

    /**
     * Map参数转JSON字符串
     *
     * @param params
     * @return
     */
    public static String paramToJsonString(Map<String, Object> params) {
        JSONObject jsonObject = new JSONObject();

        Set<Map.Entry<String, Object>> set = params.entrySet();
        Iterator<Map.Entry<String, Object>> iter = set.iterator();

        try {
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();

                Object value = entry.getValue();

                if (value instanceof String) {
                    jsonObject.put(entry.getKey(), (String) value);
                } else if (value instanceof String[]) {
                    JSONArray jsonArray = new JSONArray();
                    String[] values = (String[]) value;
                    for (String subValue : values) {
                        jsonArray.put(subValue);
                    }

                    jsonObject.put(entry.getKey(), jsonArray);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            return null;
        }

        return jsonObject.toString().replace("[", "").replace("]", "");
    }

    /**
     * 按尺寸缩小图片
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();

        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);

        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();

        float scale = ((float) w / width);

        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 距离转换公式
     *
     * @param distance
     * @return
     */
    public static String changeDistance(double distance) {
        if (distance > 1000) {
            return Math.round(distance / 10.0) / 100.0 + " KM";
        } else {
            return Math.round(distance * 100.0) / 100.0 + " M";
        }
    }

    /**
     * 经纬度转整型
     *
     * @param latlng
     * @return
     */
    public static int latlngToE6(double latlng) {
        return (int) (latlng * 1e6);
    }

    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static final String DELIMITER = "#";

    /**
     * 字符数组转字符串
     *
     * @param sources
     * @return
     */
    public static String arrayToString(String[] sources) {
        if (sources != null && sources.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String theUses : sources) {
                if (sb.length() > 0) {
                    sb.append(DELIMITER);
                }
                sb.append(theUses);
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    /**
     * 字符串转字符数组
     *
     * @param sources
     * @return
     */
    public static String[] stringToArray(String sources) {
        sources = clearStringSpace(sources);
        if (sources != null) {
            return sources.split(DELIMITER);
        } else {
            return null;
        }
    }

    /**
     * 从SDCard中读取内容缓存图片
     *
     * @param line
     * @return
     */
    public static Bitmap readContentSDcardImg(String id) {
        String imgPath = BaseData.SD_CARD_PATH + id + ".png";

        File file = new File(imgPath);
        if (file.exists()) {
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            bitmapFactoryOptions.inPurgeable = true;
            return BitmapFactory.decodeFile(file.toString(),
                    bitmapFactoryOptions);
        } else {
            return null;
        }
    }

    /**
     * 从SDCard中读取内容缓存图片 直接传入文件的绝对路径
     *
     * @param path     文件的路径
     * @param fileName 文件名
     * @return
     * @author kevin 2013-2-20
     */
    public static Bitmap readContentSDcardImg(String path, String fileName) {

        File file = new File(path, fileName);
        if (file.exists()) {
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            bitmapFactoryOptions.inPurgeable = true;
            return BitmapFactory.decodeFile(file.toString(),
                    bitmapFactoryOptions);
        } else {
            return null;
        }
    }

    /**
     * 根据图片地址分析图片名称
     *
     * @param imgUrl
     * @return
     */
    public static String analyImageName(String imgUrl) {
        int index = imgUrl.lastIndexOf("/");

        if (index != -1) {
            return imgUrl.substring(index + 1);
        } else {
            return imgUrl;
        }
    }

    public static void saveImageToSDcard(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                outStream.flush();
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 截取标题长度，超过范围用....代替
     *
     * @param text
     * @param length
     * @return
     */
    public static String subTitle(String text) {

        if (text.length() <= 8) {
            return text;
        } else {
            return text.substring(0, 8) + "...";
        }
    }

    public static String findPicName(String imagePath) {
        int m = imagePath.lastIndexOf("/");
        int n = imagePath.lastIndexOf(".");

        if (n > m) {
            return imagePath.substring(m, n);
        } else {
            return null;
        }
    }

    /**
     * 截取标题长度，超过范围用....代替
     *
     * @param text
     * @param length
     * @return
     */
    public static String subString(String text, Integer length) {

        if (text.length() <= length) {
            return text;
        } else {
            return text.substring(0, length) + "...";
        }
    }

    private static final Pattern ENCODE_PATTERN = Pattern
            .compile("[\u4e00-\u9fa5]+");

    /**
     * 中文地址转码
     *
     * @param path
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeChineseString(String path)
            throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();

        Matcher matcher = ENCODE_PATTERN.matcher(path);
        while (matcher.find()) {
            String temp = matcher.group();
            matcher.appendReplacement(sb, URLEncoder.encode(temp, "UTF-8"));
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 递归删除图片
     *
     * @param path   //文件的路径
     * @param remove //目录本身是否删除
     * @return
     */
    public static void deleteFile(File path, boolean remove) {
        if (path.isFile()) {
            path.delete();
        } else if (path.isDirectory()) {
            File[] childFiles = path.listFiles();
            if (childFiles != null && childFiles.length > 0) {
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFile(childFiles[i], true);
                }
            }

            if (remove) {
                path.delete();
            }
        }
    }

    public static final SimpleDateFormat FMT_1 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * 字符串转日期
     *
     * @param content
     * @return
     * @throws java.text.ParseException
     */
    public static synchronized Date parseDate1(String content)
            throws java.text.ParseException {
        content = clearStringSpace(content);
        if (content == null) {
            return null;
        }

        try {
            return FMT_1.parse(content);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * 从byte[]中获取图片
     *
     * @param bytes
     * @param opts
     * @return
     * @date 2013-03-16 13:37
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 将输入流转换为字节数组
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    /**
     * 将图片转换为字节数组
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] bitmapToByte(Bitmap bitmap) throws Exception {
        byte[] mContent = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mContent = baos.toByteArray();
        }
        baos.close();
        return mContent;
    }

    private static DecimalFormat DF = new DecimalFormat("00");

    /**
     * 取得当前时间
     *
     * @return
     */
    public static synchronized String getCurrentTime() {
        Calendar currentlyGregorianCalendar = GregorianCalendar.getInstance();

        return currentlyGregorianCalendar.get(GregorianCalendar.YEAR)
                + "-"
                + DF.format((currentlyGregorianCalendar
                .get(GregorianCalendar.MONTH) + 1))
                + "-"
                + DF.format(currentlyGregorianCalendar
                .get(GregorianCalendar.DAY_OF_MONTH))
                + " "
                + DF.format(currentlyGregorianCalendar
                .get(GregorianCalendar.HOUR_OF_DAY))
                + ":"
                + DF.format(currentlyGregorianCalendar
                .get(GregorianCalendar.MINUTE))
                + ":"
                + DF.format(currentlyGregorianCalendar
                .get(GregorianCalendar.SECOND));
    }

    /**
     * 清除支付宝特殊字符
     *
     * @param content
     * @return
     */
    public static String replaceSpecialChar(String content) {
        return content != null ? content.replaceAll("[\\+=&\"']", " ") : "";
    }

    /**
     * 传入一个时间，验证多次点击是否小于该时间
     *
     * @param maxTime
     * @return
     */
    // 最后一个点击的时间
    public static long lastClickTime = 0;

    public static boolean isFastDoubleClick(long maxTime) {
        long time = System.currentTimeMillis();
        long diffTime = time - lastClickTime;
        lastClickTime = time;
        if (0 < diffTime && diffTime < maxTime) {
            return true;
        } else {
            return false;
        }
    }

    public static Object getData(String url_address, Class<?> c)
            throws Exception {
        Object temp = new Object();
        URL url = new URL(url_address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            byte[] b = readInputStream(conn.getInputStream());
            if (c == String.class) {
                temp = new String(b);
                return temp;
            } else {
                temp = BitmapFactory.decodeByteArray(b, 0, b.length);
                return temp;
            }
        }
        return temp;
    }

    private static byte[] readInputStream(InputStream in) throws Exception {
        // TODO Auto-generated method stub
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = in.read(b)) != -1) {
            baos.write(b, 0, len);
        }
        in.close();
        return baos.toByteArray();
    }

    public static String toJson(Object object) {
        Gson gson = new Gson();
        String temp = gson.toJson(object);
        return temp;
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            // 注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(
                    "com.visitxm.ui", 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verCode;
    }
}
