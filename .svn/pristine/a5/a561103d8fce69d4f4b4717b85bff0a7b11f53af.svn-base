package com.jinr.core.utils;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static String PACKAGE_NAME = "com.jinr.core";
    public static boolean is_show = false;
    private static long lastClickTime;

    /**
     * 判断是否含有SD卡 @author Ysw created at 2017/3/15 13:08
     */
    public static boolean hasSDCard() {
        try {
            String status = Environment.getExternalStorageState();
            if (null == status) {
                return false;
            }
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取应用常用保存位置路径 @author Ysw created at 2017/3/15 13:07
     */
    public static String getRootFilePath() {
        return Environment.getDataDirectory().getAbsolutePath() + "/data/" + PACKAGE_NAME; // filePath: /data/data/
    }

    /**
     * 判断密码格式是否正确 @author Ysw created at 2017/3/15 13:03
     */
    public static boolean isPwdRegular(String pwd) {
        // 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
//        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");
//        Matcher m = p.matcher(pwd);
//        boolean b = m.matches();
//        return b;
        return true;
    }

    /**
     * 判断密码格式是否正确 @author Ysw created at 2017/3/15 13:03
     */
    public static boolean isDealPwdRegular(String pwd) {
        // 注册、忘记密码 只可以输入6位数字
        Pattern p = Pattern.compile("^\\d{6}$");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        return b;
    }

    /**
     * 判断网络是否可用 @author Ysw created at 2017/3/15 13:05
     */
    public static boolean checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

    /**
     * 获取IP地址 @author Ysw created at 2017/3/15 13:05
     */
    public static String getIP() {
        String IP = null;
        StringBuilder IPStringBuilder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {
                        IPStringBuilder.append(inetAddress.getHostAddress().toString() + "\n");
                    }
                }
            }
        } catch (SocketException ex) {

        }
        String[] ips = IPStringBuilder.toString().split("\n");
        return ips[0];  // 小米手机会获取两个IP值，只取第一个
    }

    /**
     * 此方法是用来处理PHP服务端回传的字符串中存在空格符，导致2.3机型无法正常解析的问题
     *
     * @param response
     * @return response
     */
    public static String transResponse(String response) {
        int start = response.indexOf("{");
        // 部分机型出现start值为-1的情况，导致页面崩溃
        if (start != -1) {
            String response_new = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            return response_new;
        }
        String response_new = response.substring(0, response.lastIndexOf("}") + 1);
        return response_new;
    }

    public static void showToask(Context context, String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取屏幕宽度 @author Ysw created at 2017/3/15 13:05
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取屏幕高度 @author Ysw created at 2017/3/15 13:06
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 判断密码是否含有中文 @author Ysw created at 2017/3/15 13:06
     */
    public static boolean isPwCh(String pwd) {
        Pattern p = Pattern.compile(".*[\\u4e00-\\u9faf].*");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        return b;
    }

    /**
     * 判断是否多次点击 @author Ysw created at 2017/3/15 13:06
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 隐藏键盘
     *
     * @param a
     */
    public static void hideKeyboard(final Activity a) {
        if (a == null || a.getCurrentFocus() == null)
            return;
        InputMethodManager inputManager = (InputMethodManager) a.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
