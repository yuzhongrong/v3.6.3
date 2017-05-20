package com.jinr.core.security.address;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jinr.core.R;

import java.util.ArrayList;

import model.DBAddressMode;

public class AddressFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private View mView;
    private CityListViewAdapter mAdapter;
    private OnListViewItemClick mListener;
    private ArrayList<DBAddressMode> mList;
    private int index;

    public interface OnListViewItemClick {
        void ListViewItemClickListen(int position, int id, String addr_name, String jd_code, int index);
    }

    public void setOnListViewItemClickListener(OnListViewItemClick listener) {
        this.mListener = listener;
    }

    public AddressFragment() {
    }

    public ArrayList<DBAddressMode> getList() {
        return mList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        mView = inflater.inflate(R.layout.fragment_address, container, false);
        mListView = (ListView) mView.findViewById(R.id.listView);
        mAdapter = new CityListViewAdapter();
        Bundle bundle = getArguments();
        mList = (ArrayList<DBAddressMode>) bundle.getSerializable("data");
        index = bundle.getInt("index");
        mAdapter.setParent(getActivity(), mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            for (int i = 0; i < mList.size(); i++) {
                if (i == position) {
                    mList.get(position).setChoose(true);
                } else {
                    mList.get(position).setChoose(false);
                }
            }
            mAdapter.notifyDataSetChanged();
            mListener.ListViewItemClickListen(position, mList.get(position).getId(), mList.get(position).getAddres_name(),
                    mList.get(position).getJd_code(), index);
        }
    }
}
