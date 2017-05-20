package com.jinr.core.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jinr.core.JinrApp;

/**
 * Created by jzs on 2016/11/25.
 */

public class BaiduUtils {
    // 百度定位
    public LocationClient mLocationClient = null;
    private static BaiduUtils instance;

    public static BaiduUtils getInstance() {
        if (instance == null) {
            instance = new BaiduUtils();
        }
        return instance;
    }

    /**
     * 百度地图定位初始化
     */
    public void initBaidu(BaiduLocationListener OnRecevieListener) {
//        ProgressDialog.getInstance().createLoadingDialog(context);
        // 声明LocationClient类
        mLocationClient = new LocationClient(JinrApp.instance().getApplicationContext());
        BDLocationListener myListener = new MyLocationListener(OnRecevieListener);
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        // 初始化定位参数
        initLocation();
        // 开始定位
        mLocationClient.start();
    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        BaiduLocationListener OnRecevieListener;

        public MyLocationListener(BaiduLocationListener OnRecevieListener) {
            this.OnRecevieListener = OnRecevieListener;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
//            ProgressDialog.getInstance().dismissDialog();

            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                OnRecevieListener.onSuccess(location);
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                OnRecevieListener.onSuccess(location);
            } else if (location.getLocType() == BDLocation.TypeGpsLocation) {
                OnRecevieListener.onSuccess(location);
            } else {
                OnRecevieListener.onFail();
            }
            MyLog.e("百度定位", "onReceiveLocation: getLocType =" + location.getLocType());
            // 停止定位
            mLocationClient.stop();
        }

    }

    /**
     * LocType错误码
     * 61 ： GPS定位结果
     * 62 ： 扫描整合定位依据失败。此时定位结果无效。
     * 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
     * 65 ： 定位缓存的结果。
     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
     * 161： 表示网络定位结果
     * 162~167： 服务端定位失败
     * 502：KEY参数错误
     * 505：KEY不存在或者非法
     * 601：KEY服务被开发者自己禁用
     * 602: KEY Mcode不匹配,意思就是您的ak配置过程中安全码设置有问题，请确保： sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名
     * 501-700：KEY验证失败
     */

    public interface BaiduLocationListener {

        void onSuccess(BDLocation location);

        void onFail();
    }
}
