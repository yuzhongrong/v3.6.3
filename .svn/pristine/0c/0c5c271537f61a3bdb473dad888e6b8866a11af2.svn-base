package com.jinr.core.security.address;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.security.address.AddressFragment.OnListViewItemClick;

import java.util.ArrayList;

import model.AddrBean;
import model.DBAddressMode;

public class AddressDialogFragment extends DialogFragment implements OnKeyListener, OnClickListener, OnListViewItemClick {
    private SQLiteDatabase mDatabase;
    private ImageView image_close;
    private NoScrollViewPager mViewPage;
    private TextView tv_first;
    private TextView tv_unfirst;
    private TextView tv_second;
    private TextView tv_unsecond;
    private TextView tv_three;
    private TextView tv_unthree;
    private TextView tv_four;
    private TextView tv_unfour;
    private TextView tv_five;
    private TextView tv_unfive;
    private TextView tv_six;
    private TextView tv_unsix;
    private FragmentAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList;
    private int mIndex = 1;
    private int[] mColors;
    private int[] mVisible;
    private TextView[] mTab;
    private TextView[] mUnTab;
    private OnAreaChoosedListener mListener;

    public AddressDialogFragment() {
    }

    public interface OnAreaChoosedListener {
        // 地区选择完毕时的回调，需要添加回调的参数 @author Ysw created at 2016/12/20 0:11
        void OnChoosedListener(AddrBean addrBean);
    }

