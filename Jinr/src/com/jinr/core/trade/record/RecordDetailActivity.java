package com.jinr.core.trade.record;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.Record;
import model.RecordDetail;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
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

/**
 * 交易记录-转入结果详情
 * 
 * 
 */
public class RecordDetailActivity extends BaseActivity implements OnClickListener {
	private ImageView title_left_img; // title左边图片
	private ImageView iv_state;//
	private TextView title_center_text; // title标题
	private TextView amount_tv; // 转入金额
	private TextView jy_time_tv; // 付款时间
	private TextView date_start_tv; // 开始计算收益时间
	private TextView tv_start;// 开始计算收益
	private TextView date_arrive_tv; // 收益到账
	private TextView tv_arrive;
	private int color_blue;

	private Button finish_btn; // 完成
	private ImageView state_iv; // 交易状态
	private String amount, num_id;
	private String uid;
	private String flag;
	private Record record;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_result);
		initData();
		findViewById();
		initUI();
		setListener();
	}


	@Override
	protected void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			record=(Record) getIntent().getSerializableExtra("record");
		}
		uid = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.UID);
		color_blue =   RecordDetailActivity.this.getResources().getColor(
				R.color.blue_color);
	}

	@Override
	protected void findViewById() {
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		finish_btn = (Button) findViewById(R.id.btn_finish);
		amount_tv = (TextView) findViewById(R.id.tv_amount);
		jy_time_tv = (TextView) findViewById(R.id.tv_jy_time);
		date_start_tv = (TextView) findViewById(R.id.tv_date_start);
		date_arrive_tv = (TextView) findViewById(R.id.tv_date_arrive);
		tv_arrive = (TextView) findViewById(R.id.tv_arrive);
		iv_state = (ImageView) findViewById(R.id.iv_state);
		tv_start = (TextView) findViewById(R.id.tv_start);

	}

	@Override
	protected void initUI() {
//				2:余额 3:活期4:定期 5 :日增息6:转让
				if (!TextUtils.isEmpty(record.getGoods_type())) {
					switch (Integer.parseInt(record.getGoods_type())) {
					case 2:
						if (!TextUtils.isEmpty(record.getType())) {
							if ("1".equals(record.getType())) {
								title_center_text.setText("余额回款详情");
							}else if("2".equals(record.getType())){
								title_center_text.setText("余额转出详情");
							}else {
								title_center_text.setText("余额退款详情");//待定
							}
						}
						break;
					case 3:
						if (!TextUtils.isEmpty(record.getType())) {
							if ("1".equals(record.getType())) {
								title_center_text.setText("活期转入详情");
							}else {
								title_center_text.setText("活期转出详情");
							}
						}
						break;
		
					case 4:
						if (!TextUtils.isEmpty(record.getType())) {
							if ("1".equals(record.getType())) {
								title_center_text.setText("定期转入详情");
							}else {
								title_center_text.setText("定期到期详情");
							}
						}
						break;
		
					case 5:
						if (!TextUtils.isEmpty(record.getType())) {
							if ("1".equals(record.getType())) {
								title_center_text.setText("日增息转入详情");
							}else {
								title_center_text.setText("日增息到期详情");
							}
						}
						break;
		
					case 6:
						title_center_text.setText("转让详情");
						break;
					default:
						break;
					}
				}
//		title_center_text.setText("交易详情");
		finish_btn.setVisibility(View.GONE);
		try {
			sendJYDetail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void setListener() {
		title_left_img.setOnClickListener(this);
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

	/**
	 * 转入时间
	 */
	private void sendJYDetail() throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("uid", uid);
		obj.put("order_num", record.getOrder_id());
		obj.put("pay_type", record.getPay_type());
		obj.put("type", record.getType());
		obj.put("buy_type", record.getBuy_type());
		obj.put("goods_type", record.getGoods_type());
		RequestParams params = new RequestParams();
		MyLog.d("DES", "json Str :" + obj.toString());
		String desStr = MyDES.encrypt(obj.toString());
		params.put("cipher", desStr);
		MyLog.d("DES", "加密json Str :" + desStr);
		MyLog.d("DES", "解密json Str :" + MyDES.decrypt(desStr));
		MyhttpClient.desPost(UrlConfig.JY_DETAIL, params,
				new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showWaittingDialog(RecordDetailActivity.this);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				dismissWaittingDialog();
				ToastUtil.show(MainActivity.instance, R.string.default_error);

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				dismissWaittingDialog();
				try {
					String response = new String(arg2, "UTF-8");
					MyLog.i("时间返回=====", response);
					response = CommonUtil.transResponse(response);
					if (response == null || response.equals("")) {
						return;
					}
					JSONObject jsonObject = new JSONObject(response); // 解析
					String cipher = jsonObject.getString("cipher");
					String desStr = MyDES.decrypt(cipher); // 解密
					MyLog.d("DES", "解密请求返回：" + desStr);
					if (TextUtils.isEmpty(desStr))
						return;
					JSONObject desStrObject = new JSONObject(desStr);
					if (desStrObject.getInt("code") == 1000) {
						JSONObject jsonObj = desStrObject
								.getJSONObject("data");
						String status=jsonObj.optString("status");
						amount_tv.setText(jsonObj.optString("one_content"));
						tv_start.setText(jsonObj.optString("two_content"));
						tv_arrive.setText(jsonObj.optString("three_content"));
						jy_time_tv.setText(jsonObj.optString("create_time"));
						date_start_tv.setText(jsonObj.optString("expect_time"));
						date_arrive_tv.setText(jsonObj.optString("toAccount_time"));
						title_center_text.setText(jsonObj.optString("title"));
						if(jsonObj.optString("line_num").equals("3")){
							if ("6".equals(record.getGoods_type())) {
								if ("1".equals(status)) {
									iv_state.setImageResource(R.drawable.trans_status1);
								}else if ("2".equals(status)) {
									iv_state.setImageResource(R.drawable.trans_status2);
									tv_start.setTextColor(color_blue);
									date_start_tv.setTextColor(color_blue);
								}else if("3".equals(status)) {
									iv_state.setImageResource(R.drawable.trans_status3);
									tv_start.setTextColor(color_blue);
									date_start_tv.setTextColor(color_blue);
									date_arrive_tv.setTextColor(color_blue);
									tv_arrive.setTextColor(color_blue);
								}
							}else {
								if ("1".equals(status)) {
									iv_state.setImageResource(R.drawable.ic_purchase_result);
								}else if ("2".equals(status)) {
									iv_state.setImageResource(R.drawable.ic_purchase_result2);
									tv_start.setTextColor(color_blue);
									date_start_tv.setTextColor(color_blue);
								}else if("3".equals(status)) {
									iv_state.setImageResource(R.drawable.ic_purchase_result3);
									tv_start.setTextColor(color_blue);
									date_start_tv.setTextColor(color_blue);
									date_arrive_tv.setTextColor(color_blue);
									tv_arrive.setTextColor(color_blue);
								}
							}
							
						}else if(jsonObj.optString("line_num").equals("2")){
							if ("2".equals(status)) {
								iv_state.setImageResource(R.drawable.get_cash2);
								tv_start.setTextColor(color_blue);
								date_start_tv.setTextColor(color_blue);
							}else if("1".equals(status)){
								iv_state.setImageResource(R.drawable.get_cash);
							}
						}

					} else {
						ToastUtil.show(getApplication(),
								desStrObject.optString("msg"));
					}
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				} catch (JsonSyntaxException e) {
					MyLog.i("json解析错误", "解析错误");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

}
