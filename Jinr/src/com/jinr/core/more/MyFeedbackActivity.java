package com.jinr.core.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Feddbackinfo;

public class MyFeedbackActivity extends BaseActivity implements OnClickListener {
    private ListView list_feedback;
    private String uid;
    private TextView no_feedback;
    private ArrayList<Feddbackinfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void onResume() {
        list.clear();
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        TextView tv_title = (TextView) findViewById(R.id.title_center_text);
        no_feedback = (TextView) findViewById(R.id.no_feedback);
        tv_title.setText(getString(R.string.my_feedback));
        list_feedback = (ListView) findViewById(R.id.list_myfeedback);
        list_feedback.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        list_feedback.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Feddbackinfo fback = list.get(position);
                Intent inte = new Intent(MyFeedbackActivity.this, Detial_fbckActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("feedback", fback);
                inte.putExtras(bundle);
                startActivity(inte);
            }
        });
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void setListener() {
        findViewById(R.id.title_left_img).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            default:
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.system_gonggao, null);
                vh = new ViewHolder();
                vh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                vh.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            if (list.size() > 0) {
                Feddbackinfo fdk = list.get(position);
                vh.tv_title.setText(fdk.problem);
                vh.tv_time.setText(fdk.c_time);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_title;
    }

    protected void send() throws Exception {
        showWaittingDialog(MyFeedbackActivity.this);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.FEEDBACK_LIST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MyFeedbackActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    JSONObject object = new JSONObject(cipher);
                    int result = object.getInt("code");
                    if (result == 1000) {
                        list_feedback.setVisibility(View.VISIBLE);
                        no_feedback.setVisibility(View.GONE);
                        JSONObject data = object.getJSONObject("data");
                        MyLog.d("data", data.toString());
                        String imgSite = data.getString("img_site");
                        JSONArray listArray = data.getJSONArray("list");
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject detailobj = listArray.getJSONObject(i);
                            String id = detailobj.getString("id");
                            String problem = detailobj.getString("problem");
                            String image = detailobj.getString("image");
                            String c_time = detailobj.getString("c_time");
                            Feddbackinfo fbc = new Feddbackinfo(id, problem, image, c_time, imgSite);
                            list.add(fbc);
                        }
                        MyAdapter my = new MyAdapter();
                        list_feedback.setAdapter(my);
                    } else {
                        list_feedback.setVisibility(View.GONE);
                        no_feedback.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