    public void setOnAreaChoosedListener(OnAreaChoosedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置宽度布满的设置 @author Ysw created at 2016/12/14 10:37
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        // 设置布局显示在底部 @author Ysw created at 2016/12/14 10:40
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels;
        layoutParams.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(layoutParams);
        // 设置背景透明度 @author Ysw created at 2016/12/14 10:46
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.6f;
        window.setAttributes(windowParams);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Fullscreen);
        mVisible = new int[]{View.VISIBLE, View.GONE};
        mColors = new int[]{getActivity().getResources().getColor(R.color.blue_btn_bg),
                getActivity().getResources().getColor(R.color.dark)};
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_dialog, null);
        initUI(view);
        initData();
        return view;
    }

    private void initUI(View view) {
        image_close = (ImageView) view.findViewById(R.id.image_close);
        mViewPage = (NoScrollViewPager) view.findViewById(R.id.page);
        tv_first = (TextView) view.findViewById(R.id.tv_first);
        tv_unfirst = (TextView) view.findViewById(R.id.tv_unfirst);
        tv_second = (TextView) view.findViewById(R.id.tv_second);
        tv_unsecond = (TextView) view.findViewById(R.id.tv_unsecond);
        tv_three = (TextView) view.findViewById(R.id.tv_three);
        tv_unthree = (TextView) view.findViewById(R.id.tv_unthree);
        tv_four = (TextView) view.findViewById(R.id.tv_four);
        tv_unfour = (TextView) view.findViewById(R.id.tv_unfour);
        tv_five = (TextView) view.findViewById(R.id.tv_five);
        tv_unfive = (TextView) view.findViewById(R.id.tv_unfive);
        tv_six = (TextView) view.findViewById(R.id.tv_six);
        tv_unsix = (TextView) view.findViewById(R.id.tv_unsix);
        mTab = new TextView[]{tv_first, tv_second, tv_three, tv_four, tv_five, tv_six};
        mUnTab = new TextView[]{tv_unfirst, tv_unsecond, tv_unthree, tv_unfour, tv_unfive, tv_unsix};
        mAdapter = new FragmentAdapter(getChildFragmentManager());
        mFragmentList = new ArrayList<>();
        image_close.setOnClickListener(this);
        tv_first.setOnClickListener(this);
        tv_second.setOnClickListener(this);
        tv_three.setOnClickListener(this);
        tv_four.setOnClickListener(this);
        tv_five.setOnClickListener(this);
        tv_six.setOnClickListener(this);
    }

    // 获取第一个等级的省级数据
    private void initData() {
        AddressFragment fragment = new AddressFragment();
        fragment.setOnListViewItemClickListener(this);
        mFragmentList.add(fragment);
        mAdapter.setParent(getActivity(), mFragmentList);
        mViewPage.setOffscreenPageLimit(3);
        mViewPage.setCurrentItem(0);
        mViewPage.setAdapter(mAdapter);
        Bundle bundle = new Bundle();
        String sql = "SELECT * FROM china_city WHERE pid=0";
        bundle.putSerializable("data", getAddressData(sql));
        bundle.putInt("index", mIndex);
        fragment.setArguments(bundle);
    }

    // 获取数据库文件数据并写入到本地数据库并查询相关的数据 @author Ysw created at 2016/12/14 11:37
    public ArrayList<DBAddressMode> getAddressData(String sql) {
        mDatabase = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
        Cursor cursor = mDatabase.rawQuery(sql, null);
        ArrayList<DBAddressMode> cityList = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            DBAddressMode mode = new DBAddressMode();
            mode.setId(cursor.getInt(cursor.getColumnIndex("id")));
            mode.setAddres_name(cursor.getString(cursor.getColumnIndex("addres_name")));
            mode.setPid(cursor.getInt(cursor.getColumnIndex("pid")));
            mode.setLevel(cursor.getInt(cursor.getColumnIndex("level")));
            mode.setJd_code(cursor.getString(cursor.getColumnIndex("jd_code")));
            mode.setChoose(false);
            cityList.add(mode);
        }
        cursor.close();
        return cityList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                this.dismiss();
                break;
            case R.id.tv_first:
                setUiChange(0, 1, 1, 1, 1, 1);
                setTextColors(0, 1, 1, 1, 1, 1);
                mViewPage.setCurrentItem(0);
                break;
            case R.id.tv_second:
                setUiChange(1, 0, 1, 1, 1, 1);
                setTextColors(1, 0, 1, 1, 1, 1);
                mViewPage.setCurrentItem(1);
                break;
            case R.id.tv_three:
                setUiChange(1, 1, 0, 1, 1, 1);
                setTextColors(1, 1, 0, 1, 1, 1);
                mViewPage.setCurrentItem(2);
                break;
            case R.id.tv_four:
                setUiChange(1, 1, 1, 0, 1, 1);
                setTextColors(1, 1, 1, 0, 1, 1);
                mViewPage.setCurrentItem(3);
                break;
            case R.id.tv_five:
                setUiChange(1, 1, 1, 1, 0, 1);
                setTextColors(1, 1, 1, 1, 0, 1);
                mViewPage.setCurrentItem(4);
                break;
            case R.id.tv_six:
                setUiChange(1, 1, 1, 1, 1, 0);
                setTextColors(1, 1, 1, 1, 1, 0);
                mViewPage.setCurrentItem(5);
                break;
            default:
                break;
        }

    }

    public void setUiChange(int first, int second, int three, int four, int five, int six) {
        tv_unfirst.setVisibility(mVisible[first]);
        tv_unsecond.setVisibility(mVisible[second]);
        tv_unthree.setVisibility(mVisible[three]);
        tv_unfour.setVisibility(mVisible[four]);
        tv_unfive.setVisibility(mVisible[five]);
        tv_unsix.setVisibility(mVisible[six]);
    }

    public void setTextColors(int first, int second, int three, int four, int five, int six) {
        tv_first.setTextColor(mColors[first]);
        tv_second.setTextColor(mColors[second]);
        tv_three.setTextColor(mColors[three]);
        tv_four.setTextColor(mColors[four]);
        tv_five.setTextColor(mColors[five]);
        tv_six.setTextColor(mColors[six]);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.dismiss();
            return true;
        } else {
            return false;
        }
    }
    String address_code  = "";//地址+编码
    String region_name = "" ; //省市区镇县
    String province  = "";    //省id
    String city  = "";        //市id
    String county = "";      //县id
    String area = "" ;        //区镇id
    @Override
    public void ListViewItemClickListen(int position, int id, String addr_name, String jd_code,int index) {

        String sql = "SELECT * FROM china_city WHERE pid=" + id;
        ArrayList<DBAddressMode> list = getAddressData(sql);
        if (mIndex > index) {
            ArrayList<Fragment> arrayList = new ArrayList<>();
            for (int i = 0; i < index; i++) {
                arrayList.add(mFragmentList.get(i));
            }
            for (int i = 0; i < 5; i++) {
                if (i < index) {
                    mTab[i].setVisibility(View.VISIBLE);
                    mUnTab[i].setVisibility(View.VISIBLE);
                } else {
                    mTab[i].setVisibility(View.VISIBLE);
                    mTab[i].setText("");
                    mUnTab[i].setVisibility(View.VISIBLE);
                }
            }
            mFragmentList.clear();
            mAdapter.notifyDataSetChanged();
            mFragmentList.addAll(arrayList);
            mIndex = index;
        }
        if (list.size() > 0) {
            AddressFragment fragment = new AddressFragment();
            fragment.setOnListViewItemClickListener(AddressDialogFragment.this);
            mFragmentList.add(fragment);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", list);
            bundle.putInt("index", mIndex + 1);
            fragment.setArguments(bundle);
            mAdapter.notifyDataSetChanged();
            mViewPage.setCurrentItem(mFragmentList.size() - 1);
            if (mIndex == 1) {
                tv_first.setText(addr_name);
                tv_second.setText("请选择");
                setUiChange(1, 0, 1, 1, 1, 1);
                setTextColors(1, 0, 1, 1, 1, 1);
                province = id+"";
                address_code = address_code+addr_name+jd_code+",";
            } else if (mIndex == 2) {
                tv_second.setText(addr_name);
                tv_three.setText("请选择");
                setUiChange(1, 1, 0, 1, 1, 1);
                setTextColors(1, 1, 0, 1, 1, 1);
                city = id+"";
                address_code = address_code+addr_name+jd_code+",";
            } else if (mIndex == 3) {
                tv_three.setText(addr_name);
                tv_four.setText("请选择");
                setUiChange(1, 1, 1, 0, 1, 1);
                setTextColors(1, 1, 1, 0, 1, 1);
                area = id+"";
                address_code = address_code+addr_name+jd_code+",";
            } else if (mIndex == 4) {
                tv_four.setText(addr_name);
                tv_five.setText("请选择");
                setUiChange(1, 1, 1, 1, 0, 1);
                setTextColors(1, 1, 1, 1, 0, 1);
                county = id+"";
                address_code = address_code+addr_name+jd_code+",";
            } else if (mIndex == 5) {
                tv_five.setText(addr_name);
                tv_six.setText("请选择");
                setUiChange(1, 1, 1, 1, 1, 0);
                setTextColors(1, 1, 1, 1, 1, 0);
            }
            mIndex++;
        } else {
            String first = "";
            String second = "";
            String three = "";
            String four = "";
            String five = "";
            String six = "";
            switch (index) {
                case 0:
                    region_name = tv_first.getText().toString().trim();
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
                case 1:
                    first = tv_first.getText().toString().trim();
                    region_name = first + addr_name;
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
                case 2:
                    first = tv_first.getText().toString().trim();
                    region_name = first +  addr_name;
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
                case 3:
                    first = tv_first.getText().toString().trim();
                    second = tv_second.getText().toString().trim();
                    region_name = first + second + addr_name;
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
                case 4:
                    first = tv_first.getText().toString().trim();
                    second = tv_second.getText().toString().trim();
                    three = tv_three.getText().toString().trim();
                    region_name = first + second + three + addr_name;
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
                case 5:
                    first = tv_first.getText().toString().trim();
                    second = tv_second.getText().toString().trim();
                    three = tv_three.getText().toString().trim();
                    four = tv_four.getText().toString().trim();
                    region_name = first + second + three + four  + addr_name;
                    area = id+"";
                    address_code = address_code+addr_name+jd_code+",";
                    break;
            }
            if (mListener != null) {
                AddrBean addrBean = new AddrBean(address_code, region_name, province, city, area, county);
                mListener.OnChoosedListener(addrBean);
                dismiss();
            }
        }
    }
}
