package com.jinr.core.bankCard.banklist.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.bankCard.banklist.db.DBManager;
import com.jinr.core.bankCard.citylist.main.MyLetterListView;
import com.jinr.core.bankCard.citylist.main.MyLetterListView.OnTouchingLetterChangedListener;

/**
 * 城市列表选择界面
 * 
 * @author fym createOn 2014.11.1
 * 
 */
public class BankList extends Activity {
	private BaseAdapter adapter;
	private ListView mBankLit;
	private TextView overlay;
	private MyLetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private SQLiteDatabase database;
	private ArrayList<BankModel> mBankNames;
	private Button btn;
	private EditText et;
	private ImageView title_left_img;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_list);

		btn = (Button) findViewById(R.id.btn_b);
		et = (EditText) findViewById(R.id.et_b);
		overlay = (TextView) findViewById(R.id.overlay);
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = et.getText().toString().trim();
				mBankNames.clear();
				mBankNames = getSelectBankNames(content);
				setAdapter(mBankNames);

			}
		});
		title_left_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish_return();
			}
		});

		mBankLit = (ListView) findViewById(R.id.bank_list);
		letterListView = (MyLetterListView) findViewById(R.id.bankLetterListView);
		DBManager dbManager = new DBManager(this);
		dbManager.openDateBase();
		dbManager.closeDatabase();
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);

		mBankNames = getBankNames();
		// database.close();

		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		// initOverlay();
		setAdapter(mBankNames);
		mBankLit.setOnItemClickListener(new BankListOnItemClick());

	}

	/**
	 * @author fym creatOn 2014.11.1
	 * @param con
	 * @return
	 */
	private ArrayList<BankModel> getSelectBankNames(String con) {
		ArrayList<BankModel> names = new ArrayList<BankModel>();
		// 判断查询的内容是不是汉字
		Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p_str.matcher(con);
		String sqlString = null;
		if (m.find() && m.group(0).equals(con)) {
			sqlString = "SELECT * FROM T_bank WHERE BankName LIKE " + "\""
					+ "%" + con + "%" + "\"" + " ORDER BY BankLetter";
		} else {
			sqlString = "SELECT * FROM T_bank WHERE BankShortName LIKE " + "\""
					+ "%" + con + "%" + "\"" + " ORDER BY BankLetter";
		}
		Cursor cursor = database.rawQuery(sqlString, null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			BankModel bankModel = new BankModel();
			bankModel.setBankName(cursor.getString(cursor
					.getColumnIndex("BankName")));
			bankModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("BankLetter")));
			bankModel.setBankID(cursor.getString(cursor
					.getColumnIndex("BankNum")));
			bankModel.setBankShortName(cursor.getString(cursor
					.getColumnIndex("BankShortName")));
			names.add(bankModel);
		}
		cursor.close();
		return names;
	}

	/**
	 * 从数据库获取城市数据
	 * 
	 * @return
	 */
	private ArrayList<BankModel> getBankNames() {
		ArrayList<BankModel> names = new ArrayList<BankModel>();

		Cursor cursor = database.rawQuery(
				"SELECT * FROM T_bank ORDER BY BankLetter", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			BankModel bankModel = new BankModel();
			bankModel.setBankName(cursor.getString(cursor
					.getColumnIndex("BankName")));
			bankModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("BankLetter")));
			bankModel.setBankID(cursor.getString(cursor
					.getColumnIndex("BankNum")));
			bankModel.setBankShortName(cursor.getString(cursor
					.getColumnIndex("BankShortName")));
			names.add(bankModel);
		}
		cursor.close();
		return names;
	}

	/**
	 * 城市列表点击事件
	 * 
	 * @author sy
	 * 
	 */
	class BankListOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			BankModel bankModel = (BankModel) mBankLit.getAdapter()
					.getItem(pos);
			Intent intentGet = getIntent();
			Intent intent = new Intent();
			intent.putExtra("bankName", bankModel.getBankName());
			intent.putExtra("bankNum", bankModel.getBankID());
			intent.putExtra("name", intentGet.getStringExtra("name"));
			intent.putExtra("card_no", intentGet.getStringExtra("card_no"));
			intent.putExtra("open_bank", intentGet.getStringExtra("open_bank"));

			setResult(RESULT_OK, intent);
			finish();

		}

	}

	/**
	 * 为ListView设置适配器
	 * 
	 * @param list
	 */
	private void setAdapter(List<BankModel> list) {
		if (list != null) {
			adapter = new ListAdapter(this, list);
			mBankLit.setAdapter(adapter);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回键
			finish_return();
			// overridePendingTransition(R.anim.translate_horizontal_finish_in,
			// R.anim.translate_horizontal_finish_out);
		}
		return true;
	}

	public void finish_return() {
		Intent intentGet = getIntent();
		Intent intent = new Intent();
		intent.putExtra("name", intentGet.getStringExtra("name"));
		intent.putExtra("card_no", intentGet.getStringExtra("card_no"));
		intent.putExtra("open_bank", intentGet.getStringExtra("open_bank"));

		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<BankModel> list;

		public ListAdapter(Context context, List<BankModel> list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameSort();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1)
						.getNameSort() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameSort();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getBankName());
			String currentStr = list.get(position).getNameSort();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.getNameSort() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				if (currentStr.equals("#"))
					holder.alpha.setText("热门银行");
				else
					holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mBankLit.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

}