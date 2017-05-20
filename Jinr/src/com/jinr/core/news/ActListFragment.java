package com.jinr.core.news;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.XListView;
import com.jinr.graph_view.XListView.IXListViewListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import model.Action;

public class ActListFragment extends BaseFragment implements IXListViewListener {
    private View view;
    private XListView listView;
    private ActAdapter adapter = null;
    private int page = 1;
    private ArrayList<Action> actionList;
    private int count = 0;
    public int PAGE_COUNT = 5;
    private HashSet<String> unreadSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_act, null);
        initData();
        findViewById(view);
        initUI();
        return view;
    }

    @Override
    protected void initData() {
        actionList = new ArrayList<>();
        unreadSet = new HashSet<>();
        adapter = new ActAdapter(getActivity(), actionList, unreadSet);
    }

    @Override
    protected void findViewById(View view) {
        listView = (XListView) view.findViewById(R.id.act_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setPullLoadEnable(false);
        listView.setRefreshTime();
        listView.setXListViewListener(this, 1);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initUI() {
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {

    }


    private void send() throws Exception {
        JSONObject obj = new JSONObject();
        if (actionList.size() != 0) {
            page = (int) (Math.ceil((actionList.size()) * 1.0 / PAGE_COUNT + 1));
        } else {
            page = 1;
        }
        obj.put("page", page);
        String uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        obj.put(PreferencesUtils.Keys.UID, uid);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ACT_LIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(getActivity());
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(getActivity(), R.string.default_error);
                listView.stopRefresh();
                listView.stopLoadMore();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                listView.stopRefresh();
                listView.stopLoadMore();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject result = new JSONObject(desStr);
                    int code = result.getInt("code");
                    if (code == 1000) {
                        JSONObject data = result.getJSONObject("data");
                        if (data.has("list")) {
                            JSONArray array = data.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject js = array.getJSONObject(i);
                                Action action = new Action();
                                action.setId(js.optString("id"));
                                action.setTitle(js.optString("title"));
                                action.setUrl(js.optString("url"));
                                action.setPophoto(js.optString("photo"));
                                actionList.add(action);
                            }
                            if (data.has("notReadList")) {
                                JSONArray notReadList = data.getJSONArray("notReadList");
                                for (int i = 0; i < notReadList.length(); i++) {
                                    unreadSet.add(notReadList.get(i).toString());
                                }
                            }
                            if (adapter == null) {
                                adapter = new ActAdapter(getActivity(), actionList, unreadSet);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        count = Integer.parseInt(data.optString("count"));
                        int is_page = 0;
                        int count_in = count - (actionList.size()) + PAGE_COUNT;
                        if (count_in / PAGE_COUNT > 1) {
                            is_page = count_in / PAGE_COUNT;
                        } else {
                            is_page = count_in % PAGE_COUNT;
                        }
                        if (is_page <= 0) {
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                    } else {
                        ToastUtil.show(getActivity(), result.optString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh(int id) {
        actionList.clear();
        initUI();
    }

    @Override
    public void onLoadMore(int id) {
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
