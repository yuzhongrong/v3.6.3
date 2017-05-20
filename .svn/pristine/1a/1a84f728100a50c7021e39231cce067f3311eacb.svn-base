package com.jinr.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.regular.DialogCurrentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.RegularAccount;

public class DialogUtils {
    private static int checkPosition;
    private ArrayList<Integer> check = new ArrayList<>();

    public interface DialogListerer {
        public void ok(String... params);

        public void cancel();

    }

    public interface DialogCheckListerer {
        public void ok(String... params);

        public void cancel();

        public void check(List<HashMap<String, Object>> list);
    }

    public interface DialogKeyListerer {
        public void ok(String... params);

        public void cancel();

        public void back();
    }

    // 首次投资Dialog
    public static void InvestDialog(Context context, String content, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_cash_bonus, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        // lp.width = dm.widthPixels - DensityUtil.dip2px(context, 10); // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return true; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView content_txt = (TextView) view.findViewById(R.id.content_txt);
        content_txt.setText(content);
        Button cashBonusLookBtn = (Button) view.findViewById(R.id.cash_bonus_look_btn);
        Button cashBonusInvestmentBtn = (Button) view.findViewById(R.id.cash_bonus_investment_btn);
        cashBonusLookBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });
        cashBonusInvestmentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.cancel();
                }
                mDialog.cancel();
            }
        });
    }

    // 没有标题的dialog
    public static void noTitleDialog(Context context, String content, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_cancle_cash_invest, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        // lp.width = dm.widthPixels - DensityUtil.dip2px(context, 10); // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return true; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView content_txt = (TextView) view.findViewById(R.id.content_txt);
        content_txt.setText(content);
        Button cancleBtn = (Button) view.findViewById(R.id.cancle_btn);
        Button gotoBtn = (Button) view.findViewById(R.id.goto_btn);
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.cancel();
                }
                mDialog.cancel();
            }
        });
        gotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });
    }

    // 只有一个按钮的dialog
    public static void lookDialog(Context context, String title, String content, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_banner_invest, null);

        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return true; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView content_txt = (TextView) view.findViewById(R.id.content_txt);
        content_txt.setText(content);
        TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        LinearLayout look_btn = (LinearLayout) view.findViewById(R.id.look_btn);
        look_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });
    }

    // 判断新老用户入口
    public static void firstShotDialog(Context context, Bitmap bitmap, final DialogKeyListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_first_shot, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.WRAP_CONTENT; // 宽度
        lp.height = LayoutParams.MATCH_PARENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        ImageView cancleBtn = (ImageView) view.findViewById(R.id.cancle_img);
        ImageView gotoIv = (ImageView) view.findViewById(R.id.back);
        gotoIv.setImageBitmap(bitmap);

        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.cancel();
                }
                mDialog.cancel();
            }
        });
        gotoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });
    }

    // 转出dialog
    public static void CaseDialog(Context context, String title, String currentMoney, String regularMoney, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_roll_out, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return false; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView current_money = (TextView) view.findViewById(R.id.current_money);
        current_money.setText(currentMoney);
        TextView regular_money = (TextView) view.findViewById(R.id.regular_money);
        regular_money.setText(regularMoney);
        TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        RelativeLayout currentMoneyLay = (RelativeLayout) view.findViewById(R.id.current_money_lay);
        RelativeLayout regularMoneyLay = (RelativeLayout) view.findViewById(R.id.regular_money_lay);
        currentMoneyLay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });

        regularMoneyLay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.cancel();
                }
                mDialog.cancel();
            }
        });
    }

    // 转出dialog
    public static void currentDialog(Context context, final List<RegularAccount> list, String title, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_total_push, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.regulardialogWindowAnim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return false; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        ListView lv = (ListView) view.findViewById(R.id.regular_list);
        DialogCurrentAdapter adapter = new DialogCurrentAdapter(context, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position + 1;
                if (Integer.valueOf(list.get(position).getStatus()) != 1) {
                    if (listerer != null) {
                        listerer.ok(checkPosition + "");
                    }
                    mDialog.cancel();
                }
            }
        });
    }

    public static void totalPushDialog(Context context, final List<RegularAccount> list, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_total_push, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.regulardialogWindowAnim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return false; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        ListView lv = (ListView) view.findViewById(R.id.regular_list);
        DialogCurrentAdapter adapter = new DialogCurrentAdapter(context, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position + 1;
                if (list.get(position).getStatus() == null) {
                    if (listerer != null) {
                        listerer.ok(checkPosition + "");
                    }
                    mDialog.cancel();
                } else if (Integer.valueOf(list.get(position).getStatus()) != 1) {
                    if (listerer != null) {
                        listerer.ok(checkPosition + "");
                    }
                    mDialog.cancel();
                }
            }
        });
    }

    // 定期转出dialog
    public static void RegualrDialog(Context context, final List<HashMap<String, Object>> mapLists, final DialogCheckListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_regular, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.regulardialogWindowAnim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        // lp.width = dm.widthPixels - DensityUtil.dip2px(context, 10); // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return false; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        ListView lv = (ListView) view.findViewById(R.id.regular_list);
        final SimpleAdapter adapter = new SimpleAdapter(context, mapLists, R.layout.dialog_regualr_item, new String[]{
                "name", "value", "checked"}, new int[]{R.id.current_money_name, R.id.current_money,
                R.id.cb_shortcut_check});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position + 1;
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_shortcut_check);
                HashMap<String, Object> selectMap = mapLists.get(position);
                boolean checked = (Boolean) selectMap.get("checked");
                if (checked) {
                    cb.setChecked(false);
                    selectMap.put("checked", false);
                } else {
                    cb.setChecked(true);
                    selectMap.put("checked", true);
                }
                mapLists.remove(position);
                mapLists.add(position, selectMap);
                adapter.notifyDataSetChanged();
            }

        });
        Button look_btn = (Button) view.findViewById(R.id.look_btn);
        look_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.check(mapLists);
                }
                mDialog.cancel();
            }
        });
    }

    // 只有一个按钮的dialog
    public static void DayupDialog(Context context, String name, String money, final DialogListerer listerer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        view = inflater.inflate(R.layout.dialog_dayupturnout, null);
        final Dialog mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.show();
        // 设置对话框大小
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    return true; // 按回退键时，dialog不消失
                }
                return false;
            }
        });
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_name.setText(name);
        tv_money.setText(money);
        RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_sure);
        rl_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerer != null) {
                    listerer.ok();
                }
                mDialog.cancel();
            }
        });
    }
}
