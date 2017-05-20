package com.jinr.core.trade.purchase.product;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by: Administrator on 2017/3/21.
 */
public class ProductPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mPageList;

    public ProductPageAdapter(FragmentManager fm, ArrayList<Fragment> pageList) {
        super(fm);
        this.mPageList = pageList;
    }

    @Override
    public Fragment getItem(int position) {
        return mPageList.get(position);
    }

    @Override
    public int getCount() {
        return mPageList == null ? 0 : mPageList.size();
    }
}
