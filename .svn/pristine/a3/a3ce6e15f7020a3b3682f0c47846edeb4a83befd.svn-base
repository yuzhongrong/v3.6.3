package com.jinr.core.trade.purchase.product;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * @author jzs created 2017/4/20
 */
public class FragmentMainAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mList;
    private List<String> mTitle;

    public FragmentMainAdapter(FragmentManager fm, List<Fragment> list, List<String> title) {
        super(fm);
        this.mList = list;
        this.mTitle = title;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }

    public void setList(List<Fragment> list, List<String> title) {
        this.mList = list;
        this.mTitle = title;
    }
}
